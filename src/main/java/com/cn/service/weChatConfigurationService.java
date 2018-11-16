package com.cn.service;

import com.cn.dao.QueryPageRequest;

import java.util.Map;

/**
 * Created by cuixiaowei on 2017/6/20.
 */
public interface weChatConfigurationService {
    /**
     * 获取微信配置信息
     * @param source
     * @return
     * @throws Exception
     */
    public String getWeChatInfo(Map<String, Object> source) throws Exception;

    /**
     * 获取微信配置信息列表
     * @param request
     * @return
     * @throws Exception
     */
    public String getWeChatList(QueryPageRequest request) throws Exception;
    /**
     * 更新微信配置信息
     * @param source
     * @return
     * @throws Exception
     */
    public String updateWeChatInfo(Map<String, Object> source) throws Exception;
}
