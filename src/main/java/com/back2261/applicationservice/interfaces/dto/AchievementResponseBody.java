package com.back2261.applicationservice.interfaces.dto;

import com.back2261.applicationservice.infrastructure.entity.Achievements;
import io.github.GameBuddyDevs.backendlibrary.base.BaseModel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AchievementResponseBody extends BaseModel {
    private List<Achievements> achievementsList;
    private List<Achievements> finishedAchievementsList;
    private List<Achievements> collectedAchievementsList;
}
