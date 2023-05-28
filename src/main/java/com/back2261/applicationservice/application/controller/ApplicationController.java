package com.back2261.applicationservice.application.controller;

import com.back2261.applicationservice.domain.service.ApplicationService;
import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.response.*;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTH_MESSAGE = "Authorization field cannot be empty";

    @GetMapping("/get/user/info/{userId}")
    public ResponseEntity<UserInfoResponse> getUserInfo(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String userId) {
        return new ResponseEntity<>(applicationService.getUserInfo(userId), HttpStatus.OK);
    }

    @GetMapping("/get/keywords")
    public ResponseEntity<KeywordsResponse> getKeywords() {
        return new ResponseEntity<>(applicationService.getKeywords(), HttpStatus.OK);
    }

    @GetMapping("/get/games")
    public ResponseEntity<GamesResponse> getGames() {
        return new ResponseEntity<>(applicationService.getGames(), HttpStatus.OK);
    }

    @GetMapping("/get/game/{gameId}")
    public ResponseEntity<GameResponse> getGame(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String gameId) {
        return new ResponseEntity<>(applicationService.getGame(gameId), HttpStatus.OK);
    }

    @GetMapping("/get/popular/games")
    public ResponseEntity<GamesResponse> getPopularGames() {
        return new ResponseEntity<>(applicationService.getPopularGames(), HttpStatus.OK);
    }

    @GetMapping("/get/avatars")
    public ResponseEntity<AvatarsResponse> getAvatars(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token) {
        return new ResponseEntity<>(applicationService.getAvatars(token.substring(7)), HttpStatus.OK);
    }

    @GetMapping("/get/achievements")
    public ResponseEntity<AchievementResponse> getAchievements(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token) {
        return new ResponseEntity<>(applicationService.getAchievements(token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/collect/achievement/{achievementId}")
    public ResponseEntity<DefaultMessageResponse> collectAchievement(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String achievementId) {
        return new ResponseEntity<>(
                applicationService.collectAchievement(token.substring(7), achievementId), HttpStatus.OK);
    }

    @GetMapping("/get/marketplace")
    public ResponseEntity<MarketplaceResponse> getMarketplace() {
        return new ResponseEntity<>(applicationService.getMarketplace(), HttpStatus.OK);
    }

    @PostMapping("/buy/item/{itemId}")
    public ResponseEntity<DefaultMessageResponse> buyItem(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @PathVariable String itemId) {
        return new ResponseEntity<>(applicationService.buyItem(token.substring(7), itemId), HttpStatus.OK);
    }

    @GetMapping("/get/friends")
    public ResponseEntity<FriendsResponse> getFriends(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token) {
        return new ResponseEntity<>(applicationService.getFriends(token.substring(7)), HttpStatus.OK);
    }

    @GetMapping("/get/requests/friends")
    public ResponseEntity<FriendsResponse> getWaitingFriends(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token) {
        return new ResponseEntity<>(applicationService.getWaitingFriends(token.substring(7)), HttpStatus.OK);
    }

    @GetMapping("/get/blocked/friends")
    public ResponseEntity<FriendsResponse> getBlockedFriends(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token) {
        return new ResponseEntity<>(applicationService.getBlockedFriends(token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/accept/friend")
    public ResponseEntity<DefaultMessageResponse> addFriend(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody FriendRequest addFriendRequest) {
        return new ResponseEntity<>(
                applicationService.acceptFriend(addFriendRequest, token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/reject/friend")
    public ResponseEntity<DefaultMessageResponse> rejectFriend(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody FriendRequest addFriendRequest) {
        return new ResponseEntity<>(
                applicationService.rejectFriend(addFriendRequest, token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/remove/friend")
    public ResponseEntity<DefaultMessageResponse> removeFriend(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody FriendRequest addFriendRequest) {
        return new ResponseEntity<>(
                applicationService.removeFriend(addFriendRequest, token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/block/friend")
    public ResponseEntity<DefaultMessageResponse> blockFriend(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody FriendRequest unblockFriendRequest) {
        return new ResponseEntity<>(
                applicationService.blockUser(unblockFriendRequest, token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/unblock/friend")
    public ResponseEntity<DefaultMessageResponse> unblockFriend(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody FriendRequest blockFriendRequest) {
        return new ResponseEntity<>(
                applicationService.unblockUser(blockFriendRequest, token.substring(7)), HttpStatus.OK);
    }

    @PostMapping("/send/friend")
    public ResponseEntity<DefaultMessageResponse> sendFriendRequest(
            @Valid @RequestHeader(AUTHORIZATION) @NotBlank(message = AUTH_MESSAGE) String token,
            @Valid @RequestBody FriendRequest sendFriendRequest) {
        return new ResponseEntity<>(
                applicationService.sendFriendRequest(sendFriendRequest, token.substring(7)), HttpStatus.OK);
    }
}
