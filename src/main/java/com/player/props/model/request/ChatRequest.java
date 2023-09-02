package com.player.props.model.request;

import java.util.ArrayList;
import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequest {

    private String model;

    private List<Message> messages;

    private int n;

    private double temperature;

    public ChatRequest(String model, String prompt, int n, double temperature) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", prompt));
        this.n = n;
        this.temperature = temperature;
    }
}
