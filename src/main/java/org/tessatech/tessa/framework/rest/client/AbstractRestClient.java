/*
 * Copyright (c) 2015-2018 TessaTech LLC.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 *  except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 */

package org.tessatech.tessa.framework.rest.client;

import java.net.URI;
import java.time.ZonedDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.tessatech.tessa.framework.core.exception.system.ExternalException;
import org.tessatech.tessa.framework.core.exception.system.InternalException;
import org.tessatech.tessa.framework.core.logging.external.ExternalCallAttributesBuilder;
import org.tessatech.tessa.framework.core.security.provider.tessa.jwt.client.TessaIAMServiceClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import io.atlassian.fugue.Either;

public abstract class AbstractRestClient<ErrorResponse>
{
	private static final Logger logger = LogManager.getLogger(TessaIAMServiceClient.class);
	private static final Gson gson = buildGson();

	private String systemName;
	private String serviceName;
	private String serviceVersion;

	private Class<ErrorResponse> errorClass;

	private RestTemplate restTemplate = buildRestTemplate();

	public AbstractRestClient(String systemName, String serviceName, String serviceVersion,
			Class<ErrorResponse> errorClass)
	{
		this.systemName = systemName;
		this.serviceName = serviceName;
		this.serviceVersion = serviceVersion;
		this.errorClass = errorClass;
	}


	protected <Request, SuccessfulResponse> SuccessfulResponse execute(String methodName, Request request, String uri,
			HttpMethod method, Class<SuccessfulResponse> successfulResponseClass)
	{
		Either<ErrorResponse, SuccessfulResponse> response =
				execute(methodName, request, buildHttpHeaders(), uri, method, true, successfulResponseClass);


		if (response.isLeft())
		{
			throw new InternalException("AbstractRestClient is retuning null when throwErrors is turned on.");
		}

		return response.right().get();
	}

	protected <Request, SuccessfulResponse> Either<ErrorResponse, SuccessfulResponse> execute(String methodName,
			Request request, String uri, HttpMethod method, boolean throwErrors,
			Class<SuccessfulResponse> successfulResponseClass)
	{
		return execute(methodName, request, buildHttpHeaders(), uri, method, throwErrors, successfulResponseClass);
	}


	protected abstract HttpHeaders buildHttpHeaders();

	protected ResponseErrorHandler getResponseErrorHandler()
	{
		return new TessaClientErrorHandler();
	}

	private RestTemplate buildRestTemplate()
	{
		RestTemplate template = new RestTemplateBuilder().setConnectTimeout(15000).setReadTimeout(15000).build();
		template.setErrorHandler(getResponseErrorHandler());
		return template;
	}

	protected abstract ExternalException convertErrorIntoException(ErrorResponse errorResponse);

	protected abstract void addErrorAttributes(ExternalCallAttributesBuilder builder, ErrorResponse response);

	private <Request, SuccessfulResponse> Either<ErrorResponse, SuccessfulResponse> execute(String serviceMethodName,
			Request request, HttpHeaders headers, String uri, HttpMethod method, boolean throwErrors,
			Class<SuccessfulResponse> successfulResponseClass)
	{
		ExternalCallAttributesBuilder builder =
				new ExternalCallAttributesBuilder(systemName, serviceName, serviceMethodName, method.name(),
						serviceVersion);
		builder.setHeaderInformation(headers);

		Either<ErrorResponse, SuccessfulResponse> responseEither = null;
		try
		{
			responseEither = performCall(request, headers, uri, method, builder, successfulResponseClass);

			if (isResponseAnError(responseEither))
			{
				addErrorAttributes(builder, responseEither.left().get());
				builder.setSuccess(false);

				handleError(throwErrors, builder, responseEither);
			}
			else
			{
				builder.setSuccess(true);
			}
		}
		catch (ExternalException externalException)
		{
			throw externalException;
		}
		catch (HttpStatusCodeException httpStatusCodeException)
		{
			handleStatusCodeException(builder, httpStatusCodeException);

		}
		catch (RuntimeException exception)
		{
			handleUnknownException(builder, exception);
		}
		finally
		{
			builder.buildAndCommit();
		}

		return responseEither;
	}

	private <Request, SuccessfulResponse> Either<ErrorResponse, SuccessfulResponse> performCall(Request request,
			HttpHeaders headers, String uri, HttpMethod method, ExternalCallAttributesBuilder builder,
			Class<SuccessfulResponse> successfulResponseClass)
	{
		RequestEntity<Request> requestEntity = new RequestEntity<>(request, headers, method, URI.create(uri));

		ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);


		builder.setHttpStatusCode(responseEntity.getStatusCode().value());

		return buildEitherFromResponse(responseEntity, successfulResponseClass);
	}

	private boolean isResponseAnError(Either<ErrorResponse, ?> responseEither)
	{
		return responseEither.isLeft();
	}

	private void handleError(boolean throwErrors, ExternalCallAttributesBuilder builder,
			Either<ErrorResponse, ?> responseEither)
	{
		if (throwErrors)
		{
			ExternalException externalException = convertErrorIntoException(responseEither.left().get());
			builder.setThrowable(externalException);
			throw externalException;
		}
	}

	private void handleUnknownException(ExternalCallAttributesBuilder builder, RuntimeException exception)
	{
		logger.error("Exception occurred executing Rest request", exception);
		builder.setThrowable(exception).setSuccess(false);

		throw exception;

	}

	private void handleStatusCodeException(ExternalCallAttributesBuilder builder,
			HttpStatusCodeException httpStatusCodeException)
	{
		logger.error("HttpStatusCodeException occurred executing Rest request", httpStatusCodeException);
		builder.setHttpStatusCodeException(httpStatusCodeException).setSuccess(false);

		throw httpStatusCodeException;
	}

	private <SuccessfulResponse> Either<ErrorResponse, SuccessfulResponse> buildEitherFromResponse(
			ResponseEntity<String> responseEntity, Class<SuccessfulResponse> successClass)
	{
		String responseBody = (responseEntity.getBody() == null ? "{}": responseEntity.getBody());
		if (!responseEntity.getStatusCode().is1xxInformational() && !responseEntity.getStatusCode().is2xxSuccessful())
		{
			return Either.left(gson.fromJson(responseBody, errorClass));
		}

		return Either.right(gson.fromJson(responseBody, successClass));
	}

	private static Gson buildGson()
	{
		return new GsonBuilder()
				.registerTypeAdapter(ZonedDateTime.class,
						(JsonDeserializer<ZonedDateTime>) (json, typeOfT, context) -> ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString()))
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
				.create();
	}
}
