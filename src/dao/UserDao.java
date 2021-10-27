package dao;

import bean.Userbean;

public interface UserDao {
    public Userbean get(String name);
}