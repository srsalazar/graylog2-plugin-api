package org.graylog2.plugin.api.transport.services.duo;

import com.mashape.unirest.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.*;

import java.util.*;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

// API Plugin
import org.graylog2.plugin.api.transport.services.Service;
import org.graylog2.plugin.api.transport.configuration.ApiConfig;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

public class DuoApi extends Service {
    private static final Logger LOGGER = LoggerFactory.getLogger(DuoApi.class.getName());

    public static SimpleDateFormat RFC_2822_DATE_FORMAT
            = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z",
            Locale.US);
    private Map<String,String> duoParams;


    public DuoApi(ApiConfig config) throws MalformedURLException {
        super(config);
    }

    // Customize request builder for Bitium
    @Override
    public HttpRequest customizeHttpRequest(ApiConfig config) throws UnsupportedEncodingException {

        try {
            //Adding time filter
            DateTime dt = new DateTime(DateTimeZone.UTC);
            long executionIntervalMs = TimeUnit.MILLISECONDS.convert(config.getExecutionInterval(), config.getIntervalUnit());
            LOGGER.debug("Setting execution interval:" + executionIntervalMs);
            dt = dt.minus(executionIntervalMs);
            long unixTime = dt.getMillis() / 1000;
            this.request.queryString("mintime",
                    String.valueOf(unixTime));
            if (this.config.getRequestParams() != null) {
                this.duoParams = this.config.getRequestParams();
            } else {
                this.duoParams = new HashMap<String, String>();
            }
            this.duoParams.put("mintime", String.valueOf(unixTime));
            signRequest(this.config.getApplicationSecrets().get("iKey"), this.config.getApplicationSecrets().get("sKey"));
            return this.request;
        } catch (UnsupportedEncodingException e){
            LOGGER.debug("Error processing DUO request: " + e.toString());
            throw e;
        }
    }



    private void signRequest(String ikey, String skey)
            throws UnsupportedEncodingException {
        signRequest(ikey, skey, 2);
    }


    private void signRequest(String ikey, String skey, int sig_version)
            throws UnsupportedEncodingException {
        String date = formatDate(new Date());
        String canon = canonRequest(date, sig_version);
        String sig = signHMAC(skey, canon);

        String auth = ikey + ":" + sig;
        String header = "Basic " + Base64.encodeBytes(auth.getBytes());
        this.request.header("Authorization", header);
        if (sig_version == 2) {
            this.request.header("Date", date);
        }
    }

    protected String signHMAC(String skey, String msg) {
        try {
            byte[] sig_bytes = Util.hmacSha1(skey.getBytes(), msg.getBytes());
            String sig = Util.bytes_to_hex(sig_bytes);
            return sig;
        } catch (Exception e) {
            return "";
        }
    }

    private String formatDate(Date date) {
        // Could use ThreadLocal or a pool of format objects instead
        // depending on the needs of the application.
        synchronized (RFC_2822_DATE_FORMAT) {
            return RFC_2822_DATE_FORMAT.format(date);
        }
    }


    private String canonRequest(String date, int sig_version) throws UnsupportedEncodingException {
        String canon = "";
        if (sig_version == 2) {
            canon += date + "\n";
        }
        canon += this.config.getMethod().toUpperCase() + "\n";
        canon += this.config.getHost().toLowerCase() + "\n";
        canon += this.config.getUri() + "\n";
        canon += createQueryString();

        return canon;
    }

    private String createQueryString() throws UnsupportedEncodingException {
        ArrayList<String> args = new ArrayList<String>();
        ArrayList<String> keys = new ArrayList<String>();

        if (!this.duoParams.isEmpty()) {
            for (String key : this.duoParams.keySet()) {
                keys.add(key);
            }

            Collections.sort(keys);

            for (String key : keys) {
                String name = URLEncoder
                        .encode(key, "UTF-8")
                        .replace("+", "%20")
                        .replace("*", "%2A")
                        .replace("%7E", "~");
                String value = URLEncoder
                        .encode(this.duoParams.get(key), "UTF-8")
                        .replace("+", "%20")
                        .replace("*", "%2A")
                        .replace("%7E", "~");
                args.add(name + "=" + value);
            }
        }
        return Util.join(args.toArray(), "&");
    }

}
