package com.back2261.applicationservice.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import com.back2261.applicationservice.infrastructure.entity.*;
import com.back2261.applicationservice.infrastructure.repository.*;
import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.response.*;
import io.github.GameBuddyDevs.backendlibrary.exception.BusinessException;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DefaultApplicationServiceTest {

    @InjectMocks
    private DefaultApplicationService defaultApplicationService;

    @Mock
    private KeywordsRepository keywordsRepository;

    @Mock
    private GamesRepository gamesRepository;

    @Mock
    private GamerRepository gamerRepository;

    @Mock
    private AvatarsRepository avatarsRepository;

    @Mock
    private AchievementsRepository achievementsRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private NotificationService notificationService;

    private String token;

    @BeforeEach
    void setUp() {
        token = "test";
    }

    @Test
    void testGetUserInfo_whenUserNotFound_ReturnErrorCode103() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test");

        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultApplicationService.getUserInfo(friendRequest));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testGetUserInfo_whenUserAvatarNotFound_ReturnUserInfo() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test");
        Gamer gamer = getGamer();

        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(avatarsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(new Avatars()));

        UserInfoResponse result = defaultApplicationService.getUserInfo(friendRequest);
        assertEquals("test", result.getBody().getData().getUsername());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetUserInfo_whenCalledAndHaveAvatar_ReturnUserInfo() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test");
        Gamer gamer = getGamer();

        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(avatarsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(new Avatars()));

        UserInfoResponse result = defaultApplicationService.getUserInfo(friendRequest);
        assertEquals("test", result.getBody().getData().getUsername());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetKeywords_whenCalled_ReturnKeywords() {
        List<Keywords> keywords = new ArrayList<>();
        keywords.add(new Keywords());
        keywords.add(new Keywords());

        Mockito.when(keywordsRepository.findAll()).thenReturn(keywords);

        KeywordsResponse result = defaultApplicationService.getKeywords();
        assertEquals(2, result.getBody().getData().getKeywords().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetGames_whenCalled_ReturnGames() {
        List<Games> games = new ArrayList<>();
        games.add(new Games());
        games.add(new Games());

        Mockito.when(gamesRepository.findAll()).thenReturn(games);

        GamesResponse result = defaultApplicationService.getGames();
        assertEquals(2, result.getBody().getData().getGames().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetAvatars_whenCalled_ReturnAvatars() {
        Gamer gamer = getGamer();
        Avatars avatars = new Avatars();
        avatars.setId(UUID.randomUUID());
        avatars.setImage("test");
        avatars.setPrice(100);
        avatars.setIsSpecial(true);
        gamer.getBoughtAvatars().add(avatars);
        List<Avatars> avatarsList = new ArrayList<>();
        Avatars avatars2 = new Avatars();
        avatars2.setId(UUID.randomUUID());
        avatars2.setImage("test");
        avatars2.setPrice(0);
        avatars2.setIsSpecial(false);
        avatarsList.add(avatars2);
        avatarsList.add(avatars2);
        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(avatarsRepository.findAllByIsSpecialFalse()).thenReturn(avatarsList);

        AvatarsResponse result = defaultApplicationService.getAvatars(token);
        assertEquals(3, result.getBody().getData().getAvatars().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetAchievements_whenCalled_ReturnAchievements() {
        Gamer gamer = getGamer();
        List<Achievements> achievements = new ArrayList<>();
        Achievements achievement = new Achievements();
        achievement.setAchievementName("test2");
        achievement.setId(UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d8"));
        achievement.setValue(50);
        achievement.setDescription("test2");
        achievements.add(achievement);
        gamer.getGamerEarnedAchievements().add(achievement);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(achievementsRepository.findAll()).thenReturn(achievements);

        AchievementResponse result = defaultApplicationService.getAchievements(token);
        assertEquals(1, result.getBody().getData().getAchievementsList().size());
        assertTrue(result.getBody().getData().getAchievementsList().get(0).getIsEarned());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testCollectAchievement_whenAchievementNotFound_ReturnErrorCode124() {
        Gamer gamer = getGamer();
        String achievementId = String.valueOf(UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d9"));

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(achievementsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.collectAchievement(token, achievementId));
        assertEquals(124, exception.getTransactionCode().getId());
    }

    @Test
    void testCollectAchievement_whenAchievementNotEarned_ReturnErrorCode126() {
        Gamer gamer = getGamer();
        String achievementId = String.valueOf(UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d1"));
        Achievements achievement = getAchievement();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(achievementsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(achievement));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.collectAchievement(token, achievementId));
        assertEquals(126, exception.getTransactionCode().getId());
    }

    @Test
    void testCollectAchievement_whenAchievementAlreadyCollected_ReturnErrorCode125() {
        Gamer gamer = getGamer();
        String achievementId = String.valueOf(UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d1"));
        Achievements achievement = getAchievement();
        gamer.getGamerEarnedAchievements().add(achievement);
        gamer.getGamerCollectedAchievements().add(achievement);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(achievementsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(achievement));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.collectAchievement(token, achievementId));
        assertEquals(125, exception.getTransactionCode().getId());
    }

    @Test
    void testCollectAchievement_whenCalledWithValidAchievement_ReturnSuccess() {
        Gamer gamer = getGamer();
        String achievementId = String.valueOf(UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d1"));
        Achievements achievement = getAchievement();
        gamer.getGamerEarnedAchievements().add(achievement);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(achievementsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(achievement));

        DefaultMessageResponse result = defaultApplicationService.collectAchievement(token, achievementId);
        assertEquals("100", result.getStatus().getCode());
        assertEquals(100, gamer.getCoin());
    }

    @Test
    void testGetMarketPlace_whenCalled_ReturnMarketItems() {
        List<Avatars> specialAvatars = new ArrayList<>();
        Avatars avatar = new Avatars();
        avatar.setPrice(100);
        avatar.setId(UUID.randomUUID());
        avatar.setImage("test");
        avatar.setIsSpecial(true);
        specialAvatars.add(avatar);
        specialAvatars.add(avatar);

        Mockito.when(avatarsRepository.findAllByIsSpecialTrue()).thenReturn(specialAvatars);

        MarketplaceResponse result = defaultApplicationService.getMarketplace();
        assertEquals(2, result.getBody().getData().getSpecialAvatars().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testBuyItem_whenAvatarNotFoundWithProvidedItemId_ReturnErrorCode127() {
        Gamer gamer = getGamer();
        gamer.setCoin(100);
        UUID id = UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d1");
        String itemId = String.valueOf(id);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(avatarsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultApplicationService.buyItem(token, itemId));
        assertEquals(127, exception.getTransactionCode().getId());
    }

    @Test
    void testBuyItem_whenFreeAvatarProvided_ReturnErrorCode128() {
        Gamer gamer = getGamer();
        gamer.setCoin(100);
        UUID id = UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d1");
        String itemId = String.valueOf(id);
        Avatars avatar = new Avatars();
        avatar.setImage("test");
        avatar.setIsSpecial(false);
        avatar.setPrice(0);
        avatar.setId(id);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(avatarsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(avatar));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultApplicationService.buyItem(token, itemId));
        assertEquals(128, exception.getTransactionCode().getId());
    }

    @Test
    void testBuyItem_whenItemAlreadyBought_ReturnErrorCode128() {
        Gamer gamer = getGamer();
        gamer.setCoin(100);
        UUID id = UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d1");
        String itemId = String.valueOf(id);
        Avatars avatar = new Avatars();
        avatar.setImage("test");
        avatar.setIsSpecial(true);
        avatar.setPrice(100);
        avatar.setId(id);
        gamer.getBoughtAvatars().add(avatar);
        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(avatarsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(avatar));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultApplicationService.buyItem(token, itemId));
        assertEquals(128, exception.getTransactionCode().getId());
    }

    @Test
    void testBuyItem_whenCoinNotEnough_ReturnErrorCode129() {
        Gamer gamer = getGamer();
        UUID id = UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d1");
        String itemId = String.valueOf(id);
        Avatars avatar = new Avatars();
        avatar.setImage("test");
        avatar.setIsSpecial(true);
        avatar.setPrice(100);
        avatar.setId(id);
        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(avatarsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(avatar));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultApplicationService.buyItem(token, itemId));
        assertEquals(129, exception.getTransactionCode().getId());
    }

    @Test
    void testBuyItem_whenGamerEarnsAchievement_ReturnSuccessAndSendNotifToGamer() {
        Gamer gamer = getGamer();
        gamer.setCoin(100);
        UUID id = UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d1");
        String itemId = String.valueOf(id);
        Avatars avatar = new Avatars();
        avatar.setImage("test");
        avatar.setIsSpecial(true);
        avatar.setPrice(100);
        avatar.setId(id);
        Achievements achievement = getAchievement();
        achievement.setAchievementName("Rich in the hood!!!");
        gamer.getBoughtAvatars().add(new Avatars());
        gamer.getBoughtAvatars().add(new Avatars());
        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(avatarsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(avatar));
        Mockito.when(achievementsRepository.findByAchievementName(Mockito.anyString()))
                .thenReturn(Optional.of(achievement));

        DefaultMessageResponse result = defaultApplicationService.buyItem(token, itemId);
        assertEquals("100", result.getStatus().getCode());
        assertTrue(gamer.getGamerEarnedAchievements().contains(achievement));
    }

    @Test
    void testBuyItem_whenCalledValid_ReturnSuccess() {
        Gamer gamer = getGamer();
        gamer.setCoin(100);
        UUID id = UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d1");
        String itemId = String.valueOf(id);
        Avatars avatar = new Avatars();
        avatar.setImage("test");
        avatar.setIsSpecial(true);
        avatar.setPrice(100);
        avatar.setId(id);
        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(avatarsRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(avatar));

        DefaultMessageResponse result = defaultApplicationService.buyItem(token, itemId);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetFriends_whenAchievementNotFound_ReturnErrorCode124() {
        Gamer gamer = getGamer();
        gamer.getFriends().add(new Gamer());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(achievementsRepository.findByAchievementName(Mockito.anyString()))
                .thenReturn(Optional.empty());

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultApplicationService.getFriends(token));
        assertEquals(124, exception.getTransactionCode().getId());
    }

    @Test
    void testGetFriends_whenAchievementAlreadyEarned_ReturnListOfFriends() {
        Gamer gamer = getGamer();
        gamer.getFriends().add(getGamer());
        gamer.getGamerEarnedAchievements().add(getAchievement());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(achievementsRepository.findByAchievementName(Mockito.anyString()))
                .thenReturn(Optional.of(getAchievement()));

        FriendsResponse result = defaultApplicationService.getFriends(token);
        assertEquals(1, result.getBody().getData().getFriends().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetFriends_whenAchievementEarned_ReturnListOfFriends() {
        Gamer gamer = getGamer();
        gamer.getFriends().add(getGamer());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(achievementsRepository.findByAchievementName(Mockito.anyString()))
                .thenReturn(Optional.of(getAchievement()));

        FriendsResponse result = defaultApplicationService.getFriends(token);
        assertEquals(1, result.getBody().getData().getFriends().size());
        assertEquals("100", result.getStatus().getCode());
        assertEquals(1, gamer.getGamerEarnedAchievements().size());
    }

    @Test
    void testGetFriends_whenCalledWithValidToken_ReturnListOfFriends() {
        Gamer gamer = getGamer();
        gamer.getFriends().add(getGamer());
        gamer.getFriends().add(getGamer());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));

        FriendsResponse result = defaultApplicationService.getFriends(token);
        assertEquals(2, result.getBody().getData().getFriends().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetWaitingFriends_whenCalledWithValidToken_ReturnListOfFriendRequests() {
        Gamer gamer = getGamer();
        gamer.getWaitingFriends().add(getGamer());
        gamer.getWaitingFriends().add(getGamer());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));

        FriendsResponse result = defaultApplicationService.getWaitingFriends(token);
        assertEquals(2, result.getBody().getData().getFriends().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetBlockedFriends_whenCalledWithValidToken_ReturnListOfBlockedUsers() {
        Gamer gamer = getGamer();
        gamer.getBlockedFriends().add(getGamer());
        gamer.getBlockedFriends().add(getGamer());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));

        FriendsResponse result = defaultApplicationService.getBlockedFriends(token);
        assertEquals(2, result.getBody().getData().getFriends().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testAcceptFriend_whenUserNotFoundWithProvidedToken_ReturnErrorCode103() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.acceptFriend(friendRequest, token));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testAcceptFriend_whenUserNotFoundWithProvidedUserId_ReturnErrorCode103() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.acceptFriend(friendRequest, token));
        assertEquals(103, exception.getTransactionCode().getId());
    }

    @Test
    void testAcceptFriend_whenUserAlreadyExistsInFriendList_ReturnErrorCode115() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        gamer.getFriends().add(friend);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.acceptFriend(friendRequest, token));
        assertEquals(115, exception.getTransactionCode().getId());
    }

    @Test
    void testAcceptFriend_whenFriendRequestNotFound_ReturnErrorCode116() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.acceptFriend(friendRequest, token));
        assertEquals(116, exception.getTransactionCode().getId());
    }

    @Test
    void testAcceptFriend_whenCalledWithValidTokenAndUserId_ReturnSuccess() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        gamer.getWaitingFriends().add(friend);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        DefaultMessageResponse result = defaultApplicationService.acceptFriend(friendRequest, token);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testRejectFriend_whenFriendRequestNotFound_ReturnErrorCode116() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.rejectFriend(friendRequest, token));
        assertEquals(116, exception.getTransactionCode().getId());
    }

    @Test
    void testRejectFriend_whenCalledWithValidTokenAndUserId_ReturnSuccess() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        gamer.getWaitingFriends().add(friend);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        DefaultMessageResponse result = defaultApplicationService.rejectFriend(friendRequest, token);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testRemoveFriend_whenAlreadyNotFriendsWithProvidedUserId_ReturnErrorCode117() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.removeFriend(friendRequest, token));
        assertEquals(117, exception.getTransactionCode().getId());
    }

    @Test
    void testRemoveFriend_whenCalledWithValidTokenAndUserId_ReturnSuccess() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        gamer.getFriends().add(friend);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        DefaultMessageResponse result = defaultApplicationService.removeFriend(friendRequest, token);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testBlockUser_whenUserAlreadyBlocked_ReturnErrorCode118() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        gamer.getBlockedFriends().add(friend);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        BusinessException exception =
                assertThrows(BusinessException.class, () -> defaultApplicationService.blockUser(friendRequest, token));
        assertEquals(118, exception.getTransactionCode().getId());
    }

    @Test
    void testBlockUser_whenCalledWithValidTokenAndUserId_ReturnSuccess() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        gamer.getFriends().add(friend);
        gamer.getWaitingFriends().add(friend);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        DefaultMessageResponse result = defaultApplicationService.blockUser(friendRequest, token);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testUnblockUser_whenUserNotBlocked_ReturnErrorCode119() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.unblockUser(friendRequest, token));
        assertEquals(119, exception.getTransactionCode().getId());
    }

    @Test
    void testUnblockUser_whenCalledWithValidTokenAndUserId_ReturnSuccess() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        gamer.getBlockedFriends().add(friend);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        DefaultMessageResponse result = defaultApplicationService.unblockUser(friendRequest, token);
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testSendFriendRequest_whenUserBlockedUserWhoSendsTheFriendRequest_ReturnErrorCode121() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        friend.getBlockedFriends().add(gamer);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.sendFriendRequest(friendRequest, token));
        assertEquals(121, exception.getTransactionCode().getId());
    }

    @Test
    void testSendFriendRequest_whenUserIsBlocked_ReturnErrorCode113() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        gamer.getBlockedFriends().add(friend);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.sendFriendRequest(friendRequest, token));
        assertEquals(113, exception.getTransactionCode().getId());
    }

    @Test
    void testSendFriendRequest_whenUsersAlreadyFriends_ReturnErrorCode122() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        gamer.getFriends().add(friend);
        friend.getFriends().add(gamer);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.sendFriendRequest(friendRequest, token));
        assertEquals(122, exception.getTransactionCode().getId());
    }

    @Test
    void testSendFriendRequest_whenRequestAlreadySent_ReturnErrorCode120() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");
        friend.getWaitingFriends().add(gamer);

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        BusinessException exception = assertThrows(
                BusinessException.class, () -> defaultApplicationService.sendFriendRequest(friendRequest, token));
        assertEquals(120, exception.getTransactionCode().getId());
    }

    @Test
    void testSendFriendRequest_whenCalledWithValidTokenAndUserId_ReturnSuccess() {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setUserId("test2");
        Gamer gamer = getGamer();
        Gamer friend = getGamer();
        friend.setUserId("test2");
        friend.setGamerUsername("test2");
        friend.setEmail("test2");

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));
        Mockito.when(gamerRepository.findById(Mockito.anyString())).thenReturn(Optional.of(friend));

        DefaultMessageResponse result = defaultApplicationService.sendFriendRequest(friendRequest, token);
        assertEquals("100", result.getStatus().getCode());
    }

    private Gamer getGamer() {
        Gamer gamer = new Gamer();
        gamer.setUserId("test");
        gamer.setGamerUsername("test");
        gamer.setEmail("test");
        gamer.setAge(15);
        gamer.setCountry("test");
        gamer.setAvatar(UUID.fromString("71927b70-8a51-4844-a306-00313fec4f09"));
        gamer.setLastModifiedDate(new Date());
        gamer.setPwd("test");
        gamer.setGender("E");
        gamer.setCoin(0);
        gamer.setIsBlocked(false);
        gamer.setLikedgames(new HashSet<>());
        gamer.setKeywords(new HashSet<>());
        gamer.setFriends(new HashSet<>());
        gamer.setWaitingFriends(new HashSet<>());
        gamer.setBlockedFriends(new HashSet<>());
        gamer.setGamerEarnedAchievements(new HashSet<>());
        gamer.setGamerCollectedAchievements(new HashSet<>());
        gamer.setBoughtAvatars(new HashSet<>());
        gamer.setJoinedCommunities(new HashSet<>());
        return gamer;
    }

    private Achievements getAchievement() {
        Achievements achievement = new Achievements();
        achievement.setAchievementName("test");
        achievement.setId(UUID.fromString("c0a80164-7b1f-4b9d-8d9c-6d9715d3e7d1"));
        achievement.setValue(100);
        achievement.setDescription("test");
        return achievement;
    }
}
