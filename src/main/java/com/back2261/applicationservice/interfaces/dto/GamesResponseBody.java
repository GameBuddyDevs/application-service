package com.back2261.applicationservice.interfaces.dto;

import com.back2261.applicationservice.base.BaseModel;
import com.back2261.applicationservice.infrastructure.entity.Games;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GamesResponseBody extends BaseModel {

    List<Games> games;
}
