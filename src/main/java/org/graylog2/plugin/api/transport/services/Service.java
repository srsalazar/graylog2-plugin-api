package org.graylog2.plugin.api.transport.services;

import com.mashape.unirest.http.Unirest;
import com.ning.http.client.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.HttpHost;

import java.util.concurrent.TimeUnit;

import com.mashape.unirest.request.*;

import org.graylog2.plugin.api.transport.configuration.ApiConfig;
import static org.graylog2.plugin.api.transport.StaticVars.*;


public abstract class Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class.getName());

    protected AsyncHttpClient.BoundRequestBuilder requestBuilder;
    protected HttpRequest request;
    protected ApiConfig config;

    protected Service (ApiConfig config) throws MalformedURLException{
        this.request = null;
        this.config = config;
        setProxy(config);
        setRequestTimeout(config);
        generateRequestBuilder(config);
    }

    private void generateRequestBuilder(ApiConfig config){
        //Build request object
        if (METHOD_POST.equals(config.getMethod())) {
            configHttpPostRequest(config);
        } else if (METHOD_PUT.equals(config.getMethod())) {
            configHttpPutRequest(config);
        }  else {
            configHttpGetRequest(config);
        }
    }

    private void setProxy(ApiConfig config){
        if (config.getHttpProxyUri() != null) {
            Unirest.setProxy(new HttpHost(config.getHttpProxyUri().getHost(), config.getHttpProxyUri().getPort()));
        }
    }

    private void setRequestTimeout(ApiConfig config){
        int timeoutInMs = (int) TimeUnit.MILLISECONDS.convert(config.getTimeout(), config.getTimeoutUnit());
        Unirest.setTimeouts(timeoutInMs, timeoutInMs);
    }

    private void configHttpPutRequest(ApiConfig config){

        HttpRequestWithBody putRequest = Unirest.put(config.getUrl());
        putRequest = configHttpBodyRequest(config, putRequest);
        this.request = configHttpRequest(config, putRequest);
    }

    private void configHttpPostRequest(ApiConfig config){

        HttpRequestWithBody postRequest = Unirest.post(config.getUrl());
        postRequest = configHttpBodyRequest(config, postRequest);
        this.request = configHttpRequest(config, postRequest);
    }

    private HttpRequestWithBody configHttpBodyRequest(ApiConfig config, HttpRequestWithBody request){
        //Set body
        if (StringUtils.isNotEmpty(config.getRequestBody())) {
            request.body(config.getRequestBody());
        }
        return request;

    }

    private void configHttpGetRequest(ApiConfig config){
        GetRequest getRequest = Unirest.get(config.getUrl());
        this.request = configHttpRequest(config, getRequest);

    }

    private HttpRequest configHttpRequest(ApiConfig config, HttpRequest request){
        //Set authorization headers
        if (config.getAuthorizationHeaders() != null) {
            for (String key : config.getAuthorizationHeaders().keySet()) {
                request.header(key, config.getAuthorizationHeaders().get(key));
            }
        }

        //Set headers
        if (config.getRequestHeaders() != null) {
            for (String key : config.getRequestHeaders().keySet()) {
                request.header(key, config.getRequestHeaders().get(key));
            }
        }

        if (config.getRequestParams() != null) {
            for (String key : config.getRequestParams().keySet()) {
                request.queryString(key, config.getRequestParams().get(key));
            }
        }

        return request;
    }

    public abstract HttpRequest customizeHttpRequest(ApiConfig config) throws UnsupportedEncodingException;
}
