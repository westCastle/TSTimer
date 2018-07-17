package com.westcastle.core;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApiServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        ActionContext.getInstance().setHttpRequest(request);
        ActionContext.getInstance().setHttpResponse(response);

        // 接收url传过来的参数
        setRequestParameters();

        logger.debug("提交的url参数 ===== " + ActionContext.getInstance().getParameters());
        // 接收 post 或 put 传过来的json数据
        setRequestJSON();

        logger.debug("提交的JSON数据 ===== " + ActionContext.getInstance().getRequestData());

        // 根据url 调用对应的类的对应方法 处理请求
        processAction();
    }

    private static Map<String, Object> ACTION_CACHE = new ConcurrentHashMap<>();

    private static Logger logger = Logger.getLogger(ApiServlet.class);

    private void processAction() {
        HttpServletRequest request = ActionContext.getInstance().getHttpRequest();
        HttpServletResponse response = ActionContext.getInstance().getHttpResponse();
        String url = request.getRequestURI();
        logger.debug("请求路径 ===== " + url);
        Class actionClass = null;
        Object actionObj = null;
        String[] requestPath = url.substring(5, url.length()).split("/");
        String actionStr = requestPath[0];
        String actionPath = "com.hwadee.action." + actionStr + "Action";
        logger.debug("请求 Action ====== " + actionPath);
        String methodStr = requestPath[1];
        int code = 200;
        String errMsg = null;
        if (ACTION_CACHE.containsKey(url)) {
            actionObj = ACTION_CACHE.get(url);
            actionClass = actionObj.getClass();
        } else {
            try {
                actionClass = Class.forName(actionPath);
                actionObj = actionClass.newInstance();
                ACTION_CACHE.put(url, actionObj);
            } catch (ClassNotFoundException e) {
                errMsg = "类 " + actionPath + "找不到,请检查url是否正确,类名首字母大写，不要加Action";
                code = 400;
                e.printStackTrace(System.err);
                logger.error(errMsg, e);
            } catch (Exception ex) {
                JSONArray stackTrace = new JSONArray();
                for (StackTraceElement e : ex.getStackTrace()) {
                    stackTrace.add(e.toString());
                }
                errMsg = stackTrace.toString();
                code = 500;
                logger.error(errMsg, ex);
            }
        }

        if (code == 200) {
            try {
                logger.debug("请求方法 ===== " + actionPath + "." + methodStr);
                Method invokMethod = actionClass.getMethod(methodStr);
                invokMethod.invoke(actionObj);
            } catch (NoSuchMethodException e) {
                errMsg = "方法 " + actionPath + "." + methodStr + "找不到,请检查url是否正确";
                code = 400;
                logger.error(errMsg, e);
            } catch (InvocationTargetException ex) {
                JSONArray stackTrace = new JSONArray();
                for (StackTraceElement e : ex.getTargetException().getStackTrace()) {
                    stackTrace.add(e.toString());
                }
                errMsg = stackTrace.toString();
                code = 500;
                logger.error(errMsg, ex.getTargetException());
            } catch (Exception ex) {
                JSONArray stackTrace = new JSONArray();
                for (StackTraceElement e : ex.getStackTrace()) {
                    stackTrace.add(e.toString());
                }
                errMsg = stackTrace.toString();
                code = 500;
                logger.error(errMsg, ex);
            }
        }

        try {
            response.setContentType("application/json");

            if (code != 200) {
                ActionContext.getInstance().getResponseData().put("errMsg", errMsg);
            }

            ActionContext.getInstance().getResponseData().put("code", code);
            response.setStatus(code);
            PrintWriter out = response.getWriter();
            JSONObject obj = ActionContext.getInstance().getResponseData();
            logger.debug("响应的内容 ===== " + obj.toString());
            out.println(obj);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage(),e);
        } finally {
            ActionContext.getInstance().getRequestData().clear();
            ActionContext.getInstance().getParameters().clear();
            ActionContext.getInstance().getResponseData().clear();
        }
    }

    private void setRequestJSON() throws IOException {
        HttpServletRequest request = ActionContext.getInstance().getHttpRequest();
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String str;
        StringBuffer sb = new StringBuffer();
        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        if (sb.length() == 0) {
            sb.append("{}");
        }
        br.close();
        JSONObject requestData = JSONObject.fromObject(sb.toString());
        ActionContext.getInstance().setRequestData(requestData);
    }

    private void setRequestParameters() {
        HttpServletRequest request = ActionContext.getInstance().getHttpRequest();
        Enumeration names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement().toString();
            String value = request.getParameter(name);
            ActionContext.getInstance().getParameters().put(name, value);
        }
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        this.doPost(request, response);
    }
}
