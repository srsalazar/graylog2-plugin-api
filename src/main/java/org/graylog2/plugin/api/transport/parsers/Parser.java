package org.graylog2.plugin.api.transport.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.http.HttpResponse;
    import com.mashape.unirest.http.JsonNode;
import org.graylog2.plugin.api.transport.configuration.ApiConfig;
import org.graylog2.plugin.inputs.MessageInput;
import org.graylog2.plugin.inputs.MisfireException;
import org.graylog2.plugin.journal.RawMessage;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

public abstract class Parser {
  private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class.getName());
  protected HttpResponse<JsonNode> response;
  protected Map<String, Object> baseEventData;

  public Parser(HttpRequest request, ApiConfig config, String host) throws UnirestException{
      this.baseEventData = Maps.newHashMap();
      makeAPIRequest(request,config);
      setBaseEventData(config, host);
  }

  private void makeAPIRequest(HttpRequest request, ApiConfig config) throws UnirestException{

      long startTime, endTime, runTime;

      startTime = System.currentTimeMillis();
      try {
          this.response = request.asJson();
          endTime = System.currentTimeMillis();

      } catch (UnirestException e) {
          LOGGER.debug("Processing error: " + e.toString());
          endTime = System.currentTimeMillis();
          throw e;
      }
      runTime = endTime - startTime;
      LOGGER.debug(this.response.toString());
      this.baseEventData.put("_http_monitor_responseTime", runTime);
  }

  private void setBaseEventData(ApiConfig config, String host){
    // Set base data
    this.baseEventData.put("version", "1.0");
    this.baseEventData.put("_api_url", config.getUrl());
    this.baseEventData.put("_label", config.getLabel());

    if (this.response != null) {
        this.baseEventData.put("host", config.getHost());
        this.baseEventData.put("_api_status", this.response.getStatus());
        this.baseEventData.put("_api_statusLine", this.response.getStatusText());
      if (config.getResponseHeadersToRecord() != null) {
        for (String header : config.getResponseHeadersToRecord()) {
            this.baseEventData.put("_" + header, this.response.getHeaders().get(header));
        }
      }
    } else {
      LOGGER.debug("Failed to retrieve service data: " + config.getUrl());
    }
  }

  protected void publishToGraylog(Map<String, Object> eventData, MessageInput messageInput){
    try{
        ObjectMapper mapper = new ObjectMapper();
        //publish to graylog server
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        mapper.writeValue(byteStream, eventData);
        messageInput.processRawMessage(new RawMessage(byteStream.toByteArray()));
        byteStream.close();
    } catch (IOException e) {
      LOGGER.error("Exception while executing request for URL", e);
    }
  }

    protected JsonNode getResponseBody(){
        if (this.response != null) {
            return this.response.getBody();
        } else {
            return null;
        }
    }

  public abstract void parse(MessageInput messageInput);
}
