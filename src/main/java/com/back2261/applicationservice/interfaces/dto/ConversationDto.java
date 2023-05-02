package com.back2261.applicationservice.interfaces.dto;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConversationDto {
    private String sender;
    private String message;
    private Date date;
}
