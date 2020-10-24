package com.yanxm.chat.utils;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ElementsUtil {
	/**
	 * 解析网页元素
	 * @throws InterruptedException 
	*/
	public static Document getDom(String URL) throws InterruptedException{
		try {
			Connection conn = Jsoup.connect(URL).timeout(50000);
			conn.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			conn.header("Accept-Encoding", "gzip, deflate, sdch");
			conn.header("Accept-Language", "zh-CN,zh;q=0.8");
			conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
			Document document = conn.get();
			 
			return document;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
	
	
	/**
	 * 下载
	 * @param url
	 * @param path
	 * @param name
	 */
	public static void download(String url , String path , String name) {
		//输入输入流
		FileOutputStream outputStream  = null;
		InputStream inputStream = null;
		BufferedInputStream bis = null;
		HttpURLConnection connection = null;
		 try {
			  //创建链接
			  URL bturl = new URL(url);
		 connection = (HttpURLConnection) bturl.openConnection();
			 connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		 
			  //获取输入流 
			  inputStream = connection.getInputStream();
			  //将输入流信息放入缓冲提升读写速度 
			  bis = new BufferedInputStream(inputStream);
			  
			  //获取字节数;
			  byte[] buf = new byte[1024]; 
			  
			  //生产文件
			  outputStream = new FileOutputStream(path+name); 
			  int size = 0;
			  while ((size =bis.read(buf)) != -1) {
				  outputStream.write(buf, 0, size);
			  } 
			  	  //刷新文件流
			  	  outputStream.flush(); 
			  	  System.out.println("爬取       " + name +"        成功 ！！");
			}catch (MalformedURLException e) {
				  e.printStackTrace(); 
			}catch (IOException e) {
				  e.printStackTrace(); 
			}finally { 
				try {
					  if(outputStream != null){ 
						  outputStream.close(); 
					  } 
					  if(bis != null) {
						  bis.close(); 
					  } 
					  if(inputStream != null) {
						  inputStream.close(); 
					  }
				 } catch(Exception e) {
					 e.printStackTrace(); 
				 }
				connection.disconnect();
			}
	}
	
	/**
	 * 获取WebDriver
	 * @return
	 */
	public  static WebDriver getPhantomjsDriver() {
		System.getProperties().setProperty("phantomjs.binary.path", "D:/phantomjs.exe"); //开启webDriver进程
		WebDriver webDriver = new  PhantomJSDriver();
		webDriver.manage().timeouts().pageLoadTimeout(10,TimeUnit.SECONDS);
		webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return webDriver;
	}

	/**
	 * 获取WebDriver
	 * @return
	 */
	public  static WebDriver getChromeDriver() {
		System.getProperties().setProperty("webdriver.chrome.driver", "D:/chromedriver.exe"); //开启webDriver进程
		WebDriver webDriver = new  ChromeDriver();
		webDriver.manage().timeouts().pageLoadTimeout(10,TimeUnit.SECONDS);
		webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		return webDriver;
	}

	/**
	 * 获取WebDriver
	 * @return
	 */
	public  static WebDriver getFirefoxDriver() {
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		System.setProperty("webdriver.gecko.driver", "D:/geckodriver.exe");
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
		return driver;
	}




	/**
	 * 关闭webDriver
	 * @param webDriver
	 */
	public static void closeDriver(WebDriver webDriver) {
		 webDriver.close();
		 webDriver.quit();
		 String command = "taskkill /f /im chromedriver.exe";
		 try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}





	 /**
		* 绕过验证 , 返回httpclient
	 * @return
		* @return
		* @throws NoSuchAlgorithmException
		* @throws KeyManagementException
	 * @throws IOException
	 * @throws ParseException
		*/
		public static CloseableHttpClient createIgnoreVerifySSLReturnClient() throws NoSuchAlgorithmException, KeyManagementException, ParseException, IOException {
			 SSLContext sslcontext = SSLContext.getInstance("TLSv1.2"); //这边指定tls版本

			// 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
			X509TrustManager trustManager = new X509TrustManager() {
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
						String paramString) throws CertificateException {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
						String paramString) throws CertificateException {
				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			sslcontext.init(null, new TrustManager[] { trustManager }, null);

			//设置协议http和https对应的处理socket链接工厂的对象
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslcontext))
				.build();
			PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			connManager.setMaxTotal(10);
			connManager.setDefaultMaxPerRoute(10);

			//创建自定义的httpclient对象
			CloseableHttpClient client = HttpClients.custom()
					.setConnectionTimeToLive(5, TimeUnit.SECONDS)
					.setConnectionManager(connManager)
					.setConnectionManagerShared(true)
					.build();
			//CloseableHttpClient client = HttpClients.createDefault();
		   return client;
		}

		/**
		 * 返回响应类response
		 */
		public static CloseableHttpResponse getCloseableHttpResponse(CloseableHttpClient client , String url) {
			HttpGet get = new HttpGet(url);
			//设置头部
			get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
			get.setHeader("Accept", "text/html,application/json,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8;charset=UTF-8");
			get.setHeader("Accept-Encoding", "gzip, deflate, sdch");
			get.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
			try {
				return client.execute(get);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}



		public static  void download(HttpEntity httpEntity , String pathName ) {
			BufferedInputStream bis = null;
			FileOutputStream outputStream = null;
			 try {
				 bis = new BufferedInputStream(httpEntity.getContent());
				  //获取字节数;
				  byte[] buf = new byte[1024];
				  //生产文件
				//  String name = UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
				  //path = "D:\\aaa\\aaa\\"
				  outputStream = new FileOutputStream(pathName);
				  int size = 0;
				  while ((size =bis.read(buf)) != -1) {
					  outputStream.write(buf, 0, size);
				  }
					  //刷新文件流
					  outputStream.flush();
					  System.out.println("爬取       " + pathName +"        成功 ！！");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					  if(outputStream != null){
						  outputStream.close();
					  }
					  if(bis != null) {
						  bis.close();
					  }
				 } catch(Exception e) {
					 e.printStackTrace();
				 }

			}
		}

	  public static void closeClient(CloseableHttpClient client ) {
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
	  /**
	   * 获取真实IP
	   * @param request 请求体
	   * @return 真实IP
	   */
		public static String getClientIpAddr(HttpServletRequest request) {
			String ip = request.getHeader("x-forwarded-for");
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
			return ip;
		}

		/**
		* Gets the real ips.
		*
		* @return the real ips
		*/
		public static List<String> getRealIps() {
			List<String> ips = new ArrayList<String>();
			String localip = null;// 本地IP，如果没有配置外网IP则返回它
			String netip = null;// 外网IP
			Enumeration<NetworkInterface> netInterfaces;
			try {
				netInterfaces = NetworkInterface.getNetworkInterfaces();
			} catch (SocketException e) {
				return null;
			}
			InetAddress ip = null;
			boolean finded = false;// 是否找到外网IP
			while (netInterfaces.hasMoreElements() && !finded) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					ip = address.nextElement();
					if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
						netip = ip.getHostAddress();
						ips.add(netip);
						finded = true;
						break;
					} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
						localip = ip.getHostAddress();
						ips.add(localip);
					}
				}
			}
			return ips;
		}

		/**
		* Gets the real ip.
		*
		* @return the real ip
		*/
		public static String getRealIp() {
			return getRealIps().iterator().next();
		}
		
		
		public static HttpPost addHeader(HttpPost httppost) {
			httppost.addHeader(":method", "POST");
			httppost.addHeader(":scheme" , "https");
			httppost.addHeader("accept" , "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
			httppost.addHeader("accept-encoding" , "gzip, deflate, br");
			httppost.addHeader("accept-language","zh-CN,zh;q=0.9,en;q=0.8");
			httppost.addHeader("cache-control","no-cache");
			httppost.addHeader("content-type","multipart/form-data; boundary=----WebKitFormBoundaryjpUUDoA3S78hJ3ZB");
			httppost.addHeader("pragma","no-cache");
			httppost.addHeader("sec-fetch-dest","iframe");
			httppost.addHeader("sec-fetch-mode","navigate");
			httppost.addHeader("sec-fetch-site","cross-site");
			httppost.addHeader("sec-fetch-user","?1");
			httppost.addHeader("upgrade-insecure-requests","1");
			httppost.addHeader("user-agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36");
			return httppost;
		}
		
		public static void threadDownload(ExecutorService cachedThreadPool , final CloseableHttpClient client , final String url , final String pathName) {
			cachedThreadPool.execute(new Runnable() {
				public void run() {
					try {
						CloseableHttpResponse  execute = client.execute(new HttpGet(url));
						ElementsUtil.download(execute.getEntity(), pathName);
						execute.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			});
		}
		
}
