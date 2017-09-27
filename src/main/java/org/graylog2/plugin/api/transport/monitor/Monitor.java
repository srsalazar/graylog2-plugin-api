package org.graylog2.plugin.api.transport.monitor;

import java.net.MalformedURLException;
import com.ning.http.client.*;

import org.graylog2.plugin.api.transport.parsers.bitium.BitiumParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Graylog Plugins API
import org.graylog2.plugin.inputs.MessageInput;

// API Plugin
import org.graylog2.plugin.api.transport.services.bitium.BitiumApi;
import org.graylog2.plugin.api.transport.services.Service;
import org.graylog2.plugin.api.transport.parsers.Parser;
import org.graylog2.plugin.api.transport.configuration.ApiConfig;

public class Monitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class.getName());


  public static class MonitorTask implements Runnable {
      private ApiConfig config;
      private MessageInput messageInput;

      private Service service;


      public MonitorTask(ApiConfig config, MessageInput messageInput){
          this.config = config;
          this.messageInput = messageInput;
      }


      @Override
      public void run() {

          Service service;
          Parser parser;
          //TODO: Add switch for API type
          try {
              service = new BitiumApi(config);
              AsyncHttpClient.BoundRequestBuilder requestBuilder = service.customizeBuildRequest(config);
              parser = new BitiumParser(requestBuilder, this.config);
              parser.parse(this.messageInput);
          } catch (MalformedURLException e){
              LOGGER.info("Error generating input" + e);
          }
      }
  }
}