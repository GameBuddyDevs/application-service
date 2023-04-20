package com.back2261.applicationservice.interfaces.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationDto {
    private String sender;
    private String message;
    private String date;
}
