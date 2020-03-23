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

package org.springframework.beans.factory.config;

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.lang.Nullable;

/**
 * Extension of the {@link org.springframework.beans.factory.BeanFactory}
 * interface to be implemented by bean factories that are capable of
 * autowiring, provided that they want to expose this functionality for
 * existing bean instances.
 *
 * <p>This subinterface of BeanFactory is not meant to be used in normal
 * application code: stick to {@link org.springframework.beans.factory.BeanFactory}
 * or {@link org.springframework.beans.factory.ListableBeanFactory} for
 * typical use cases.
 *
 * <p>Integration code for other frameworks can leverage this interface to
 * wire and populate existing bean instances that Spring does not control
 * the lifecycle of. This is particularly useful for WebWork Actions and
 * Tapestry Page objects, for example.
 *
 * <p>Note that this interface is not implemented by
 * {@link org.springframework.context.ApplicationContext} facades,
 * as it is hardly ever used by application code. That said, it is available
 * from an application context too, accessible through ApplicationContext's
 * {@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()}
 * method.
 *
 * <p>You may also implement the {@link org.springframework.beans.factory.BeanFactoryAware}
 * interface, which exposes the internal BeanFactory even when running in an
 * ApplicationContext, to get access to an AutowireCapableBeanFactory:
 * simply cast the passed-in BeanFactory to AutowireCapableBeanFactory.
 *
 * @author Juergen Hoeller
 * @since 04.12.2003
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
/*
	对于想要拥有自动装配能力，并且想要把这种能力暴露给外部应用BeanFactory类需要实现此接口。

　　正常情况下不要使用此接口，应该更倾向于使用BeanFactory或者ListableBeanFactory接口。

　　此接口主要是针对框架之外，没有向Spring托管的Bean的应用。通过暴露此功能，Spring框架之外的程序，具有自动装配Spring的功能

　　需要注意的是ApplicationContext并没有实现此接口。因为应用代码很少使用此功能。如果确实需要的话可以调用ApplicationContext.getAutowireCapableBeanFactory()的方法，来获取此接口的实例

　　如果一个类实现了此接口，那么很大程度上它还需要实现BeanFactoryWare接口。它可以在应用上下文中返回BeanFactory;
 */
public interface AutowireCapableBeanFactory extends BeanFactory {
	// 常量，用于标识外部自动装配功能是否可用。但是此标识不影响正常的（基于注解的等）自动装配功能的使用
	int AUTOWIRE_NO = 0;
	// 标识按名装配的常量
	int AUTOWIRE_BY_NAME = 1;
	// 标识按类型自动装配的常量
	int AUTOWIRE_BY_TYPE = 2;
	// 标识按照贪婪策略匹配出的最符合的构造方法来自动装配的常量
	int AUTOWIRE_CONSTRUCTOR = 3;
	// 标识自动识别一种装配策略来实现自动装配的常量
	@Deprecated
	int AUTOWIRE_AUTODETECT = 4;
	String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";

	// 创建一个给定Class的实例。
	<T> T createBean(Class<T> beanClass) throws BeansException;

	// 通过调用给定Bean的after-instantiation及post-processing接口，对bean进行配置。
	void autowireBean(Object existingBean) throws BeansException;

	// 配置参数中指定的bean，包括自动装配其域，对其应用如setBeanName功能的回调函数。
	Object configureBean(Object existingBean, String beanName) throws BeansException;

	// 创建一个指定class的实例，通过参数可以指定其自动装配模式（by-name or by-type）.
	Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	// 通过指定的自动装配策略来初始化一个Bean。
	Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	// 通过指定的自动装配方式来对给定的Bean进行自动装配。
	void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
			throws BeansException;

	// 将参数中指定了那么的Bean，注入给定实例当中
	void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

	// 初始化参数中指定的Bean，调用任何其注册的回调函数如setBeanName、setBeanFactory等。
	Object initializeBean(Object existingBean, String beanName) throws BeansException;

	// 调用参数中指定Bean的postProcessBeforeInitialization方法
	Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException;

	// 调用参数中指定Bean的postProcessAfterInitialization方法
	Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException;

	// 销毁参数中指定的Bean，同时调用此Bean上的DisposableBean和DestructionAwareBeanPostProcessors方法
	void destroyBean(Object existingBean);

	// 销毁参数中指定的Bean，同时调用此Bean上的DisposableBean和DestructionAwareBeanPostProcessors方法
	<T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

	// 查找唯一符合指定类的实例，如果有，则返回实例的名字和实例本身
	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;

	// 解析出在Factory中与指定Bean有指定依赖关系的Bean
	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,
			@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;

}
