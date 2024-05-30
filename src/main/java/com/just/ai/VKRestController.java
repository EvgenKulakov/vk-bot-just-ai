package com.just.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
public class VKRestController {

    @Value("${vk.secret}")
    private String secretKey;

    @Autowired
    private VKApiService vkApiService;

    private final Logger log = LoggerFactory.getLogger(VKRestController.class);


    @PostMapping("/")
    public String handleVKCallback(@RequestBody Map<String, Object> payload) {
        String type = (String) payload.get("type");
        String secret = (String) payload.get("secret");

        if (secret == null || !secret.equals(secretKey)) {
            log.error("Incorrect secret key: {}", secret);
            return "error";
        }

        switch (type) {
            case "confirmation":
                String code = vkApiService.getConfirmationCode();
                log.info("Request for server confirmation. Verification code sent: {}", code);
                return code;
            case "message_new":
                Map<String, Object> object = (Map<String, Object>) payload.get("object");
                Map<String, Object> message = (Map<String, Object>) object.get("message");
                String text = (String) message.get("text");
                int userId = (int) message.get("from_id");

                handleMessage(userId, text);

                return "ok";
            default:
                return "ok";
        }
    }

    private void handleMessage(int userId, String text) {

        log.info("New message from user {}: {}", userId, text);

        try {
            vkApiService.sendMessage(userId, "Вы сказали: " + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
