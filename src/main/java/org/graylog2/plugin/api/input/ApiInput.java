package org.graylog2.plugin.api.input;

import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import org.graylog2.plugin.LocalMetricRegistry;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.inputs.MessageInput;
import org.graylog2.inputs.codecs.GelfCodec;

import javax.inject.Inject;

import org.graylog2.plugin.api.transport.ApiTransport;
/**
 * This is the plugin. Your class should implement one of the existing plugin
 * interfaces. (i.e. AlarmCallback, MessageInput, MessageOutput)
 */
public class ApiInput extends MessageInput {

    private static final String NAME = "API Input";

    @AssistedInject
    public ApiInput(MetricRegistry metricRegistry, @Assisted Configuration configuration,
                            ApiTransport.Factory factory, LocalMetricRegistry localRegistry,
                            GelfCodec.Factory codecFactory,
                            Config config, Descriptor descriptor, ServerStatus serverStatus) {
        super(metricRegistry, configuration, factory.create(configuration),
                localRegistry, codecFactory.create(configuration), config, descriptor, serverStatus);
    }

    public interface Factory extends MessageInput.Factory<ApiInput> {
        @Override
        ApiInput create(Configuration configuration);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Descriptor extends MessageInput.Descriptor {
        @Inject
        public Descriptor() {
            super(NAME, false, "");
        }
    }

    public static class Config extends MessageInput.Config {
        @Inject
        public Config(ApiTransport.Factory transport, GelfCodec.Factory codec) {
            super(transport.getConfig(), codec.getConfig());
        }
    }
}
