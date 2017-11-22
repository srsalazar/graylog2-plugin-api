package org.graylog2.plugin.api.transport.configuration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;

import org.graylog2.plugin.api.transport.parsers.bitium.BitiumParser;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.inputs.MisfireException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.graylog2.plugin.api.transport.StaticVars.*;

/**
 * Created on 17/6/15.
 */
public class ApiConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiConfig.class.getName());

    private String url;
    private String host;
    private String uri;
    private String label;
    private String type;
    private String method;
    private String requestBody;
    private String apiType;
    private Map<String, String>  requestHeaders;
    private Map<String, String>  authorizationHeaders;
    private Map<String, String>  applicationSecrets;
    private Map<String, String> requestParams;
    private int timeout, executionInterval;
    private String[] responseHeadersToRecord;
    private boolean logResponseBody;
    private TimeUnit intervalUnit,timeoutUnit;


    public ApiConfig(Configuration configuration) throws MisfireException {

      this.setUrl(configuration.getString(CK_CONFIG_URL));
      this.setUrlDetails(configuration.getString(CK_CONFIG_URL));
      this.setLabel(configuration.getString(CK_CONFIG_LABEL));
      this.setMethod(configuration.getString(CK_CONFIG_METHOD));
      this.setApiType(configuration.getString(CK_CONFIG_API));

      String proxyUri = configuration.getString(CK_CONFIG_HTTP_PROXY);
      if (proxyUri != null && !proxyUri.isEmpty()) {
          this.setHttpProxyUri(URI.create(proxyUri));
      }

      String authHeaders = configuration.getString(CK_CONFIG_AUTHORIZATION_HEADERS);
      if (StringUtils.isNotEmpty(authHeaders)) {
          this.setAuthorizationHeaders(
                  new ArrayList(Arrays.asList(authHeaders.split(","))));
      }

      String appSecrets = configuration.getString(CK_CONFIG_SECRETS);
      if (this.getApiType() == DUO && StringUtils.isEmpty(appSecrets)){
          LOGGER.info("Duo API missing request secrets");
          throw new MisfireException("Duo API missing requests secrets");
      }
      if (StringUtils.isNotEmpty(appSecrets)) {
          this.setApplicationSecrets(
                  new ArrayList(Arrays.asList(appSecrets.split(","))));
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
    public Map<String,String> getRequestHeaders() {

        return requestHeaders;
    }
    public void setRequestHeaders(ArrayList<String> requestHeadersToSend) {

        this.requestHeaders = new HashMap<String,String>();
        for (String headers : requestHeadersToSend) {
            String tokens[] = headers.split(":");
            this.requestHeaders.put(tokens[0].trim(), tokens[1].trim());
        }
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
    public Map<String,String> getAuthorizationHeaders() {

        return authorizationHeaders;
    }
    public void setAuthorizationHeaders(ArrayList<String> authHeaders) {

        this.authorizationHeaders = new HashMap<String,String>();
        for (String headers : authHeaders) {
            String tokens[] = headers.split(":");
            this.authorizationHeaders.put(tokens[0].trim(), tokens[1].trim());
        }
    }

    // Request Params
    public Map<String, String> getRequestParams() {
        return requestParams;
    }
    public void setRequestParams(ArrayList<String> requestParameters) {
        this.requestParams = new HashMap<String,String>();
        for (String params : requestParameters) {
            String tokens[] = params.split("=");
            this.requestParams.put(tokens[0].trim(), tokens[1].trim());
        }
    }

    // Application Secrets
    public Map<String,String> getApplicationSecrets() {
        return applicationSecrets;
    }
    public void setApplicationSecrets(ArrayList<String> appSecrets) {
        this.applicationSecrets = new HashMap<String,String>();
        for (String secrets : appSecrets) {
            String tokens[] = secrets.split(":");
            this.applicationSecrets.put(tokens[0].trim(), tokens[1].trim());
        }
    }

    // API selected
    public String getApiType() { return apiType; }
    public void setApiType(String apiType) { this.apiType = apiType; }

    // URL Processing
    public String getHost() { return this.host; }
    public String getUri() { return uri; }
    public void setUrlDetails(String urlString) throws MisfireException {
        try {
            URL url = new URL(urlString);
            this.host = url.getHost();
            this.uri = url.getPath();
        } catch (MalformedURLException e){
            throw new MisfireException(e);
        }
    }



}
