package com.just.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/callback")
public class VKRestController {

    @Value("${vk.access.key}")
    private String vkAccessKey;

    @Value("${vk.secret}")
    private String secretKey;

    @Value("${vk.confirmation.token}")
    private String confirmationToken;

    @Autowired
    private VKApiService vkApiService;

    @PostMapping
    public String handleVKCallback(@RequestBody Map<String, Object> payload) {
        String type = (String) payload.get("type");
        String secret = (String) payload.get("secret");

        // Проверка секретного ключа
        if (secret == null || !secret.equals(secretKey)) {
            return "error";
        }

        switch (type) {
            case "confirmation":
                // Отправка confirmation_token для подтверждения URL
                return confirmationToken;
            case "message_new":
                Map<String, Object> object = (Map<String, Object>) payload.get("object");
                Map<String, Object> message = (Map<String, Object>) object.get("message");
                String text = (String) message.get("text");
                int userId = (int) message.get("from_id");

                // Обработка нового сообщения
                handleMessage(userId, text);

                return "ok";
            default:
                return "ok";
        }
    }

    private void handleMessage(int userId, String text) {
        // Ваш код для обработки сообщения
        System.out.println("New message from user " + userId + ": " + text);

        try {
            vkApiService.sendMessage(userId, "Ваше сообщение: " + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
