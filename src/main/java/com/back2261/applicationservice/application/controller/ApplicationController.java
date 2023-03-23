package com.back2261.applicationservice.application.controller;

import com.back2261.applicationservice.domain.service.ApplicationService;
import com.back2261.applicationservice.interfaces.response.GamesResponse;
import com.back2261.applicationservice.interfaces.response.KeywordsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_MESSAGE = "Authorization field cannot be empty";

    @GetMapping("/get/keywords")
    public ResponseEntity<KeywordsResponse> getKeywords() {
        return new ResponseEntity<>(applicationService.getKeywords(), HttpStatus.OK);
    }

    @GetMapping("/get/games")
    public ResponseEntity<GamesResponse> getGames() {
        return new ResponseEntity<>(applicationService.getGames(), HttpStatus.OK);
    }
}
