package com.cn.service.impl;

import com.cn.MsgCode.MsgAndCode;
import com.cn.common.CommonHelper;
import com.cn.common.LogHelper;
import com.cn.dao.QueryPageRequest;
import com.cn.dao.WeChatPublicDao;

import com.cn.entity.WeChatPublic;
import com.cn.service.weChatConfigurationService;
import com.yunrich.monster.common.paginator.PageList;
import com.yunrich.monster.common.paginator.Paginator;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/6/20.
 */
@Service
public class weChatConfigurationServiceImpl implements weChatConfigurationService
{
    @Autowired(required = false)
    private WeChatPublicDao wpDao;
    @Override
    public String getWeChatInfo(Map<String, Object> source) throws Exception {
        LogHelper.info("获取微信配置信息=="+source);
        JSONObject wechatJo=new JSONObject();
        if(CommonHelper.isNullOrEmpty(source.get("weChatPublic_id")))
        {
            return returnMissParamMessage("weChatPublic_id");
        }
        WeChatPublic weChatPublic=new WeChatPublic();
        weChatPublic.setWeChatPublic_id(Integer.valueOf(source.get("weChatPublic_id").toString()));
        List<WeChatPublic> weChatPublicList= wpDao.getWeChatPublicByParamters(weChatPublic);
        if(weChatPublicList==null || weChatPublicList.size()==0)
        {
            wechatJo.put(MsgAndCode.PARAM_MISSING_CODE, MsgAndCode.CODE_003);
            wechatJo.put(MsgAndCode.PARAM_MISSING_MSG, MsgAndCode.MESSAGE_003);
            return wechatJo.toString();
        }
        WeChatPublic wp=weChatPublicList.get(0);
        wechatJo.put("weChatPublic_id",wp.getWeChatPublic_id());
        wechatJo.put("weChatPublic_appid",wp.getWeChatPublic_appid());
        wechatJo.put("weChatPublic_appid_secert",wp.getWeChatPublic_appid_secert());
        wechatJo.put("Authorization_state",wp.getAuthorization_state());//授权状态 1：授权成功 2：取消授权 3：授权更新
        wechatJo.put("wechat_num",wp.getWechat_num()==null?"":wp.getWechat_num());
        wechatJo.put("wechat_payapi",wp.getWechat_payapi()==null?"":wp.getWechat_payapi());
        wechatJo.put("wechat_nickname",wp.getWechat_nickname()==null?"":wp.getWechat_nickname());
        wechatJo.put("wechat_headimage",wp.getWechat_headimage()==null?"":wp.getWechat_headimage());
        wechatJo.put("wechat_pay_return_url",wp.getWechat_pay_return_url()==null?"":wp.getWechat_pay_return_url());
        wechatJo.put("wechat_codeurl",wp.getWechat_codeurl()==null?"":wp.getWechat_codeurl());
        wechatJo.put("wechat_merchant_id",wp.getWechat_merchantid()==null?"":wp.getWechat_merchantid());
        wechatJo.put("wechat_pay_pem1",wp.getWechat_pay_pem1()==null?"":wp.getWechat_pay_pem1());
        wechatJo.put("wechat_pay_pem2",wp.getWechat_pay_pem2()==null?"":wp.getWechat_pay_pem2());
        wechatJo.put("wechat_pay_p12",wp.getWechat_pay_p12()==null?"":wp.getWechat_pay_p12());
        wechatJo.put("wechat_openf_common_url",wp.getWechat_openf_common_url()==null?"":wp.getWechat_openf_common_url());

        wechatJo.put(MsgAndCode.RSP_CODE, MsgAndCode.SUCCESS_CODE);
        wechatJo.put(MsgAndCode.RSP_DESC, MsgAndCode.SUCCESS_MESSAGE);

        LogHelper.info("根据主键ID："+source.get("weChatPublic_id")+"，查询的微信配置信息："+wechatJo);
        return wechatJo.toString();
    }

