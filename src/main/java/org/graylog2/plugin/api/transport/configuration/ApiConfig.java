package org.graylog2.plugin.api.transport.configuration;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;

import org.graylog2.plugin.configuration.Configuration;

import static org.graylog2.plugin.api.transport.StaticVars.*;

/**
 * Created on 17/6/15.
 */
public class ApiConfig {
    private String url,label,type,method,requestBody;
    private ArrayList<String> requestHeaders;
    private ArrayList<String> authorizationHeaders;
    private ArrayList<String> requestParams;
    private int timeout, executionInterval;
    private String[] responseHeadersToRecord;
    private boolean logResponseBody;
    private TimeUnit intervalUnit,timeoutUnit;

    public ApiConfig(Configuration configuration){
      this.setUrl(configuration.getString(CK_CONFIG_URL));
      this.setLabel(configuration.getString(CK_CONFIG_LABEL));
      this.setMethod(configuration.getString(CK_CONFIG_METHOD));

      String proxyUri = configuration.getString(CK_CONFIG_HTTP_PROXY);
      if (proxyUri != null && !proxyUri.isEmpty()) {
          this.setHttpProxyUri(URI.create(proxyUri));
      }

      String authHeaders = configuration.getString(CK_CONFIG_AUTHORIZATION_HEADERS);
      if (StringUtils.isNotEmpty(authHeaders)) {
          this.setAuthorizationHeaders(
                  new ArrayList(Arrays.asList(authHeaders.split(","))));
      }

      String requestHeaders = configuration.getString(CK_CONFIG_HEADERS_TO_SEND);
      if (StringUtils.isNotEmpty(requestHeaders)) {
          this.setRequestHeaders(
                  new ArrayList(Arrays.asList(requestHeaders.split(","))));
      }
      String requestParams = configuration.getString(CK_CONFIG_REQUEST_PARAMS);
      if (StringUtils.isNotEmpty(requestParams)) {
          this.setRequestParams(
                  new ArrayList(Arrays.asList(requestParams.split(","))));
      }

      this.setRequestBody(configuration.getString(CK_CONFIG_REQUEST_BODY));

      this.setExecutionInterval(configuration.getInt(CK_CONFIG_INTERVAL));
      this.setTimeout(configuration.getInt(CK_CONFIG_TIMEOUT));
      this.setTimeoutUnit(TimeUnit.valueOf(configuration.getString(CK_CONFIG_TIMEOUT_UNIT)));
      this.setIntervalUnit(TimeUnit.valueOf(configuration.getString(CK_CONFIG_INTERVAL_UNIT)));

      String responseHeaders = configuration.getString(CK_CONFIG_HEADERS_TO_RECORD);
      if (StringUtils.isNotEmpty(responseHeaders)) {
          this.setResponseHeadersToRecord(
                  responseHeaders.split(","));
      }

      this.setLogResponseBody(configuration.getBoolean(CK_CONFIG_LOG_RESPONSE_BODY));
    }
    // Proxy URI
    public URI getHttpProxyUri() {
        return httpProxyUri;
    }
    public void setHttpProxyUri(URI httpProxyUri) {
        this.httpProxyUri = httpProxyUri;
    }

    private URI httpProxyUri;

    // Site URL
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    // Input Label
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    // Input Type
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    // HTTP Method
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }

    // Request Body
    public String getRequestBody() {
        return requestBody;
    }
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    // Request Headers
    public ArrayList<String> getRequestHeaders() {
        return requestHeaders;
    }
    public void setRequestHeaders(ArrayList<String> requestHeadersToSend) {
        this.requestHeaders = requestHeadersToSend;
    }

    // Request Timeout
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    // Timeout unit of measurement (second, minutes, etc.)
    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }
    public void setTimeoutUnit(TimeUnit timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
    }

    // Frequency of requests
    public int getExecutionInterval() {
        return executionInterval;
    }
    public void setExecutionInterval(int executionInterval) {
        this.executionInterval = executionInterval;
    }

    // Frequency unit of measurement (second, minutes, etc.)
    public TimeUnit getIntervalUnit() {
        return intervalUnit;
    }
    public void setIntervalUnit(TimeUnit intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    // Response Headers to Record
    public String[] getResponseHeadersToRecord() {
        return responseHeadersToRecord;
    }
    public void setResponseHeadersToRecord(String[] responseHeadersToRecord) {
        this.responseHeadersToRecord = responseHeadersToRecord;
    }

    // Determine if response body should be logged
    public boolean isLogResponseBody() {
        return logResponseBody;
    }
    public void setLogResponseBody(boolean logResponseBody) {
        this.logResponseBody = logResponseBody;
    }

    // API authorization headers
    public ArrayList<String> getAuthorizationHeaders() {
        return authorizationHeaders;
    }
    public void setAuthorizationHeaders(ArrayList<String> authorizationHeaders) {
        this.authorizationHeaders = authorizationHeaders;
    }

    // Request Params
    public ArrayList<String> getRequestParams() {
        return requestParams;
    }
    public void setRequestParams(ArrayList<String> requestParams) {
        this.requestParams = requestParams;
    }

}
