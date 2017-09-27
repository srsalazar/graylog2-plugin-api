package org.graylog2.plugin.api.transport.parsers.bitium;


import com.ning.http.client.AsyncHttpClient;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;

import org.graylog2.plugin.Message;
import org.graylog2.plugin.inputs.MessageInput;
import org.json.JSONArray;
import org.json.JSONObject;
import com.ning.http.client.Response;

// API Plugin
import org.graylog2.plugin.api.transport.configuration.ApiConfig;
import org.graylog2.plugin.api.transport.parsers.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BitiumParser extends Parser{
    private static final Logger LOGGER = LoggerFactory.getLogger(BitiumParser.class.getName());
    public BitiumParser(AsyncHttpClient.BoundRequestBuilder requestBuilder, ApiConfig config){
        super(requestBuilder, config);
    }

    @Override
    public void parse(MessageInput messageInput) {


        JSONArray jsonArray = getResponseBody();

        //Not logging empty responses
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);

                //Copy base into eventData
                Map<String, Object> eventData = Maps.newHashMap(this.baseEventData);
                eventData.put("_type", jsonobject.get("type"));
                eventData.put("_name", jsonobject.getJSONObject("actor").getString("name"));
                eventData.put("_email", jsonobject.getJSONObject("actor").getString("email"));
                eventData.put("_created_at", jsonobject.getString("created_at"));
                eventData.put("short_message", jsonobject.toString());
                publishToGraylog(eventData, messageInput);
            }
        }
    }
}
