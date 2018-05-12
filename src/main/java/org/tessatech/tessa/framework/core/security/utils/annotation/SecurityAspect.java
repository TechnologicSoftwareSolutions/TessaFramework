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

package org.tessatech.tessa.framework.core.security.utils.annotation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tessatech.tessa.framework.core.logging.context.LoggingContextHolder;
import org.tessatech.tessa.framework.core.security.utils.SecurityUtils;

@Aspect
@Component
public class SecurityAspect
{
	private static final Logger logger = LogManager.getLogger(SecurityAspect.class);

	@Autowired
	private SecurityUtils securityUtils;

	@Pointcut("execution(public * *(..))")
	void anyPublicMethod()
	{
	}

	@Around("anyPublicMethod() && @annotation(hasAllRoles)")
	public Object hasAllRoles(ProceedingJoinPoint proceedingJoinPoint, HasAllRoles hasAllRoles) throws Throwable
	{
		securityUtils.validateUserHasAllRoles(hasAllRoles.authorizedRoles());
		return proceedingJoinPoint.proceed();
	}

	@Around("anyPublicMethod() && @annotation(hasRole)")
	public Object hasRole(ProceedingJoinPoint proceedingJoinPoint, HasRole hasRole) throws Throwable
	{
		securityUtils.validateUserHasRole(hasRole.authorizedRole());
		return proceedingJoinPoint.proceed();
	}

	@Around("anyPublicMethod() && @annotation(hasAnyRole)")
	public Object hasAnyRole(ProceedingJoinPoint proceedingJoinPoint, HasAnyRole hasAnyRole) throws Throwable
	{
		securityUtils.validateUserHasAnyRole(hasAnyRole.authorizedRoles());
		return proceedingJoinPoint.proceed();
	}
}
