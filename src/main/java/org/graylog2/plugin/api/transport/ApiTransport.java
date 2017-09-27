package org.graylog2.plugin.api.transport;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import java.util.concurrent.*;


//Graylog Plugin API
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.api.transport.configuration.ApiConfig;
import org.graylog2.plugin.api.transport.configuration.UIConfig;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.inputs.MessageInput;
import org.graylog2.plugin.inputs.MisfireException;
import org.graylog2.plugin.inputs.annotations.ConfigClass;
import org.graylog2.plugin.inputs.annotations.FactoryClass;
import org.graylog2.plugin.inputs.codecs.CodecAggregator;
import org.graylog2.plugin.inputs.transports.Transport;

//Plugin
import org.graylog2.plugin.api.transport.monitor.Monitor.*;

/**
 * Created on 17/6/15.
 */
public class ApiTransport implements Transport {
    private final Configuration configuration;
    private final MetricRegistry metricRegistry;
    private ServerStatus serverStatus;
    private ScheduledExecutorService executorService;
    private ScheduledFuture future;
    private MessageInput messageInput;

    @AssistedInject
    public ApiTransport(@Assisted Configuration configuration,
                                MetricRegistry metricRegistry,
                                ServerStatus serverStatus) {
        this.configuration = configuration;
        this.metricRegistry = metricRegistry;
        this.serverStatus = serverStatus;
    }


    @Override
    public void setMessageAggregator(CodecAggregator codecAggregator) {}

    @Override
    public void launch(MessageInput messageInput) throws MisfireException {
        this.messageInput = messageInput;
        ApiConfig apiConfig = new ApiConfig(this.configuration);
        executorService = Executors.newSingleThreadScheduledExecutor();
        long initalDelayMs = TimeUnit.MILLISECONDS.convert(Math.round(Math.random() * 60), TimeUnit.SECONDS);
        long executionIntervalMs = TimeUnit.MILLISECONDS.convert(apiConfig.getExecutionInterval(), apiConfig.getIntervalUnit());
        future = executorService.scheduleAtFixedRate(new MonitorTask(apiConfig, messageInput), initalDelayMs,
                executionIntervalMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {

        if (future != null) {
            future.cancel(true);
        }

        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    @Override
    public MetricSet getMetricSet() {
        return null;
    }

    @FactoryClass
    public interface Factory extends Transport.Factory<ApiTransport> {

        @Override
        ApiTransport create(Configuration configuration);

        @Override
        Config getConfig();

    }

    @ConfigClass
    public static class Config implements Transport.Config {
        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            return UIConfig.getUIConfig();
        }
    }
}
