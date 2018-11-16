package com.cn.service.impl;

import com.cn.cache.ComponentAccessTokenMap;
import com.cn.cache.ComponentVerifyTicket;
import com.cn.cache.PreAuthCodeMap;
import com.cn.common.LogHelper;
import com.cn.common.WeChatCommon.WXBizMsgCrypt;
import com.cn.common.WeChatCommon.WeiXinDecode;
import com.cn.common.WeChatCommon.commenUtil;
import com.cn.common.httpsPostMethod;

import com.cn.dao.VerifyTicketDao;
import com.cn.dao.WeChatPublicDao;
import com.cn.entity.OpenPlatform;
import com.cn.entity.WeChatPublic;
import com.cn.service.weChatThridService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/2/23.
 */
@Service
public class weChatThridServiceImpl implements weChatThridService {

    @Value("${token}")
    private String token;
    @Value("${app_key}")
    private String key;
    @Value("${app_id}")
    private String app_id;





    @Value("${getopenid_url_secret}")
    private String getopenid_url_secret;
    @Value("${getopenid_url_component}")
    private String getopenid_url_component;
    @Value("${unified_order_url}")
    private String unified_order_url;



    @Autowired(required = false)
    private VerifyTicketDao ticketDao;
    @Autowired(required = false)
    private WeChatPublicDao wpDao;


