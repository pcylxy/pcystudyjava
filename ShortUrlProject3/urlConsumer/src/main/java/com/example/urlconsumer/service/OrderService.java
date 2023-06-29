package com.example.urlconsumer.service;

import com.pcy.UserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class OrderService {
    @Resource
    private RestTemplate restTemplate;
    @DubboReference(version = "1.0")
    private UserService userService;

    public String getOrder(String longUrl) throws IOException {
        return userService.getShortUrl(longUrl);
    }

    public String getOrder2(String shortUrl) throws IOException {
        return userService.getLongUrl(shortUrl);
       /* try {
            URL url = new URL(shortUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false); // 禁用自动重定向
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    statusCode == HttpURLConnection.HTTP_MOVED_PERM) {
                String newUrl = userService.getLongUrl(shortUrl);
                URL newRedirectUrl = new URL(newUrl);
                HttpURLConnection newConnection = (HttpURLConnection) newRedirectUrl.openConnection();
                //当检测到重定向状态码后，我们获取重定向的新URL，并使用该URL创建一个新的HttpURLConnection连接
                int newStatusCode = newConnection.getResponseCode();
                if (newStatusCode == HttpURLConnection.HTTP_OK) {
                    //如果新的状态码为200（HTTP_OK），表示重定向成功，并可以在此处处理重定向后的逻辑。
                    return restTemplate.getForObject(newUrl, String.class);
                } else {//如果新的状态码不是200，表示重定向失败
                    System.out.println("Failed to follow redirect. Status code: " + newStatusCode);
                }
                newConnection.disconnect();
            } else {
                throw new RuntimeException("Request was not redirected.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;*/
    }
}
