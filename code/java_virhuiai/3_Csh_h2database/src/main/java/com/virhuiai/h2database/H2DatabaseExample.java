package com.virhuiai.h2database;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;


public class H2DatabaseExample {
//    jdbc:h2:./md
    private static final String DB_URL =  "jdbc:h2:/Volumes/RamDisk/test";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        try {
//            Class.forName("org.h2.Driver");

            // 建立数据库连接
            Connection conn = DriverManager.getConnection(DB_URL);

//            Connection conn = DriverManager.
//                    getConnection("jdbc:h2:~/test");

            System.out.println("数据库连接成功!");

            // 创建用户表
            createTable(conn);

            // 执行插入操作
            insertUser(conn, 1, "张三", "zhangsan@example.com", "北京");
            insertUser(conn, 2, "李四", "lisi@example.com", "上海");
            insertUser(conn, 3, "王五", "wangwu@example.com", "北京");
            insertUser(conn, 4, "赵六", "zhaoliu@example.com", "广州");

            // 查询所有用户
            System.out.println("\n所有用户:");
            List<User> users = getAllUsers(conn);
            displayUsers(users);

            // 根据ID查询用户
            System.out.println("\n查询ID为2的用户:");
            User user = getUserById(conn, 2);
            if (user != null) {
                System.out.println(user);
            }

            // 更新用户信息
            System.out.println("\n更新ID为3的用户:");
            updateUser(conn, 3, "王五", "wangwu_updated@example.com", "深圳");
            user = getUserById(conn, 3);
            if (user != null) {
                System.out.println(user);
            }

            // 根据城市去重查询
            System.out.println("\n不同的城市列表:");
            List<String> distinctCities = getDistinctCities(conn);
            for (String city : distinctCities) {
                System.out.println(city);
            }

            // 根据城市查询用户
            String city = "北京";
            System.out.println("\n居住在" + city + "的用户:");
            List<User> usersByCity = getUsersByCity(conn, city);
            displayUsers(usersByCity);

            // 删除用户
            System.out.println("\n删除ID为4的用户:");
            deleteUser(conn, 4);

            // 再次查询所有用户
            System.out.println("\n删除后的所有用户:");
            users = getAllUsers(conn);
            displayUsers(users);

            // 关闭连接
            conn.close();
            System.out.println("\n数据库连接已关闭");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 创建用户表
    private static void createTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // 如果表已存在，先删除
            stmt.execute("DROP TABLE IF EXISTS users");

            // 创建新表
            String sql = "CREATE TABLE users (" +
                    "id INT PRIMARY KEY, " +
                    "name VARCHAR(50), " +
                    "email VARCHAR(100), " +
                    "city VARCHAR(50))";
            stmt.execute(sql);
            System.out.println("用户表创建成功");
        }
    }

    // 插入用户数据
    private static void insertUser(Connection conn, int id, String name, String email, String city) throws SQLException {
        String sql = "INSERT INTO users (id, name, email, city) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, city);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("插入了 " + rowsAffected + " 条记录");
        }
    }

    // 查询所有用户
    private static List<User> getAllUsers(Connection conn) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, city FROM users";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("city")
                );
                users.add(user);
            }
        }

        return users;
    }

    // 根据ID查询用户
    private static User getUserById(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, name, email, city FROM users WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("city")
                    );
                }
            }
        }

        return null;
    }

    // 更新用户信息
    private static void updateUser(Connection conn, int id, String name, String email, String city) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, city = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, city);
            pstmt.setInt(4, id);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("更新了 " + rowsAffected + " 条记录");
        }
    }

    // 删除用户
    private static void deleteUser(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("删除了 " + rowsAffected + " 条记录");
        }
    }

    // 获取去重后的城市列表
    private static List<String> getDistinctCities(Connection conn) throws SQLException {
        List<String> cities = new ArrayList<>();
        String sql = "SELECT DISTINCT city FROM users";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cities.add(rs.getString("city"));
            }
        }

        return cities;
    }

    // 根据城市查询用户
    private static List<User> getUsersByCity(Connection conn, String city) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, city FROM users WHERE city = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, city);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("city")
                    );
                    users.add(user);
                }
            }
        }

        return users;
    }

    // 展示用户列表
    private static void displayUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("没有找到用户");
            return;
        }

        for (User user : users) {
            System.out.println(user);
        }
    }

    // 用户类
    private static class User {
        private int id;
        private String name;
        private String email;
        private String city;

        public User(int id, String name, String email, String city) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.city = city;
        }

        @Override
        public String toString() {
            return "User [id=" + id + ", name=" + name + ", email=" + email + ", city=" + city + "]";
        }
    }
}
