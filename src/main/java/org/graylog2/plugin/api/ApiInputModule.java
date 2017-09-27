package org.graylog2.plugin.api;

import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;

import java.util.Collections;
import java.util.Set;

import org.graylog2.plugin.api.input.ApiInput;
import org.graylog2.plugin.api.transport.ApiTransport;

/**
 * Extend the PluginModule abstract class here to add you plugin to the system.
 */
public class ApiInputModule extends PluginModule {
    /**
     * Returns all configuration beans required by this plugin.
     *
     * Implementing this method is optional. The default method returns an empty {@link Set}.
     */
//    @Override
//    public Set<? extends PluginConfigBean> getConfigBeans() {
//        return Collections.emptySet();
//    }

    @Override
    protected void configure() {
        installTransport(transportMapBinder(),"api-transport",ApiTransport.class);
        installInput(inputsMapBinder(), ApiInput.class, ApiInput.Factory.class);
    }
}
