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

package org.springframework.context;

import java.io.Closeable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.lang.Nullable;

/**
 *
 * @since 03.11.2003
 * ConfigurableApplicationContext 接口提供的方法都是对 ApplicationContext 进行配置的
 */
/*
	同时它还继承了如下两个接口：
		Lifecycle：【对 context 生命周期的管理】，它提供 #start() 和 #stop() 方法启动和暂停组件。
		Closeable：关闭，释放资源。标准 JDK 所提供的一个接口，用于最后关闭组件释放资源等。

	WebApplicationContext 接口和 ConfigurableApplicationContext 接口有一个共同的子类接口 ConfigurableWebApplicationContext
 */
public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable {

	String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

	String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

	String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";

	String ENVIRONMENT_BEAN_NAME = "environment";

	String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";

	String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";

	// 为 ApplicationContext 设置唯一 ID
	void setId(String id);

	// 为 ApplicationContext 设置 parent
	// 父类不应该被修改：如果创建的对象不可用时，则应该在构造函数外部设置它
	void setParent(@Nullable ApplicationContext parent);

	// 设置 Environment
	void setEnvironment(ConfigurableEnvironment environment);

	// 获取 Environment
	@Override
	ConfigurableEnvironment getEnvironment();

	// 添加 BeanFactoryPostProcessor
	void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

	// 添加 ApplicationListener
	void addApplicationListener(ApplicationListener<?> listener);

	// 添加 ProtocolResolver
	void addProtocolResolver(ProtocolResolver resolver);

	// 加载或者刷新配置
	// 】】】刷新整个 Spring 上下文信息，定义了整个 Spring 上下文加载的流程。
	// 其实现是在 AbstractApplicationContext 中实现。
	void refresh() throws BeansException, IllegalStateException;

	// 注册 shutdown hook
	void registerShutdownHook();

	// 关闭 ApplicationContext
	@Override
	void close();

	// ApplicationContext 是否处于激活状态
	boolean isActive();

	// 获取当前上下文的 BeanFactory
	ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

}
