package com.back2261.applicationservice.domain.service;

import static org.junit.jupiter.api.Assertions.*;

import com.back2261.applicationservice.infrastructure.entity.Gamer;
import com.back2261.applicationservice.infrastructure.entity.Games;
import com.back2261.applicationservice.infrastructure.entity.Keywords;
import com.back2261.applicationservice.infrastructure.repository.GamerRepository;
import com.back2261.applicationservice.infrastructure.repository.GamesRepository;
import com.back2261.applicationservice.infrastructure.repository.KeywordsRepository;
import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.response.FriendsResponse;
import com.back2261.applicationservice.interfaces.response.GamesResponse;
import com.back2261.applicationservice.interfaces.response.KeywordsResponse;
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
    private JwtService jwtService;

    private String token;

    @BeforeEach
    void setUp() {
        token = "test";
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
    void testGetFriends_whenCalledWithValidToken_ReturnListOfFriends() {
        Gamer gamer = getGamer();
        gamer.getFriends().add(new Gamer());
        gamer.getFriends().add(new Gamer());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));

        FriendsResponse result = defaultApplicationService.getFriends(token);
        assertEquals(2, result.getBody().getData().getFriends().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetWaitingFriends_whenCalledWithValidToken_ReturnListOfFriendRequests() {
        Gamer gamer = getGamer();
        gamer.getWaitingFriends().add(new Gamer());
        gamer.getWaitingFriends().add(new Gamer());

        Mockito.when(jwtService.extractUsername(Mockito.anyString())).thenReturn("test@test.com");
        Mockito.when(gamerRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(gamer));

        FriendsResponse result = defaultApplicationService.getWaitingFriends(token);
        assertEquals(2, result.getBody().getData().getFriends().size());
        assertEquals("100", result.getStatus().getCode());
    }

    @Test
    void testGetBlockedFriends_whenCalledWithValidToken_ReturnListOfBlockedUsers() {
        Gamer gamer = getGamer();
        gamer.getBlockedFriends().add(new Gamer());
        gamer.getBlockedFriends().add(new Gamer());

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
        gamer.setAvatar(new byte[0]);
        gamer.setLastModifiedDate(new Date());
        gamer.setPwd("test");
        gamer.setGender("E");
        gamer.setIsBlocked(false);
        gamer.setLikedgames(new HashSet<>());
        gamer.setKeywords(new HashSet<>());
        gamer.setFriends(new HashSet<>());
        gamer.setWaitingFriends(new HashSet<>());
        gamer.setBlockedFriends(new HashSet<>());
        return gamer;
    }
}
