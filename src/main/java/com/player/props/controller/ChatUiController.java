package com.player.props.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.player.props.model.request.ChatRequest;
import com.player.props.model.request.ChatRequestBody;
import com.player.props.model.response.ChatResponse;

import lombok.extern.slf4j.Slf4j;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class ChatUiController {

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    private String model = "gpt-3.5-turbo";

    private String apiUrl = "https://api.openai.com/v1/chat/completions";

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequestBody body) {
        // create a request

        log.info("Calling Chat UI");
        ChatRequest request = new ChatRequest(model, body.request, 1, 1.0);

        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            log.error("Unable to fetch chat");
            return null;
        }
        log.info("Ending call for Chat UI");
        // return the first response
        return response;
    }
}
