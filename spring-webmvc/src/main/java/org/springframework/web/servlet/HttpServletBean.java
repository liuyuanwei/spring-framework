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

package org.springframework.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple extension of {@link javax.servlet.http.HttpServlet} which treats
 * its config parameters ({@code init-param} entries within the
 * {@code servlet} tag in {@code web.xml}) as bean properties.
 *
 * <p>A handy superclass for any type of servlet. Type conversion of config
 * parameters is automatic, with the corresponding setter method getting
 * invoked with the converted value. It is also possible for subclasses to
 * specify required properties. Parameters without matching bean property
 * setter will simply be ignored.
 *
 * <p>This servlet leaves request handling to subclasses, inheriting the default
 * behavior of HttpServlet ({@code doGet}, {@code doPost}, etc).
 *
 * <p>This generic servlet base class has no dependency on the Spring
 * {@link org.springframework.context.ApplicationContext} concept. Simple
 * servlets usually don't load their own context but rather access service
 * beans from the Spring root application context, accessible via the
 * filter's {@link #getServletContext() ServletContext} (see
 * {@link org.springframework.web.context.support.WebApplicationContextUtils}).
 *
 * <p>The {@link FrameworkServlet} class is a more specific servlet base
 * class which loads its own application context. FrameworkServlet serves
 * as direct base class of Spring's full-fledged {@link DispatcherServlet}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #addRequiredProperty
 * @see #initServletBean
 * @see #doGet
 * @see #doPost
 *  负责将 ServletConfig的参数数据（初始化参数（如<init-param>）在web.xml配置的） 设置到【当前】 Servlet 对象中
 *
 *  实现 EnvironmentCapable、EnvironmentAware 接口，继承 HttpServlet 抽象类，
 *  负责将 ServletConfig 集成到 Spring 中。当然，HttpServletBean 自身也是一个抽象类。
 */
@SuppressWarnings("serial")
public abstract class HttpServletBean extends HttpServlet implements EnvironmentCapable, EnvironmentAware {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private ConfigurableEnvironment environment;

    /**
     * 必须配置的属性的集合
     *
     * 在 {@link ServletConfigPropertyValues} 中，会校验是否有对应的属性
	 * 可通过 {@link HttpServletBean#addRequiredProperty(java.lang.String)} 方法，添加到其中
     */
	private final Set<String> requiredProperties = new HashSet<>(4);

	/**
	 * @param property name of the required property
	 *  requiredProperties 属性，必须配置的属性的集合。可通过 #addRequiredProperty(String property) 方法，
	 */
	protected final void addRequiredProperty(String property) {
		this.requiredProperties.add(property);
	}

	/**
	 * Set the {@code Environment} that this servlet runs in.
	 * <p>Any environment set here overrides the {@link StandardServletEnvironment}
	 * provided by default.
	 * @throws IllegalArgumentException if environment is not assignable to
	 * {@code ConfigurableEnvironment}
	 */
	@Override // 实现自 EnvironmentAware 接口，【会自动注入】
	public void setEnvironment(Environment environment) {
		Assert.isInstanceOf(ConfigurableEnvironment.class, environment, "ConfigurableEnvironment required");
		this.environment = (ConfigurableEnvironment) environment;
	}

	/**
	 * Return the {@link Environment} associated with this servlet.
	 * <p>If none specified, a default environment will be initialized via
	 * {@link #createEnvironment()}.
	 */
	@Override // 实现自 EnvironmentCapable 接口
	public ConfigurableEnvironment getEnvironment() {
	    // 如果 environment 为空，主动创建
		if (this.environment == null) {
			this.environment = createEnvironment();
		}
		return this.environment;
	}

	/**
	 * Create and return a new {@link StandardServletEnvironment}.
	 * <p>Subclasses may override this in order to configure the environment or
	 * specialize the environment type returned.
	 */
	protected ConfigurableEnvironment createEnvironment() {
		return new StandardServletEnvironment();
	}

