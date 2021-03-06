/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.handler;

import org.springframework.web.method.HandlerMethod;

/**
 * Handler 的 Method 的 Mapping 的名字生成策略接口
 *
 * A strategy for assigning a name to a handler method's mapping.
 *
 * <p>The strategy can be configured on
 * {@link org.springframework.web.servlet.handler.AbstractHandlerMethodMapping
 * AbstractHandlerMethodMapping}. It is used to assign a name to the mapping of
 * every registered handler method. The names can then be queried via
 * {@link org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#getHandlerMethodsForMappingName(String)
 * AbstractHandlerMethodMapping#getHandlerMethodsForMappingName}.
 *
 * <p>Applications can build a URL to a controller method by name with the help
 * of the static method
 * {@link org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder#fromMappingName(String)
 * MvcUriComponentsBuilder#fromMappingName} or in JSPs through the "mvcUrl"
 * function registered by the Spring tag library.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 * @param <T> the mapping type
 */
/**
 * Mapping 命名策略
 * Handler 的 Method 的 Mapping 的名字生成策略接口
 * 可能不太好理解，获得 Mapping 的名字。这样，我们就可以根据 Mapping 的名字，获得 Handler 。
 */
/*
	org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMethodMappingNamingStrategy 实现该接口
	比较好理解，分成两种情况。
		情况一，如果 Mapping 已经配置名字，则直接返回。例如，@RequestMapping(name = "login", value = "user/login") 注解的方法，
		它对应的 Mapping 的名字就是 "login" 。
		情况二，如果 Mapping 未配置名字，则使用使用类名大写 + "#" + 方法名。例如，@RequestMapping(value = "user/login") 注解的方法，
		假设它所在的类为 UserController ，对应的方法名为 login ，则它对应的 Mapping 的名字就是 USERCONTROLLER#login 。
 */
@FunctionalInterface
public interface HandlerMethodMappingNamingStrategy<T> {

	/**
     * 获得名字
	 */
	String getName(HandlerMethod handlerMethod, T mapping);

}