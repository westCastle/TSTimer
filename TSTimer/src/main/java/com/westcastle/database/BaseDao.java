package com.westcastle.database;


import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;


/**
 * 增删改查等相关操作
 */
public final class BaseDao {

    private static Logger logger = Logger.getLogger(BaseDao.class);
    /**
     * 根据id查询
     * @param table
     * @param id
     * @return
     */
    public static Map<String, Object> getById(String table, String id) {
        String sql = "select * from " + table + " where id = '" + id + "'";
        DbConnector helper = new DbConnector();
        try {
            ResultSet rs = helper.executeQuery(sql, null);
            logger.debug("执行sql语句 ====== " + sql);
            if (rs.next()) {
                Map<String, Object> obj = new HashMap<>();

                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    obj.put(md.getColumnName(i),
                            rs.getObject(i));
                }
                logger.debug("查询出的数据 ====== " + obj);
                return obj;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            helper.closeDB();
        }
    }

    /**
     * 根据条件查一条数据
     * @param table
     * @param whereStr
     * @return
     */
    public static Map<String, Object> getOneByWhere(String table, String whereStr) {
        String sql = "select * from " + table + " where 1 = 1 ";
        if (StringUtils.isNotBlank(whereStr)) {
            sql += " and " + whereStr;
        }
        sql += " limit 1";
        DbConnector helper = new DbConnector();
        try {
            ResultSet rs = helper.executeQuery(sql, null);
            logger.debug("执行sql语句 ====== " + sql);
            if (rs.next()) {
                Map<String, Object> obj = new HashMap<>();
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    obj.put(md.getColumnName(i),
                            rs.getObject(i));
                }
                logger.debug("查询出的数据 ====== " + obj);
                return obj;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            helper.closeDB();
        }
    }

    /**
     * 根据条件 查数据条数
     * @param table
     * @param whereStr
     * @return
     */
    public static long getCountByWhere(String table, String whereStr) {
        String sql = "select count(*) from " + table + " where 1 = 1 ";
        if (StringUtils.isNotBlank(whereStr)) {
            sql += " and " + whereStr;
        }
        DbConnector helper = new DbConnector();
        try {
            ResultSet rs = helper.executeQuery(sql, null);
            logger.debug("执行sql语句 ====== " + sql);
            rs.next();
            long count = rs.getLong(1);
            logger.debug("查询出的数据 ====== " + count);
            return count;
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            helper.closeDB();
        }
    }

