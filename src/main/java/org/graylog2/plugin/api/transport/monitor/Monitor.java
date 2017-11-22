package org.graylog2.plugin.api.transport.monitor;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;

import org.graylog2.plugin.api.transport.parsers.bitium.BitiumParser;
import org.graylog2.plugin.api.transport.parsers.duo.DuoParser;
import org.graylog2.plugin.api.transport.services.duo.DuoApi;
import org.graylog2.plugin.inputs.MisfireException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Graylog Plugins API
import org.graylog2.plugin.inputs.MessageInput;

// API Plugin
import static org.graylog2.plugin.api.transport.StaticVars.*;
import org.graylog2.plugin.api.transport.services.bitium.BitiumApi;
import org.graylog2.plugin.api.transport.services.Service;
import org.graylog2.plugin.api.transport.parsers.Parser;
import org.graylog2.plugin.api.transport.configuration.ApiConfig;

public class Monitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class.getName());


  public static class MonitorTask implements Runnable {
      private ApiConfig config;
      private MessageInput messageInput;


      public MonitorTask(ApiConfig config, MessageInput messageInput){
          this.config = config;
          this.messageInput = messageInput;
      }

      @Override
      public void run() {
          Service service;
          Parser parser;
          switch (this.config.getApiType()) {
              case BITIUM:
                  try {
                      service = new BitiumApi(this.config);
                      HttpRequest request = service.customizeHttpRequest(this.config);
                      parser = new BitiumParser(request, this.config);
                      parser.parse(this.messageInput);
                  } catch (MalformedURLException | UnirestException | UnsupportedEncodingException e) {
                      LOGGER.info("Error generating input" + e);
                  }
                  break;

              case DUO:
                  try {
                      service = new DuoApi(this.config);
                      HttpRequest request = service.customizeHttpRequest(this.config);
                      parser = new DuoParser(request, this.config);
                      parser.parse(this.messageInput);
                  } catch (MalformedURLException | UnirestException | UnsupportedEncodingException e) {
                      LOGGER.info("Error generating input" + e);
                  }
                  break;
          }
      }
  }
}