    @Override
    public String getWeChatList(QueryPageRequest request) throws Exception {

        JSONObject wechatJos=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        PageList pageList = new PageList();
        Map<String, Object> map = new HashMap<String, Object>();
        //map.put("weChatPublic_id",request.getMsgSeqId());
        map.put("Authorization_state",request.getMsgStat());
        map.put("wechat_nickname",request.getMsgStat());
        List<WeChatPublic> newUsrMsgLogDos = new ArrayList<WeChatPublic>();
        Paginator paginator = new Paginator();
        int count=wpDao.queryPageCount(map);
        if (0 == request.getPageSize()) {
            paginator.setItemsPerPage(count);
        }else {
            paginator.setItemsPerPage(request.getPageSize());
        }
        paginator.setPage(request.getPageNum());
        paginator.setItems(count);
        pageList.setPaginator(paginator);
        if(paginator.getPage() == request.getPageNum()) {
            if (paginator.getBeginIndex() <= paginator.getItems()) {
                map.put("endRow", paginator.getEndIndex()+"");
                map.put("startRow", paginator.getBeginIndex()+"");
                List<WeChatPublic> weChatPublics = wpDao.queryPageByConditons(map);
                if (null == weChatPublics || weChatPublics.size() == 0) {
                    LogHelper.info("未查询到满足条件的微信消息列表！");
                }
                newUsrMsgLogDos.addAll(weChatPublics);
                pageList.add(newUsrMsgLogDos);
                LogHelper.info("查询满足条件的微信消息数：【count="+paginator.getItems()+"】");
            }
        } else {
            pageList.add(newUsrMsgLogDos);
        }

        if(pageList != null && pageList.size()>0)
        {
            List<WeChatPublic> wcpList= (List<WeChatPublic>) pageList.get(0);
            for(WeChatPublic wcpMap : wcpList)
            {
                JSONObject wechatJo=new JSONObject();
                wechatJo.put("weChatPublic_id",checkIsOrNotNull(wcpMap.getWeChatPublic_id()));
                wechatJo.put("weChatPublic_appid",checkIsOrNotNull(wcpMap.getWeChatPublic_appid()));
                wechatJo.put("weChatPublic_appid_secert",wcpMap.getWeChatPublic_appid_secert());
                wechatJo.put("Authorization_state",checkIsOrNotNull(wcpMap.getAuthorization_state()));//授权状态 1：授权成功 2：取消授权 3：授权更新
                wechatJo.put("wechat_num",checkIsOrNotNull(wcpMap.getWechat_num()));
                wechatJo.put("wechat_payapi",checkIsOrNotNull(wcpMap.getWechat_payapi()));
                wechatJo.put("wechat_nickname",checkIsOrNotNull(wcpMap.getWechat_nickname()));
                wechatJo.put("wechat_headimage",checkIsOrNotNull(wcpMap.getWechat_headimage()));
                wechatJo.put("wechat_pay_return_url",checkIsOrNotNull(wcpMap.getWechat_pay_return_url()));
                wechatJo.put("wechat_merchant_id",checkIsOrNotNull(wcpMap.getWechat_merchantid()));
                wechatJo.put("wechat_codeurl",checkIsOrNotNull(wcpMap.getWechat_codeurl()));
                wechatJo.put("wechat_pay_pem1",checkIsOrNotNull(wcpMap.getWechat_codeurl()));
                wechatJo.put("wechat_pay_pem2",checkIsOrNotNull(wcpMap.getWechat_codeurl()));
                wechatJo.put("wechat_pay_p12",checkIsOrNotNull(wcpMap.getWechat_codeurl()));
                wechatJo.put("wechat_openf_common_url",checkIsOrNotNull(wcpMap.getWechat_openf_common_url()));
                jsonArray.put(wechatJo);
            }

        }
        wechatJos.put(MsgAndCode.RSP_CODE, MsgAndCode.SUCCESS_CODE);
        wechatJos.put(MsgAndCode.RSP_DESC, MsgAndCode.SUCCESS_MESSAGE);
        wechatJos.put("list",jsonArray);
        wechatJos.put("curragePage",request.getPageNum());
        wechatJos.put("pageSize",request.getPageSize());
        wechatJos.put("total",count);

        LogHelper.info("查询微信列表："+wechatJos.toString());
        return wechatJos.toString();
    }

