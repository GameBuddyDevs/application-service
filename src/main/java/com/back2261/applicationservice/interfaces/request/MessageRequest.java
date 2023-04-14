package com.back2261.applicationservice.interfaces.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private String sender;
    private String receiver;
    private String message;
    private boolean read;
}