    /**
     * 根据条件查多条数据
     * @param table
     * @param whereStr
     * @param orderByStr
     * @param limit
     * @return
     */
    public static List<Map<String, Object>> getListByWhere(String table, String whereStr, String orderByStr, String
            limit) {
        String sql = "select * from " + table + " where 1 = 1 ";
        if (StringUtils.isNotBlank(whereStr)) {
            sql += " and " + whereStr;
        }
        if (StringUtils.isNotBlank(orderByStr)) {
            sql += " order by " + orderByStr;
        }
        if (StringUtils.isNotBlank(limit)) {
            sql += " limit " + limit;
        }
        DbConnector helper = new DbConnector();
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            ResultSet rs = helper.executeQuery(sql, null);
            logger.debug("执行sql语句 ====== " + sql);
            while (rs.next()) {
                Map<String, Object> obj = new HashMap<>();
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    obj.put(md.getColumnName(i),
                            rs.getObject(i));
                }
                result.add(obj);
            }
            logger.debug("查询出的数据 ====== " + result);
            return result;
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            helper.closeDB();
        }
    }

    /**
     * 插入一条数据
     * @param table
     * @param insertData
     * @return
     */
    public static Map<String, Object> insert(String table, Map<String, Object> insertData) {
        Map<String, String> keyMapping = new HashMap<>();
        for (String k : insertData.keySet()) {
            keyMapping.put(k.toLowerCase(), k);
        }
        DbConnector helper = new DbConnector();
        String selectSql = "select * from " + table + " limit 0";
        try {
            ResultSet rs = helper.executeQuery(selectSql, null);
            Map<String, Object> data = new HashMap<>();
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                String cname = md.getColumnName(i).toLowerCase();
                if (keyMapping.containsKey(cname)) {
                    data.put(keyMapping.get(cname), insertData.get(keyMapping.get(cname)));
                }
            }
            if (!data.containsKey("id")) {
                data.put("id", UUID.randomUUID().toString().replaceAll("-", ""));
            }
            List<Object> insertValues = new ArrayList<>();
            StringBuffer sql = new StringBuffer("insert into ").append(table).append(" ( ");
            for (Map.Entry<String, Object> kv : data.entrySet()) {
                sql.append("`").append(kv.getKey()).append("`").append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") values ( ");
            for (Map.Entry<String, Object> kv : data.entrySet()) {
                sql.append("?").append(",");
                insertValues.add(kv.getValue());
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" )");
            helper.executeUpdate(sql.toString(), insertValues.toArray());
            insertData.put("id",data.get("id"));
            logger.debug("执行sql语句 ====== " + sql);
            logger.debug("参数列表 ====== " + insertValues);
            return data;
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            helper.closeDB();
        }
    }

    /**
     * 修改一条数据
     * @param table
     * @param updateData
     * @return
     */
    public static Map<String, Object> update(String table, Map<String, Object> updateData) {
        Map<String, String> keyMapping = new HashMap<>();
        for (String k : updateData.keySet()) {
            keyMapping.put(k.toLowerCase(), k);
        }
        DbConnector helper = new DbConnector();
        String selectSql = "select * from " + table + " limit 0";
        try {
            ResultSet rs = helper.executeQuery(selectSql, null);
            Map<String, Object> data = new HashMap<>();
            ResultSetMetaData md = rs.getMetaData();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                String cname = md.getColumnName(i).toLowerCase();
                if (keyMapping.containsKey(cname)) {
                    data.put(keyMapping.get(cname), updateData.get(keyMapping.get(cname)));
                }
            }
            if (!data.containsKey("id")) {
                throw new RuntimeException("修改数据，必须 有id 字段");
            }
            List<Object> updateValues = new ArrayList<>();
            StringBuffer sql = new StringBuffer("update ").append(table).append(" set ");
            for (Map.Entry<String, Object> kv : data.entrySet()) {
                if (!kv.getKey().equals("id")) {
                    sql.append("`").append(kv.getKey()).append("`").append(" = ? ").append(",");
                    updateValues.add(kv.getValue());
                }
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" where id = ?");
            updateValues.add(data.get("id"));
            helper.executeUpdate(sql.toString(), updateValues.toArray());
            logger.debug("执行sql语句 ====== " + sql);
            logger.debug("参数列表 ====== " + updateValues);
            return data;
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            helper.closeDB();
        }
    }

    /**
     * 删除一条数据
     * @param table
     * @param id
     */
    public static void delete(String table, String id) {
        String sql = "delete from " + table + " where id = ?";
        DbConnector helper = new DbConnector();
        try {
            helper.executeUpdate(sql, new Object[]{id});
            logger.debug("执行sql语句 ====== " + sql);
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            helper.closeDB();
        }
    }


    /**
     * 用于多表连接查询数据
     * @param sql
     * @return
     */
    public static List<Map<String, Object>> getListBySql(String sql) {
        DbConnector helper = new DbConnector();
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            ResultSet rs = helper.executeQuery(sql, null);
            logger.debug("执行sql语句 ====== " + sql);
            while (rs.next()) {
                Map<String, Object> obj = new HashMap<>();
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    obj.put(md.getColumnName(i),
                            rs.getObject(i));
                }
                result.add(obj);
            }
            logger.debug("查询出的数据 ====== " + result);
            return result;
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            helper.closeDB();
        }
    }

    /**
     * 用于多表联接查询记录数量
     * @param sql
     * @return
     */
    public static long getCountBySql(String sql) {
        DbConnector helper = new DbConnector();
        try {
            ResultSet rs = helper.executeQuery(sql, null);
            logger.debug("执行sql语句 ====== " + sql);
            rs.next();
            long result =rs.getLong(1);
            logger.debug("查询出的数据 ====== " + result);
            return result;
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        } finally {
            helper.closeDB();
        }
    }
}
