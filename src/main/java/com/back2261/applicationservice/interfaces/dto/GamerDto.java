package com.back2261.applicationservice.interfaces.dto;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GamerDto {
    private String username;
    private Integer age;
    private String country;
    private byte[] avatar;
    private Date lastOnlineDate;
}
