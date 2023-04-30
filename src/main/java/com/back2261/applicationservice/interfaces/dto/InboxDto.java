package com.back2261.applicationservice.interfaces.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InboxDto {

    private String userId;
    private String username;
    private String avatar;
    private String lastMessage;
    private String lastMessageTime;
}
