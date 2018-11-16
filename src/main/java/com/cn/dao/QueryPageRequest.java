package com.cn.dao;

import java.io.Serializable;

/**
 * User: cuixiaowei
 * Date: 2018/10/19
 * PackageName: com.cn.dao
 */
public class QueryPageRequest implements Serializable {
    /**  */
    private static final long serialVersionUID = 218349038743509231L;

    private String beginMsgDate;

    private String endMsgDate;

    private String custId;

    private String msgSeqId;

    private String maxSeqId;

    private String msgStat;

    private String msgDirct;

    private String msgSource;

    private String nickname;

    private int pageSize;

    private int pageNum;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBeginMsgDate() {
        return beginMsgDate;
    }

    public void setBeginMsgDate(String beginMsgDate) {
        this.beginMsgDate = beginMsgDate;
    }

    public String getEndMsgDate() {
        return endMsgDate;
    }

    public void setEndMsgDate(String endMsgDate) {
        this.endMsgDate = endMsgDate;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getMsgSeqId() {
        return msgSeqId;
    }

    public void setMsgSeqId(String msgSeqId) {
        this.msgSeqId = msgSeqId;
    }

    public String getMaxSeqId() {
        return maxSeqId;
    }

    public void setMaxSeqId(String maxSeqId) {
        this.maxSeqId = maxSeqId;
    }

    public String getMsgStat() {
        return msgStat;
    }

    public void setMsgStat(String msgStat) {
        this.msgStat = msgStat;
    }

    public String getMsgDirct() {
        return msgDirct;
    }

    public void setMsgDirct(String msgDirct) {
        this.msgDirct = msgDirct;
    }

    public String getMsgSource() {
        return msgSource;
    }

    public void setMsgSource(String msgSource) {
        this.msgSource = msgSource;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
