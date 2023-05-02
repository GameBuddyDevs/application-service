package com.back2261.applicationservice.interfaces.dto;

import com.back2261.applicationservice.infrastructure.entity.Achievements;
import io.github.GameBuddyDevs.backendlibrary.base.BaseModel;
import java.util.List;
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
    private List<GamesDto> games;
    private List<KeywordsDto> keywords;
    private List<Achievements> achievements;
    private List<CommunityDto> joinedCommunities;
    private List<GamerDto> friends;
}
