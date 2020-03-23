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

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.lang.Nullable;

/**
 * A BeanDefinition describes a bean instance, which has property values,
 * constructor argument values, and further information supplied by
 * concrete implementations.
 *
 * <p>This is just a minimal interface: The main intention is to allow a
 * {@link BeanFactoryPostProcessor} such as {@link PropertyPlaceholderConfigurer}
 * to introspect and modify property values and other bean metadata.
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 19.03.2004
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 * BeanDefinition 继承 AttributeAccessor 和 BeanMetadataElement 接口
 * 是一个【描述接口】，它描述了一个 Bean 实例的定义，包括属性值、构造方法值和继承自它的类的更多信息
 * 虽然接口方法比较多，但是是不是一下子和我们平时使用 <bean> 标签的属性，能够对应上落
 */
/*
	我们常用的三个实现类有：
		org.springframework.beans.factory.support.ChildBeanDefinition
		org.springframework.beans.factory.support.RootBeanDefinition
		org.springframework.beans.factory.support.GenericBeanDefinition
	ChildBeanDefinition、RootBeanDefinition、GenericBeanDefinition 三者都继承 AbstractBeanDefinition 抽象类，
	即 AbstractBeanDefinition 对三个子类的共同的类信息进行抽象。

	如果配置文件中定义了父 <bean> 和 子 <bean> ，
	则父 <bean> 用 RootBeanDefinition 表示，子 <bean> 用 ChildBeanDefinition 表示，
	而没有父 <bean> 的就使用RootBeanDefinition 表示。

	GenericBeanDefinition 为一站式服务类。😈 这个解释一脸懵逼？没事，继续往下看。
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	// 我们可以看到，默认只提供 sington 和 prototype 两种，
	// 很多读者都知道还有 request, session, globalSession, application, websocket 这几种，
	// 不过，它们属于基于 web 的扩展。
	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;


	/**
     * 角色 - 应用
     * 通常对于用户定义的 Bean 对象。
	 */
	int ROLE_APPLICATION = 0;
	int ROLE_SUPPORT = 1;
	/**
     * 角色 - 基础设施
     *
     * 通常用于框架内部工作的 Bean 对象来使用。
	 */
	int ROLE_INFRASTRUCTURE = 2;

	// 设置父 Bean，这里涉及到 bean 继承，不是 java 继承。请参见附录介绍
	void setParentName(@Nullable String parentName);
	// 获取父 Bean
	@Nullable
	String getParentName();

	// 设置 Bean 的类名称
	void setBeanClassName(@Nullable String beanClassName);
	// 获取 Bean 的类名称
	@Nullable
	String getBeanClassName();

	// 设置 bean 的 scope
	void setScope(@Nullable String scope);
	// 获取 Bean 的 scope
	@Nullable
	String getScope();

	// 设置是否懒加载
	void setLazyInit(boolean lazyInit);
	boolean isLazyInit();

	// 设置该 Bean 依赖的所有的 Bean，注意，这里的依赖不是指属性依赖(如 @Autowire 标记的)，
	// 是 depends-on="" 属性设置的值。
	void setDependsOn(@Nullable String... dependsOn);
	// 返回该 Bean 的所有依赖
	@Nullable
	String[] getDependsOn();

	// 设置该 Bean 是否可以注入到其他 Bean 中，只对根据类型注入有效，
	// 如果根据名称注入，即使这边设置了 false，也是可以的
	void setAutowireCandidate(boolean autowireCandidate);
	// 该 Bean 是否可以注入到其他 Bean 中
	boolean isAutowireCandidate();

	// 主要的。同一接口的多个实现，如果不指定名字的话，Spring 会优先选择设置 primary 为 true 的 bean
	void setPrimary(boolean primary);
	// 是否是 primary 的
	boolean isPrimary();

	// 如果该 Bean 采用工厂方法生成，【指定工厂名称】。对工厂不熟悉的读者，请参加附录
	void setFactoryBeanName(@Nullable String factoryBeanName);
	// 获取工厂名称
	@Nullable
	String getFactoryBeanName();

	// 指定工厂类中的 工厂方法名称
	void setFactoryMethodName(@Nullable String factoryMethodName);
	// 获取工厂类中的 工厂方法名称
	@Nullable
	String getFactoryMethodName();

	// 构造器参数
	ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * Return if there are constructor argument values defined for this bean.
	 * @since 5.0.2
	 */
	default boolean hasConstructorArgumentValues() {
		return !getConstructorArgumentValues().isEmpty();
	}

	// Bean 中的属性值，后面给 bean 注入属性值的时候会说到
	MutablePropertyValues getPropertyValues();

	/**
	 * Return if there are property values values defined for this bean.
	 * @since 5.0.2
	 */
	default boolean hasPropertyValues() {
		return !getPropertyValues().isEmpty();
	}

	/**
	 * Set the name of the initializer method.
	 * @since 5.1
	 */
	void setInitMethodName(@Nullable String initMethodName);

	/**
	 * Return the name of the initializer method.
	 * @since 5.1
	 */
	@Nullable
	String getInitMethodName();

	/**
	 * Set the name of the destroy method.
	 * @since 5.1
	 */
	void setDestroyMethodName(@Nullable String destroyMethodName);

	/**
	 * Return the name of the destroy method.
	 * @since 5.1
	 */
	@Nullable
	String getDestroyMethodName();

	/**
	 * Set the role hint for this {@code BeanDefinition}. The role hint
	 * provides the frameworks as well as tools with an indication of
	 * the role and importance of a particular {@code BeanDefinition}.
	 * @since 5.1
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 */
	void setRole(int role);

	/**
	 * Get the role hint for this {@code BeanDefinition}. The role hint
	 * provides the frameworks as well as tools with an indication of
	 * the role and importance of a particular {@code BeanDefinition}.
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 */
	int getRole();

	/**
	 * Set a human-readable description of this bean definition.
	 * @since 5.1
	 */
	void setDescription(@Nullable String description);

	/**
	 * Return a human-readable description of this bean definition.
	 */
	@Nullable
	String getDescription();


	// Read-only attributes

	// 是否 singleton
	boolean isSingleton();

	// 是否 prototype
	boolean isPrototype();

	// 如果这个 Bean 原生是抽象类，那么不能实例化
	boolean isAbstract();

	/**
	 * Return a description of the resource that this bean definition
	 * came from (for the purpose of showing context in case of errors).
	 */
	@Nullable
	String getResourceDescription();

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * Allows for retrieving the decorated bean definition, if any.
	 * <p>Note that this method returns the immediate originator. Iterate through the
	 * originator chain to find the original BeanDefinition as defined by the user.
	 */
	@Nullable
	BeanDefinition getOriginatingBeanDefinition();

}
