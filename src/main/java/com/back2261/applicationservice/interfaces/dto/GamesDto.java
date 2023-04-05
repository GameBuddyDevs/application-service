package com.back2261.applicationservice.interfaces.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GamesDto {
    private String gameId;
    private String gameName;
    private byte[] gameIcon;
    private String category;
    private Float avgVote;
    private String description;
}
