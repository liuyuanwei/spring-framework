/*
 * Copyright 2002-2016 the original author or authors.
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

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Additional interface that a {@link HandlerMapping} can implement to expose
 * a request matching API aligned with its internal request matching
 * configuration and implementation.
 *
 * @author Rossen Stoyanchev
 * @since 4.3.1
 * @see HandlerMappingIntrospector
 * 定义判断请求和指定 pattern 路径是否匹配的接口方法
 * 目前实现 MatchableHandlerMapping 接口的类，有 RequestMappingHandlerMapping 类和 AbstractUrlHandlerMapping 抽象类。
 */
public interface MatchableHandlerMapping extends HandlerMapping {

	/**
     * 判断请求和指定 `pattern` 路径是否匹配的接口方法
	 * 返回：请求匹配结果。
	 */
	@Nullable
	RequestMatchResult match(HttpServletRequest request, String pattern);

}