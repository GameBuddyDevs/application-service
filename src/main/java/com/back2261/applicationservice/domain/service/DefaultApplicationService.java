package com.back2261.applicationservice.domain.service;

import com.back2261.applicationservice.infrastructure.entity.Gamer;
import com.back2261.applicationservice.infrastructure.entity.Games;
import com.back2261.applicationservice.infrastructure.entity.Keywords;
import com.back2261.applicationservice.infrastructure.entity.Message;
import com.back2261.applicationservice.infrastructure.repository.GamerRepository;
import com.back2261.applicationservice.infrastructure.repository.GamesRepository;
import com.back2261.applicationservice.infrastructure.repository.KeywordsRepository;
import com.back2261.applicationservice.interfaces.dto.*;
import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.request.MessageRequest;
import com.back2261.applicationservice.interfaces.response.FriendsResponse;
import com.back2261.applicationservice.interfaces.response.GamesResponse;
import com.back2261.applicationservice.interfaces.response.KeywordsResponse;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.enums.TransactionCode;
import io.github.GameBuddyDevs.backendlibrary.exception.BusinessException;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultApplicationService implements ApplicationService {
    private final KeywordsRepository keywordsRepository;
    private final GamesRepository gamesRepository;
    private final GamerRepository gamerRepository;
    private final JwtService jwtService;
    private final MongoClient mongoClient;

    @Value("${spring.data.mongodb.database}")
    private String mongoDbDatabase;

    @Value("${spring.data.mongodb.collection}")
    private String mongoDbCollection;

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
        List<GamesDto> gamesDtoList = new ArrayList<>();
        gamesList.forEach(games -> {
            GamesDto gamesDto = new GamesDto();
            BeanUtils.copyProperties(games, gamesDto);
            gamesDtoList.add(gamesDto);
        });
        body.setGames(gamesDtoList);
        gamesResponse.setBody(new BaseBody<>(body));
        gamesResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return gamesResponse;
    }

    @Override
    public FriendsResponse getFriends(String token) {
        List<GamerDto> friendDtoList = new ArrayList<>();
        Gamer gamer = extractGamer(token);
        Set<Gamer> friends = gamer.getFriends();
        return getFriendsResponse(friendDtoList, friends);
    }

    @Override
    public FriendsResponse getWaitingFriends(String token) {
        List<GamerDto> friendDtoList = new ArrayList<>();
        Gamer gamer = extractGamer(token);
        Set<Gamer> waitingFriends = gamer.getWaitingFriends();
        return getFriendsResponse(friendDtoList, waitingFriends);
    }

    @Override
    public FriendsResponse getBlockedFriends(String token) {
        List<GamerDto> friendDtoList = new ArrayList<>();
        Gamer gamer = extractGamer(token);
        Set<Gamer> blockedFriend = gamer.getBlockedFriends();
        return getFriendsResponse(friendDtoList, blockedFriend);
    }

    @Override
    public DefaultMessageResponse acceptFriend(FriendRequest addFriendRequest, String token) {
        String id = addFriendRequest.getUserId();
        Gamer gamer = extractGamer(token);
        Gamer user = getGamerFromId(id);
        Set<Gamer> waitingFriends = gamer.getWaitingFriends();
        Set<Gamer> myFriends = gamer.getFriends();
        if (myFriends.stream().anyMatch(friend -> friend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.FRIEND_ALREADY_EXISTS);
        }
        if (waitingFriends.stream()
                .noneMatch(waitingFriend -> waitingFriend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.FRIEND_NO_REQUEST);
        }

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
        Gamer user = getGamerFromId(id);
        Set<Gamer> waitingFriends = gamer.getWaitingFriends();

        if (waitingFriends.stream()
                .noneMatch(waitingFriend -> waitingFriend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.FRIEND_NO_REQUEST);
        }

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
        Gamer friend = getGamerFromId(id);

        if (gamer.getFriends().stream().noneMatch(friend1 -> friend1.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.FRIEND_NOT_FOUND);
        }

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
        Gamer user = getGamerFromId(id);

        if (gamer.getBlockedFriends().stream()
                .anyMatch(friend -> friend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.USER_ALREADY_BLOCKED);
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
        Gamer user = getGamerFromId(id);
        if (gamer.getBlockedFriends().stream()
                .noneMatch(friend -> friend.getUserId().equals(id))) {
            throw new BusinessException(TransactionCode.USER_NOT_BLOCKED);
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
        Gamer user = getGamerFromId(id);

        if (user.getBlockedFriends().stream()
                .anyMatch(blockedFriend -> blockedFriend.getUserId().equals(gamer.getUserId()))) {
            throw new BusinessException(TransactionCode.USER_BLOCKED_YOU);
        }

        if (gamer.getBlockedFriends().stream()
                .anyMatch(blockedFriend -> blockedFriend.getUserId().equals(user.getUserId()))) {
            throw new BusinessException(TransactionCode.USER_BLOCKED);
        }

        if (user.getFriends().stream().anyMatch(friend -> friend.getUserId().equals(gamer.getUserId()))) {
            throw new BusinessException(TransactionCode.ALREADY_FRIENDS);
        }

        if (user.getWaitingFriends().stream()
                .anyMatch(waitingFriend -> waitingFriend.getUserId().equals(gamer.getUserId()))) {
            throw new BusinessException(TransactionCode.ALREADY_SENT_REQUEST);
        }

        user.getWaitingFriends().add(gamer);
        gamerRepository.save(user);

        // TODO: Notification gönderilecek user a

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Friend request sent successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse saveMessageToMongo(MessageRequest messageRequest) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoDbDatabase);
        MongoCollection<Message> collection = mongoDatabase.getCollection(mongoDbCollection, Message.class);
        Message message = new Message();
        message.setMessage(messageRequest.getMessage());
        message.setId(UUID.randomUUID().toString());
        message.setRead(messageRequest.isRead());
        message.setSender(messageRequest.getSender());
        message.setReceiver(messageRequest.getReceiver());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        message.setDate(dateFormat.format(new Date()));
        collection.insertOne(message);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Message saved successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    private FriendsResponse getFriendsResponse(List<GamerDto> friendDtoList, Set<Gamer> friends) {
        mapFriendsToDto(friends, friendDtoList);
        FriendsResponse friendsResponse = new FriendsResponse();
        FriendsResponseBody body = new FriendsResponseBody();
        body.setFriends(friendDtoList);
        friendsResponse.setBody(new BaseBody<>(body));
        friendsResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return friendsResponse;
    }

    private Gamer extractGamer(String token) {
        String email = jwtService.extractUsername(token);
        Optional<Gamer> gamerOptional = gamerRepository.findByEmail(email);
        if (gamerOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.USER_NOT_FOUND);
        }
        return gamerOptional.get();
    }

    private Gamer getGamerFromId(String id) {
        Optional<Gamer> gamerOptional = gamerRepository.findById(id);
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
