package com.cn.test.Aes;

import com.cn.common.LogHelper;
import com.cn.common.WeChatCommon.WeiXinDecode;
import com.cn.dao.VerifyTicketDao;
import com.cn.entity.OpenPlatform;
import org.apache.ibatis.session.SqlSession;
import org.junit.BeforeClass;
import org.junit.Test;
import org.json.JSONObject;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by cuixiaowei on 2017/3/6.
 */

@ContextConfiguration("classpath:spring/spring-dao.xml")
@Transactional
public class DecodeTest {
    @Autowired
    public static VerifyTicketDao verifyTicketDao;
    @Test
    public void decode()
    {
        String str="<xml>\n" +
                "    <AppId><![CDATA[wxfa3422f1f3dd9689]]></AppId>\n" +
                "    <Encrypt><![CDATA[PErlFV1OTPmfLvIZ5ZLVgVhgyDlilyoZnDhU7+dKn1IlbduWNoDWfc/sn+LPlNDjLzQ/od3fpQHesVrrX1KvDeZEW3/afOC5BVpKY1v6evrnLGfUufU+Uen2UbABRzfjr6X6zOyxm8gRpYUeQMsq7s39IIgO3GOcegmEvUXsUy4H5YRlSM4LWWrkE5gDZwz2tM1/POxzAdmQJgWB9zcJcVHlwO8MZdLmoUrtUA0ZdVHUHfaON2i2Ghyf8kgWdrWrRlAZrRWUFM7W1fn+mZK2uJuJadBt/MPdncy3cedMeWtzsXQeEQBdhwc4C/WZXzPD6vlFqah5Nsog4xYWDb9SAEeClnAoXeOiWREBZpMgDM7UGmoOvxhO7fh/TzxYiJs40aTldiWJmO8eB3aLGOsw5h5a8y2ps+pD3hiUGBGrASao+a0ZanZAkHWngScFfKuwYAL3T1i51gCZTYyLTnHBCg==]]></Encrypt>\n" +
                "</xml>\n";
        String token="uwU5ANAtbeNfVbu";
        String key="avAnztwetUbepplienNf4ureppixiappwANVbliuwma";
        String app_id="wxfa3422f1f3dd9689";
        String msg_signature="16c32c179c05d9877d9f6625ccc0101da41506f6";
        String timestamp="1488766485";
        String nonce="1990126894";
        String result_json="";
        try {
            result_json= WeiXinDecode.decode(token, key, app_id, msg_signature, timestamp, nonce, str);
        } catch (Exception e) {
            LogHelper.error(e,"解码异常！！！！",false);
            e.printStackTrace();
        }
        System.out.println("解码后："+result_json);
        JSONObject obj=new JSONObject(result_json);
        System.out.println("ComponentVerifyTicket:"+obj.getString("ComponentVerifyTicket"));

    }
    @BeforeClass
    public static void init() {//junit之前init spring
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/spring-*.xml");//这里路径之前没有配对于是一直出错
        verifyTicketDao = (VerifyTicketDao)context.getBean("verifyTicketDao");
        System.out.println("11111111111");
    }

    @Test
    public void insert()
    {
        /*SqlSession session=MyBatisUtil.getSession();
        VerifyTicketDao verifyTicketDao=session.getMapper(VerifyTicketDao.class);*/

        OpenPlatform opf =new OpenPlatform();
        opf.setOpenPlatform_appid("1");
        opf.setOpenPlatform_ticket("2");
        opf.setOpenPlatform_ticket_time("3");
        verifyTicketDao.inserVerifyTicketAppid(opf);
    }
    @Test
    /*
     * 将时间戳转换为时间
     */
    public void stampToDate(){

        //秒
        long second = 1509412775l;

        //转换为所需日期格式
        String dateString = secondToDate(second,"yyyy-MM-dd hh:mm:ss");
        System.out.println(dateString);

    }
    /**
     * 秒转换为指定格式的日期
     * @param second
     * @param patten
     * @return
     */
    private String secondToDate(long second,String patten) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(second * 1000);//转换为毫秒
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(patten);
        String dateString = format.format(date);
        return dateString;
    }

    /**
     * 判断系统是Windows还是linux并且拼接ffmpegPath
     * @return
     */
    @Test
    public void getLinuxOrWindowsFfmpegPath() {
        String ffmpegPath = "";
        String osName = System.getProperties().getProperty("os.name");
        if (osName.toLowerCase().indexOf("linux") >= 0) {
            ffmpegPath = "";
        } else {
            URL url = Thread.currentThread().getContextClassLoader().getResource(".");
            if (url != null) {
                ffmpegPath = url.getFile();
            }
        }
        System.out.println(ffmpegPath);
        //return ffmpegPath;
    }
    @Test
    public void test1() throws ExecutionException, InterruptedException {
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        new Thread(() -> {
            // 模拟执行耗时任务
            System.out.println("task doing...");
            try {
                Thread.sleep(3000);
                int i = 1/0;
            } catch (InterruptedException e) {
                //e.printStackTrace();
                completableFuture.completeExceptionally(e);
            }
            // 告诉completableFuture任务已经完成
            completableFuture.complete("ok");
        }).start();
        // 获取任务结果，如果没有完成会一直阻塞等待
        String result = completableFuture.get();
        System.out.println("计算结果:" + result);
    }
}
