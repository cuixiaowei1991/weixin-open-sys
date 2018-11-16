package com.cn.entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by cuixiaowei on 2017/2/23.
 */

public class WeChatPublic {
    /**
     * 微信公众号表主键ID
     */

    private Integer weChatPublic_id;
    /**
     * 微信公众号appid
     */

    private String weChatPublic_appid;
    /**
     * 微信公众号appid_secert
     */

    private String weChatPublic_appid_secert;
    /**
     * 微信公众号（第三方获取的）授权token
     */

    private String weChatPublic_appid_token;
    /**
     * 微信公众号（第三方获取的）授权刷新token
     */

    private String weChatPublic_appid_refreshtoken;
    /**
     * 微信公众号（第三方获取的）授权的三方平台appid
     */

    private String weChatPublic_openPlat_appid;

    /**
     *授权公众号授权码（有效期一小时）
     */

    private String wechatpublic_Authorizer_code;

    /**
     *微信服务端授权码创建日期（毫秒）存入数据库后转为正常日期
     */

    private String wechatpublic_Code_CreateTime;

    /**
     *微信服务端授权码失效日期（毫秒）存入数据库后转为正常日期
     */

    private String wechatpublic_CodeExpiredTime;

    /**
     *授权状态 1：授权成功 2：取消授权 3：授权更新
     */

    private String Authorization_state;



    /**
     * token 返回时间
     */

    private Date authorizer_token_time;

    /**
     *商户号
     */

    private String wechat_num;
    /**
     *支付秘钥
     */

    private String wechat_payapi;
    /**
     *公众号昵称
     */

    private String wechat_nickname;
    /**
     *公众号头像
     */

    private String wechat_headimage;

    /**
     *商户ID
     */

    private String wechat_merchantid;
    /**
     *商户公众号二维码
     */

    private String wechat_codeurl;

    /**
     *预留字段1
     */

    private String wechat_reserved_field1;
    /**
     *预留字段2
     */

    private String wechat_reserved_field2;
    /**
     *预留字段3
     */

    private String wechat_reserved_field3;

    /**
     *支付回调地址WECHAT_PAY_RETURN_URL
     */

    private String wechat_pay_return_url;
    /**
     * WECHAT_OPENF_COMMON_URL三方平台回调各个公众号的地址
     */
    private String wechat_openf_common_url;
    /**
     * WECHAT_PAY_PEM1公众号支付PEM证书1
     */
    private String wechat_pay_pem1;
    /**
     * WECHAT_PAY_PEM2公众号支付PEM证书2
     */
    private String wechat_pay_pem2;
    /**
     * WECHAT_PAY_P12   公众号支付P12证书
     */
    private String wechat_pay_p12;

    public String getWechat_codeurl() {
        return wechat_codeurl;
    }

    public void setWechat_codeurl(String wechat_codeurl) {
        this.wechat_codeurl = wechat_codeurl;
    }

    public String getWechat_reserved_field1() {
        return wechat_reserved_field1;
    }

    public void setWechat_reserved_field1(String wechat_reserved_field1) {
        this.wechat_reserved_field1 = wechat_reserved_field1;
    }

    public String getWechat_reserved_field2() {
        return wechat_reserved_field2;
    }

    public void setWechat_reserved_field2(String wechat_reserved_field2) {
        this.wechat_reserved_field2 = wechat_reserved_field2;
    }

    public String getWechat_reserved_field3() {
        return wechat_reserved_field3;
    }

    public void setWechat_reserved_field3(String wechat_reserved_field3) {
        this.wechat_reserved_field3 = wechat_reserved_field3;
    }

    public String getWechat_pay_return_url() {
        return wechat_pay_return_url;
    }

    public void setWechat_pay_return_url(String wechat_pay_return_url) {
        this.wechat_pay_return_url = wechat_pay_return_url;
    }

    public String getWechat_openf_common_url() {
        return wechat_openf_common_url;
    }

    public void setWechat_openf_common_url(String wechat_openf_common_url) {
        this.wechat_openf_common_url = wechat_openf_common_url;
    }

    public String getWechat_pay_pem1() {
        return wechat_pay_pem1;
    }

    public void setWechat_pay_pem1(String wechat_pay_pem1) {
        this.wechat_pay_pem1 = wechat_pay_pem1;
    }

