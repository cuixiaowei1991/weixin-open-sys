package com.cn.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * Created by cuixiaowei on 2017/2/23.
 */

public class OpenPlatform {
    /**
     * 三方平台表主键ID
     */

    private Integer openPlatform_id;

    /**
     * 三方平台ID
     */

    private String openPlatform_appid;

    /**
     * 三方平台十分钟ticket
     */

    private String openPlatform_ticket;

    /**
     * 三方平台十分钟ticket时间戳
     */

    private String openPlatform_ticket_time;
    /**
     * 获取第三方平台component_access_token
     */

    private String openPlatform_com_access_token;

    public String getOpenPlatform_ticket_time() {
        return openPlatform_ticket_time;
    }

    public void setOpenPlatform_ticket_time(String openPlatform_ticket_time) {
        this.openPlatform_ticket_time = openPlatform_ticket_time;
    }

    public Integer getOpenPlatform_id() {
        return openPlatform_id;
    }

    public void setOpenPlatform_id(Integer openPlatform_id) {
        this.openPlatform_id = openPlatform_id;
    }

    public String getOpenPlatform_com_access_token() {
        return openPlatform_com_access_token;
    }

    public void setOpenPlatform_com_access_token(String openPlatform_com_access_token) {
        this.openPlatform_com_access_token = openPlatform_com_access_token;
    }

    public String getOpenPlatform_appid() {
        return openPlatform_appid;
    }

    public void setOpenPlatform_appid(String openPlatform_appid) {
        this.openPlatform_appid = openPlatform_appid;
    }

    public String getOpenPlatform_ticket() {
        return openPlatform_ticket;
    }

    public void setOpenPlatform_ticket(String openPlatform_ticket) {
        this.openPlatform_ticket = openPlatform_ticket;
    }
}
