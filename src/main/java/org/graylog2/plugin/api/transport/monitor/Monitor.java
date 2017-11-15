package org.graylog2.plugin.api.transport.monitor;

import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static org.graylog2.plugin.api.transport.StaticVars.SSL_VERSIONS;

public class Monitor {
  private static final Logger LOGGER = LoggerFactory.getLogger(Monitor.class.getName());


  public static class MonitorTask implements Runnable {
      private ApiConfig config;
      private MessageInput messageInput;
      private AsyncHttpClient httpClient;


      public MonitorTask(ApiConfig config, MessageInput messageInput){
          this.config = config;
          this.messageInput = messageInput;
          generateAsyncHttpClientConfigBuilder();
      }

      private void generateAsyncHttpClientConfigBuilder(){
          AsyncHttpClientConfig.Builder configBuilder = new AsyncHttpClientConfig.Builder();
          configBuilder.setEnabledProtocols(SSL_VERSIONS)
                  .setSSLContext(getSSLContext())
                  .setMaxConnections(500)
                  .setMaxConnectionsPerHost(200)
                  .setPooledConnectionIdleTimeout(100)
                  .setConnectionTTL(500);
          this.httpClient = new AsyncHttpClient(configBuilder.build());
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

      @Override
      public void run() {

          Service service;
          Parser parser;
          //TODO: Add switch for API type
          try {
              service = new BitiumApi(config, this.httpClient);
              AsyncHttpClient.BoundRequestBuilder requestBuilder = service.customizeBuildRequest(config);
              parser = new BitiumParser(requestBuilder, this.config);
              parser.parse(this.messageInput);
          } catch (MalformedURLException e){
              LOGGER.info("Error generating input" + e);
          }
      }
  }
}
