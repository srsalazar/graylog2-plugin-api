package org.graylog2.plugin.api.transport.services;

import com.mashape.unirest.http.Unirest;
import com.ning.http.client.*;
import java.net.MalformedURLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import org.apache.http.HttpHost;

import java.util.concurrent.TimeUnit;

import com.mashape.unirest.request.*;

import org.graylog2.plugin.api.transport.configuration.ApiConfig;
import static org.graylog2.plugin.api.transport.StaticVars.*;


public abstract class Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class.getName());

    protected AsyncHttpClient.BoundRequestBuilder requestBuilder;
    protected HttpRequest request = null;
    protected Service (ApiConfig config) throws MalformedURLException{
        setProxy(config);
        setRequestTimeout(config);
        generateRequestBuilder(config);
    }

    private void generateRequestBuilder(ApiConfig config) throws MalformedURLException{
        //Validate URL
        validURL(config.getUrl());

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

        //Set headers
        if (config.getRequestHeaders() != null) {
            for (String header : config.getRequestHeaders()) {
                String tokens[] = header.split(":");
                request.header(tokens[0].trim(), tokens[1].trim());
            }
        }

        if (config.getRequestParams() != null) {
            for (String requestParams : config.getRequestParams()) {
                String tokens[] = requestParams.split("=");
                request.field(tokens[0].trim(), tokens[1].trim());
            }
        }
        return request;

    }

    private void configHttpGetRequest(ApiConfig config){
        GetRequest getRequest = Unirest.get(config.getUrl());
        this.request = configHttpRequest(config, getRequest);

    }

    private HttpRequest configHttpRequest(ApiConfig config,HttpRequest request){
        //Set authorization headers
        if (config.getAuthorizationHeaders() != null) {
            for (String authHeader : config.getAuthorizationHeaders()) {
                String tokens[] = authHeader.split(":");
                request.header(tokens[0].trim(), tokens[1].trim());
            }
        }

        //Set headers
        if (config.getRequestHeaders() != null) {
            for (String header : config.getRequestHeaders()) {
                String tokens[] = header.split(":");
                request.header(tokens[0].trim(), tokens[1].trim());
            }
        }

        if (config.getRequestParams() != null) {
            for (String requestParams : config.getRequestParams()) {
                String tokens[] = requestParams.split("=");
                request.queryString(tokens[0].trim(), tokens[1].trim());
            }
        }

        return request;
    }


    private void validURL(String urlString) throws MalformedURLException{
        URL url = new URL(urlString);
    }

    public abstract HttpRequest customizeHttpRequest(ApiConfig config);
}
