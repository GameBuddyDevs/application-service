package com.back2261.applicationservice.interfaces.dto;

import com.back2261.applicationservice.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DefaultMessageBody extends BaseModel {
    private String message;
}
