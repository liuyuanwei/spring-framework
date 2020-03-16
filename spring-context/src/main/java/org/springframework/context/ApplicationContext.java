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

package org.springframework.context;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

/**
 * Central interface to provide configuration for an application.
 * This is read-only while the application is running, but may be
 * reloaded if the implementation supports this.
 *
 * <p>An ApplicationContext provides:
 * <ul>
 * <li>Bean factory methods for accessing application components.
 * Inherited from {@link org.springframework.beans.factory.ListableBeanFactory}.
 * <li>The ability to load file resources in a generic fashion.
 * Inherited from the {@link org.springframework.core.io.ResourceLoader} interface.
 * <li>The ability to publish events to registered listeners.
 * Inherited from the {@link ApplicationEventPublisher} interface.
 * <li>The ability to resolve messages, supporting internationalization.
 * Inherited from the {@link MessageSource} interface.
 * <li>Inheritance from a parent context. Definitions in a descendant context
 * will always take priority. This means, for example, that a single parent
 * context can be used by an entire web application, while each servlet has
 * its own child context that is independent of that of any other servlet.
 * </ul>
 *
 * <p>In addition to standard {@link org.springframework.beans.factory.BeanFactory}
 * lifecycle capabilities, ApplicationContext implementations detect and invoke
 * {@link ApplicationContextAware} beans as well as {@link ResourceLoaderAware},
 * {@link ApplicationEventPublisherAware} and {@link MessageSourceAware} beans.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see ConfigurableApplicationContext
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.core.io.ResourceLoader
 * 这个就是大名鼎鼎的 Spring 容器，它叫做应用上下文，与我们应用息息相关。
 * 【它继承 BeanFactory ，所以它是 BeanFactory 的扩展升级版】，如果BeanFactory 是屌丝的话，那么 ApplicationContext 则是名副其实的高富帅。
 *
 * 由于 ApplicationContext 的结构就决定了它与 BeanFactory 的不同，其主要区别有：
 * 		继承 org.springframework.context.MessageSource 接口，提供国际化的标准访问策略。
 * 		继承 org.springframework.context.ApplicationEventPublisher 接口，提供强大的事件机制。
 * 		扩展 ResourceLoader ，可以用来加载多种 Resource ，可以灵活访问不同的资源。
 * 		对 Web 应用的支持。
 */
/*
	BeanFactory：Spring 管理 Bean 的顶层接口，我们可以认为他是一个简易版的 Spring 容器。ApplicationContext 继承 BeanFactory 的两个子类：HierarchicalBeanFactory 和 ListableBeanFactory。HierarchicalBeanFactory 是一个具有层级关系的 BeanFactory，拥有属性 parentBeanFactory 。ListableBeanFactory 实现了枚举方法可以列举出当前 BeanFactory 中所有的 bean 对象而不必根据 name 一个一个的获取。
	ApplicationEventPublisher：用于封装事件发布功能的接口，向事件监听器（Listener）发送事件消息。
	ResourceLoader：Spring 加载资源的顶层接口，用于从一个源加载资源文件。ApplicationContext 继承 ResourceLoader 的子类 ResourcePatternResolver，该接口是将 location 解析为 Resource 对象的策略接口。
	MessageSource：解析 message 的策略接口，用不支撑国际化等功能。
	EnvironmentCapable：用于获取 Environment 的接口。
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
		MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

	// 获取id
	@Nullable
	String getId();

	//获取应用名
	String getApplicationName();

	//获取应用显示名称
	String getDisplayName();

	//获取应用启动时间
	long getStartupDate();

	//获取父级应用上下文
	@Nullable
	ApplicationContext getParent();

	//获取bean工厂(DefaultListableBeanFactory)
	AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

}
