package com.yanxm.chat.config;

import org.springframework.context.annotation.Configuration;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */
//@Configuration
public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016101100657116";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCVDOrECwLro1usGtbEMvtI5897o4VVhaizMOyN2sPibutRABDaDtBxy1JnaoEyz7F0rIhk/T5NeKBUP3X6GgqysacRafuUVx6KQwM2YQ3HWgIMiehKPl/iahFVfqFAc8IbfvMFyqCXF06LDAw5jCwuRPJhfqfsOfkA2dimjKLw9x46DdxvyZT7vg5jQ1wi+c5KXiGKB5u319Do/xO+k+q9y17mRE6iihFxx+iPFKjdV5AJlcbIZh8k0G/+esf/XkUf8jAjNO9PQ297N8Nu2D3eJuQgkrTIyBv12nQQMGdl1EpnxUUzeIUGUE1M0/zgSUB6yl/DtR2E6qDct2AJHcm7AgMBAAECggEAfMNlOFinRytkrvmAUJcENJCl4q6MC3xoLCaFvHAki8OhAhp2ALFp1fcFsT87ipbDyhAp068PmbWhLyQ30vQ9hDqkyDTYu+D41W8mnzw6VmaHVIuTf6IajOGQROpMfDDpiC6Jqo1kQ1TlEAszqHAnA0v/P5DPazNTtVyktzT9p4lIRFzxTotQhjiTJ95oRer4TBNtd4gBROILPhGgJY6CrL9oBsxcT04jgBb+d3oaY4UMgOmI0LhJ6ezsZ0Io5B6Q3dqaUh85Y7TW2GMQvj+YzsmJxOR7SD3l0QTxay6azWdYvXTgC8M6PRHj7p/LGM/vH6da234Hq5n2DXSnIScvAQKBgQDj0D41BFMAJZKHL3yhbfFwFHmY65UFmKzEqEhAHOnjvqhTVoFiYU9hognmWXCRGht2/5zkTteQe5S96vrNphTszpQggIKtzaIQQu4Qck258BEf/VYmZOdmvPWyVsddxkg1O4YEqgvrpnuuar80ZVIKx7B5byQ9amRxbkV34wz/wQKBgQCnfe/rnEXbb1weny9FCsofvRXQpeN8erYVcgk4FD6hF0zV53RJHLwA7zrnGpAQfmx9gspGp/TRa5yQL7+0SIIKHPRomJmUypidZrPGX+244n3Y5ufY7lIdfQNSJTooNUKaEtzTnZP1CMBVvvFpYbSLCO1LRi3pAlpZUk1WMBjoewKBgQDJU1Y2Sj0DqzCkEX5Ft0CgOSgA0WjgCj3ciY9YQXUdVHezNoafJ7ocYTP4guAtC3JsJpdDWL2+LMiVUh0VMonZgPJ9CrTx3gEz98IlfK5d/N9Vcu/4fEHXlHRZh8EmiT109caJRrHELlutFx8kscngRvsZRfQz5lsft0DjIx9wwQKBgAa+lH2pUkrOxifZgx7Fm3QBujInq+xkSJa+e86NSiRUetyWMGGNTai/+WA6+pgfbyDytlB4DOyt56UKty58SPg9SD45/t0190VHUG/phvoN7MFiMu3SUu4rDYouQ5RA6Mipf8jprH6Odqg9Qx1aONnsZxnD3q9mT9sO7FxPHYGnAoGAZ7gFL6k/ii10Plrr2n8cX0UO7lLzUUCfpF2XtYHLYdVXDpBEMMUSxHhDUQD7UTIULEt7OxVQoaYYscC156SZUpXYDpYhIJ4K7v9aRnSdXXDcyWzbUT1VX9vRnHJB+5f58NO1gRP3PcdPgzcyAz9OSNgpK4WUg0XXYiSnavmLPR4=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlCOnyxD0SvlnnB2qOzTOFmX+ukxJMtn3UpEwAzE32l7VXPCWhjLrRZRGAKTFcTceoG0HohVPMQiFMBp5d26o5FAn3mHR24UNn55AaEnogD9MNxX9iPF4xSXLvTZnOHobY1J+kiMOLRz2L2YZ43GKqjDz1dt/ZDFNTk/YHARB3oQ2qgHUICx+XhTQzPlIGRBHRqzH6IQac2cCvb/uYmNG8297kXOk/VyvYBFyYV5XIPx+Uvcp5ZkcZFDMMh6owHXiFPuFE+PkDAWGtK4qCOUyQ8yS4XWvT1TbFchSVInsdS6WrVHUNI02NGeZzYpKqPsjOxgvEhElfS7pUhbUrE3bdQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://127.0.0.1:8080/";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://127.0.0.1:8080/returnPay/aliReturnPay";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 日志
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     *
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis() + ".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

