package com.cn.common;

import com.alipay.api.internal.util.codec.EncoderException;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.apache.commons.lang3.RandomUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.beans.Encoder;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 工具类
 * @author songzhili
 * 2016年6月29日下午1:19:19
 */
public class CommonHelper {
	public static Random random = new Random();

	public static boolean isEmpty(String source) {
		if (source == null || source.length() == 0 || "null".equals(source)) {
			return true;
		}
		return false;
	}

	public static boolean isNullOrEmpty(Object source) {

		if (source == null || source.toString().trim().length() == 0
				|| "null".equals(source)) {
			return true;
		}
		return false;
	}

	/**
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static int obtainSpaceDays(String startTime, String endTime) {
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		Long countOne = 0l;
		try {
			Date dateOne = format.parse(startTime);
			Date dateTwo = format.parse(endTime);
			long timeOne = dateOne.getTime();
			long timeTwo = dateTwo.getTime();
			long count = (timeTwo - timeOne) / (24 * 3600 * 1000);
			countOne = Math.abs(count);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
		return countOne.intValue();
	}

	/**
	 * 获取现在时间
	 *
	 * @return返回长时间格式 yyyy-MM-dd HH:mm:ss
	 */

	public static Date getNowDateShort() {
		Date currentTime = new Date();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		Date dateString1 = null;
		try {
			dateString1 = formatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateString1;
	}

	/**
	 * 将时间转为指定的格式
	 *
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String formatTime(Object date, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String date_e = null;
		try {
			date_e = sdf.format(date);
			return date_e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 返回当前日期的23时59分59秒的日期格式
	 *
	 * @param date
	 * @return
	 */
	public static Date getDayEnd(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	/**
	 * 返回当前日期的0时0分0秒的日期格式
	 *
	 * @param date
	 * @return
	 */
	public static Date getDayBegin(Date date) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	/**
	 * String转Date
	 *
	 * @param dateString 传入的时间类型字符串
	 * @param dateFormat 需要转义的时间类型格式 例如：yyyy-MM-dd
	 * @return
	 */
	public static Date getStringToDate(String dateString, String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = null;
		try {
			date = sdf.parse(dateString);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public static boolean isNumeric(String str) {
		if (isEmpty(str)) {
			return false;
		}
		for (int i = str.length(); --i >= 0; ) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57) {
				return false;
			}
		}
		return true;
	}


	/**
	 * 获取指定位数的随机数(纯数字)
	 *
	 * @param length 随机数的位数
	 * @return String
	 */
	public static String getRandomNum(int length) {
		if (length <= 0) {
			length = 1;
		}
		StringBuilder res = new StringBuilder();
		Random random = new Random();
		int i = 0;
		while (i < length) {
			res.append(random.nextInt(10));
			i++;
		}
		return res.toString();
	}

	/**
	 * getRStr 取得指定位数的随机字符串
	 *
	 * @param myString String[] 指定字符串组
	 * @param length   int 生成随机数的位数
	 * @return String 返回随机字符串
	 */
	public static String getRStr(String[] myString, int length) {
		StringBuffer mystrbuf = new StringBuffer();
		for (int i = 0; i < length; i = i + 1) {
			int r = random.nextInt(myString.length);
			mystrbuf.append(myString[r]);
		}
		return mystrbuf.toString();
	}


	/**
	 * 自动生成Allpay分配给代理商接入key1
	 */
	@Transactional(rollbackForClassName = "Exception")
	public String createkey() {
		String[] myString = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
				"l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
				"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		return getRStr(myString, 32);

	}

	/**
	 * 将空对象转化成空字符串""
	 *
	 * @param object
	 * @return
	 */
	public static String nullToString(Object object) {
		String result = object == null ? "" : object.toString();
		return result;
	}

	public static String nullOrEmptyToString(Object obj) {

		if (!CommonHelper.isNullOrEmpty(obj)) {
			return obj.toString();
		}
		return "";
	}

	public static String map2json(Map<String, Object> source)

	{
		String json = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(source);
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}


	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String obtainRequestBody(HttpServletRequest request) throws IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		StringWriter writer = new StringWriter();
		char[] chars = new char[256];
		int count = 0;
		while ((count = reader.read(chars)) > 0) {
			writer.write(chars, 0, count);
		}
		String toRe = writer.toString();
		writer.flush();
		writer.close();
		return toRe;
	}

	/*
     * 将时间戳转换为时间
     */
	public static String stampToDate(String s) {
		long date = Long.valueOf(s);
		//转换为所需日期格式
		String dateString = secondToDate(date, "yyyy-MM-dd hh:mm:ss");
		return dateString;

	}

	/**
	 * 秒转换为指定格式的日期
	 *
	 * @param second
	 * @param patten
	 * @return
	 */
	private static String secondToDate(long second, String patten) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(second * 1000);//转换为毫秒
		Date date = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat(patten);
		String dateString = format.format(date);
		return dateString;
	}

