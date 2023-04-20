package com.back2261.applicationservice.interfaces.dto;

import io.github.GameBuddyDevs.backendlibrary.base.BaseModel;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketplaceResponseBody extends BaseModel {
    List<SpecialAvatarsDto> specialAvatars;
}
