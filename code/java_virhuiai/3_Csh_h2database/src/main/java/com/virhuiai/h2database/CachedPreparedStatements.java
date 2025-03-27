package com.virhuiai.h2database;
/*
 * Copyright 2004-2024 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 这个示例应用展示了如何缓存预编译语句。
 * This sample application shows how to cache prepared statements.
 */
public class CachedPreparedStatements {

    // 数据库连接对象
    private Connection conn;
    // SQL语句执行对象
    private Statement stat;
    // 使用线程安全的ConcurrentHashMap来存储预编译语句
    private final ConcurrentHashMap<String, PreparedStatement> prepared = new ConcurrentHashMap<>();

    /**
     * 这个方法在从命令行执行此示例应用程序时被调用。
     * This method is called when executing this sample application from the
     * command line.
     *
     * @param args 命令行参数
     * @throws Exception 执行失败时抛出异常
     */
    public static void main(String... args) throws Exception {
        // 创建并运行示例
        new CachedPreparedStatements().run();
    }

    /**
     * 运行示例的主要方法
     * @throws Exception 执行失败时抛出异常
     */
    private void run() throws Exception {
        // 加载H2数据库驱动
        Class.forName("org.h2.Driver");
        // 创建内存数据库连接
        conn = DriverManager.getConnection(
                "jdbc:h2:mem:", "sa", "");
        // 创建SQL语句执行对象
        stat = conn.createStatement();
        // 创建测试表
        stat.execute(
                "create table test(id int primary key, name varchar)");
        // 获取缓存的预编译语句（如果不存在则创建）
        PreparedStatement prep = prepare(
                "insert into test values(?, ?)");
        // 设置第一个参数为整数1
        prep.setInt(1, 1);
        // 设置第二个参数为字符串"Hello"
        prep.setString(2, "Hello");
        // 执行SQL语句
        prep.execute();
        // 关闭数据库连接
        conn.close();
    }

    /**
     * 从缓存中获取预编译语句，如果不存在则创建并缓存它
     *
     * @param sql SQL语句
     * @return 预编译语句对象
     * @throws SQLException SQL异常
     */
    private PreparedStatement prepare(String sql)
            throws SQLException {
        // 尝试从缓存中获取预编译语句
        PreparedStatement prep = prepared.get(sql);
        // 如果缓存中不存在该预编译语句
        if (prep == null) {
            // 创建新的预编译语句
            prep = conn.prepareStatement(sql);
            // 将预编译语句存入缓存
            prepared.put(sql, prep);
        }
        // 返回预编译语句
        return prep;
    }

}