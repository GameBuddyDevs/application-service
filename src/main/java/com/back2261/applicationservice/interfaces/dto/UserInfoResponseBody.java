package com.back2261.applicationservice.interfaces.dto;

import io.github.GameBuddyDevs.backendlibrary.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponseBody extends BaseModel {
    private String username;
    private String email;
    private String age;
    private String country;
    private String avatar;
    private String gender;
    private Integer coin;
}
