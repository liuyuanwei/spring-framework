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

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * Configuration interface to be implemented by most bean factories. Provides
 * facilities to configure a bean factory, in addition to the bean factory
 * client methods in the {@link org.springframework.beans.factory.BeanFactory}
 * interface.
 *
 * <p>This bean factory interface is not meant to be used in normal application
 * code: Stick to {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * needs. This extended interface is just meant to allow for framework-internal
 * plug'n'play and for special access to bean factory configuration methods.
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.ListableBeanFactory
 * @see ConfigurableListableBeanFactory
 *
 * 参考
 * 		https://blog.csdn.net/u013412772/article/details/80819398
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

	// 单例
	String SCOPE_SINGLETON = "singleton";

	//原型
	String SCOPE_PROTOTYPE = "prototype";


	/**
	 * 设置父类的BeanFactory,可以在HierarchicalBeanFactory中看到父类的BeanFactory的设置
	 * 【父容器设置.而且一旦设置了就不让修改】搭配HierarchicalBeanFactory接口的getParentBeanFactory方法
	 */
	void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

	/**
	 * 设置类加载器。默认使用当前线程中的类加载器
	 * 类加载器设置，下面有获取的方法。
	 * @param beanClassLoader
	 */
	void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);

	/**
	 * 类加载器设置与获取.默认使用当前线程中的类加载器
	 * @return
	 */
	@Nullable
	ClassLoader getBeanClassLoader();

	/**
	 * 设置临时加载器
	 * 为了类型匹配,搞个临时类加载器.好在一般情况为null,使用上面定义的标准加载器
	 * @param tempClassLoader
	 */
	void setTempClassLoader(@Nullable ClassLoader tempClassLoader);

	/**
	 * 为了类型匹配,搞个临时类加载器.好在一般情况为null,使用上面定义的标准加载器
	 * @return
	 */
	@Nullable
	ClassLoader getTempClassLoader();

	/**
	 * 是否需要缓存bean metadata,【比如bean difinition 和 解析好的classes】。默认开启缓存
	 * 设置、是否缓存元数据，如果false，那么每次请求实例，都会从类加载器重新加载（热加载）
	 * @param cacheBeanMetadata
	 */
	void setCacheBeanMetadata(boolean cacheBeanMetadata);

	/**
	 * 是否需要缓存bean metadata,比如bean difinition 和 解析好的classes.默认开启缓存
	 *  是否缓存元数据
	 * @return
	 */
	boolean isCacheBeanMetadata();

	/**
	 * 定义用于解析bean definition的表达式解析器
	 * @param resolver
	 */
	void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver);

	/**
	 * 获取bean的表达式解析
	 * @return
	 */
	@Nullable
	BeanExpressionResolver getBeanExpressionResolver();

	// 类型转化器
	/*
	 * 设置、返回一个转换服务
	 */
	void setConversionService(@Nullable ConversionService conversionService);

	// 类型转化器
	@Nullable
	ConversionService getConversionService();

	// 属性编辑器
	/*
	 * 设置属性编辑登记员...
	 */
	void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

	// 属性编辑器
	/*
	 * 注册常用属性编辑器
	 */
	void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

	// 属性编辑器
	/*
	 * 用工厂中注册的通用的编辑器初始化指定的属性编辑注册器
	 */
	void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

	// BeanFactory用来转换bean属性值或者参数值的自定义转换器
	/*
	 * 设置、得到一个类型转换器
	 */
	void setTypeConverter(TypeConverter typeConverter);

	// BeanFactory用来转换bean属性值或者参数值的自定义转换器
	TypeConverter getTypeConverter();

	// string值解析器(想起mvc中的ArgumentResolver了)
	/*
	 * 增加一个嵌入式的StringValueResolver
	 */
	void addEmbeddedValueResolver(StringValueResolver valueResolver);

	/**
	 * Determine whether an embedded value resolver has been registered with this
	 * bean factory, to be applied through {@link #resolveEmbeddedValue(String)}.
	 * @since 4.3
	 */
	boolean hasEmbeddedValueResolver();

	// string值解析器(想起mvc中的ArgumentResolver了)
	//分解指定的嵌入式的值
	@Nullable
	String resolveEmbeddedValue(String value);

	/**
	 * 【返回Bean后处理器的数量】
	 * @param beanPostProcessor
	 */
	void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

	// 返回Bean后处理器的数量
	int getBeanPostProcessorCount();

	/// 作用域定义
	//注册范围
	void registerScope(String scopeName, Scope scope);

	// 作用域定义
	//返回注册的范围名
	String[] getRegisteredScopeNames();

	// 作用域定义
	//返回指定的范围
	@Nullable
	Scope getRegisteredScope(String scopeName);

	// 访问权限控制
	//返回本工厂的一个安全访问上下文
	AccessControlContext getAccessControlContext();

	// 合并其他ConfigurableBeanFactory的配置,包括上面说到的BeanPostProcessor,作用域等
	//从其他的工厂复制相关的所有配置
	void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

	// bean定义处理
	// 注册别名
	/*
	 * 【给指定的Bean注册别名】
	 */
	void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

	// bean定义处理
	//根据指定的StringValueResolver移除所有的别名
	void resolveAliases(StringValueResolver valueResolver);

	// bean定义处理
	// 合并bean定义,包括父容器的
	/*
	 * 返回指定Bean合并后的Bean定义
	 */
	BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	// bean定义处理
	// 是否是FactoryBean类型
	//判断指定Bean是否为一个工厂Bean
	boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

	// bean创建状态控制.在解决循环依赖时有使用
	// 【设置一个Bean是否正在创建】
	void setCurrentlyInCreation(String beanName, boolean inCreation);

	// bean创建状态控制.在解决循环依赖时有使用
	//返回指定Bean是否已经成功创建
	boolean isCurrentlyInCreation(String beanName);

	// 处理bean依赖问题
	//注册一个依赖于指定bean的Bean
	void registerDependentBean(String beanName, String dependentBeanName);

	// 处理bean依赖问题
	//返回依赖于指定Bean的所欲Bean名
	String[] getDependentBeans(String beanName);

	// 处理bean依赖问题
	//返回指定Bean依赖的所有Bean名
	String[] getDependenciesForBean(String beanName);

	// bean生命周期管理-- 销毁bean
	//销毁指定的Bean
	void destroyBean(String beanName, Object beanInstance);

	// bean生命周期管理-- 销毁bean
	//销毁指定的范围Bean
	void destroyScopedBean(String beanName);

	// bean生命周期管理-- 销毁bean
	//销毁所有的单例类
	void destroySingletons();

}
