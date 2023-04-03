package com.back2261.applicationservice.application.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.back2261.applicationservice.domain.service.DefaultApplicationService;
import com.back2261.applicationservice.infrastructure.entity.Games;
import com.back2261.applicationservice.interfaces.dto.*;
import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.response.FriendsResponse;
import com.back2261.applicationservice.interfaces.response.GamesResponse;
import com.back2261.applicationservice.interfaces.response.KeywordsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
        value = ApplicationController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DefaultApplicationService defaultApplicationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private FriendsResponse friendsResponse;
    private FriendRequest friendRequest;
    private DefaultMessageResponse defaultMessageResponse;

    @BeforeEach
    void setUp() {
        token = "test";
        friendsResponse = new FriendsResponse();
        FriendsResponseBody friendsResponseBody = new FriendsResponseBody();
        List<GamerDto> friends = new ArrayList<>();
        friends.add(new GamerDto());
        friends.add(new GamerDto());
        friendsResponseBody.setFriends(friends);
        friendsResponse.setBody(new BaseBody<>(friendsResponseBody));
        friendRequest = new FriendRequest();
        friendRequest.setUserId("test");
        defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody defaultMessageBody = new DefaultMessageBody("test");
        defaultMessageResponse.setBody(new BaseBody<>(defaultMessageBody));
    }

    @Test
    void testGetKeywords_whenRequested_shouldReturnKeywords() throws Exception {
        KeywordsResponse keywordsResponse = new KeywordsResponse();
        KeywordsResponseBody body = new KeywordsResponseBody();
        List<KeywordsDto> keywords = new ArrayList<>();
        keywords.add(new KeywordsDto());
        keywords.add(new KeywordsDto());
        body.setKeywords(keywords);
        keywordsResponse.setBody(new BaseBody<>(body));

        Mockito.when(defaultApplicationService.getKeywords()).thenReturn(keywordsResponse);

        var request = MockMvcRequestBuilders.get("/application/get/keywords").contentType("application/json");
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        KeywordsResponse responseObj = objectMapper.readValue(responseJson, KeywordsResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, responseObj.getBody().getData().getKeywords().size());
    }

    @Test
    void testGetGames_whenRequested_shouldReturnGames() throws Exception {
        GamesResponse gamesResponse = new GamesResponse();
        GamesResponseBody body = new GamesResponseBody();
        List<Games> games = new ArrayList<>();
        games.add(new Games());
        games.add(new Games());
        body.setGames(games);
        gamesResponse.setBody(new BaseBody<>(body));

        Mockito.when(defaultApplicationService.getGames()).thenReturn(gamesResponse);

        var request = MockMvcRequestBuilders.get("/application/get/games").contentType("application/json");
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        GamesResponse responseObj = objectMapper.readValue(responseJson, GamesResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, responseObj.getBody().getData().getGames().size());
    }

    @Test
    void testGetFriends_whenValidTokenProvided_shouldReturnUserFriends() throws Exception {
        Mockito.when(defaultApplicationService.getFriends(token)).thenReturn(friendsResponse);

        var request = MockMvcRequestBuilders.get("/application/get/friends")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        FriendsResponse responseObj = objectMapper.readValue(responseJson, FriendsResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, responseObj.getBody().getData().getFriends().size());
    }

    @Test
    void testGetWaitingFriends_whenValidTokenProvided_shouldReturnUserFriendRequests() throws Exception {
        Mockito.when(defaultApplicationService.getWaitingFriends(token)).thenReturn(friendsResponse);

        var request = MockMvcRequestBuilders.get("/application/get/requests/friends")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        FriendsResponse responseObj = objectMapper.readValue(responseJson, FriendsResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, responseObj.getBody().getData().getFriends().size());
    }

    @Test
    void testGetBlockedFriends_whenValidTokenProvided_shouldReturnUserBlockedFriends() throws Exception {
        Mockito.when(defaultApplicationService.getBlockedFriends(token)).thenReturn(friendsResponse);

        var request = MockMvcRequestBuilders.get("/application/get/blocked/friends")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        FriendsResponse responseObj = objectMapper.readValue(responseJson, FriendsResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, responseObj.getBody().getData().getFriends().size());
    }

    @Test
    void testAddFriend_whenValidTokenProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultApplicationService.acceptFriend(friendRequest, token))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/application/accept/friend")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(friendRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testRejectFriend_whenValidTokenProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultApplicationService.rejectFriend(friendRequest, token))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/application/reject/friend")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(friendRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testRemoveFriend_whenValidTokenProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultApplicationService.removeFriend(friendRequest, token))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/application/remove/friend")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(friendRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testBlockFriend_whenValidTokenProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultApplicationService.blockUser(friendRequest, token)).thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/application/block/friend")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(friendRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testUnblockFriend_whenValidTokenProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultApplicationService.unblockUser(friendRequest, token))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/application/unblock/friend")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(friendRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testSendFriendRequest_whenValidTokenProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultApplicationService.sendFriendRequest(friendRequest, token))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/application/send/friend")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(friendRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }
}
