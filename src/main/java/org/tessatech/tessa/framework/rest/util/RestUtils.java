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

package org.tessatech.tessa.framework.rest.util;

import org.springframework.http.HttpHeaders;
import org.tessatech.tessa.framework.core.event.context.EventContext;
import org.tessatech.tessa.framework.core.event.context.EventContextHolder;
import org.tessatech.tessa.framework.core.security.context.SecurityContext;
import org.tessatech.tessa.framework.core.security.context.SecurityContextHolder;
import org.tessatech.tessa.framework.core.transaction.context.TransactionContext;
import org.tessatech.tessa.framework.core.transaction.context.TransactionContextHolder;
import org.tessatech.tessa.framework.core.util.UniqueIdentifierUtils;
import org.tessatech.tessa.framework.rest.request.TessaHttpHeaders;

import com.google.gson.Gson;

public class RestUtils
{
	private static final Gson gson = new Gson();

	public static TessaHttpHeaders buildTessaHttpHeaders()
	{
		// TODO setting the content-type header should be optional, possibly overload
		// this method with a headers Map as parameter
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("content-type", "application/json");
		TessaHttpHeaders headers = new TessaHttpHeaders(httpHeaders);
		EventContextHolder.getContextOptional().ifPresent(context -> populateTessaHeaders(headers, context));
		TransactionContextHolder.getContextOptional().ifPresent(context -> populateTessaHeaders(headers, context));
		return headers;
	}

	public static HttpHeaders buildHttpHeaders()
	{
		return buildTessaHttpHeaders().getHeaders();
	}

	public static TessaHttpHeaders buildTessaHttpHeadersWithAuth()
	{
		TessaHttpHeaders headers = buildTessaHttpHeaders();
		addClientAuthToHeaders(headers);
		return headers;
	}

	public static HttpHeaders buildHttpHeadersWithAuth()
	{
		return buildTessaHttpHeadersWithAuth().getHeaders();
	}

	private static TessaHttpHeaders addClientAuthToHeaders(TessaHttpHeaders headers)
	{
		SecurityContext context = SecurityContextHolder.getContext();

		// TODO This needs to be moved into a service/util, not hacked in here.
		String token = "Bearer " + context.getSecurityToken().getRawToken();
		headers.setAuthorization(token);

		return addClientTraceDetailsToHeaders(headers);
	}

	public static TessaHttpHeaders addClientTraceDetailsToHeaders(TessaHttpHeaders headers)
	{
		if (TransactionContextHolder.isPresent())
		{
			TransactionContext context = TransactionContextHolder.getContext();
			headers.setSessionId(context.getSessionId());
			headers.setClientIp(context.getClientIp());
			headers.setDeviceId(context.getDeviceId());
			headers.setDeviceType(context.getDeviceType());
		}

		return headers;
	}

	private static TessaHttpHeaders populateTessaHeaders(TessaHttpHeaders headers, TransactionContext context)
	{
		String requestId = String.valueOf(UniqueIdentifierUtils.getUniqueId());
		String correlationId = context.getCorrelationId();

		if (correlationId == null)
		{
			correlationId = requestId;
		}

		headers.setRequestId(requestId);
		headers.setCorrelationId(context.getCorrelationId());
		headers.setSessionId(context.getSessionId());

		return headers;
	}

	private static TessaHttpHeaders populateTessaHeaders(TessaHttpHeaders headers, EventContext context)
	{
		String requestId = String.valueOf(UniqueIdentifierUtils.getUniqueId());

		headers.setRequestId(requestId);
		headers.setCorrelationId(requestId);
		return headers;
	}

}
