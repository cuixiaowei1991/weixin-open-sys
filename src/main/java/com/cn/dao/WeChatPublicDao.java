package com.cn.dao;

import com.cn.entity.WeChatPublic;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by cuixiaowei on 2017/2/24.
 */
public interface WeChatPublicDao {
    /**
     * 根据第三方ID和公众号appid获取信息
     * @param
     * @param
     * @return
     */
    public List<WeChatPublic> getWeChatPublicByParamters(WeChatPublic weChatPublic);

    //public String saveOrUpdateWeChatPublic(WeChatPublic wp);

    public int updateById(WeChatPublic wp);

    public int insert(WeChatPublic wp);

    /**
     * 总数
     * @param weChatPublic
     * @return
     * @throws Exception
     */
    public int count(WeChatPublic weChatPublic) throws Exception;


    public int queryPageCount(@Param("map")Map<String, Object> map);

    public List<WeChatPublic> queryPageByConditons(@Param("map")Map<String, Object> map);

}
