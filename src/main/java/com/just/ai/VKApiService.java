package com.just.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service
public class VKApiService {

    @Value("${vk.access.key}")
    private String vkAccessKey;

    @Value("${vk.api.version}")
    private String apiVersion;

    private static final String REQUEST_URL = "https://api.vk.com/method/messages.send";
    private static final Random RANDOM_ID = new Random();


    public void sendMessage(int userId, String message) throws IOException {

        StringBuilder postData = new StringBuilder();
        postData.append("user_id=").append(userId)
                .append("&message=").append(URLEncoder.encode(message, StandardCharsets.UTF_8))
                .append("&random_id=").append(RANDOM_ID.nextInt())
                .append("&access_token=").append(vkAccessKey)
                .append("&v=").append(apiVersion);

        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

        URL url = new URL(REQUEST_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(postDataBytes);
            os.flush();
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("Message sent successfully.");
        } else {
            System.out.println("Failed to send message. Response code: " + responseCode);
        }
    }
}

