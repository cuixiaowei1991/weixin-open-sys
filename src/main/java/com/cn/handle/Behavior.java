package com.cn.handle;

import com.cn.common.LogHelper;
import com.cn.common.httpsPostMethod;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.cn.common.CommonHelper.amrToMP3;
import static com.cn.common.CommonHelper.saveMediaToDisk;

/**
 * User: cuixiaowei
 * Date: 2018/10/24
 * PackageName: com.cn.handle
 */
public class Behavior {
    public static String handleVoice(String accesstoken,String mediaId,String mediaType) {

        InputStream is = null;

        String url = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + accesstoken + "&media_id=" + mediaId;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);

            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            // 获取文件转化为byte流
            is = http.getInputStream();
            //保存到本地磁盘,返回保存路径
            String mediaPath="";
            if("voice".equals(mediaType))
            {
                mediaPath=amrToMP3(is,"崔小伟.amr");
            }
            else if("image".equals(mediaType))
            {
                mediaPath=saveMediaToDisk(is,"测试崔小伟图片","jpg");
            }else if("video".equals(mediaType))
            {
                mediaPath=saveMediaToDisk(is,"测试崔小伟视频","mp4");
            }

            return mediaPath;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
