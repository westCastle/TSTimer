package com.westcastle.core;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ActionContext {

    private static ThreadLocal<ActionContext> contextThreadLocal;

    public JSONObject getRequestData() {
        if (requestData == null)
            requestData = new JSONObject();
        return requestData;
    }

    public void setRequestData(JSONObject requestData) {
        this.requestData = requestData;
    }

    public JSONObject getResponseData() {
        if (responseData == null)
            responseData = new JSONObject();
        return responseData;
    }

    public void setResponseData(JSONObject responseData) {
        this.responseData = responseData;
    }

    private JSONObject requestData;

    private JSONObject responseData;


    private Map<String, String> parameters;


    static {
        contextThreadLocal = new ThreadLocal<ActionContext>() {
            public ActionContext initialValue() {
                return new ActionContext();
            }
        };
    }

    private ActionContext() {
    }

    public HttpServletRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public HttpServletResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    public static ActionContext getInstance() {
        ActionContext context = contextThreadLocal.get();
        return context;
    }

    public static String getString(String key) {
        if (getInstance().getRequestData().containsKey(key)) {
            return getInstance().getRequestData().getString(key);
        } else {
            return null;
        }
    }

    public static Integer getInt(String key) {
        if (getInstance().getRequestData().containsKey(key)) {
            return getInstance().getRequestData().getInt(key);
        } else {
            return null;
        }
    }

    public static Long getLong(String key) {
        if (getInstance().getRequestData().containsKey(key)) {
            return getInstance().getRequestData().getLong(key);
        } else {
            return null;
        }
    }

    public static Boolean getBool(String key) {
        if (getInstance().getRequestData().containsKey(key)) {
            return getInstance().getRequestData().getBoolean(key);
        } else {
            return null;
        }
    }

    public static JSONObject getJSONObject(String key) {
        if (getInstance().getRequestData().containsKey(key)) {
            return getInstance().getRequestData().getJSONObject(key);
        } else {
            return null;
        }
    }

    public static JSONArray getJSONArray(String key) {
        if (getInstance().getRequestData().containsKey(key)) {
            return getInstance().getRequestData().getJSONArray(key);
        } else {
            return null;
        }
    }


    public static void setString(String key, String value) {
        getInstance().getResponseData().put(key, value);
    }

    public static void setInt(String key, Integer value) {
        getInstance().getResponseData().put(key, value);
    }

    public static void setLong(String key, Long value) {
        getInstance().getResponseData().put(key, value);
    }

    public static void setBool(String key, Boolean value) {
        getInstance().getResponseData().put(key, value);
    }

    public static void setJSONObject(String key, JSONObject value) {
        getInstance().getResponseData().put(key, value);
    }

    public static void setJSONArray(String key, JSONArray value) {
        getInstance().getResponseData().put(key, value);
    }

    public static void setMap(String key, Map<String, Object> value) {
        getInstance().getResponseData().put(key, value);
    }

    public static void setList(String key, List<Map<String, Object>> value) {
        getInstance().getResponseData().put(key, value);
    }

    public static String getParameter(String key) {
        return getInstance().getParameters().get(key);
    }


    public Map<String, String> getParameters() {
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        return parameters;
    }
}
