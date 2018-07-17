package com.westcastle.database;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * 数据库连接工具类
 * 负责创建连接 执行查询语句等
 */

public class DbConnector {

    //一.建立连接
    public static String user;
    public static String password;
    public static String url;
    public static String driver;

    static Logger logger = Logger.getLogger(DbConnector.class);

    static {
        Properties dbProperties = new Properties();
        try {
            InputStream is =
                    Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("db.properties");
            dbProperties.load(is);
            is.close();
            user = dbProperties.getProperty("db.user");
            password = dbProperties.getProperty("db.password");
            url = dbProperties.getProperty("db.url");
            driver = dbProperties.getProperty("db.driver");

            logger.debug("加载数据库配置信息 =====  " + dbProperties.toString());
        } catch (IOException e) {
            throw new RuntimeException("加载数据库配置文件失败，请检查classpath下 db.properties 是否存在");

        }
    }


    private Connection conn = null;
    private PreparedStatement statement = null;
    private ResultSet rs = null;

    private Connection createConnection() {
        try {
            Class.forName(driver);
            //2.获取连接
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (Exception e) {
            logger.error("建立数据库连接失败，请检查配置文件",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行增删改
     *
     * @param sql    语句
     * @param values 参数
     * @return
     */
    public void executeUpdate(String sql, Object[] values) throws SQLException {
        createConnection();
        statement = conn.prepareStatement(sql);
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
        }
        statement.executeUpdate();
    }


    /**
     * 执行查询
     *
     * @param sql    语句
     * @param values 参数
     * @return
     */
    public ResultSet executeQuery(String sql, Object[] values) throws SQLException {

        createConnection();
        statement = conn.prepareStatement(sql);
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
        }
        rs = statement.executeQuery();
        return rs;
    }

    public void closeDB() {
        try {
            if (rs != null)
                rs.close();
            if (statement != null)
                statement.close();
            if (conn != null)
                conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


