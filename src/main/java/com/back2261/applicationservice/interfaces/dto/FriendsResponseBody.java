package com.back2261.applicationservice.interfaces.dto;

import io.github.GameBuddyDevs.backendlibrary.base.BaseModel;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendsResponseBody extends BaseModel {

    private List<GamerDto> friends;
}
