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

package org.springframework.core.env;

/**
 * Interface representing the environment in which the current application is running.
 * Models two key aspects of the application environment: <em>profiles</em> and
 * <em>properties</em>. Methods related to property access are exposed via the
 * {@link PropertyResolver} superinterface.
 *
 * <p>A <em>profile</em> is a named, logical group of bean definitions to be registered
 * with the container only if the given profile is <em>active</em>. Beans may be assigned
 * to a profile whether defined in XML or via annotations; see the spring-beans 3.1 schema
 * or the {@link org.springframework.context.annotation.Profile @Profile} annotation for
 * syntax details. The role of the {@code Environment} object with relation to profiles is
 * in determining which profiles (if any) are currently {@linkplain #getActiveProfiles
 * active}, and which profiles (if any) should be {@linkplain #getDefaultProfiles active
 * by default}.
 *
 * <p><em>Properties</em> play an important role in almost all applications, and may
 * originate from a variety of sources: properties files, JVM system properties, system
 * environment variables, JNDI, servlet context parameters, ad-hoc Properties objects,
 * Maps, and so on. The role of the environment object with relation to properties is to
 * provide the user with a convenient service interface for configuring property sources
 * and resolving properties from them.
 *
 * <p>Beans managed within an {@code ApplicationContext} may register to be {@link
 * org.springframework.context.EnvironmentAware EnvironmentAware} or {@code @Inject} the
 * {@code Environment} in order to query profile state or resolve properties directly.
 *
 * <p>In most cases, however, application-level beans should not need to interact with the
 * {@code Environment} directly but instead may have to have {@code ${...}} property
 * values replaced by a property placeholder configurer such as
 * {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 * PropertySourcesPlaceholderConfigurer}, which itself is {@code EnvironmentAware} and
 * as of Spring 3.1 is registered by default when using
 * {@code <context:property-placeholder/>}.
 *
 * <p>Configuration of the environment object must be done through the
 * {@code ConfigurableEnvironment} interface, returned from all
 * {@code AbstractApplicationContext} subclass {@code getEnvironment()} methods. See
 * {@link ConfigurableEnvironment} Javadoc for usage examples demonstrating manipulation
 * of property sources prior to application context {@code refresh()}.
 *
 * @author Chris Beams
 * @since 3.1
 * @see PropertyResolver
 * @see EnvironmentCapable
 * @see ConfigurableEnvironment
 * @see AbstractEnvironment
 * @see StandardEnvironment
 * @see org.springframework.context.EnvironmentAware
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#setEnvironment
 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
 * 【表示当前应用程序正在运行的环境】
 */
/*
	应用程序的环境有两个关键方面：profile 和 properties。
		properties 的方法由 PropertyResolver 定义。
		profile 则表示当前的运行环境，
		对于应用程序中的 properties 而言，【并不是所有的都会加载到系统中】，只有其属性与 profile 一致才会被激活加载，

	所以 Environment 对象的作用，
	是确定哪些【配置文件】（如果有）当前处于活动状态，以及默认情况下【哪些配置文件】（如果有）应处于活动状态。

	properties 在几乎所有应用程序中都发挥着重要作用，
		【并且有多种来源：属性文件，JVM 系统属性，系统环境变量，JNDI，servlet 上下文参数，ad-hoc 属性对象，映射等。】

	同时Environment继承 PropertyResolver 接口，
	所以与属性相关的 Environment 对象其主要是为用户提供方便的服务接口，用于配置属性源和从中属性源中解析属性。
 */
public interface Environment extends PropertyResolver {
	/*
		PropertyResolver：提供属性访问功能
		Environment：提供访问和判断 profiles 的功能
		ConfigurableEnvironment：提供设置激活的 profile 和默认的 profile 的功能以及操作 Properties 的工具
		ConfigurableWebEnvironment：提供配置 Servlet 上下文和 Servlet 参数的功能
		AbstractEnvironment：实现了 ConfigurableEnvironment 接口，默认属性和存储容器的定义，并且实现了 ConfigurableEnvironment 的方法，并且为子类预留可覆盖了扩展方法
		StandardEnvironment：继承自 AbstractEnvironment ，非 Servlet(Web) 环境下的标准 Environment 实现
		StandardServletEnvironment：继承自 StandardEnvironment ，Servlet(Web) 环境下的标准 Environment 实现
	 */

	// 返回此环境下激活的配置文件集
	String[] getActiveProfiles();

	// 如果未设置激活配置文件，则返回默认的激活的配置文件集
	String[] getDefaultProfiles();

	/**
	 */
	@Deprecated
	boolean acceptsProfiles(String... profiles);

	/**
	 * Return whether the {@linkplain #getActiveProfiles() active profiles}
	 * match the given {@link Profiles} predicate.
	 */
	boolean acceptsProfiles(Profiles profiles);

}
