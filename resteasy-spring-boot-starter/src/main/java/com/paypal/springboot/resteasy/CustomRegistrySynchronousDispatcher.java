package com.paypal.springboot.resteasy;

import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

/**
 * This class will be removed as soon as RESTEasy 3.0.17.Final is released.
 * See:
 * https://github.com/resteasy/Resteasy/pull/744
 * https://issues.jboss.org/browse/RESTEASY-1305
 *
 * ------------------------------------------------ 
 *
 * Ideally, instead the need of this custom class, the original class
 * {@link SynchronousDispatcher} should be modified to allow, not just stand-alone,
 * but also embedded Servlet container scenarios.<br>
 * <br>
 * This custom class is a temporary solution until we fork the Resteasy project
 * where {@link SynchronousDispatcher} is, modify such class, and sends a pull
 * request with the necessary change.<br>
 * <br>
 * This class extends the original {@link SynchronousDispatcher} overwriting only
 * the method it cares about, which is the one that need changes.<br>
 * <br>
 * As soon as the pull request is sent, and Resteasy incorporates it, then this
 * class can be removed
 * 
 * @author Fabio Carvalho (facarvalho@paypal.com or fabiocarvalho777@gmail.com)
 */
public class CustomRegistrySynchronousDispatcher extends SynchronousDispatcher {

	public CustomRegistrySynchronousDispatcher(ResteasyProviderFactory providerFactory, ResourceMethodRegistry registry) {
		super(providerFactory);
		this.registry = registry;
		defaultContextObjects.put(Registry.class, registry);
	}

}
