package dao.impl;

import DButil.Dbutil;
import bean.Userbean;
import dao.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDaoImpl implements UserDao {

    @Override
    public Userbean get(String name) {

        String sql = "select name,ip from login where name =?";
        Dbutil dbutil = new Dbutil();
        Connection connection = dbutil.getConnection();
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, name);
            Userbean userbean = new Userbean();
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                String ip = rs.getString(2);
                userbean.setName(username);
                userbean.setIp(ip);
                return userbean;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();
        Userbean userbean = userDao.get("zz");
        System.out.println(userbean.getName());
        System.out.println(userbean.getIp());
    }
}