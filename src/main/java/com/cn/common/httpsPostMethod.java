package com.cn.common;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import sun.net.www.protocol.https.Handler;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

//import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by cuixiaowei on 2017/2/27.
 */
public class httpsPostMethod
{
    /**
     * httpPost请求
     *
     * @param url
     * @param param
     */
    public static String sendHttpsPost(String url, String param,
                                       String explanatory) {

        String str_return = null;
        HttpsURLConnection conn = null;
        LogHelper.info(explanatory + "  请求微信https开始:");
        SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
                    new java.security.SecureRandom());
            URL console = new URL(null,url,new Handler());
            conn = (HttpsURLConnection) console.openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            if("advert_1473755156372".equals(explanatory))
            {
                conn.setRequestProperty("distributionid","1473755156372");
            }
            conn.setRequestProperty("sign",explanatory.substring(4));
            conn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            if (param != null)
                out.write(param.getBytes("UTF-8"));

            out.flush();
            out.close();

            //
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            int code = conn.getResponseCode();
            if (HttpsURLConnection.HTTP_OK == code) {
                String temp = in.readLine();

                while (temp != null) {
                    if (str_return != null)
                        str_return += temp;
                    else
                        str_return = temp;
                    temp = in.readLine();
                }
            } else {
                LogHelper.info(explanatory + "  出现异常,HTTP错误代码:" + code);
            }
            conn.disconnect();

        } catch (NoSuchAlgorithmException e) {
            str_return = "error";
            LogHelper.error(e, explanatory + "  出现异常", false);
            e.printStackTrace();
        } catch (KeyManagementException e) {
            str_return = "error";
            LogHelper.error(e, explanatory + "  出现异常", false);
            e.printStackTrace();
        } catch (MalformedURLException e) {
            str_return = "error";
            LogHelper.error(e, explanatory + "  出现异常", false);
            e.printStackTrace();
        } catch (IOException e) {
            str_return = "error";
            LogHelper.error(e, explanatory + "  出现异常", false);
            e.printStackTrace();
        }
        LogHelper.info("返回:" + str_return + "@@");
        return str_return;
    }

    public static String httpRequest(String urlStr, String content, String requestMethod)  {
        StringBuilder response = new StringBuilder();
        try {
            LogHelper.info("====================================");
            URL url = new URL(urlStr);
            LogHelper.info("=+++++++++++++++++++++++++++++++++++");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(requestMethod);
            connection.setUseCaches(false);
            connection.setConnectTimeout(1000 * 10);//5秒
            connection.setReadTimeout(1000 * 60);//1分钟
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-type", "application/json");
            connection.connect();
            Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            if (content != null && !"".equals(content)) {
                writer.write(content);
            }
            writer.flush();
            writer.close();
            InputStream in = null;
            if (connection.getResponseCode() >= 400) {
                in = connection.getErrorStream();
            } else {
                in = connection.getInputStream();
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

            String tmp;
            while ((tmp = bufferedReader.readLine()) != null) {
                response.append(tmp);
            }
            if (in != null) {
                in.close();
            }
            bufferedReader.close();
            connection.disconnect();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return response.toString();
    }



    /**
     *  httpPost访问请求
     *
     * @param marked
     * @param jsonStr
     * @return
     */
    public static String postHttp(String marked, String jsonStr, String url) {
        String responseMsg = "";
        // 1.构造HttpClient的实例
        HttpClient httpClient = new HttpClient();

        httpClient.getParams().setContentCharset("utf-8");
        String urlServer = url;
        // 2.构造PostMethod的实例

        PostMethod postMethod = new PostMethod(urlServer);
        postMethod.addParameter("marked", marked);
        postMethod.addParameter("jsonStr", jsonStr);
        try {
            // 4.执行postMethod,调用http接口
            httpClient.executeMethod(postMethod);// 200
            // 5.读取内容
            responseMsg = postMethod.getResponseBodyAsString().trim();
            // responseMsg = new
            // String(responseMsg.getBytes("iso-8859-1"),"UTF-8");
            // 6.处理返回的内容
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 7.释放连接
            postMethod.releaseConnection();
        }
        return responseMsg;
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    private static class TrustAnyTrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

}
