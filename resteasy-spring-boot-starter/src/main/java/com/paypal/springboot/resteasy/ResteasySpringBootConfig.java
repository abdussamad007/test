package com.paypal.springboot.resteasy;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.jboss.resteasy.plugins.spring.SpringBeanProcessor;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This is the main class that configures this Resteasy Sring Boot starter
 * 
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 * 
 */
@Configuration
@Import({ResteasyApplicationBuilder.class, ResteasyEmbeddedServletInitializer.class})
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
@EnableConfigurationProperties
public class ResteasySpringBootConfig {

	private static Logger logger = LoggerFactory.getLogger(ResteasySpringBootConfig.class);

	private static ResteasyProviderFactory resteasyProviderFactory = new ResteasyProviderFactory();
	private static ResourceMethodRegistry resourceMethodRegistry = new ResourceMethodRegistry(resteasyProviderFactory);

	@Bean
	public static BeanFactoryPostProcessor springBeanProcessor() {
		SpringBeanProcessor springBeanProcessor;

		springBeanProcessor = new SpringBeanProcessor();

		springBeanProcessor.setProviderFactory(resteasyProviderFactory);
		springBeanProcessor.setRegistry(resourceMethodRegistry);

		logger.debug("SpringBeanProcessor has been created");

		return springBeanProcessor;
	}

	/**
	 * This is a modified version of {@link ResteasyBootstrap}
	 * 
	 * @return a ServletContextListener object that configures and start a ResteasyDeployment
	 */
	@Bean
	public ServletContextListener resteasyBootstrapListener() {
		ServletContextListener servletContextListener = new ServletContextListener() {

			protected CustomDispatcherResteasyDeployment deployment;

			public void contextInitialized(ServletContextEvent sce) {
				ServletContext servletContext = sce.getServletContext();

				ListenerBootstrap config = new CustomListenerBootstrap(servletContext);

				deployment = (CustomDispatcherResteasyDeployment) config.createDeployment();

				deployment.setProviderFactory(resteasyProviderFactory);
				deployment.setRegistry(resourceMethodRegistry);

				SynchronousDispatcher dispatcher = new CustomRegistrySynchronousDispatcher(resteasyProviderFactory, resourceMethodRegistry);
				dispatcher.getUnwrappedExceptions().addAll(deployment.getUnwrappedExceptions());
				deployment.setDispatcher(dispatcher);

				deployment.start();

				servletContext.setAttribute(ResteasyProviderFactory.class.getName(), deployment.getProviderFactory());
				servletContext.setAttribute(Dispatcher.class.getName(), deployment.getDispatcher());
				servletContext.setAttribute(Registry.class.getName(), deployment.getRegistry());
			}

			public void contextDestroyed(ServletContextEvent sce) {
				if (deployment != null) {
					deployment.stop();
				}
			}
		};

		logger.debug("ServletContextListener has been created");

		return servletContextListener;
	}
	
}
