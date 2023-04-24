package com.back2261.applicationservice.interfaces.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AchievementsDto {
    private String id;
    private String achievementName;
    private Integer value;
    private String description;
    private Boolean isCollected;
    private Boolean isEarned;
}
