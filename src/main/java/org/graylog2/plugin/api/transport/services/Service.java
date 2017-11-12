package org.graylog2.plugin.api.transport.services;

import com.ning.http.client.*;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.MalformedURLException;
import java.security.cert.X509Certificate;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;

import java.security.cert.CertificateException;
import java.security.GeneralSecurityException;
import java.util.concurrent.TimeUnit;

import org.graylog2.plugin.api.transport.configuration.ApiConfig;
import static org.graylog2.plugin.api.transport.StaticVars.*;

public abstract class Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(Service.class.getName());

    protected AsyncHttpClient.BoundRequestBuilder requestBuilder;
    protected AsyncHttpClient httpClient;

    protected Service (ApiConfig config) throws MalformedURLException{
      generateHTTPClient();
      generateRequestBuilder(config);
    }

    private void generateHTTPClient(){
        AsyncHttpClientConfig.Builder configBuilder = new AsyncHttpClientConfig.Builder();
        configBuilder.setEnabledProtocols(SSL_VERSIONS);
        configBuilder.setSSLContext(getSSLContext());
        configBuilder.setMaxConnections(500)
                .setMaxConnectionsPerHost(200)
                .setPooledConnectionIdleTimeout(100);
        this.httpClient = new AsyncHttpClient(configBuilder.build());
    }

    private void generateRequestBuilder(ApiConfig config) throws MalformedURLException{
        //Validate URL
        validURL(config.getUrl());

        //Build request object
        if (METHOD_POST.equals(config.getMethod())) {
            this.requestBuilder = httpClient.preparePost(config.getUrl());
        } else if (METHOD_PUT.equals(config.getMethod())) {
            this.requestBuilder = httpClient.preparePut(config.getUrl());
        } else if (METHOD_HEAD.equals(config.getMethod())) {
            this.requestBuilder = httpClient.prepareHead(config.getUrl());
        } else {
            this.requestBuilder = httpClient.prepareGet(config.getUrl());
        }

        if (StringUtils.isNotEmpty(config.getRequestBody())) {
            this.requestBuilder.setBody(config.getRequestBody());
        }

        if (config.getRequestHeaders() != null) {
            for (String header : config.getRequestHeaders()) {
                String tokens[] = header.split(":");
                this.requestBuilder.setHeader(tokens[0].trim(), tokens[1].trim());
            }
        }

        if (config.getAuthorizationHeaders() != null) {
            for (String authHeader : config.getAuthorizationHeaders()) {
                String tokens[] = authHeader.split(":");
                this.requestBuilder.setHeader(tokens[0].trim(), tokens[1].trim());
            }
        }

        if (config.getRequestParams() != null) {
            for (String requestParams : config.getRequestParams()) {
                String tokens[] = requestParams.split("=");
                this.requestBuilder.addQueryParam(tokens[0].trim(), tokens[1].trim());
            }
        }

        int timeoutInMs = (int) TimeUnit.MILLISECONDS.convert(config.getTimeout(), config.getTimeoutUnit());
        this.requestBuilder.setRequestTimeout(timeoutInMs);

        if (config.getHttpProxyUri() != null) {
            ProxyServer proxyServer = new ProxyServer(config.getHttpProxyUri().getHost(), config.getHttpProxyUri().getPort());
            this.requestBuilder.setProxyServer(proxyServer);
        }
    }

    private void validURL(String urlString) throws MalformedURLException{
        URL url = new URL(urlString);
    }

    //Accept all certficates
    private SSLContext getSSLContext() {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[]{
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            }, null);
            return context;
        } catch (GeneralSecurityException e) {
            LOGGER.debug("Exception while creating certs ",e);
        }
        return null;
    }

    public abstract AsyncHttpClient.BoundRequestBuilder customizeBuildRequest(ApiConfig config) ;
}
