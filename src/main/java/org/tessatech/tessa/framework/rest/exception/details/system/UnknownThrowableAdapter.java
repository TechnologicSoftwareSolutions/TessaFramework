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

package org.tessatech.tessa.framework.rest.exception.details.system;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.tessatech.tessa.framework.rest.exception.details.TessaExceptionCodes;
import org.tessatech.tessa.framework.core.exception.details.ThrowableAdapterFinder;
import org.tessatech.tessa.framework.core.exception.system.UnknownException;
import org.tessatech.tessa.framework.rest.exception.details.RestThrowableAdapter;

@Component
public class UnknownThrowableAdapter extends RestThrowableAdapter
{
	public UnknownThrowableAdapter()
	{
		super(UnknownException.class);
		ThrowableAdapterFinder.registerFallbackExceptionDetails(this);
	}

	@Override
	public HttpStatus getHttpStatus()
	{
		return TessaExceptionCodes.UNKNOWN_ERROR_HTTP_STATUS;
	}

	@Override
	public long getExceptionCode(Throwable throwable)
	{
		return TessaExceptionCodes.UNKNOWN_ERROR_CODE;
	}

	@Override
	public String getExceptionMessage(Throwable throwable)
	{
		return TessaExceptionCodes.UNKNOWN_ERROR_MESSAGE;
	}

}
