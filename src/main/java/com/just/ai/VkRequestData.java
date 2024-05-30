package com.just.ai;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class VkRequestData {

    private final int userId;
    private final String message;
    private final int randomId;
    private final String accessToken;
    private final String apiVersion;

    public VkRequestData(int userId, String message, int randomId, String accessToken, String apiVersion) {
        this.userId = userId;
        this.message = message;
        this.randomId = randomId;
        this.accessToken = accessToken;
        this.apiVersion = apiVersion;
    }

    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public int getRandomId() {
        return randomId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("user_id=").append(userId)
                .append("&message=").append(URLEncoder.encode(message, StandardCharsets.UTF_8))
                .append("&random_id=").append(randomId)
                .append("&access_token=").append(accessToken)
                .append("&v=").append(apiVersion);

        return stringBuilder.toString();
    }

    public byte[] toBytes() {
        return toString().getBytes(StandardCharsets.UTF_8);
    }
}
