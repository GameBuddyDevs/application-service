package com.back2261.applicationservice.application.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.back2261.applicationservice.domain.service.DefaultApplicationService;
import com.back2261.applicationservice.interfaces.dto.*;
import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.request.MessageRequest;
import com.back2261.applicationservice.interfaces.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.ArrayList;
import java.util.Date;
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
        token = "3745290384765934782659238q475";
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
    void testGetUserInfo_whenRequested_shouldReturnUsersInfo() throws Exception {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        UserInfoResponseBody body = new UserInfoResponseBody();
        body.setCountry("test");
        body.setGender("M");
        body.setAvatar("test");
        body.setCoin(100);
        body.setAge("18");
        body.setEmail("test");
        body.setUsername("test");
        userInfoResponse.setBody(new BaseBody<>(body));

        Mockito.when(defaultApplicationService.getUserInfo(Mockito.anyString())).thenReturn(userInfoResponse);

        var request = MockMvcRequestBuilders.get("/application/get/user/info")
                .contentType("application/json")
                .header("Authorization", token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        UserInfoResponse responseObj = objectMapper.readValue(responseJson, UserInfoResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals("test", responseObj.getBody().getData().getUsername());
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
        List<GamesDto> games = new ArrayList<>();
        GamesDto game = new GamesDto();
        game.setGameId("test");
        game.setAvgVote(7.7F);
        game.setGameName("test");
        game.setCategory("test");
        game.setGameIcon(new byte[0]);
        game.setDescription("test");
        games.add(game);
        games.add(new GamesDto());
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
    void testGetAvatars_whenValidTokenProvided_shouldReturnFreeAvatarsAndOwnedAvatars() throws Exception {
        AvatarsResponse avatarsResponse = new AvatarsResponse();
        AvatarsResponseBody body = new AvatarsResponseBody();
        List<AvatarsDto> avatars = new ArrayList<>();
        AvatarsDto avatar = new AvatarsDto();
        avatar.setImage("test");
        avatar.setId("test");
        avatars.add(avatar);
        avatars.add(new AvatarsDto());
        body.setAvatars(avatars);
        avatarsResponse.setBody(new BaseBody<>(body));

        Mockito.when(defaultApplicationService.getAvatars(token)).thenReturn(avatarsResponse);

        var request = MockMvcRequestBuilders.get("/application/get/avatars")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        AvatarsResponse responseObj = objectMapper.readValue(responseJson, AvatarsResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, responseObj.getBody().getData().getAvatars().size());
    }

    @Test
    void testGetAchievements_whenValidTokenProvided_shouldReturnUserAchievements() throws Exception {
        AchievementResponse achievementResponse = new AchievementResponse();
        AchievementResponseBody body = new AchievementResponseBody();
        List<AchievementsDto> achievements = new ArrayList<>();
        achievements.add(new AchievementsDto());
        achievements.add(new AchievementsDto());
        body.setAchievementsList(achievements);
        achievementResponse.setBody(new BaseBody<>(body));

        Mockito.when(defaultApplicationService.getAchievements(token)).thenReturn(achievementResponse);

        var request = MockMvcRequestBuilders.get("/application/get/achievements")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        AchievementResponse responseObj = objectMapper.readValue(responseJson, AchievementResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(2, responseObj.getBody().getData().getAchievementsList().size());
    }

    @Test
    void testCollectAchievement_whenValidIdProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultApplicationService.collectAchievement(token, "test"))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/application/collect/achievement/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testMarketPlace_whenRequested_shouldReturnMarketPlaceItems() throws Exception {
        MarketplaceResponse marketplaceResponse = new MarketplaceResponse();
        MarketplaceResponseBody body = new MarketplaceResponseBody();
        List<SpecialAvatarsDto> marketplace = new ArrayList<>();
        SpecialAvatarsDto item = new SpecialAvatarsDto();
        item.setId("test");
        item.setImage("test");
        item.setPrice("100");
        marketplace.add(item);
        body.setSpecialAvatars(marketplace);
        marketplaceResponse.setBody(new BaseBody<>(body));

        Mockito.when(defaultApplicationService.getMarketplace()).thenReturn(marketplaceResponse);

        var request = MockMvcRequestBuilders.get("/application/get/marketplace").contentType("application/json");
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String responseJson = response.getResponse().getContentAsString();

        MarketplaceResponse responseObj = objectMapper.readValue(responseJson, MarketplaceResponse.class);
        assertEquals(200, response.getResponse().getStatus());
        assertEquals(1, responseObj.getBody().getData().getSpecialAvatars().size());
    }

    @Test
    void testBuyItem_whenValidIdAndTokenProvided_shouldReturnSuccessMessage() throws Exception {
        Mockito.when(defaultApplicationService.buyItem(token, "test")).thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/application/buy/item/test")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
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

    @Test
    void testSaveMessageToMongo_whenValidRequestProvided_shouldReturnSuccessMessage() throws Exception {
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setMessage("test");
        messageRequest.setReceiverId("test");
        messageRequest.setRead(false);

        Mockito.when(defaultApplicationService.saveMessageToMongo(token, messageRequest))
                .thenReturn(defaultMessageResponse);

        var request = MockMvcRequestBuilders.post("/application/save/message")
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(messageRequest));
        var response = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(200, response.getResponse().getStatus());
    }

    @Test
    void testGetMessages_whenValidIdProvided_shouldReturnMessageHistory() throws Exception {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test");
        ConversationResponse conversationResponse = new ConversationResponse();
        ConversationResponseBody body = new ConversationResponseBody();
        List<ConversationDto> conversationDtos = new ArrayList<>();
        ConversationDto conversationDto = new ConversationDto();
        conversationDto.setMessage("test");
        conversationDto.setDate(String.valueOf(new Date()));
        conversationDto.setSender("test");
        conversationDtos.add(conversationDto);
        conversationDtos.add(conversationDto);
        body.setConversations(conversationDtos);
        conversationResponse.setBody(new BaseBody<>(body));

        Mockito.when(defaultApplicationService.getUserConversation(token, friendRequest))
                .thenReturn(conversationResponse);

        var request = MockMvcRequestBuilders.get("/application/get/messages")
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
