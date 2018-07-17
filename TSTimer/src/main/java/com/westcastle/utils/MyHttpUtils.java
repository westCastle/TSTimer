package com.westcastle.utils;

import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class MyHttpUtils {
    public static JSONObject getJSON(String url){
        // 请求Http get 请求 api 接口
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            connection = (HttpURLConnection) new URL(url)
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 请求超时时间
            connection.setReadTimeout(8000); // 读取数据超时时间
            InputStream in = connection.getInputStream();
            // 下面对获取到的输入流进行读取
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            in.close();
            // 构造 JSON 对象
            // JSONArray
            JSONObject obj = JSONObject.fromObject(response.toString());
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static JSONObject postJSON(String url,JSONObject json){
        // 请求Http post 请求 api 接口
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            connection = (HttpURLConnection) new URL(url)
                    .openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            // 设置文件类型:
            connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            // 设置接收类型否则返回415错误
            //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
            connection.setRequestProperty("accept","application/json");
            connection.setConnectTimeout(5000); // 请求超时时间
            connection.setReadTimeout(8000); // 读取数据超时时间
            // 往服务器里面发送数据
            if (json != null) {
                byte[] writebytes = json.toString().getBytes();
                // 设置文件长度
                connection.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = connection.getOutputStream();
                outwritestream.write(writebytes);
                outwritestream.flush();
                outwritestream.close();
                System.out.println("提交给服务器的数据" + json);
            }
            // 读取服务器返回的数据
            InputStream in = connection.getInputStream();
            // 下面对获取到的输入流进行读取
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            in.close();
            // 构造 JSON 对象
            JSONObject obj = JSONObject.fromObject(response.toString());
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
