package com.back2261.applicationservice.interfaces.dto;

import com.back2261.applicationservice.base.BaseModel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeywordsResponseBody extends BaseModel {

    List<KeywordsDto> keywords;
}
