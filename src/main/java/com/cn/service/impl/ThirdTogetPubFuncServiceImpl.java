package com.cn.service.impl;

import com.cn.MsgCode.MsgAndCode;
import com.cn.cache.*;
import com.cn.common.CommonHelper;
import com.cn.common.LogHelper;
import com.cn.common.WeChatCommon.MD5;
import com.cn.common.WeChatCommon.WXBizMsgCrypt;
import com.cn.common.WeChatCommon.WeiXinDecode;
import com.cn.common.WeChatCommon.commenUtil;
import com.cn.common.httpsPostMethod;
import com.cn.dao.VerifyTicketDao;
import com.cn.dao.WeChatPublicDao;

import com.cn.entity.OpenPlatform;
import com.cn.entity.WeChatPublic;
import com.cn.enums.MediaEnum;
import com.cn.service.ThirdTogetPubFuncService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cn.common.CommonHelper.*;
import static com.cn.handle.Behavior.handleVoice;

/**
 * Created by cuixiaowei on 2017/2/28.
 */
@Service
public class ThirdTogetPubFuncServiceImpl implements ThirdTogetPubFuncService {
    @Autowired
    private weChatThridServiceImpl weChatThridService;
    @Autowired(required = false)
    private WeChatPublicDao wcpDao;
    @Value("${app_id}")
    private String app_id;
    @Value("${api_query_auth_url}")
    private String api_query_auth_url;
    @Value("${api_authorizer_token_url}")
    private String api_authorizer_token_url;
    @Value("${token}")
    private String token;
    @Value("${app_key}")
    private String key;
    @Value("${openid_url}")
    private String openid_url;
    @Value("${getopenid_url_component}")
    private String getopenid_url_component;
    @Value("${getopenid_url_secret}")
    private String getopenid_url_secret;
    @Value("${publicNum_url}")
    private String publicNum_url;
    @Value("${menu_url}")
    private String menu_url;
    @Value("${app_secret}")
    private String app_secret;
    @Value("${component_access_token_url}")
    private String component_access_token_url;
    @Value("${pre_auth_code_url}")
    private String pre_auth_code_url;
    @Value("${qR_code_url}")
    private String qR_code_url;
    @Value("${redirect_uri}")
    private String redirect_uri;
    @Autowired(required = false)
    private VerifyTicketDao ticketDao;
    @Autowired(required = false)
    private WeChatPublicDao wpDao;
    private static String bb = null;
    @Override
    public String getSignPackage(String appid, String url) throws Exception
    {
        LogHelper.info("vvvvvvvvvvvvvvvvvvvvvvvvvvH5获取JS访问条件接口vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String nonceStr = commenUtil.createSuiJi();

        String rawstring = "jsapi_ticket=" + getJsapi_ticket(appid)
                + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url="
                + url + "";
        LogHelper.info("H5获取JS访问条件接口--生成的rawstring结果为：---" + rawstring);
        String signature = commenUtil.getSHA1(rawstring);
        JSONObject jj2 = new JSONObject();
        try {
            jj2.put("appId", appid);

            jj2.put("timestamp", timestamp);
            jj2.put("nonceStr", nonceStr);
            jj2.put("signature", signature);
        } catch (JSONException e) {
            LogHelper.error(e, "H5获取JS访问条件接口异常！！！！", false);
        }
        return jj2.toString();
    }

    /**
     * 调用微信JS接口的临时票据 用于生成JS请求微信的签名
     *
     * @return
     */
    public String getJsapi_ticket(String authaccess_appid) {
        JsapiticketInfo js = JsapiticketInfoMap.get(authaccess_appid);
        LogHelper.info("获取的授权token----------------》" + getAuthAccesstoken(authaccess_appid));
        LogHelper.info("AuthorizerInfo----------------》" + js);
        if (js==null || js.jsapi_ticketExpires()) {
            String rJsapi = httpsPostMethod.sendHttpsPost(
                    "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="
                            + getAuthAccesstoken(authaccess_appid), "&type=jsapi",
                    "获取jsapi_ticket");
            if (!"error".equals(rJsapi)) {
                try {
                    JSONObject jsapiJSON = new JSONObject(rJsapi);
                    int errcode = -1;
                    errcode = jsapiJSON.getInt("errcode");
                    if (errcode == 0) {
                        String jsapi_ticket = jsapiJSON.getString("ticket");
                        long expires_in = jsapiJSON.getInt("expires_in");
                        JsapiticketInfo af1=new JsapiticketInfo();
                        af1.setJsapi_ticket(jsapi_ticket);
                        af1.setJsapi_ticketExpires(expires_in);
                        JsapiticketInfoMap.put(authaccess_appid, af1);

                    } else {
                        return null;
                    }
                } catch (JSONException e) {
                    LogHelper.info("获取jsapi_ticket,authaccess_appid=" + authaccess_appid + "抛异常:"
                            + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        return JsapiticketInfoMap.get(authaccess_appid).getJsapi_ticket();
    }
    /**
     * 根据授权码和第三方平台appid换取公众号的接口调用凭据和授权信息
     * @return
     */
    public String getAuthAccesstoken(String authaccess_appid)
    {
        LogHelper.info("授权方的appid(getAuthAccesstoken)--------------》" + authaccess_appid);
        JSONObject accessToken_json = new JSONObject();
        AuthorizerInfo authorizerInfo= AuthorizerInfoMap.get(authaccess_appid);
        WeChatPublic weChatPublic=new WeChatPublic();
        weChatPublic.setWeChatPublic_appid(authaccess_appid);
        weChatPublic.setWeChatPublic_openPlat_appid(app_id);
        List<WeChatPublic> weChatPublicList= wcpDao.getWeChatPublicByParamters(weChatPublic);
        if(null==authorizerInfo)
        {
            authorizerInfo=new AuthorizerInfo();
        }
        if(weChatPublicList==null || weChatPublicList.size()<=0)
        {
            accessToken_json.put(MsgAndCode.CODE_001, MsgAndCode.MESSAGE_001);
            return accessToken_json.toString();
        }
        WeChatPublic war=weChatPublicList.get(0);
        if( (null==war.getWeChatPublic_appid_refreshtoken()|| "".equals(war.getWeChatPublic_appid_refreshtoken())))
        {
            String auth_code="";
            try
                {
                    if(war.getAuthorization_state()==null || (war.getAuthorization_state().equals("2")))
                    {//已取消授权，重新获取授权码
                        accessToken_json.put(MsgAndCode.CODE_002, MsgAndCode.MESSAGE_002);
                        return accessToken_json.toString();
                    }
                    auth_code=war.getWechatpublic_Authorizer_code();
                    accessToken_json.put("component_appid",app_id);
                    accessToken_json.put("authorization_code", auth_code);
                    String responseStr= httpsPostMethod.sendHttpsPost(api_query_auth_url+"?component_access_token="+getComponentAccessToken(),
                            accessToken_json.toString() ,"根据授权码和第三方平台appid换取公众号的接口调用凭据和授权信息");
                    LogHelper.info("根据授权码和第三方平台appid换取公众号的接口调用凭据和授权信息-----------》"+responseStr);
                    JSONObject rjson = new JSONObject(responseStr);
                    String authorization_info=rjson.get("authorization_info")+"";
                    JSONObject js_authorization = new JSONObject(authorization_info);
                    AuthorizerInfo ai=new AuthorizerInfo(app_id,authaccess_appid,js_authorization.getString("authorizer_access_token"),
                            js_authorization.getLong("expires_in"),js_authorization.getString("authorizer_refresh_token"));
                    AuthorizerInfoMap.put(js_authorization.getString("authorizer_appid"), ai);
                    war.setWeChatPublic_appid_refreshtoken(js_authorization.getString("authorizer_refresh_token"));
                    war.setWeChatPublic_appid(war.getWeChatPublic_appid());
                    war.setWeChatPublic_id(war.getWeChatPublic_id());
                    wcpDao.updateById(war);
                 } catch (JSONException e) {
                    LogHelper.error(e,"换取公众号的接口调用凭据和授权信息(本地authorizer_refresh_token)为空异常！！！！",false);
                    e.printStackTrace();
                }

        }
        else
        {
            if( authorizerInfo.authorizer_access_tokenExprise())
            {
                try
                {
                    //从数据库中查询出刷新令牌用的refreshtoken
                    LogHelper.debug("刷新令牌用的refreshtoken------------------>" + war.getWeChatPublic_appid_refreshtoken());
                    accessToken_json.put("component_appid",app_id);
                    accessToken_json.put("authorizer_appid",authaccess_appid);
                    accessToken_json.put("authorizer_refresh_token", war.getWeChatPublic_appid_refreshtoken());
                    String responseStr= httpsPostMethod.sendHttpsPost(api_authorizer_token_url+"?component_access_token=" + getComponentAccessToken(),
                            accessToken_json.toString(), "根据刷新token重新获取授权token");
                    LogHelper.debug("(本地过期根据authorizer_refresh_token重新获取token)-----------》"+responseStr);
                    JSONObject rjson = new JSONObject(responseStr);
                    AuthorizerInfo ai=new AuthorizerInfo(app_id,authaccess_appid,rjson.getString("authorizer_access_token"),
                            rjson.getLong("expires_in"),rjson.getString("authorizer_refresh_token"));
                    AuthorizerInfoMap.put(authaccess_appid,ai);
                    war.setWeChatPublic_appid_refreshtoken(rjson.getString("authorizer_refresh_token"));
                    war.setWeChatPublic_id(war.getWeChatPublic_id());
                    wcpDao.updateById(war);
                   } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        LogHelper.info("获取的公众号token========================"+ AuthorizerInfoMap.get(authaccess_appid).getAuthorizer_access_token());
        return AuthorizerInfoMap.get(authaccess_appid).getAuthorizer_access_token();
    }

    public void handleAuthorize(HttpServletRequest request, HttpServletResponse response)
            throws Exception
    {

        System.out.println("------------" + this);
        String msg_signature=request.getParameter("msg_signature");
        String timestamp=request.getParameter("timestamp");
        String nonce=request.getParameter("nonce");
        LogHelper.info("签名串------------:" + msg_signature);
        LogHelper.info("时间戳------------:" + timestamp);
        LogHelper.info("随机数------------:" + nonce);
        BufferedReader br;
        String postStr = null;

            br = request.getReader();
            String buffer = null;
            StringBuffer buff = new StringBuffer();
            while ((buffer = br.readLine()) != null) {
                buff.append(buffer + "\n");
            }
            br.close();
            postStr = buff.toString();
            LogHelper.info("接收post发送数据:\n" + postStr);

        String result_json= WeiXinDecode.decode(token, key, app_id, msg_signature, timestamp, nonce, postStr);
        JSONObject json=new JSONObject(result_json);
        LogHelper.info("获取ticket或者修改授权状态时，微信推送的数据："+json);
        String appid=json.getString("AppId");//第三方平台appid
        String createTime=json.getString("CreateTime");//时间戳
        String infoType=json.getString("InfoType");//component_verify_ticket
        OpenPlatform opf=new OpenPlatform();
        opf.setOpenPlatform_appid(appid);
        List<OpenPlatform> mapList= ticketDao.getVerifyTicketByAppid(opf);
        if(infoType!=null && "component_verify_ticket".equalsIgnoreCase(infoType))
        {//每十分钟推一次验证ticket
            output(response,"success");
            String componentVerifyTicket=json.getString("ComponentVerifyTicket");//Ticket内容
            LogHelper.info("接收的ticket为："+componentVerifyTicket);
            if(mapList.size()>0)
            {
                OpenPlatform opUpdate=mapList.get(0);
                opUpdate.setOpenPlatform_ticket(componentVerifyTicket);
                opUpdate.setOpenPlatform_ticket_time(CommonHelper.stampToDate(createTime));
                ticketDao.updateVerifyTicket(opUpdate);
            }
            else
            {
                OpenPlatform opIns=new OpenPlatform();
                opIns.setOpenPlatform_ticket(componentVerifyTicket);
                opIns.setOpenPlatform_ticket_time(CommonHelper.stampToDate(createTime));
                opIns.setOpenPlatform_appid(appid);
                ticketDao.inserVerifyTicketAppid(opIns);
            }
            LogHelper.info("每隔十分钟获取一次ticket！！！");
        }
        if(infoType!=null && ("unauthorized".equalsIgnoreCase(infoType)
                || "authorized".equalsIgnoreCase(infoType)))
        {//授权状态修改
            String result= AssemblingWeChatPublic(appid,infoType,json);
            LogHelper.info("授权状态修改："+result);
        }

    }

    @Override
    public String getMenu(Map<String, Object> source) throws Exception {
        LogHelper.info("接收查询自定义菜单配置接口参数：" + source);
        if(isNullOrEmpty(source.get("appid")))
        {
            return returnMissParamMessage("appid");
        }
        String result= httpsPostMethod.sendHttpsPost(
                menu_url+"?access_token=" + getAuthAccesstoken(source.get("appid").toString()), "", "接收查询自定义菜单配置接口参数");
        return result;
    }

    @Override
    public String createMenu(Map<String, Object> source) throws Exception {
        String deleteResponse= deleteMenu(source.get("appid").toString());
        String json= map2json(source);
        JSONObject jsonObject=new JSONObject(json);
        LogHelper.info("创建菜单传入的请求字符串参数-----》" + json);
        LogHelper.info("删除自定义菜单返回-----》" + deleteResponse);
        LogHelper.info("创建菜单传入的菜单格式-----》" + jsonObject.get("menuinfo"));
        JSONObject deleteinfo = new JSONObject(deleteResponse);
        String result="";
        if(deleteinfo.getString("errmsg")!=null && "ok".equalsIgnoreCase(deleteinfo.getString("errmsg")))
        {
            result= httpsPostMethod.sendHttpsPost(
                    "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="
                            + getAuthAccesstoken(source.get("appid").toString()), jsonObject.get("menuinfo").toString(), "创建自定义菜单");

        }
        else
        {
            result="{\"error\":\"创建之前删除失败！\"}";
        }
        return  result ;
    }

    @Override
    public String deleteMenu(String appid) throws Exception {
         LogHelper.info("接收查询自定义菜单配置接口参数：" + appid);
        String rp = httpsPostMethod.sendHttpsPost(
             "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token="+ getAuthAccesstoken(appid), "", "删除自定义菜单");
        LogHelper.info("删除自定义菜单微信回应：" + rp);
        return rp;
    }

    public String AssemblingWeChatPublic(String appid,String infoType ,JSONObject json)
    {

        String authorizerAppid=json.getString("AuthorizerAppid");//需授权的公众号appid
        String CreateTime=json.getString("CreateTime");//微信服务端授权码创建日期（毫秒）存入数据库后转为正常日期


        String authorizationCodeExpiredTime="";//过期时间
        String authorizationCode="";
        String result="";
        if(infoType!=null && "authorized".equalsIgnoreCase(infoType))
        {
            authorizationCode=json.getString("AuthorizationCode");//需授权公众号授权码
            authorizationCodeExpiredTime=json.getString("AuthorizationCodeExpiredTime");//过期时间
        }
        WeChatPublic weChatPublic=new WeChatPublic();
        weChatPublic.setWeChatPublic_openPlat_appid(appid);
        weChatPublic.setWeChatPublic_appid(authorizerAppid);
        List<WeChatPublic> weChatPublicList= wcpDao.getWeChatPublicByParamters(weChatPublic);
        WeChatPublic wechat=null;
        if(weChatPublicList!=null&&weChatPublicList.size()>0)
        {
            wechat=weChatPublicList.get(0);
        }
        else
        {
            wechat=new WeChatPublic();
        }
        if(infoType!=null && "unauthorized".equalsIgnoreCase(infoType))
        {
            wechat.setAuthorization_state("2");//取消授权
            wechat.setWeChatPublic_appid_refreshtoken("");
            wechat.setWechatpublic_CodeExpiredTime("");
            result=authorizerAppid+"--取消授权！";
        }
        else if(infoType!=null && "authorized".equalsIgnoreCase(infoType))
        {
            wechat.setAuthorization_state("1");//授权成功
            result=authorizerAppid+"--授权成功！";
        }
        wechat.setWechatpublic_CodeExpiredTime(authorizationCodeExpiredTime);
        wechat.setWechatpublic_Authorizer_code(authorizationCode);
        wechat.setWechatpublic_Code_CreateTime(CreateTime);

        wechat.setWeChatPublic_appid(authorizerAppid);
        wechat.setWeChatPublic_openPlat_appid(appid);
        String shopnumInfo= null;//获取商户公众号信息
        if(!authorizerAppid.equals("wx570bc396a51b8ff8"))
        {
            try {
                shopnumInfo = getShopPublicNumberInfo(authorizerAppid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject body_js = new JSONObject(shopnumInfo);
            String authorizer_info=body_js.get("authorizer_info")+"";
            JSONObject bb = new JSONObject(authorizer_info);
            String nick_name="";
            String head_img="";
            String qrcode_url="";
            try
            {
                nick_name= bb.getString("nick_name");
                head_img=bb.getString("head_img");
                qrcode_url=bb.getString("qrcode_url");
            }catch (Exception e)
            {}
            wechat.setWechat_headimage(head_img);
            wechat.setWechat_nickname(nick_name);
            wechat.setWechat_codeurl(qrcode_url);
        }
        if(weChatPublicList!=null&&weChatPublicList.size()>0)
        {
            wcpDao.updateById(wechat);
            result=result+"--更新数据库成功！";
        }
        else
        {
            wcpDao.insert(wechat);
            result=result+"--新增数据库成功！";
        }
        try {
            getAuthAccesstoken(authorizerAppid);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }
    /**
     * 获取第三方平台component_access_token
     * @return
     */
    public String getComponentAccessToken()
    {
        try {
            ComponentVerifyTicket cvt = ComponentAccessTokenMap.get(app_id);
            OpenPlatform opf=new OpenPlatform();
            opf.setOpenPlatform_appid(app_id);
            List<OpenPlatform> mapList= ticketDao.getVerifyTicketByAppid(opf);
            String ticket=mapList.get(0).getOpenPlatform_ticket();
            JSONObject componentAccessToken_json = new JSONObject();
            componentAccessToken_json.put("component_appid",app_id);
            componentAccessToken_json.put("component_appsecret",app_secret);
            componentAccessToken_json.put("component_verify_ticket",ticket);
            if(null==cvt)
            {
                String responseStr= httpsPostMethod.sendHttpsPost(component_access_token_url,
                        componentAccessToken_json.toString(), "获取第三方平台component_access_token");
                LogHelper.info("获取第三方平台token微信返回-----------》" + responseStr);
                JSONObject rjson = new JSONObject(responseStr);
                if(rjson.isNull("component_access_token"))
                {
                    return null;
                }
                String component_access_token=rjson.getString("component_access_token");
                long expires_in=rjson.getLong("expires_in");
                ComponentVerifyTicket componentVerifyTicket=new ComponentVerifyTicket(app_id,component_access_token,expires_in);
                ComponentAccessTokenMap.put(app_id,componentVerifyTicket);
                LogHelper.info("第三方平台component_access_token第一次请求存入-----------》" + ComponentAccessTokenMap.get(app_id).getComponent_access_token());

            }
            else
            {
                if(cvt.component_access_tokenExprise())
                {
                    String responseStr= httpsPostMethod.sendHttpsPost(component_access_token_url,
                            componentAccessToken_json.toString(), "获取第三方平台component_access_token");
                    LogHelper.info("获取第三方平台token微信返回（本地过期）-----------》" + responseStr);
                    JSONObject rjson = new JSONObject(responseStr);
                    String component_access_token=rjson.getString("component_access_token");
                    long expires_in=rjson.getLong("expires_in");
                    ComponentVerifyTicket componentVerifyTicket=new ComponentVerifyTicket(app_id,component_access_token,expires_in);
                    ComponentAccessTokenMap.put(app_id, componentVerifyTicket);
                    LogHelper.info("第三方平台component_access_token过期存入-----------》" + ComponentAccessTokenMap.get(app_id).getComponent_access_token());
                }
            }
            return ComponentAccessTokenMap.get(app_id).getComponent_access_token();


        }catch (Exception e)
        {
            LogHelper.error(e,"获取第三方平台component_access_token异常！！！！",false);
            e.printStackTrace();
            return null;
        }

    }
    /**
     * 获取预授权码pre_auth_code 预授权码用于公众号授权时的第三方平台方安全验证
     * @return
     */
    public String getPreAuthCode()
    {
        ComponentVerifyTicket cvt = PreAuthCodeMap.get(app_id);
        JSONObject PreAuthCode_json = new JSONObject();
        PreAuthCode_json.put("component_appid", app_id);
        try {
            if(null==cvt)
            {
                String responseStr= httpsPostMethod.sendHttpsPost(pre_auth_code_url+"?component_access_token="+getComponentAccessToken(),
                        PreAuthCode_json.toString(),"获取预授权码pre_auth_code（第一次访问）");
                LogHelper.info("获取预授权码pre_auth_code微信返回----------------》" + responseStr);
                JSONObject rjson = new JSONObject(responseStr);
                if(rjson.isNull("pre_auth_code"))
                {
                    return null;
                }
                else
                {
                    long expires_in=rjson.getLong("expires_in");//有效期20分钟
                    ComponentVerifyTicket componentVerifyTicket=new ComponentVerifyTicket(app_id,expires_in,rjson.getString("pre_auth_code"));
                    PreAuthCodeMap.put(app_id, componentVerifyTicket);
                    LogHelper.info("预授权码pre_auth_code第一次存入缓存-----------》" + PreAuthCodeMap.get(app_id).getPre_auth_code());
                }
            }
            else {
                if (cvt.preauthcode_Exprise()) {
                    String responseStr= httpsPostMethod.sendHttpsPost(pre_auth_code_url+"?component_access_token="+getComponentAccessToken(),
                            PreAuthCode_json.toString(),"获取预授权码pre_auth_code（过期）");
                    LogHelper.info("获取预授权码pre_auth_code微信返回（本地过期）----------------》" + responseStr);
                    JSONObject rjson = new JSONObject(responseStr);
                    long expires_in=rjson.getLong("expires_in");//有效期20分钟
                    ComponentVerifyTicket componentVerifyTicket=new ComponentVerifyTicket(app_id,expires_in,rjson.getString("pre_auth_code"));
                    PreAuthCodeMap.put(app_id,componentVerifyTicket);
                    LogHelper.info("预授权码pre_auth_code过期重新存入缓存-----------》" + PreAuthCodeMap.get(app_id).getPre_auth_code());
                }
            }
            return PreAuthCodeMap.get(app_id).getPre_auth_code();
        }
        catch (Exception e)
        {
            LogHelper.error(e,"预授权码pre_auth_code异常！！！！",false);
            e.printStackTrace();;
            return null;
        }
    }
    /**
     * 微信第三方平台授权的入口
     * @return
     */
    public String entranceWinXin() throws Exception
    {
        JSONObject json=new JSONObject();
        String url=qR_code_url+"?component_appid="+app_id+"&pre_auth_code="
                +getPreAuthCode()+"&redirect_uri="+redirect_uri;
        LogHelper.info("返回到前台的地址为：---------------》" + url);
        json.put("qrcode_url",url);
        return json.toString();
    }
    /**
     *微信统一回复接收接口
     * @return
     * @throws Exception
     */
    @Override
    public void commonReturn(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String msg_signature=request.getParameter("msg_signature");
        String timestamp=request.getParameter("timestamp");
        String nonce=request.getParameter("nonce");
        LogHelper.info("签名串------------:" + msg_signature);
        LogHelper.info("时间戳------------:" + timestamp);
        LogHelper.info("随机数------------:" + nonce);
        String appid_reply="";
        try {
            appid_reply = request.getParameter("appid");
            LogHelper.info("传过来的appid为------------------》" + appid_reply);
        } catch (Exception e) {
            LogHelper.error(e,"截取appid时出现异常！！！",false);
        }
        BufferedReader br;
        String postStr = null;
        try {
            br = request.getReader();
            String buffer = null;
            StringBuffer buff = new StringBuffer();
            while ((buffer = br.readLine()) != null) {
                buff.append(buffer + "\n");
            }
            br.close();
            postStr = buff.toString();
            LogHelper.debug("接收-------------（授权后）公众号消息与事件接收---------post发送数据:\n" + postStr);
            //收到消息后，5秒内回复success
            output(response, "success");
            String result_json= WeiXinDecode.decode(token, key, app_id, msg_signature, timestamp, nonce, postStr);
            JSONObject json=new JSONObject(result_json);
            LogHelper.info("微信统一回复接收接口接收微信数据json---------------》" + json);
            String MsgType=json.getString("MsgType");
            String ToUserName=json.getString("ToUserName");//微信公众号
            String FromUserName=json.getString("FromUserName");//用户openid
            String event="";
            if("event".equalsIgnoreCase(MsgType))
            {
                event=json.getString("Event");
            }
            if("text".equalsIgnoreCase(MsgType))
            {
                event="text";
            }
            if("image".equalsIgnoreCase(MsgType))
            {
                event="image";
            }
            if("voice".equalsIgnoreCase(MsgType))
            {
                event="voice";
            }
            WXBizMsgCrypt pc = new WXBizMsgCrypt(token, key, app_id);
            if(ToUserName.equals("gh_3c884a361561"))
            {
                String Content="";
                if(json.toString().contains("Content"))
                {
                    Content=json.getString("Content");
                }
                if ("event".equals(MsgType)) {
                    event = json.getString("Event");

                    String quanwang_event = event + "from_callback";

                    Long createTime = Calendar.getInstance().getTimeInMillis() / 1000;
                    StringBuffer sb = new StringBuffer();
                    sb.append("<xml>");
                    sb.append("<ToUserName><![CDATA["+FromUserName+"]]></ToUserName>");
                    sb.append("<FromUserName><![CDATA["+ToUserName+"]]></FromUserName>");
                    sb.append("<CreateTime>"+createTime+"</CreateTime>");
                    sb.append("<MsgType><![CDATA[text]]></MsgType>");
                    sb.append("<Content><![CDATA[" + quanwang_event + "]]></Content>");
                    sb.append("</xml>");
                    String replyMsg = sb.toString();
                    String quanwang_event_xml_miwen = pc.encryptMsg(replyMsg, createTime.toString(), request.getParameter("nonce"));
                    /*LogHelper.info("----bb---------" + bb);
                    String responseStr= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+bb,
                            "{\"touser\":\""+FromUserName+"\",\"msgtype\":\""+"text"+"\",\"text\":{\"content\":\""+quanwang_event+"\"}}","");
                    String aa_event="{\"touser\":\""+FromUserName+"\",\"msgtype\":\""+"text"+"\",\"text\":{\"content\":\""+quanwang_event+"\"}}";
                    response.getWriter().write("");*/
                    output(response,replyMsg.replaceAll("\\s*", "").replaceAll(" ",""));
                    /*response.getWriter().write(quanwang_event_xml_miwen);
                    response.getWriter().flush();
                    response.getWriter().close();*/
                    //return quanwang_event_xml_miwen;
                    //output(response,quanwang_event_xml_miwen);

                }
                else if ("text".equals(MsgType) && !Content.contains("QUERY_AUTH_CODE:")) {

                    String quanwang_content = json.getString("Content");

                    if ("TESTCOMPONENT_MSG_TYPE_TEXT".equals(quanwang_content)) {
                        quanwang_content = "TESTCOMPONENT_MSG_TYPE_TEXT_callback";
                        Long createTime = System.currentTimeMillis() / 1000;
                        StringBuffer sb = new StringBuffer(512);
                        sb.append("<xml>");
                        sb.append("<ToUserName><![CDATA["+FromUserName+"]]></ToUserName>");
                        sb.append("<FromUserName><![CDATA["+ToUserName+"]]></FromUserName>");
                        sb.append("<CreateTime>"+createTime+"</CreateTime>");
                        sb.append("<MsgType><![CDATA[text]]></MsgType>");
                        sb.append("<Content><![CDATA["+quanwang_content+"]]></Content>");
                        sb.append("</xml>");
                        String replyMsg = sb.toString();
                        LogHelper.info("确定发送的XML为：" + replyMsg);//千万别加密
                        //String quanwang_content_xml = commenUtil.map2xml(hashMap);
                        LogHelper.info("测试全网发布=========text（加密之前）=======" + replyMsg);
                        String quanwang_content_xml_miwen = pc.encryptMsg(replyMsg, createTime.toString(), request.getParameter("nonce"));
                        LogHelper.info("\"测试全网发布=========text（加密之后）=======\"" + quanwang_content_xml_miwen);
                        /*response.setContentType("text/html;charset=UTF-8");
                        response.getWriter().write(replyMsg);
                        response.getWriter().flush();
                        response.getWriter().close();*/
                        output(response,replyMsg.replaceAll("\\s*", "").replaceAll(" ",""));
                        /*response.getWriter().write(quanwang_content_xml_miwen);
                        response.getWriter().flush();
                        response.getWriter().close();*/

                        /*LogHelper.info("----bb---------" + bb);
                        String responseStr= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token="+bb,
                                "{\"touser\":\""+FromUserName+"\",\"msgtype\":\""+"text"+"\",\"text\":{\"content\":\""+quanwang_content+"\"}}","");
                        String aa_event="{\"touser\":\""+FromUserName+"\",\"msgtype\":\""+"text"+"\",\"text\":{\"content\":\""+quanwang_content+"\"}}";
                        LogHelper.info("全网发布测试test-----------》" + responseStr + "--------------实例字符串-----------------》" + aa_event);
*/
                        /*response.getWriter().write(quanwang_content_xml_miwen);
                        response.getWriter().flush();
                        response.getWriter().close();*/
                        //output(response,quanwang_content_xml_miwen);
                    }


                }
                //3.全网发布第三步
                else if(Content.contains("QUERY_AUTH_CODE:"))

                {
                    output(response, "");
                    String queryAuthCode=Content.substring(16);
                    LogHelper.info("------截取的QUERY_AUTH_CODE为--------"+queryAuthCode+"-----getComponentAccessToken()="+getComponentAccessToken());
                    /*String responseStr= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token="+getComponentAccessToken(),
                            "{\"component_appid\":\""+app_id+"\",\"authorization_code\":\""+queryAuthCode+"\"}","全网发布根据微信传的QUERY_AUTH_CODE获取token");
                    JSONObject rjson = new JSONObject(responseStr);
                    String authorization_info=rjson.get("authorization_info")+"";
                    JSONObject js_authorization = new JSONObject(authorization_info);*/
                    //String authorizer_access_token=js_authorization.getString("authorizer_access_token");//授权方token
                    String authorizer_access_token=getAuthAccesstoken(appid_reply.substring(1));//授权方token
                   /* String kefu_response_1= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + authorizer_access_token,
                            "{\"touser\":\"" + FromUserName + "\",\"msgtype\":\"" + "text" + "\",\"text\":{\"content\":\"" + "" + "\"}}", "");
*/                    bb=authorizer_access_token;
                    LogHelper.info("----bb---------" + bb);
                    response.getWriter().write("success");
                    response.getWriter().flush();
                    response.getWriter().close();
                    String kefu_response_2= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + authorizer_access_token,
                            "{\"touser\":\"" + FromUserName + "\",\"msgtype\":\"" + "text" + "\",\"text\":{\"content\":\"" + queryAuthCode+"_from_api" + "\"}}", "");

                }
            }
            else
            {
                WeChatPublic wp=new WeChatPublic();
                wp.setWeChatPublic_appid(appid_reply.substring(1));
                List<WeChatPublic> weChatPublics= wpDao.getWeChatPublicByParamters(wp);
                if(weChatPublics!=null && weChatPublics.size()>0)
                {
                    LogHelper.info("下发的下游的URL："+weChatPublics.get(0).getWechat_openf_common_url()+"，APPID："+appid_reply.substring(1));
                    json.put("marchant_appid",appid_reply.substring(1));
                    if(!isNullOrEmpty(weChatPublics.get(0).getWechat_openf_common_url()))
                     {
                         String sign=MD5.md5(appid_reply.substring(1)+"&"+token,"UTF-8");
                         httpsPostMethod.sendHttpsPost(weChatPublics.get(0).getWechat_openf_common_url(),String.valueOf(json),"POST"+sign);
                        LogHelper.info("执行完了================");
                         if(!isNullOrEmpty(weChatPublics.get(0).getWechat_pay_return_url()))
                        {
                            httpsPostMethod.sendHttpsPost(weChatPublics.get(0).getWechat_pay_return_url(),String.valueOf(json),"POST"+sign);
                        }
                     }
                     else
                     {
                        errorMessage(FromUserName,ToUserName,response);
                     }
                }else
                {
                    errorMessage(FromUserName,ToUserName,response);
                }
                /*if("subscribe".equals(event)) {
                String EventKey=json.get("EventKey")+"";//二维码参数值;
                LogHelper.info("关注之后返回数据：微信公众号：-----------》"+ToUserName+"用户openid：————————》"+FromUserName+"二维码参数值：——————————》"+EventKey);
                Map userMap=new HashMap();
                    userMap.put("app_id",appid_reply.substring(1));
                    userMap.put("openid",FromUserName);
                String getuserinfo = getUserInfo(userMap);
                *//*String kefu_response_2= httpsPostMethod.sendHttpsPost("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + getAuthAccesstoken(appid_reply.substring(1)),
                            "{\"touser\":\"" + FromUserName + "\",\"msgtype\":\"" + "text" + "\",\"text\":{\"content\":\"" + "你好！！！！！！"+"_from_api" + "\"}}", "");
*//*              Long createTime = System.currentTimeMillis() / 1000;
                StringBuffer sb = new StringBuffer(512);
                    sb.append("<xml>");
                    sb.append("<ToUserName><![CDATA["+FromUserName+"]]></ToUserName>");
                    sb.append("<FromUserName><![CDATA["+ToUserName+"]]></FromUserName>");
                    sb.append("<CreateTime>"+createTime+"</CreateTime>");
                    sb.append("<MsgType><![CDATA[text]]></MsgType>");
                    sb.append("<Content><![CDATA[啦啦啦啦啦]]></Content>");
                    sb.append("</xml>");
                    System.out.println("================="+sb.toString());
                    output(response,sb.toString());

                }else
                //else if("voice".equals(event))
                {
                    *//*LogHelper.info("获取用户上传的信息");
                    String madiaPath=handleVoice(
                            getAuthAccesstoken(appid_reply.substring(1)),json.get("MediaId").toString(), MediaEnum.getMediaCodeEnum(event).getMediaType());
                    LogHelper.info("存储到本地的多媒体路径："+madiaPath);*//*
                    Long createTime = System.currentTimeMillis() / 1000;
                    StringBuffer sb = new StringBuffer(512);
                    sb.append("<xml>");
                    sb.append("<ToUserName><![CDATA["+FromUserName+"]]></ToUserName>");
                    sb.append("<FromUserName><![CDATA["+ToUserName+"]]></FromUserName>");
                    sb.append("<CreateTime>"+createTime+"</CreateTime>");
                    sb.append("<MsgType><![CDATA[text]]></MsgType>");
                    sb.append("<Content><![CDATA[hahahahahhahaha]]></Content>");
                    sb.append("</xml>");
                    System.out.println("================="+sb.toString());
                    output(response,sb.toString());

                }*/
            }



        }catch (Exception e)
        {
            LogHelper.error(e,"统一回复接收接口异常！！！",false);
        }

    }

    public void errorMessage(String FromUserName,String ToUserName,HttpServletResponse response)
    {
        Long createTime = System.currentTimeMillis() / 1000;
        StringBuffer sb = new StringBuffer(512);
        sb.append("<xml>");
        sb.append("<ToUserName><![CDATA["+FromUserName+"]]></ToUserName>");
        sb.append("<FromUserName><![CDATA["+ToUserName+"]]></FromUserName>");
        sb.append("<CreateTime>"+createTime+"</CreateTime>");
        sb.append("<MsgType><![CDATA[text]]></MsgType>");
        sb.append("<Content><![CDATA[通知管理员在微信配置系统中配置相应参数]]></Content>");
        sb.append("</xml>");
        System.out.println("================="+sb.toString());
        output(response,sb.toString());
    }
    /**
     * 获取用户openid
     * @param source 包含商户app_id 商户appsecret 商户code 券id
     * @return
     */
    public String getOpenId(Map<String, Object> source) throws Exception
    {
        String shop_appid="";
        String code="";
        String getopenid="";
        String openid="";
        try {
            LogHelper.info("获取openid前台传给的参数(根据appid和APPsecret获取openid)-------------》" + source);
            shop_appid=source.get("app_id").toString();
            WeChatPublic weChatPublic=new WeChatPublic();
            weChatPublic.setWeChatPublic_openPlat_appid(app_id);
            weChatPublic.setWeChatPublic_appid(shop_appid);
            List<WeChatPublic> weChatPublicList= wpDao.getWeChatPublicByParamters(weChatPublic);
            String appsecret=weChatPublicList.get(0).getWeChatPublic_appid_secert();
            code=source.get("code").toString();
            getopenid = httpsPostMethod.sendHttpsPost(getopenid_url_secret+"?appid="+ shop_appid + "&secret=" + appsecret + "&code=" + code, "&grant_type=authorization_code",
                    "获取获取openid");
            JSONObject openid_js=new JSONObject(getopenid);
            openid=openid_js.getString("openid");

        } catch (Exception e)
        {
            LogHelper.info("获取openid前台传给的参数(根据三方平台ID和三方平台授权token获取openid)-------------》" + source);
            getopenid= httpsPostMethod.sendHttpsPost(getopenid_url_component+"?appid=" + shop_appid + "&code=" + code +
                    "&grant_type=authorization_code&component_appid=" + app_id + "&component_access_token=" + getComponentAccessToken(), "", "");
            JSONObject openid_js= null;
            try {
                openid_js = new JSONObject(getopenid);
                openid=openid_js.getString("openid");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return openid;
    }
    /**
     * 获取微信用户信息
     * @return
     */
    public String getUserInfo(Map<String, Object> source) throws Exception
    {
        JSONObject requestJSON = new JSONObject();
        String shop_appid="";//商户appid
        String openid="";

        try {
            shop_appid=source.get("app_id").toString();
            openid=source.get("openid").toString();
            LogHelper.info("前台页面回传的openid-------------》" + openid);
        } catch (Exception e)
        {
            openid="";
            LogHelper.info("未传入openid");
        }
        String getuserinfo="";
        if(openid==null || "".equals(openid))
        {
            getuserinfo = httpsPostMethod.sendHttpsPost(
                    openid_url+"?access_token="+getAuthAccesstoken(shop_appid)+"&openid="+getOpenId(source)
                            +""
                    , "&lang=zh_CN",
                    "获取获取openid");
        }
        else
        {
            getuserinfo = httpsPostMethod.sendHttpsPost(
                    openid_url+"?access_token="+getAuthAccesstoken(shop_appid)+"&openid="+openid
                            +""
                    , "&lang=zh_CN",
                    "获取获取openid");
        }


        LogHelper.info("通过openid获取用户信息 微信返回的数据userinfo---------------------》" + getuserinfo);

        try {
            JSONObject userinfo_json=new JSONObject(getuserinfo);
            requestJSON.put("userinfo",userinfo_json);
            requestJSON.put("rspCode","000");
            requestJSON.put("rspDesc","成功");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return requestJSON.toString();
    }
    /**
     * 获取公众号基本信息
     * @return
     */
    public String getShopPublicNumberInfo(String authorizer_appid) throws Exception
    {
        JSONObject vas_callback = new JSONObject();
        vas_callback.put("component_appid", app_id);
        vas_callback.put("authorizer_appid", authorizer_appid);
        String ShopPublicNumberInfo = httpsPostMethod.sendHttpsPost(
                publicNum_url + "?component_access_token=" + getComponentAccessToken(), vas_callback.toString(),
                "获取公众号基本信息");
        return ShopPublicNumberInfo;
    }
    /**
     * 工具类：回复微信服务器"文本消息"
     * @param response
     * @param returnvaleue
     */
    public void output(HttpServletResponse response,String returnvaleue){
        try {
            PrintWriter pw = response.getWriter();
            pw.write(returnvaleue);
//          System.out.println("****************returnvaleue***************="+returnvaleue);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String returnMissParamMessage(String errorMessage){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put(MsgAndCode.RSP_CODE, MsgAndCode.PARAM_MISSING_CODE);
        node.put(MsgAndCode.RSP_DESC, errorMessage + MsgAndCode.PARAM_MISSING_MSG);
        return node.toString();
    }
}
