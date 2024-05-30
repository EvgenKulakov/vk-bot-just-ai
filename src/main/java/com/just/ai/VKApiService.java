package com.just.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;

@Service
public class VKApiService {

    @Value("${vk.access.key}")
    private String vkAccessKey;

    @Value("${vk.api.version}")
    private String apiVersion;

    @Value("${vk.group.id}")
    private String groupId;

    private static final String REQUEST_URL = "https://api.vk.com/method/messages.send";
    private static final Random RANDOM_ID = new Random();
    private final Logger log = LoggerFactory.getLogger(VKApiService.class);


    public void sendMessage(int userId, String message) throws IOException {

        VkRequestData vkRequestData = new VkRequestData(
                userId,
                message,
                RANDOM_ID.nextInt(),
                vkAccessKey,
                apiVersion
        );

        byte[] postDataBytes = vkRequestData.toBytes();

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
            log.info("Message sent successfully.");
        } else {
            log.error("Failed to send message. Response code: {}", responseCode);
        }
    }

    public String getConfirmationCode() {
        String url = String.format("https://api.vk.com/method/groups.getCallbackConfirmationCode?group_id=%s&access_token=%s&v=%s",
                groupId, vkAccessKey, apiVersion);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("response")) {
            Map<String, String> responseBody = (Map<String, String>) response.get("response");
            return responseBody.get("code");
        } else {
            log.error("Error when receiving verification code. Check your vk.access.key");
            return null;
        }
    }
}

