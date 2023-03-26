package com.back2261.applicationservice.interfaces.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendRequest {
    @NotBlank(message = "User ID cannot be empty")
    private String userId;
}
