package org.graylog2.plugin.api;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

/**
 * Implement the PluginMetaData interface here.
 */
public class ApiInputMetaData implements PluginMetaData {
    @Override
    public String getUniqueId() {
        return "org.graylog2.plugin.api.ApiInputPlugin";
    }

    @Override
    public String getName() {
        return "ApiInput";
    }

    @Override
    public String getAuthor() {
        return "Romulo Salazar";
    }

    @Override
    public URI getURL() {
        return URI.create("https://www.graylog.org/");
    }

    @Override
    public Version getVersion() {
        return new Version(1, 0, 0);
    }

    @Override
    public String getDescription() {
        return "Open Table API Input Plugin";
    }

    @Override
    public Version getRequiredVersion() {
        return new Version(2, 0, 0);
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}