    @Override
    public String updateWeChatInfo(Map<String, Object> source) throws Exception {
        LogHelper.info("修改微信配置信息=="+source);
        if(CommonHelper.isNullOrEmpty(source.get("weChatPublic_id")))
        {
            return returnMissParamMessage("weChatPublic_id");
        }
        JSONObject wechatJo=new JSONObject();
        WeChatPublic weChatPublic=new WeChatPublic();
        weChatPublic.setWeChatPublic_id(Integer.valueOf(source.get("weChatPublic_id").toString()));
        List<WeChatPublic> weChatPublicList= wpDao.getWeChatPublicByParamters(weChatPublic);
        if(weChatPublicList==null || weChatPublicList.size()==0)
        {
            wechatJo.put(MsgAndCode.PARAM_MISSING_CODE, MsgAndCode.CODE_003);
            wechatJo.put(MsgAndCode.PARAM_MISSING_MSG, MsgAndCode.MESSAGE_003);
            return wechatJo.toString();
        }
        WeChatPublic wp=weChatPublicList.get(0);
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_num")))
        {
            wp.setWechat_num(source.get("wechat_num").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_payapi")))
        {
            wp.setWechat_payapi(source.get("wechat_payapi").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("weChatPublic_appid_secert")))
        {
            wp.setWeChatPublic_appid_secert(source.get("weChatPublic_appid_secert").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_pay_return_url")))
        {
            wp.setWechat_pay_return_url(source.get("wechat_pay_return_url").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_codeurl")))
        {
            wp.setWechat_codeurl(source.get("wechat_codeurl").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_merchant_id")))
        {
            wp.setWechat_merchantid(source.get("wechat_merchant_id").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_openf_common_url")))
        {
            wp.setWechat_openf_common_url(source.get("wechat_openf_common_url").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_pay_p12")))
        {
            wp.setWechat_pay_p12(source.get("wechat_pay_p12").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_pay_pem1")))
        {
            wp.setWechat_pay_pem1(source.get("wechat_pay_pem1").toString());
        }
        if(!CommonHelper.isNullOrEmpty(source.get("wechat_pay_pem2")))
        {
            wp.setWechat_pay_pem2(source.get("wechat_pay_pem2").toString());
        }

        int result= wpDao.updateById(wp);
        if(result==0)
        {
            wechatJo.put(MsgAndCode.RSP_CODE, MsgAndCode.FAILE_CODE);
            wechatJo.put(MsgAndCode.RSP_DESC, MsgAndCode.FAILE_MESSAGE);
        }
        else
        {
            wechatJo.put(MsgAndCode.RSP_CODE, MsgAndCode.SUCCESS_CODE);
            wechatJo.put(MsgAndCode.RSP_DESC, MsgAndCode.SUCCESS_MESSAGE);
        }

        return wechatJo.toString();
    }

    /**
     * 校验　非空
     * @param obj
     * @return
     */
    private String checkIsOrNotNull(Object obj){

        if(!CommonHelper.isNullOrEmpty(obj)){
            return obj.toString();
        }
        return "";
    }
    private String returnMissParamMessage(String errorMessage){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put(MsgAndCode.RSP_CODE, MsgAndCode.PARAM_MISSING_CODE);
        node.put(MsgAndCode.RSP_DESC, errorMessage + MsgAndCode.PARAM_MISSING_MSG);
        return node.toString();
    }
}
