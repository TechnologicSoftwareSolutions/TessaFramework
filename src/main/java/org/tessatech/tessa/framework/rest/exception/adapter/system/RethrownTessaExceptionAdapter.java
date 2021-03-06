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

package org.tessatech.tessa.framework.rest.exception.adapter.system;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.tessatech.tessa.framework.core.exception.adapter.ExternalExceptionAdapter;
import org.tessatech.tessa.framework.core.exception.system.ExternalException;
import org.tessatech.tessa.framework.core.exception.system.RethrownTessaExternalException;
import org.tessatech.tessa.framework.rest.exception.adapter.RestThrowableAdapter;
import org.tessatech.tessa.framework.rest.exception.adapter.TessaExceptionCodes;

@Component
public class RethrownTessaExceptionAdapter extends RestThrowableAdapter implements ExternalExceptionAdapter
{
	public RethrownTessaExceptionAdapter()
	{
		super(RethrownTessaExternalException.class);
	}

	@Override
	public HttpStatus getHttpStatus(Throwable throwable)
	{
		if (throwable instanceof RethrownTessaExternalException)
		{
			return HttpStatus.valueOf(((RethrownTessaExternalException) throwable).getHttpStatus());
		}

		return TessaExceptionCodes.EXTERNAL_ERROR_HTTP_STATUS;
	}

	@Override
	public long getExceptionCode(Throwable throwable)
	{
		if (throwable instanceof RethrownTessaExternalException)
		{
			return ((RethrownTessaExternalException) throwable).getExceptionCode();
		}

		return TessaExceptionCodes.EXTERNAL_ERROR_CODE;
	}

	@Override
	public String getExceptionMessage(Throwable throwable)
	{
		if (throwable instanceof RethrownTessaExternalException)
		{
			return ((RethrownTessaExternalException) throwable).getExceptionMessage();
		}

		return TessaExceptionCodes.EXTERNAL_ERROR_MESSAGE;
	}

	@Override
	public String getExternalExceptionCode(Throwable throwable)
	{
		if(throwable instanceof ExternalException)
		{
			return ((ExternalException) throwable).getExternalExceptionCode();
		}

		return null;
	}

	@Override
	public String getExternalExceptionMessage(Throwable throwable)
	{
		if(throwable instanceof ExternalException)
		{
			return ((ExternalException) throwable).getExternalExceptionMessage();
		}

		return null;
	}
}
