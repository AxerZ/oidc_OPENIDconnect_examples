/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.demo.demoapp.model;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao {
    
    protected Connection connection;
    Statement pstmt;
    File db;
    private final Logger logger = LoggerFactory.getLogger(UserDao.class);
    
    public UserDao(String cwd) {
        //連結資料庫

        db = new File(cwd + "/db/demoApp.sqlite");
        if (!db.exists()) {
            logger.info("db dao not exists.");
        }
        
    }
    
    public void getDBConnection() {
        connection = null;
        pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + db.getAbsolutePath());
            
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        
    }
    
    public boolean isBindingByID(String sub) throws SQLException {
        
        int count = 0;
        Boolean isBinding = false;
        getDBConnection();
        String sql = "Select * FROM users WHERE mappingID=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sub);
            
            ResultSet rs = pstmt.executeQuery();
            count = 0;
            while (rs.next()) {
                count++;
            }
        }
        connection.close();
        if (count > 0) {
            isBinding = true;
        }
        
        return isBinding;
    }
    
    public User findByMappindID(String sub) throws SQLException {
        getDBConnection();
        User user = new User();
        String sql = "select * from users where mappingID=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sub);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                user.setNickname(rs.getString("nickname"));
                user.setUsername(rs.getString("username"));
                user.setMappingID(rs.getString("mappingID"));
            }
        }
        connection.close();
        return user;
    }
    
    public User findByUsername(String username) throws SQLException {
        getDBConnection();
        User user = new User();
        
        String sql = "select * from users,userinfo on users.username=userinfo.username where users.username=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
//                logger.info(rs.getString("username"));
//                logger.info(rs.getString("password"));
                user.setNickname(rs.getString("nickname"));
                user.setCoins(rs.getInt("coins"));
                user.setMappingID(rs.getString("mappingID"));
                user.setPassword(rs.getString("password"));
                user.setUsername(rs.getString("username"));
            }
        }
        connection.close();
        return user;
    }
    
    public boolean updateMappingID(String username, String sub) throws SQLException {
        Boolean isUpdated = false;
        getDBConnection();
        int result = 0;
        String sql = "UPDATE users SET mappingID=? where username=?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, sub);
            pstmt.setString(2, username);
            result = pstmt.executeUpdate();
            logger.info("update result:" + String.format("%d", result));
        }
        if (result != 0) {
            isUpdated = true;
        }
        connection.close();
        return isUpdated;
    }
    
    public boolean userAuth(String username, String password) throws SQLException {
        Boolean isValid = false;
        getDBConnection();
        String sql = "Select * FROM users WHERE username=? AND password=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                if (username.equals(rs.getString("username")) && password.equals(rs.getString("password"))) {
                    isValid = true;
                }
            }
            
        }
        
        connection.close();
        
        return isValid;
    }
    
}
