package com.cn.dao;

import com.cn.entity.OpenPlatform;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by cuixiaowei on 2017/2/23.
 */
@Component
public interface VerifyTicketDao {
    /**
     *
     * @return
     */
    public Integer inserVerifyTicketAppid(OpenPlatform opf);

    /**
     * 根据第三方appid获取10分钟ticket
     * @param opf
     * @return
     */
    public List<OpenPlatform> getVerifyTicketByAppid(OpenPlatform opf);

    /**
     * 更新
     * @param opf
     * @return
     */
    public Integer updateVerifyTicket(OpenPlatform opf);
}
