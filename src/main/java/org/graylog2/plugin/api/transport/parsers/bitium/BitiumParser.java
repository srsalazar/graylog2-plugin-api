package org.graylog2.plugin.api.transport.parsers.bitium;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.google.common.collect.Maps;
import java.util.Map;

import static org.graylog2.plugin.api.transport.StaticVars.*;
import org.json.JSONArray;
import org.json.JSONObject;

import org.graylog2.plugin.inputs.MessageInput;

// API Plugin
import org.graylog2.plugin.api.transport.configuration.ApiConfig;
import org.graylog2.plugin.api.transport.parsers.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class    BitiumParser extends Parser{
    private static final Logger LOGGER = LoggerFactory.getLogger(BitiumParser.class.getName());
    public BitiumParser(HttpRequest request, ApiConfig config) throws UnirestException {
        super(request, config, BITIUM);
    }

    @Override
    public void parse(MessageInput messageInput) {


        if (getResponseBody() != null) {
            JSONArray jsonArray = getResponseBody().getArray();

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
}
