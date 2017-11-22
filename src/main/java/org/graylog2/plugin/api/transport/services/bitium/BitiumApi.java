package org.graylog2.plugin.api.transport.services.bitium;

import com.mashape.unirest.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.*;

// API Plugin
import org.graylog2.plugin.api.transport.services.Service;
import org.graylog2.plugin.api.transport.configuration.ApiConfig;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class BitiumApi extends Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(BitiumApi.class.getName());

    public BitiumApi(ApiConfig config) throws MalformedURLException {
        super(config);
    }

    @Override
    public HttpRequest customizeHttpRequest(ApiConfig config) {

        //Authorization
        DateTime dt = new DateTime();
        long executionIntervalMs = TimeUnit.MILLISECONDS.convert(config.getExecutionInterval(), config.getIntervalUnit());
        LOGGER.debug("Setting execution interval:" + executionIntervalMs);
        dt = dt.minus(executionIntervalMs);
        this.request.queryString("filters[time_from]",
                dt.withZone(DateTimeZone.forID("America/Los_Angeles")).toString());
        return this.request;
    }

}
