package com.back2261.applicationservice.domain.service;

import com.back2261.applicationservice.infrastructure.entity.Gamer;
import com.back2261.applicationservice.infrastructure.entity.Games;
import com.back2261.applicationservice.infrastructure.entity.Keywords;
import com.back2261.applicationservice.infrastructure.repository.GamerRepository;
import com.back2261.applicationservice.infrastructure.repository.GamesRepository;
import com.back2261.applicationservice.infrastructure.repository.KeywordsRepository;
import com.back2261.applicationservice.interfaces.dto.*;
import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.response.FriendsResponse;
import com.back2261.applicationservice.interfaces.response.GamesResponse;
import com.back2261.applicationservice.interfaces.response.KeywordsResponse;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.enums.TransactionCode;
import io.github.GameBuddyDevs.backendlibrary.exception.BusinessException;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultApplicationService implements ApplicationService {
    private final KeywordsRepository keywordsRepository;
    private final GamesRepository gamesRepository;
    private final GamerRepository gamerRepository;
    private final JwtService jwtService;

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

    @Override
    public FriendsResponse getFriends(String token) {
        List<GamerDto> friendDtoList = new ArrayList<>();
        Gamer gamer = extractGamer(token);
        Set<Gamer> friends = gamer.getFriends();
        mapFriendsToDto(friends, friendDtoList);
        FriendsResponse friendsResponse = new FriendsResponse();
        FriendsResponseBody body = new FriendsResponseBody();
        body.setFriends(friendDtoList);
        friendsResponse.setBody(new BaseBody<>(body));
        friendsResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return friendsResponse;
    }

    @Override
    public FriendsResponse getWaitingFriends(String token) {
        List<GamerDto> friendDtoList = new ArrayList<>();
        Gamer gamer = extractGamer(token);
        Set<Gamer> waitingFriends = gamer.getWaitingFriends();
        mapFriendsToDto(waitingFriends, friendDtoList);
        FriendsResponse friendsResponse = new FriendsResponse();
        FriendsResponseBody body = new FriendsResponseBody();
        body.setFriends(friendDtoList);
        friendsResponse.setBody(new BaseBody<>(body));
        friendsResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return friendsResponse;
    }

    @Override
    public FriendsResponse getBlockedFriends(String token) {
        List<GamerDto> friendDtoList = new ArrayList<>();
        Gamer gamer = extractGamer(token);
        Set<Gamer> blockedFriend = gamer.getBlockedFriends();
        mapFriendsToDto(blockedFriend, friendDtoList);
        FriendsResponse friendsResponse = new FriendsResponse();
        FriendsResponseBody body = new FriendsResponseBody();
        body.setFriends(friendDtoList);
        friendsResponse.setBody(new BaseBody<>(body));
        friendsResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return friendsResponse;
    }

    @Override
    public DefaultMessageResponse acceptFriend(FriendRequest addFriendRequest, String token) {
        String id = addFriendRequest.getUserId();
        Gamer gamer = extractGamer(token);
        Optional<Gamer> userOptional = gamerRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        Set<Gamer> waitingFriends = gamer.getWaitingFriends();
        Set<Gamer> myFriends = gamer.getFriends();
        if (myFriends.stream().anyMatch(friend -> friend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.DB_ERROR); // FRIEND_ALREADY diye code
        }
        if (waitingFriends.stream()
                .noneMatch(waitingFriend -> waitingFriend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.DB_ERROR); // FRIEND_NO_REQUEST diye code
        }
        Gamer user = userOptional.get();

        waitingFriends.remove(user);
        myFriends.add(user);
        gamerRepository.save(gamer);
        user.getFriends().add(gamer);
        gamerRepository.save(user);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Friend added successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse rejectFriend(FriendRequest rejectFriendRequest, String token) {
        String id = rejectFriendRequest.getUserId();
        Gamer gamer = extractGamer(token);
        Optional<Gamer> userOptional = gamerRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        Set<Gamer> waitingFriends = gamer.getWaitingFriends();

        if (waitingFriends.stream()
                .noneMatch(waitingFriend -> waitingFriend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.DB_ERROR); // FRIEND_NO_REQUEST diye code
        }
        Gamer user = userOptional.get();
        gamer.getWaitingFriends().remove(user);
        gamerRepository.save(gamer);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Friend rejected successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse removeFriend(FriendRequest removeFriendRequest, String token) {
        String id = removeFriendRequest.getUserId();
        Gamer gamer = extractGamer(token);
        Optional<Gamer> friendOptional = gamerRepository.findById(id);
        if (friendOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }

        if (gamer.getFriends().stream().noneMatch(friend -> friend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.DB_ERROR); // USER_NOT_FRIEND diye code
        }

        Gamer friend = friendOptional.get();
        gamer.getFriends().remove(friend);
        gamerRepository.save(gamer);
        friend.getFriends().remove(gamer);
        gamerRepository.save(friend);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Friend removed successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse blockUser(FriendRequest blockFriendRequest, String token) {
        String id = blockFriendRequest.getUserId();
        Gamer gamer = extractGamer(token);
        Optional<Gamer> userOptional = gamerRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        Gamer user = userOptional.get();

        if (gamer.getBlockedFriends().stream()
                .anyMatch(friend -> friend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.DB_ERROR); // USER_ALREADY_BLOCKED diye code
        }

        if (gamer.getFriends().stream().anyMatch(friend -> friend.getUserId().equals(id))) {
            gamer.getFriends().remove(user);
            user.getFriends().remove(gamer);
        }

        if (gamer.getWaitingFriends().stream()
                .anyMatch(friend -> friend.getUserId().equals(id))) {
            gamer.getWaitingFriends().remove(user);
            user.getWaitingFriends().remove(gamer);
        }

        gamer.getBlockedFriends().add(user);
        gamerRepository.save(gamer);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("User blocked successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse unblockUser(FriendRequest unblockFriendRequest, String token) {
        String id = unblockFriendRequest.getUserId();
        Gamer gamer = extractGamer(token);
        Optional<Gamer> userOptional = gamerRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        Gamer user = userOptional.get();
        if (gamer.getBlockedFriends().stream()
                .noneMatch(friend -> friend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.DB_ERROR); // USER_NOT_BLOCKED diye code
        }

        gamer.getBlockedFriends().remove(user);
        gamerRepository.save(gamer);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("User unblocked successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse sendFriendRequest(FriendRequest sendRequest, String token) {
        String id = sendRequest.getUserId();
        Gamer gamer = extractGamer(token);
        Optional<Gamer> userOptional = gamerRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        Gamer user = userOptional.get();
        if (user.getWaitingFriends().stream()
                .anyMatch(waitingFriend -> waitingFriend.getUserId().equals(gamer.getUserId()))) {
            throw new BusinessException(TransactionCode.DB_ERROR); // SENT_REQUEST_ALREADY diye code
        }

        if (user.getBlockedFriends().stream()
                .anyMatch(blockedFriend -> blockedFriend.getUserId().equals(gamer.getUserId()))) {
            throw new BusinessException(
                    TransactionCode.DB_ERROR); // USER_BLOCKED diye code "istek atamazsın seni blocklamış"
        }
        user.getWaitingFriends().add(gamer);
        gamerRepository.save(user);

        // TODO: Notification gönderilecek

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Friend request sent successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    private Gamer extractGamer(String token) {
        String email = jwtService.extractUsername(token);
        Optional<Gamer> gamerOptional = gamerRepository.findByEmail(email);
        if (gamerOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        return gamerOptional.get();
    }

    private void mapKeywordsToDto(List<Keywords> keywordsList, List<KeywordsDto> keywordsDtoList) {
        for (Keywords keywords : keywordsList) {
            KeywordsDto keywordsDto = new KeywordsDto();
            BeanUtils.copyProperties(keywords, keywordsDto);
            keywordsDtoList.add(keywordsDto);
        }
    }

    private void mapFriendsToDto(Set<Gamer> friends, List<GamerDto> friendDtoList) {
        for (Gamer friend : friends) {
            GamerDto friendDto = new GamerDto();
            friendDto.setUsername(friend.getGamerUsername());
            friendDto.setAge(friend.getAge());
            friendDto.setCountry(friend.getCountry());
            friendDto.setLastOnlineDate(friend.getLastOnlineDate());
            friendDto.setAvatar(friend.getAvatar());
            friendDtoList.add(friendDto);
        }
    }
}