    /**
     * 获取当前Request对象.
     *
     * @return 当前Request对象 可能为null
     * @throws IllegalStateException 当前线程不是web请求抛出此异常.
     */
    protected static HttpServletRequest currentRequest() throws IllegalStateException {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("当前线程中不存在 Request 上下文");
        }
        return attrs.getRequest();
    }


















    /**
     * 微信支付入口
     * @return
     */
    public String weixinForPay(Map<String, Object> source) throws Exception
    {
        LogHelper.info("---------------进入微信支付接口------------"+source);
        WeChatPublic weChatPublic=new WeChatPublic();
        weChatPublic.setWeChatPublic_appid(String.valueOf(source.get("app_id")));
        weChatPublic.setWeChatPublic_openPlat_appid(app_id);
        List<WeChatPublic> weChatPublicList= wpDao.getWeChatPublicByParamters(weChatPublic);
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("appid",String.valueOf(source.get("app_id")));//商户微信公众号appid
        hashMap.put("mch_id",weChatPublicList.get(0).getWechat_num());//商户号
        hashMap.put("device_info","WEB");
        hashMap.put("nonce_str", commenUtil.createSuiJi());
        hashMap.put("body",String.valueOf(source.get("body")));//商户内容
        hashMap.put("out_trade_no",String.valueOf(source.get("out_trade_no")));//本地的商户订单ID
        hashMap.put("fee_type","CNY");
        hashMap.put("total_fee", String.valueOf(source.get("total_fee")));//支付总金额
        if ( currentRequest().getHeader("x-forwarded-for") == null)
        {
            hashMap.put("spbill_create_ip", currentRequest().getRemoteAddr());
        }
        else
        {
            hashMap.put("spbill_create_ip", currentRequest().getHeader("x-forwarded-for"));
        }
        hashMap.put("notify_url","");
        hashMap.put("trade_type","JSAPI");
        hashMap.put("openid",String.valueOf(source.get("openid")));
        String sign= commenUtil.getSign(weChatPublicList.get(0).getWechat_payapi(), hashMap, "utf-8");
        hashMap.put("sign", sign);
        String xml= commenUtil.map2xml(hashMap);
        LogHelper.info("统一下单转换成的map转换成的xml为----------------------》" + xml);
        String responseStr= httpsPostMethod.sendHttpsPost(unified_order_url,xml,"微信支付统一下单");
        JSONObject rjson = new JSONObject(commenUtil.xml2JSON(responseStr));
        String return_code=rjson.getString("return_code");
        String return_msg=rjson.getString("return_msg");
        JSONObject return_json=new JSONObject();
        if("SUCCESS".equals(return_code))
        {
            String result_code=rjson.getString("result_code");
            if("SUCCESS".equalsIgnoreCase(result_code))
            {

                String timeStamp=String.valueOf(System.currentTimeMillis() / 1000);
                String nonceStr= commenUtil.createSuiJi();
                Map<String, String> return_map = new HashMap<>();
                return_map.put("appId",rjson.getString("appid"));
                return_map.put("timeStamp",nonceStr);
                return_map.put("signType","MD5");
                return_map.put("nonceStr", commenUtil.createSuiJi());
                return_map.put("package", "prepay_id=" + rjson.getString("prepay_id"));
                String sign_order= commenUtil.getSign(weChatPublicList.get(0).getWechat_payapi(), return_map, "utf-8");


                return_json.put("appId",rjson.getString("appid"));
                return_json.put("nonceStr",nonceStr);
                return_json.put("signType","MD5");
                return_json.put("timeStamp",timeStamp);
                return_json.put("package","prepay_id="+rjson.getString("prepay_id"));
                return_json.put("paySign",sign);
                return_json.put("rspCode","000");
                return_json.put("rspDesc","下单成功");
            }
            else
            {
                return_json.put("rspCode",rjson.getString("err_code"));
                return_json.put("rspDesc",rjson.getString("err_code_des"));
            }

        }
        else
        {
            return_json.put("rspCode", "321009");
            return_json.put("rspDesc", return_msg);
        }
        return return_json.toString();
    }

    /**
     * 微信支付返回信息
     */
    public void payReturn(HttpServletResponse response) throws Exception
    {
        JSONObject requestJSON = new JSONObject();
        BufferedReader br;
        br = currentRequest().getReader();
        String buffer = null;
        StringBuffer buff = new StringBuffer();
        while ((buffer = br.readLine()) != null) {
             buff.append(buffer + "\n");
        }
            br.close();
        String postStr = buff.toString();
        LogHelper.info("接收-----------------------微信支付-------------post发送数据:\n" + postStr);
        String result_json= commenUtil.xml2JSON(postStr);
        JSONObject rjson = new JSONObject(result_json);
        String return_code=rjson.getString("return_code");
        if("success".equalsIgnoreCase(return_code))
        {
            String result_code=rjson.getString("result_code");
            if("SUCCESS".equalsIgnoreCase(result_code))
            {
                String sign=rjson.getString("sign");
                WeChatPublic weChatPublic=new WeChatPublic();
                weChatPublic.setWeChatPublic_openPlat_appid(app_id);
                weChatPublic.setWeChatPublic_appid(rjson.getString("appid"));
                List<WeChatPublic> weChatPublicList= wpDao.getWeChatPublicByParamters(weChatPublic);
                Document doc = DocumentHelper.parseText(postStr);
                Element root = doc.getRootElement();
                Map<String, String> return_map = (Map<String, String>) commenUtil.xml2map(root);
                return_map.remove("sign");
                String sign_local = commenUtil.getSign(weChatPublicList.get(0).getWechat_payapi(), return_map, "utf-8");
                if(sign_local.equals(sign))
                {
                    String result="<xml>\n" +
                            "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                            "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                            "</xml>";
                    output(response,result);
                    if(weChatPublicList.get(0).getWechat_pay_return_url() !=null && !weChatPublicList.get(0).getWechat_pay_return_url().equals(""))
                    {
                        JSONObject vas_callback = new JSONObject();
                        vas_callback.put("orderNo",rjson.getString("out_trade_no"));
                        vas_callback.put("weichatOpenid",rjson.getString("openid"));
                        vas_callback.put("remoteOrderNo",rjson.getString("transaction_id"));
                        vas_callback.put("appid",rjson.getString("appid"));
                        vas_callback.put("amount",Long.parseLong(rjson.getString("cash_fee")));
                        httpsPostMethod.postHttp("balance_weichat_callback", vas_callback.toString(), weChatPublicList.get(0).getWechat_pay_return_url());
                    }
                }
                else
                {
                    LogHelper.info("支付验签失败！");
                }

            }
        }
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
}
