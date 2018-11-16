package com.cn.enums;

import org.apache.commons.lang.StringUtils;

/**
 * User: cuixiaowei
 * Date: 2018/10/24
 * PackageName: com.cn.enums
 */
public enum MediaEnum {
    VOICE("语音","voice","voice"),
    IMAGE("图片","image","image"),
    TEXT("文字","text","text"),
    VIDEO("视频","video","video");
    private String name;
    private String mediaType;
    private String mediaCode;

    private MediaEnum(String name, String mediaType, String mediaCode) {
        this.name = name;
        this.mediaType = mediaType;
        this.mediaCode = mediaCode;
    }
    public static boolean isMatched(String mediaCode) {
        MediaEnum[] mediaEnums = MediaEnum.values();
        for (MediaEnum mediaEnum : mediaEnums) {
            if (StringUtils.equals(mediaEnum.getMediaCode(), mediaCode)) {
                return true;
            }
        }
        return false;
    }
    public static MediaEnum getMediaCodeEnum(String mediaCode) {
        MediaEnum[] mediaEnums = MediaEnum.values();
        for (MediaEnum mediaCodeEnum : mediaEnums) {
            if (StringUtils.equals(mediaCodeEnum.getMediaCode(), mediaCode)) {
                return mediaCodeEnum;
            }
        }
        return null;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMediaCode() {
        return mediaCode;
    }

    public void setMediaCode(String mediaCode) {
        this.mediaCode = mediaCode;
    }
}
