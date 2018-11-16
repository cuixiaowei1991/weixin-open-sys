package com.cn.entity;

/**
 * User: cuixiaowei
 * Date: 2018/10/18
 * PackageName: com.cn.entify
 */

/**
 * 实体类：属性对应数据库表的字段
 */
public class User {

    private Integer id;
    private String name;
    private String passwd;
    private String appid;

    public User(Integer id , String name, String passwd, String appid) {
        this.id = id;
        this.name = name;
        this.passwd = passwd;
        this.appid = appid;
    }


    public User(String name, String passwd, String appid) {
        this.name = name;
        this.passwd = passwd;
        this.appid = appid;
    }
    public User(String name, String passwd) {
        this.name = name;
        this.passwd = passwd;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", passwd='" + passwd + '\'' +
                ", appid='" + appid + '\'' +
                '}';
    }
}

