package com.back2261.applicationservice.interfaces.dto;

import io.github.GameBuddyDevs.backendlibrary.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResponseBody extends BaseModel {

    private GamesDto gameData;
}
