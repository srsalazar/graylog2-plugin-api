package org.graylog2.plugin.api.transport.services.bitium;

import com.ning.http.client.*;
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

  // Customize request builder for Bitium
  public AsyncHttpClient.BoundRequestBuilder customizeBuildRequest(ApiConfig config) {

      //Adding time filter
      DateTime dt = new DateTime();
      long executionIntervalMs = TimeUnit.MILLISECONDS.convert(config.getExecutionInterval(), config.getIntervalUnit());
      dt = dt.minus(executionIntervalMs);
      this.requestBuilder.addQueryParam("filters[time_from]",
              dt.withZoneRetainFields(DateTimeZone.forID("America/Los_Angeles")).toString());
      return this.requestBuilder;
  }
}
