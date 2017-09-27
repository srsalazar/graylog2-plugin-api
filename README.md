# Graylog API Plugin

[![Build Status](https://travis-ci.org/srsalazar/graylog2-plugin-api.svg?branch=master)](https://travis-ci.org/srsalazar/graylog2-plugin-api)

Note: This was based on [Graylog HTTP Monitor Plugin](https://github.com/sivasamyk/graylog2-plugin-input-httpmonitor)

An input monitor plugin for API endpoints (event logs).
Works by periodically polling the API endpoints and recording the responses as messages.

This plugin provides support for monitoring following parameters

* Response time in milliseconds
* HTTP Status Code
* HTTP Status Text
* HTTP Response Body
* HTTP Response size in bytes
* Timeouts and connection failures
* Custom Response Headers

Getting started
---------------
For Graylog v2.0 and above download this [jar](https://github.com/srsalazar/graylog2-plugin-api/releases/download/1.0.0/graylog2-plugin-api-1.0.0.jar)

* Shutdown the graylog server.
* Place the plugin jar in the Graylog plugins directory.
* Restart the server.
* In the graylog web UI, goto System->Inputs to launch new input of type 'HTTP Monitor'

Following parameters can be configured while launching the plugin

* URL to monitor ( supports HTTPS URLs with self-signed certificates also)
* Polling interval - Interval to execute the HTTP methods (poll the URL)
* Timeout - Time to wait before declaring the request as timed out.
* HTTP Method - GET/POST/PUT method to be executed
* Authorization Headers - API specific
* HTTP Headers - Comma separated list of HTTP request headers to be sent as part of request. e.g. CAccept:application/json, X-Requester:Graylog2
* Additional HTTP headers to log - Command separated list of HTTP response headers to log as part of message. e.g. Expires,Date
* HTTP Proxy URI

The status code will be 999 on connection failures, 998 on connection timeouts and 997 for others errors.

Polling interval and timeout can be configured in milliseconds/seconds/minutes/hours/days