	/**
	 * 负责将 ServletConfig初始化参数（如<init-param>） 设置到当前 Servlet 对象中。
	 * 】】】servlet中的方法，tomcat服务器创建servlet对象的时候,会帮我们去调用init(ServletConfig config)进行servlet类中的初始化工作
	 */
	@Override
	public final void init() throws ServletException {
		/*
			<init-param>
				<param-name>contextConfigLocation</param-name>
				<param-value>/WEB-INF/spring-servlet.xml</param-value> // 默认
			</init-param>
		 */
        // 1 解析 <init-param /> 标签，封装到 PropertyValues pvs 中
		// 其中，ServletConfigPropertyValues 是 HttpServletBean 的私有静态类，继承 MutablePropertyValues 类，
		// 【ServletConfig 的 PropertyValues】 封装实现类。
		/*
		 	代码简单，实现两方面的逻辑：
		 		<1> 处，遍历 ServletConfig 的初始化参数集合，添加到 ServletConfigPropertyValues 中；
		 		<2> 处，判断要求的属性是否齐全。如果不齐全，则抛出 ServletException 异常
		 */
		PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
		if (!pvs.isEmpty()) {
			try {
			    // <2.1> 将当前的这个 Servlet 对象，转化成一个 BeanWrapper 对象。
				// 从而能够以 Spring 的方式来将 pvs 注入到该 BeanWrapper 对象中
				/*
					【简单来说，BeanWrapper 是 Spring 提供的一个用来操作 Java Bean 属性的工具，使用它可以直接修改一个对象的属性。】
				 */
				BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
				ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
				// <2.2> 注册自定义属性编辑器，一旦碰到 Resource 类型的属性，将会使用 ResourceEditor 进行解析
				bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, getEnvironment()));
                // <2.3>空实现，留给子类覆盖
				// 【然而实际上，子类暂时木有任何实现】。
				initBeanWrapper(bw);


				// <2.4> 以 Spring 的方式来将 pvs 注入到该 BeanWrapper 对象中——即设置到当前 Servlet 对象中。
				bw.setPropertyValues(pvs, true);
				/*
					<2.4> 我们还是举个例子。假设如下：
					<servlet>
						<servlet-name>spring</servlet-name>
						<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
						<init-param>
							<param-name>contextConfigLocation</param-name>
							<param-value>/WEB-INF/spring-servlet.xml</param-value>
						</init-param>
						<load-on-startup>1</load-on-startup>
					</servlet>
					<servlet-mapping>
						<servlet-name>spring</servlet-name>
						<url-pattern>*.do</url-pattern>
					</servlet-mapping>
					此处有配置了 contextConfigLocation 属性，那么通过 <2.4> 处的逻辑，会反射设置到 FrameworkServlet.contextConfigLocation 属性。代码如下：
					@Nullable
					private String contextConfigLocation;

					public void setContextConfigLocation(@Nullable String contextConfigLocation) {
						this.contextConfigLocation = contextConfigLocation;
					}
				 */
			} catch (BeansException ex) {
				if (logger.isErrorEnabled()) {
					logger.error("Failed to set bean properties on servlet '" + getServletName() + "'", ex);
				}
				throw ex;
			}
		}

		// Let subclasses do whatever initialization they like.
        // 子类来实现，实现自定义的初始化逻辑。
		// 】】】目前，有具体的代码实现。
		initServletBean();
	}

	/**
	 * Initialize the BeanWrapper for this HttpServletBean,
	 * possibly with custom editors.
	 * <p>This default implementation is empty.
	 * @param bw the BeanWrapper to initialize
	 * @throws BeansException if thrown by BeanWrapper methods
	 * @see org.springframework.beans.BeanWrapper#registerCustomEditor
	 */
	protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
	}

	/**
	 * Subclasses may override this to perform custom initialization.
	 * All bean properties of this servlet will have been set before this
	 * method is invoked.
	 * <p>This default implementation is empty.
	 * @throws ServletException if subclass initialization fails
	 */
	protected void initServletBean() throws ServletException {
	}

	/**
	 * Overridden method that simply returns {@code null} when no
	 * ServletConfig set yet.
	 * @see #getServletConfig()
	 */
	@Override
	@Nullable
	public String getServletName() {
		return (getServletConfig() != null ? getServletConfig().getServletName() : null);
	}


	/**
     * ServletConfig 的 PropertyValues 封装实现类
     *
	 * PropertyValues implementation created from ServletConfig init parameters.
	 */
	private static class ServletConfigPropertyValues extends MutablePropertyValues {

		/**
		 * Create new ServletConfigPropertyValues.
		 * @param config the ServletConfig we'll use to take PropertyValues from
		 * @param requiredProperties set of property names we need, where
		 * we can't accept default values
		 * @throws ServletException if any required properties are missing
		 */
		/**
		 * 代码简单，实现两方面的逻辑：
		 * 	<1> 处，遍历 ServletConfig 的初始化参数集合，添加到 ServletConfigPropertyValues 中；
		 * <2> 处，判断要求的属性是否齐全。如果不齐全，则抛出 ServletException 异常。
		 * @param config
		 * @param requiredProperties
		 * @throws ServletException
		 */
		public ServletConfigPropertyValues(ServletConfig config, Set<String> requiredProperties)
				throws ServletException {
		    // 获得缺失的属性的集合
			Set<String> missingProps = (!CollectionUtils.isEmpty(requiredProperties) ?
					new HashSet<>(requiredProperties) : null);

			// <1> 遍历 ServletConfig 的初始化参数集合，添加到 ServletConfigPropertyValues 中，并从 missingProps 移除
			Enumeration<String> paramNames = config.getInitParameterNames();
			while (paramNames.hasMoreElements()) {
				String property = paramNames.nextElement();
				Object value = config.getInitParameter(property);
				// 添加到 ServletConfigPropertyValues 中
				addPropertyValue(new PropertyValue(property, value));
				// 从 missingProps 中移除
				if (missingProps != null) {
					missingProps.remove(property);
				}
			}

			// Fail if we are still missing properties.
            // <2> 如果存在缺失的属性，抛出 ServletException 异常
			if (!CollectionUtils.isEmpty(missingProps)) {
				throw new ServletException(
						"Initialization from ServletConfig for servlet '" + config.getServletName() +
						"' failed; the following required properties were missing: " +
						StringUtils.collectionToDelimitedString(missingProps, ", "));
			}
		}

	}

}