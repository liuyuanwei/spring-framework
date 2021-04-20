/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/**
 * From 《Spring 源码深度解析》：主要定义资源文件读取并转换为 BeanDefinition 的各个功能。
 *
 * Simple interface for bean definition readers.
 * Specifies load methods with Resource and String location parameters.
 *
 * <p>Concrete bean definition readers can of course add additional
 * load and register methods for bean definitions, specific to
 * their bean definition format.
 *
 * <p>Note that a bean definition reader does not have to implement
 * this interface. It only serves as suggestion for bean definition
 * readers that want to follow standard naming conventions.
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see org.springframework.core.io.Resource
 * 作用是读取 Spring 的配置文件的内容，【并将其转换成 Ioc 容器内部的数据结构 ：BeanDefinition 】。
 *
 */
/*
	ClassPathResource resource = new ClassPathResource("bean.xml"); // <1>
	DefaultListableBeanFactory factory = new DefaultListableBeanFactory(); // <2>
	XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory); // <3>
	reader.loadBeanDefinitions(resource); // <4>
	这段代码是 Spring 中编程式使用 IoC 容器，通过这四段简单的代码，我们可以初步判断 IoC 容器的使用过程。
		获取资源
		获取 BeanFactory
		根据新建的 BeanFactory 创建一个 BeanDefinitionReader 对象，该 Reader 对象为资源的解析器
		装载资源
 */
public interface BeanDefinitionReader {

	/**
	 */
	BeanDefinitionRegistry getRegistry();

	/**
	 */
	@Nullable
	ResourceLoader getResourceLoader();

	/**
	 */
	@Nullable
	ClassLoader getBeanClassLoader();

	/**
	 */
	BeanNameGenerator getBeanNameGenerator();


	/**
	 */
	int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

	/**
	 */
	int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException;

	/**
	 */
	int loadBeanDefinitions(String location) throws BeanDefinitionStoreException;

	/**
	 */
	int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException;

}
