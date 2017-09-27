package org.graylog2.plugin.api.transport.parsers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.graylog2.plugin.api.transport.configuration.ApiConfig;
import org.graylog2.plugin.inputs.MessageInput;
import org.graylog2.plugin.journal.RawMessage;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public abstract class Parser {
  private static final Logger LOGGER = LoggerFactory.getLogger(Parser.class.getName());
  protected Response response;
  protected Map<String, Object> baseEventData;

  public Parser(AsyncHttpClient.BoundRequestBuilder requestBuilder, ApiConfig config){
      this.baseEventData = Maps.newHashMap();
      makeAPIRequest(requestBuilder,config);
      setBaseEventData(config);
  }

  private void makeAPIRequest(AsyncHttpClient.BoundRequestBuilder requestBuilder, ApiConfig config){

      long startTime, endTime, runTime;

      startTime = System.currentTimeMillis();
      try {

          this.response = requestBuilder.execute().get();
          endTime = System.currentTimeMillis();

      } catch (InterruptedException | ExecutionException e) {
          endTime = System.currentTimeMillis();
          this.response = null;
      }
      runTime = endTime - startTime;
      this.baseEventData.put("_http_monitor_responseTime", runTime);
  }

  private void setBaseEventData(ApiConfig config){

    // Set base data
    this.baseEventData.put("version", "1.0");
    this.baseEventData.put("_api_url", config.getUrl());
    this.baseEventData.put("_label", config.getLabel());

    if (this.response != null) {
        this.baseEventData.put("host", this.response.getUri().getHost());
        this.baseEventData.put("_api_status", this.response.getStatusCode());
        this.baseEventData.put("_api_statusLine", this.response.getStatusText());
      if (config.getResponseHeadersToRecord() != null) {
        for (String header : config.getResponseHeadersToRecord()) {
            this.baseEventData.put("_" + header, this.response.getHeader(header));
        }
      }
    } else {
      LOGGER.debug("Failed to retrieve service data " + config.getUrl());
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

    protected JSONArray getResponseBody(){
        if (this.response != null) {
            try {
                return new JSONArray(new String(this.response.getResponseBodyAsBytes()));
            } catch (IOException e) {

                LOGGER.debug("Failed to extract response body" + e);
                return null;
            }
        } else {
            return null;
        }
    }

  public abstract void parse(MessageInput messageInput);
}
