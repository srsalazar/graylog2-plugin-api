package org.graylog2.plugin.api.transport.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

// Graylog Plugin API
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.*;

import static org.graylog2.plugin.api.transport.StaticVars.*;

public class UIConfig{

  public static ConfigurationRequest getUIConfig(){
    final ConfigurationRequest cr = new ConfigurationRequest();
    cr.addField(new TextField(CK_CONFIG_URL,
            "URL to monitor",
            "",
            ""));
    cr.addField(new TextField(CK_CONFIG_LABEL,
            "API Input Label",
            "",
            "Label to identify this API monitor"));

    Map<String, String> httpMethods = new HashMap<>();
    httpMethods.put(METHOD_GET, METHOD_GET);
    httpMethods.put(METHOD_POST, METHOD_POST);
    httpMethods.put(METHOD_PUT, METHOD_PUT);
    cr.addField(new DropdownField(CK_CONFIG_METHOD,
            "HTTP Method",
            "GET",
            httpMethods,
            "HTTP Method (i.e. GET, POST)",
            ConfigurationField.Optional.NOT_OPTIONAL));

    cr.addField(new TextField(CK_CONFIG_AUTHORIZATION_HEADERS,
              "Authorization API Headers",
              "",
              "Comma separated authorization headers to send. i.e: Authorization: token {token}, X-Device-Id: {unique_id} ",
              ConfigurationField.Optional.OPTIONAL));

    cr.addField(new TextField(CK_CONFIG_HEADERS_TO_SEND,
            "Additional HTTP Headers",
            "",
            "Comma separated list of additional HTTP headers to send. For example: Accept: application/json, X-Requester: Graylog2",
            ConfigurationField.Optional.OPTIONAL));

    cr.addField(new TextField(CK_CONFIG_REQUEST_PARAMS,
              "Request API Parameters",
              "",
              "Comma separated list of parameters to send. i.e: count=1, pages=2",
              ConfigurationField.Optional.OPTIONAL));


    cr.addField(new TextField(CK_CONFIG_REQUEST_BODY,
              "Request Body",
              "",
              "Request Body to send",
              ConfigurationField.Optional.OPTIONAL,
              TextField.Attribute.TEXTAREA));

    cr.addField(new NumberField(CK_CONFIG_INTERVAL,
            "Request Interval",
            1,
            "Time between between requests",
            ConfigurationField.Optional.NOT_OPTIONAL));

    Map<String, String> timeUnits = DropdownField.ValueTemplates.timeUnits();
    //Do not add nano seconds and micro seconds
    timeUnits.remove(TimeUnit.NANOSECONDS.toString());
    timeUnits.remove(TimeUnit.MICROSECONDS.toString());

    cr.addField(new DropdownField(
            CK_CONFIG_INTERVAL_UNIT,
            "Request Interval Time Unit",
            TimeUnit.MINUTES.toString(),
            timeUnits,
            ConfigurationField.Optional.NOT_OPTIONAL
    ));

    cr.addField(new NumberField(CK_CONFIG_TIMEOUT,
            "Request Timeout",
            1,
            "Timeout for requests",
            ConfigurationField.Optional.NOT_OPTIONAL));

    cr.addField(new DropdownField(
            CK_CONFIG_TIMEOUT_UNIT,
            "Request Timeout Time Unit",
            TimeUnit.MINUTES.toString(),
            timeUnits,
            ConfigurationField.Optional.NOT_OPTIONAL
    ));

    cr.addField(new TextField(CK_CONFIG_HTTP_PROXY,
            "HTTP Proxy URI",
            "",
            "URI of HTTP Proxy to be used if required e.g. http://myproxy:8888",
            ConfigurationField.Optional.OPTIONAL));


    cr.addField(new TextField(CK_CONFIG_HEADERS_TO_RECORD,
            "Response Headers to Log",
            "",
            "Comma separated response headers to log. For example: Accept,Server,Expires",
            ConfigurationField.Optional.OPTIONAL));

    cr.addField(new BooleanField(CK_CONFIG_LOG_RESPONSE_BODY,
            "Log Full Response Body",
            false,
            "Select if the complete response body needs to be logged as part of message"));
    return cr;
  }
}
