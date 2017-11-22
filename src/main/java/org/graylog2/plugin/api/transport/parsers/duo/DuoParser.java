package org.graylog2.plugin.api.transport.parsers.duo;


import com.mashape.unirest.request.HttpRequest;
import com.google.common.collect.Maps;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

// Graylog API
import org.graylog2.plugin.inputs.MessageInput;

// API Plugin
import static org.graylog2.plugin.api.transport.StaticVars.*;
import org.graylog2.plugin.api.transport.configuration.ApiConfig;
import org.graylog2.plugin.api.transport.parsers.Parser;

// Logging
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DuoParser extends Parser{
    private static final Logger LOGGER = LoggerFactory.getLogger(DuoParser.class.getName());
    public DuoParser(HttpRequest request, ApiConfig config){
        super(request, config, DUO);
    }

    @Override
    public void parse(MessageInput messageInput) {


        JSONArray jsonArray = getResponseBody().getObject().getJSONArray("response");

        //Not logging empty responses
        if (jsonArray.length() > 0) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);

                //Copy base into eventData
                Map<String, Object> eventData = Maps.newHashMap(this.baseEventData);
                eventData.put("_username", jsonobject.getString("username"));
                eventData.put("_device", jsonobject.getString("device"));
                eventData.put("_factor", jsonobject.getString("factor"));
                eventData.put("_integration", jsonobject.getString("integration"));
                eventData.put("_reason", jsonobject.getString("reason"));
                eventData.put("_result", jsonobject.getString("result"));
                eventData.put("_location", jsonobject.getJSONObject("location").toString());
                eventData.put("_new_enrollment", jsonobject.getBoolean("new_enrollment"));
                eventData.put("_timestamp", jsonobject.getLong("timestamp"));
                eventData.put("short_message", jsonobject.toString());
                publishToGraylog(eventData, messageInput);
            }
        }
    }
}
