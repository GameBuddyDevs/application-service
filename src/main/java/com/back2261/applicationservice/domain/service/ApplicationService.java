package com.back2261.applicationservice.domain.service;

import com.back2261.applicationservice.interfaces.response.GamesResponse;
import com.back2261.applicationservice.interfaces.response.KeywordsResponse;

public interface ApplicationService {

    KeywordsResponse getKeywords();

    GamesResponse getGames();
}
