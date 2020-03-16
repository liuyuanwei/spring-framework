/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.web.context;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;

/**
 *
 * @since 05.12.2003
 */
/*
	WebApplicationContext 接口和 ConfigurableApplicationContext 接口有一个共同的子类接口 ConfigurableWebApplicationContext
	该接口将这两个接口进行合并，提供了一个可配置、可管理、可关闭的 WebApplicationContext ，
	同时该接口还增加了 #setServletContext(ServletContext servletContext)，setServletConfig(ServletConfig servletConfig) 等方法，
	用于装配 WebApplicationContext 。

	上面三个接口就可以构成一个比较完整的 Spring 容器，
 */
public interface ConfigurableWebApplicationContext extends WebApplicationContext, ConfigurableApplicationContext {


	String APPLICATION_CONTEXT_ID_PREFIX = WebApplicationContext.class.getName() + ":";

	String SERVLET_CONFIG_BEAN_NAME = "servletConfig";

	// 为spring设置web应用上下文
	void setServletContext(@Nullable ServletContext servletContext);

	// 设置ServletConfig
	void setServletConfig(@Nullable ServletConfig servletConfig);

	// 获取ServletConfig
	@Nullable
	ServletConfig getServletConfig();

	void setNamespace(@Nullable String namespace);

	@Nullable
	String getNamespace();

	// 设置Spring配置的文件地址
	void setConfigLocation(String configLocation);
	void setConfigLocations(String... configLocations);

	// 获取配置文件
	@Nullable
	String[] getConfigLocations();

}
