package org.graylog2.plugin.api.transport;

public class StaticVars {
  // HTTP Methods
  public static final String METHOD_POST = "POST";
  public static final String METHOD_HEAD = "HEAD";
  public static final String METHOD_PUT = "PUT";
  public static final String METHOD_GET = "GET";

  public static final String BITIUM = "bitium";
  public static final String DUO = "duo";

  // Supported Schemas
  public static String[] HTTP_SCHEMAS = {
          "http",
          "https",
  };

  //Supported SSL
  public static String[] SSL_VERSIONS = {
          "TLSv1.2",
          "TLSv1.1",
          "TLSv1"
  };

  // Fields
  public static final String CK_CONFIG_INTERVAL_UNIT = "configIntervalUnit";
  public static final String CK_CONFIG_URL = "configURL";
  public static final String CK_CONFIG_LABEL = "configLabel";
  public static final String CK_CONFIG_METHOD = "configMethod";
  public static final String CK_CONFIG_REQUEST_BODY = "configRequestBody";
  public static final String CK_CONFIG_HEADERS_TO_SEND = "configHeadersToSend";
  public static final String CK_CONFIG_TIMEOUT = "configTimeout";
  public static final String CK_CONFIG_TIMEOUT_UNIT = "configTimeoutUnit";
  public static final String CK_CONFIG_INTERVAL = "configInterval";
  public static final String CK_CONFIG_HEADERS_TO_RECORD = "configHeadersToRecord";
  public static final String CK_CONFIG_LOG_RESPONSE_BODY = "configLogResponseBody";
  public static final String CK_CONFIG_HTTP_PROXY = "configHttpProxy";
  public static final String CK_CONFIG_AUTHORIZATION_HEADERS = "configAuthHeaders";
  public static final String CK_CONFIG_REQUEST_PARAMS = "configRequestParams";
  public static final String CK_CONFIG_SECRETS = "configRequestSecrets";
  public static final String CK_CONFIG_API = "configApi";
}