	/**
	 * 把amr格式的语音转换成MP3
	 *
	 * @param @param sourcePath amr格式文件路径
	 * @param @param targetPath 存放mp3格式文件路径
	 * @return void 返回类型
	 * @throws
	 * @Title: changeToMp3
	 * @Description: TODO(把amr格式的语音转换成MP3)
	 * @author pll
	 */
	/*public static void changeToMp3(String sourcePath, String targetPath) {
		File source = new File(sourcePath);
		File target = new File(targetPath);
		AudioAttributes audio = new AudioAttributes();
		Encoder encoder = new Encoder();

		audio.setCodec("libmp3lame");
		EncodingAttributes attrs = new EncodingAttributes();
		attrs.setFormat("mp3");
		attrs.setAudioAttributes(audio);
		try {
			encoder.encode(source, target, attrs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	/** 
	      *  
	      * 创建文件路径和文件名称
	      */
		private static String getFilePath(String name,String suffix) {
			String baseFolder = "D://testFold/"+File.separator + "media";
			Date nowDate = new Date();
			// yyyy/MM/dd
			String fileFolder = baseFolder + File.separator + formatTime(nowDate,"yyyyMMddhhmmssSSSS");
			File file = new File(fileFolder);
			if (!file.isDirectory()) {
				// 如果目录不存在，则创建目录
				file.mkdirs();
			}
			// 生成新的文件名
			//String fileName = new DateTime(nowDate).toString("yyyyMMddhhmmssSSSS")
			//        + RandomUtils.nextInt(100, 9999) + "." + StringUtils.substringAfterLast(sourceFileName, ".");
			// 生成新的文件名--微信下载的文件格式为amr
			String fileName = formatTime(nowDate,"yyyyMMddhhmmssSSSS")+name+RandomUtils.nextInt(100, 9999) + "." + suffix;
			return fileFolder + File.separator + fileName;
		}
		public static String saveMediaToDisk(InputStream inputStream,String name,String suffix)
		{
			byte[] data = new byte[10240];
			int len = 0;
			FileOutputStream fileOutputStream = null;
			String filepath=getFilePath(name,suffix);
			try {
				fileOutputStream = new FileOutputStream(filepath);
				while ((len = inputStream.read(data)) != -1) {
					fileOutputStream.write(data, 0, len);
				}
				return filepath;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				System.out.println("关闭流。。。。。。。。。。。");
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return null;
		}

	/**
	 * 判断系统是Windows还是linux并且拼接ffmpegPath
	 * @return
	 */
	private static String getLinuxOrWindowsFfmpegPath() {
		String ffmpegPath = "";
		String osName = System.getProperties().getProperty("os.name");
		if (osName.toLowerCase().indexOf("linux") >= 0) {
			ffmpegPath = "";
		} else {
			URL url = Thread.currentThread().getContextClassLoader().getResource("ffmpeg/windows/");
			if (url != null) {
				ffmpegPath = url.getFile();
			}
		}
		return ffmpegPath;
	}
	/**
	 * 将amr文件输入流转为mp3格式
	 * @param inputStream  amr文件的输入流（也可以是其它的文件流）
	 * @param fileName  文件名（包含后缀）
	 * @return
	 */
	public static String amrToMP3(InputStream inputStream, String fileName) {
		String ffmpegPath = getLinuxOrWindowsFfmpegPath();
		Runtime runtime = Runtime.getRuntime();
		try {
			String filePath = copyFile(inputStream, fileName);
			String substring = filePath.substring(0, filePath.lastIndexOf("."));
			String mp3FilePath = substring + ".mp3";

			//执行ffmpeg文件，将amr格式转为mp3
			//filePath ----> amr文件在临时文件夹中的地址
			//mp3FilePath  ----> 转换后的mp3文件地址
			Process p = runtime.exec(ffmpegPath + "ffmpeg -i" + " " +filePath + " " + mp3FilePath);//执行ffmpeg.exe,前面是ffmpeg.exe的地址，中间是需要转换的文件地址，后面是转换后的文件地址。-i是转换方式，意思是可编码解码，mp3编码方式采用的是libmp3lame

			//释放进程
			p.getOutputStream().close();
			p.getInputStream().close();
			p.getErrorStream().close();
			p.waitFor();

			File file = new File(mp3FilePath);
			InputStream fileInputStream = new FileInputStream(file);
			fileInputStream.close();
			//应该在调用该方法的地方关闭该input流（使用完后），并且要删除掉临时文件夹下的相应文件
            File amrFile = new File(filePath);
            //File mp3File = new File(mp3FilePath);
            if (amrFile.exists()) {
                boolean delete = amrFile.delete();
                System.out.println("删除源文件："+delete);
            }
            /*if (mp3File.exists()) {
                boolean delete = mp3File.delete();
                System.out.println("删除mp3文件："+delete);
            }*/
			return mp3FilePath;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			runtime.freeMemory();
		}
		return null;
	}
	/**
	 * 将用户输入的amr音频文件流转为音频文件并存入临时文件夹中
	 * @param inputStream  输入流
	 * @param fileName  文件姓名
	 * @return  amr临时文件存放地址
	 * @throws IOException
	 */
	private static String copyFile(InputStream inputStream, String fileName) throws IOException {
		Properties props = System.getProperties();
		//String filePath = props.getProperty("user.home") + File.separator + "MP3TempFile"; //创建临时目录
		String filePath = "D://testFold/" + File.separator + "media/voice"; //创建临时目录
		File dir = new File(filePath);
		if (!dir.exists()) {
			dir.mkdir();
		}

		String outPutFile = dir + File.separator + fileName;

		OutputStream outputStream = new FileOutputStream(outPutFile);
		int bytesRead;
		byte[] buffer = new byte[8192];
		while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
		outputStream.flush();
		outputStream.close();
		inputStream.close();

		return outPutFile;
	}
	public static void main(String[] args) {
		String aa=getLinuxOrWindowsFfmpegPath();
		System.out.println(aa);
	}
}