    public String getWechat_pay_pem2() {
        return wechat_pay_pem2;
    }

    public void setWechat_pay_pem2(String wechat_pay_pem2) {
        this.wechat_pay_pem2 = wechat_pay_pem2;
    }

    public String getWechat_pay_p12() {
        return wechat_pay_p12;
    }

    public void setWechat_pay_p12(String wechat_pay_p12) {
        this.wechat_pay_p12 = wechat_pay_p12;
    }

    public String getWechat_nickname() {
        return wechat_nickname;
    }

    public void setWechat_nickname(String wechat_nickname) {
        this.wechat_nickname = wechat_nickname;
    }

    public String getWechat_headimage() {
        return wechat_headimage;
    }

    public void setWechat_headimage(String wechat_headimage) {
        this.wechat_headimage = wechat_headimage;
    }

    public String getWechat_num() {
        return wechat_num;
    }

    public void setWechat_num(String wechat_num) {
        this.wechat_num = wechat_num;
    }

    public String getWechat_payapi() {
        return wechat_payapi;
    }

    public void setWechat_payapi(String wechat_payapi) {
        this.wechat_payapi = wechat_payapi;
    }

    public String getWechatpublic_Authorizer_code() {
        return wechatpublic_Authorizer_code;
    }

    public void setWechatpublic_Authorizer_code(String wechatpublic_Authorizer_code) {
        this.wechatpublic_Authorizer_code = wechatpublic_Authorizer_code;
    }

    public String getWechatpublic_Code_CreateTime() {
        return wechatpublic_Code_CreateTime;
    }

    public void setWechatpublic_Code_CreateTime(String wechatpublic_Code_CreateTime) {
        this.wechatpublic_Code_CreateTime = wechatpublic_Code_CreateTime;
    }

    public String getWechatpublic_CodeExpiredTime() {
        return wechatpublic_CodeExpiredTime;
    }

    public void setWechatpublic_CodeExpiredTime(String wechatpublic_CodeExpiredTime) {
        this.wechatpublic_CodeExpiredTime = wechatpublic_CodeExpiredTime;
    }

    public String getAuthorization_state() {
        return Authorization_state;
    }

    public void setAuthorization_state(String authorization_state) {
        Authorization_state = authorization_state;
    }


    public Date getAuthorizer_token_time() {
        return authorizer_token_time;
    }

    public void setAuthorizer_token_time(Date authorizer_token_time) {
        this.authorizer_token_time = authorizer_token_time;
    }

    public Integer getWeChatPublic_id() {
        return weChatPublic_id;
    }

    public void setWeChatPublic_id(Integer weChatPublic_id) {
        this.weChatPublic_id = weChatPublic_id;
    }

    public String getWeChatPublic_appid() {
        return weChatPublic_appid;
    }

    public void setWeChatPublic_appid(String weChatPublic_appid) {
        this.weChatPublic_appid = weChatPublic_appid;
    }

    public String getWeChatPublic_appid_secert() {
        return weChatPublic_appid_secert;
    }

    public void setWeChatPublic_appid_secert(String weChatPublic_appid_secert) {
        this.weChatPublic_appid_secert = weChatPublic_appid_secert;
    }

    public String getWeChatPublic_appid_token() {
        return weChatPublic_appid_token;
    }

    public void setWeChatPublic_appid_token(String weChatPublic_appid_token) {
        this.weChatPublic_appid_token = weChatPublic_appid_token;
    }

    public String getWeChatPublic_appid_refreshtoken() {
        return weChatPublic_appid_refreshtoken;
    }

    public void setWeChatPublic_appid_refreshtoken(String weChatPublic_appid_refreshtoken) {
        this.weChatPublic_appid_refreshtoken = weChatPublic_appid_refreshtoken;
    }

    public String getWeChatPublic_openPlat_appid() {
        return weChatPublic_openPlat_appid;
    }

    public void setWeChatPublic_openPlat_appid(String weChatPublic_openPlat_appid) {
        this.weChatPublic_openPlat_appid = weChatPublic_openPlat_appid;
    }

    public String getWechat_merchantid() {
        return wechat_merchantid;
    }

    public void setWechat_merchantid(String wechat_merchantid) {
        this.wechat_merchantid = wechat_merchantid;
    }
}
