package org.graylog2.plugin.api;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Arrays;
import java.util.Collection;

/**
 * Implement the Plugin interface here.
 */
public class ApiInputPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new ApiInputMetaData();
    }

    @Override
    public Collection<PluginModule> modules () {
        return Arrays.<PluginModule>asList(new ApiInputModule());
    }
}
