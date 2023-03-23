package com.back2261.applicationservice.domain.service;

import com.back2261.applicationservice.infrastructure.entity.Games;
import com.back2261.applicationservice.infrastructure.entity.Keywords;
import com.back2261.applicationservice.infrastructure.repository.GamesRepository;
import com.back2261.applicationservice.infrastructure.repository.KeywordsRepository;
import com.back2261.applicationservice.interfaces.dto.GamesResponseBody;
import com.back2261.applicationservice.interfaces.dto.KeywordsDto;
import com.back2261.applicationservice.interfaces.dto.KeywordsResponseBody;
import com.back2261.applicationservice.interfaces.response.GamesResponse;
import com.back2261.applicationservice.interfaces.response.KeywordsResponse;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.enums.TransactionCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultApplicationService implements ApplicationService {
    private final KeywordsRepository keywordsRepository;
    private final GamesRepository gamesRepository;

    @Override
    public KeywordsResponse getKeywords() {
        List<KeywordsDto> keywordsDtoList = new ArrayList<>();
        List<Keywords> keywordsList = keywordsRepository.findAll();
        mapKeywordsToDto(keywordsList, keywordsDtoList);
        KeywordsResponse keywordsResponse = new KeywordsResponse();
        KeywordsResponseBody body = new KeywordsResponseBody();
        body.setKeywords(keywordsDtoList);
        keywordsResponse.setBody(new BaseBody<>(body));
        keywordsResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return keywordsResponse;
    }

    @Override
    public GamesResponse getGames() {
        List<Games> gamesList = gamesRepository.findAll();
        GamesResponse gamesResponse = new GamesResponse();
        GamesResponseBody body = new GamesResponseBody();
        body.setGames(gamesList);
        gamesResponse.setBody(new BaseBody<>(body));
        gamesResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return gamesResponse;
    }

    private void mapKeywordsToDto(List<Keywords> keywordsList, List<KeywordsDto> keywordsDtoList) {
        for (Keywords keywords : keywordsList) {
            KeywordsDto keywordsDto = new KeywordsDto();
            BeanUtils.copyProperties(keywords, keywordsDto);
            keywordsDtoList.add(keywordsDto);
        }
    }
}
