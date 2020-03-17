/*
 * Copyright 2002-2012 the original author or authors.
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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.ObjectUtils;

/**
 * Abstract implementation of the {@link org.springframework.web.servlet.HandlerMapping}
 * interface, detecting URL mappings for handler beans through introspection of all
 * defined beans in the application context.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see #determineUrlsForHandler
 * 继承 AbstractUrlHandlerMapping 抽象类，自动探测的 UrlHandlerMapping 抽象实现类。
 * 重写了initApplicationContext方法
 */
public abstract class AbstractDetectingUrlHandlerMapping extends AbstractUrlHandlerMapping {

    /**
     * 是否只扫描可访问的 Handler 们
     */
	private boolean detectHandlersInAncestorContexts = false;


	public void setDetectHandlersInAncestorContexts(boolean detectHandlersInAncestorContexts) {
		this.detectHandlersInAncestorContexts = detectHandlersInAncestorContexts;
	}

	/**
	 * Calls the {@link #detectHandlers()} method in addition to the
	 * superclass's initialization.
	 */
	@Override
	public void initApplicationContext() throws ApplicationContextException {
        // 调用父类方法，进行初始化
		super.initApplicationContext();
		// 自动探测处理器
		detectHandlers();
	}

	/**
	 * Register all handlers found in the current ApplicationContext.
	 * <p>The actual URL determination for a handler is up to the concrete
	 * {@link #determineUrlsForHandler(String)} implementation. A bean for
	 * which no such URLs could be determined is simply not considered a handler.
	 * @throws org.springframework.beans.BeansException if the handler couldn't be registered
	 * @see #determineUrlsForHandler(String)
	 */
	protected void detectHandlers() throws BeansException {
	    // <1> 获得 Bean 的名字的数组
		ApplicationContext applicationContext = obtainApplicationContext();
		String[] beanNames = (this.detectHandlersInAncestorContexts ?
				BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, Object.class) :
				applicationContext.getBeanNamesForType(Object.class));

		// Take any bean name that we can determine URLs for.
        // <2> 遍历 Bean ，逐个注册
		for (String beanName : beanNames) {

			/*
				determineUrlsForHandler抽象方法，获得 Bean 对应的 URL 们。
				这是 AbstractDetectingUrlHandlerMapping 的关键方法。但是，AbstractDetectingUrlHandlerMapping 只是搭建了自动探测的骨架。
				具体的探索逻辑，还是交给子类处理。
			 */
		    // <2.1> 获得 Bean 对应的 URL 们
			String[] urls = determineUrlsForHandler(beanName);
			// <2.2> 如果 URL 们非空，则执行注册处理器
			if (!ObjectUtils.isEmpty(urls)) {
				/*
					如果 URL 们非空，则调用父类 AbstractUrlHandlerMapping 的 #registerHandler(String[] urlPaths, String beanName) 方法，执行注册处理器。
				 */
				registerHandler(urls, beanName);
			}
		}

		if ((logger.isDebugEnabled() && !getHandlerMap().isEmpty()) || logger.isTraceEnabled()) {
			logger.debug("Detected " + getHandlerMap().size() + " mappings in " + formatMappingName());
		}
	}

	/**
	 * Determine the URLs for the given handler bean.
	 * @param beanName the name of the candidate bean
	 * @return the URLs determined for the bean, or an empty array if none
	 */
	protected abstract String[] determineUrlsForHandler(String beanName);

}
