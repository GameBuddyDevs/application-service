package com.back2261.applicationservice.domain.service;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.ascending;
import static com.mongodb.client.model.Indexes.descending;

import com.back2261.applicationservice.infrastructure.entity.*;
import com.back2261.applicationservice.infrastructure.repository.*;
import com.back2261.applicationservice.interfaces.dto.*;
import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.request.MessageRequest;
import com.back2261.applicationservice.interfaces.request.SendNotificationTokenRequest;
import com.back2261.applicationservice.interfaces.response.*;
import com.mongodb.client.FindIterable;
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
import io.github.GameBuddyDevs.backendlibrary.util.Constants;
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
    private final AvatarsRepository avatarsRepository;
    private final AchievementsRepository achievementRepository;
    private final JwtService jwtService;
    private final MongoClient mongoClient;
    private final NotificationService notificationService;

    @Value("${spring.data.mongodb.database}")
    private String mongoDbDatabase;

    @Value("${spring.data.mongodb.collection}")
    private String mongoDbCollection;

    @Override
    public UserInfoResponse getUserInfo(FriendRequest userRequest) {
        String userId = userRequest.getUserId();
        Gamer gamer = gamerRepository
                .findById(userId)
                .orElseThrow(() -> new BusinessException(TransactionCode.USER_NOT_FOUND));
        String avatar = avatarsRepository
                .findById(gamer.getAvatar())
                .orElse(new Avatars())
                .getImage();
        Set<Games> gamesList = gamer.getLikedgames();
        Set<Keywords> keywordsList = gamer.getKeywords();
        Set<Achievements> earnedAchievements = gamer.getGamerEarnedAchievements();

        List<GamesDto> gamesDtoList = new ArrayList<>();
        List<KeywordsDto> keywordsDtoList = new ArrayList<>();

        mapGamesToDto(gamesList.stream().toList(), gamesDtoList);
        mapKeywordsToDto(keywordsList.stream().toList(), keywordsDtoList);

        Set<Community> joinedCommunities = gamer.getJoinedCommunities();
        List<CommunityDto> communityDtoList = new ArrayList<>();
        joinedCommunities.forEach(community -> {
            CommunityDto communityDto = new CommunityDto();
            BeanUtils.copyProperties(community, communityDto);
            communityDto.setIsOwner(community.getOwner().getUserId().equals(userId));
            communityDtoList.add(communityDto);
        });

        UserInfoResponse userInfoResponse = new UserInfoResponse();
        UserInfoResponseBody body = new UserInfoResponseBody();
        body.setAge(String.valueOf(gamer.getAge()));
        body.setAvatar(avatar);
        body.setCountry(gamer.getCountry());
        body.setUsername(gamer.getGamerUsername());
        body.setGender(gamer.getGender());
        body.setEmail(gamer.getEmail());
        body.setCoin(gamer.getCoin());
        body.setGames(gamesDtoList);
        body.setKeywords(keywordsDtoList);
        body.setAchievements(earnedAchievements.stream().toList());
        body.setJoinedCommunities(communityDtoList);
        userInfoResponse.setBody(new BaseBody<>(body));
        userInfoResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return userInfoResponse;
    }

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
    public AvatarsResponse getAvatars(String token) {
        Gamer gamer = extractGamer(token);
        List<Avatars> boughtAvatars = gamer.getBoughtAvatars().stream().toList();
        List<Avatars> avatarsList = avatarsRepository.findAllByIsSpecialFalse();
        avatarsList.addAll(boughtAvatars);
        List<AvatarsDto> avatarsDtoList = new ArrayList<>();
        avatarsList.forEach(avatars -> {
            AvatarsDto avatarsDto = new AvatarsDto();
            avatarsDto.setId(avatars.getId().toString());
            avatarsDto.setImage(avatars.getImage());
            avatarsDtoList.add(avatarsDto);
        });

        AvatarsResponse avatarsResponse = new AvatarsResponse();
        AvatarsResponseBody body = new AvatarsResponseBody();
        body.setAvatars(avatarsDtoList);
        avatarsResponse.setBody(new BaseBody<>(body));
        avatarsResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return avatarsResponse;
    }

    @Override
    public AchievementResponse getAchievements(String token) {
        Gamer gamer = extractGamer(token);
        Set<Achievements> collected = gamer.getGamerCollectedAchievements();
        Set<Achievements> earned = gamer.getGamerEarnedAchievements();
        List<Achievements> achievementsList = achievementRepository.findAll();
        List<AchievementsDto> achievementsDtoList = new ArrayList<>();

        achievementsList.forEach(achievements -> {
            AchievementsDto achievementsDto = new AchievementsDto();
            BeanUtils.copyProperties(achievements, achievementsDto);
            achievementsDto.setId(achievements.getId().toString());
            achievementsDto.setIsCollected(collected.contains(achievements));
            achievementsDto.setIsEarned(earned.contains(achievements));
            achievementsDtoList.add(achievementsDto);
        });

        AchievementResponse achievementResponse = new AchievementResponse();
        AchievementResponseBody body = new AchievementResponseBody();
        body.setAchievementsList(achievementsDtoList);
        achievementResponse.setBody(new BaseBody<>(body));
        achievementResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return achievementResponse;
    }

    @Override
    public DefaultMessageResponse collectAchievement(String token, String achievementId) {
        Gamer gamer = extractGamer(token);
        Integer coin = gamer.getCoin();
        Optional<Achievements> achievementOptional = achievementRepository.findById(UUID.fromString(achievementId));
        if (achievementOptional.isEmpty()) {
            throw new BusinessException(TransactionCode.ACHIEVEMENT_NOT_FOUND);
        }
        Achievements achievement = achievementOptional.get();

        if (Boolean.FALSE.equals(gamer.getGamerEarnedAchievements().contains(achievement))) {
            throw new BusinessException(TransactionCode.ACHIEVEMENT_NOT_EARNED);
        }

        if (Boolean.TRUE.equals(gamer.getGamerCollectedAchievements().contains(achievement))) {
            throw new BusinessException(TransactionCode.ALREADY_COLLECTED);
        }

        gamer.setCoin(coin + achievement.getValue());
        gamer.getGamerCollectedAchievements().add(achievement);
        gamerRepository.save(gamer);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body =
                new DefaultMessageBody("Achievement " + achievement.getAchievementName() + " collected");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public MarketplaceResponse getMarketplace() {
        List<Avatars> specialAvatars = avatarsRepository.findAllByIsSpecialTrue();
        List<SpecialAvatarsDto> specialAvatarsDtoList = new ArrayList<>();
        specialAvatars.forEach(avatars -> {
            SpecialAvatarsDto avatarsDto = new SpecialAvatarsDto();
            avatarsDto.setId(avatars.getId().toString());
            avatarsDto.setImage(avatars.getImage());
            avatarsDto.setPrice(String.valueOf(avatars.getPrice()));
            specialAvatarsDtoList.add(avatarsDto);
        });
        MarketplaceResponse marketplaceResponse = new MarketplaceResponse();
        MarketplaceResponseBody body = new MarketplaceResponseBody();
        body.setSpecialAvatars(specialAvatarsDtoList);
        marketplaceResponse.setBody(new BaseBody<>(body));
        marketplaceResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return marketplaceResponse;
    }

    @Override
    public DefaultMessageResponse buyItem(String token, String itemId) {
        Gamer gamer = extractGamer(token);
        Integer coin = gamer.getCoin();
        Avatars avatar = avatarsRepository
                .findById(UUID.fromString(itemId))
                .orElseThrow(() -> new BusinessException(TransactionCode.AVATAR_NOT_FOUND));
        if (Boolean.FALSE.equals(avatar.getIsSpecial())) {
            throw new BusinessException(TransactionCode.AVATAR_ALREADY_OWNED);
        }
        if (gamer.getBoughtAvatars().contains(avatar)) {
            throw new BusinessException(TransactionCode.AVATAR_ALREADY_OWNED);
        }
        if (coin < avatar.getPrice()) {
            throw new BusinessException(TransactionCode.COIN_NOT_ENOUGH);
        }

        gamer.setCoin(coin - avatar.getPrice());
        gamer.getBoughtAvatars().add(avatar);
        if (gamer.getBoughtAvatars().size() == 3) {
            Achievements achievement = achievementRepository
                    .findByAchievementName("Rich in the hood!!!")
                    .orElseThrow(() -> new BusinessException(TransactionCode.ACHIEVEMENT_NOT_FOUND));
            setAchievementAndSendNotification(gamer, achievement);
        }
        gamerRepository.save(gamer);
        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Item bought");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public FriendsResponse getFriends(String token) {
        List<GamerDto> friendDtoList = new ArrayList<>();
        Gamer gamer = extractGamer(token);
        Set<Gamer> friends = gamer.getFriends();
        if (friends.size() == 1) {
            Achievements achievement = achievementRepository
                    .findByAchievementName("Friendly Person!!!")
                    .orElseThrow(() -> new BusinessException(TransactionCode.ACHIEVEMENT_NOT_FOUND));
            if (Boolean.FALSE.equals(gamer.getGamerEarnedAchievements().contains(achievement))) {
                setAchievementAndSendNotification(gamer, achievement);
            }
        }
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

        SendNotificationTokenRequest tokenRequest = new SendNotificationTokenRequest();
        tokenRequest.setToken(user.getFcmToken());
        tokenRequest.setTitle(Constants.FRIEND_REQUEST_ACCEPTED_TITLE);
        tokenRequest.setBody(String.format(Constants.FRIEND_REQUEST_ACCEPTED_BODY, gamer.getGamerUsername()));
        try {
            notificationService.sendToToken(tokenRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        SendNotificationTokenRequest tokenRequest = new SendNotificationTokenRequest();
        tokenRequest.setToken(user.getFcmToken());
        tokenRequest.setTitle(Constants.FRIENT_REQUEST_TITLE);
        tokenRequest.setBody(String.format(Constants.FRIENT_REQUEST_BODY, gamer.getGamerUsername()));
        try {
            notificationService.sendToToken(tokenRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Friend request sent successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public DefaultMessageResponse saveMessageToMongo(String token, MessageRequest messageRequest) {
        Gamer gamer = extractGamer(token);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoDbDatabase);
        MongoCollection<Message> collection = mongoDatabase.getCollection(mongoDbCollection, Message.class);
        Message message = new Message();
        message.setMessageBody(messageRequest.getMessage());
        message.setId(UUID.randomUUID().toString());
        message.setSender(gamer.getUserId());
        message.setReceiver(messageRequest.getReceiverId());
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        message.setDate(dateFormat.format(new Date()));
        collection.insertOne(message);

        DefaultMessageResponse defaultMessageResponse = new DefaultMessageResponse();
        DefaultMessageBody body = new DefaultMessageBody("Message saved successfully");
        defaultMessageResponse.setBody(new BaseBody<>(body));
        defaultMessageResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return defaultMessageResponse;
    }

    @Override
    public ConversationResponse getUserConversation(String token, FriendRequest friendRequest) {
        Gamer gamer = extractGamer(token);
        if (Objects.equals(gamer.getUserId(), friendRequest.getUserId())) {
            throw new BusinessException(TransactionCode.SAME_IDS); // Kendin ile konuşma yapamazsın
        }
        Gamer friend = getGamerFromId(friendRequest.getUserId());
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoDbDatabase);
        MongoCollection<Message> collection = mongoDatabase.getCollection(mongoDbCollection, Message.class);
        FindIterable<Message> sendedMessages = collection
                .find(eq("sender", gamer.getUserId()))
                .filter(eq("receiver", friend.getUserId()))
                .sort(ascending("date"));
        FindIterable<Message> recievedMessages = collection
                .find(eq("sender", friend.getUserId()))
                .filter(eq("receiver", gamer.getUserId()))
                .sort(ascending("date"));
        List<Message> messageList = new ArrayList<>();
        sendedMessages.into(messageList);
        recievedMessages.into(messageList);
        List<ConversationDto> conversationDtoList = new ArrayList<>();
        for (Message message : messageList) {
            ConversationDto conversationDto = new ConversationDto();
            BeanUtils.copyProperties(message, conversationDto);
            conversationDtoList.add(conversationDto);
        }

        ConversationResponse conversationResponse = new ConversationResponse();
        ConversationResponseBody body = new ConversationResponseBody();
        body.setConversations(conversationDtoList);
        conversationResponse.setBody(new BaseBody<>(body));
        conversationResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return conversationResponse;
    }

    @Override
    public InboxResponse getInbox(String token) {
        Gamer gamer = extractGamer(token);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoDbDatabase);
        MongoCollection<Message> collection = mongoDatabase.getCollection(mongoDbCollection, Message.class);
        List<Message> lastSentMessages = new ArrayList<>();
        List<String> receivers = collection
                .distinct("receiver", String.class)
                .filter(eq("sender", gamer.getUserId()))
                .into(new ArrayList<>());
        List<String> senders = collection
                .distinct("sender", String.class)
                .filter(eq("receiver", gamer.getUserId()))
                .into(new ArrayList<>());
        Set<String> total = new HashSet<>();
        total.addAll(receivers);
        total.addAll(senders);

        for (String user : total) {
            Message receivedMessage = collection
                    .find(eq("receiver", gamer.getUserId()))
                    .filter(eq("sender", user))
                    .sort(descending("date"))
                    .first();
            Message sentMessage = collection
                    .find(eq("sender", gamer.getUserId()))
                    .filter(eq("receiver", user))
                    .sort(descending("date"))
                    .first();

            if (receivedMessage != null && sentMessage != null) {
                if (receivedMessage.getDate().compareTo(sentMessage.getDate()) > 0) {
                    lastSentMessages.add(receivedMessage);
                } else {
                    lastSentMessages.add(sentMessage);
                }
            } else {
                lastSentMessages.add(receivedMessage == null ? sentMessage : receivedMessage);
            }
        }

        List<InboxDto> inboxDtoList = new ArrayList<>();
        for (Message message : lastSentMessages) {
            InboxDto inboxDto = new InboxDto();
            Gamer user;
            if (message.getSender().equals(gamer.getUserId())) {
                user = getGamerFromId(message.getReceiver());
            } else {
                user = getGamerFromId(message.getSender());
            }
            inboxDto.setUserId(user.getUserId());
            inboxDto.setUsername(user.getGamerUsername());
            Avatars avatar = avatarsRepository.findById(user.getAvatar()).orElse(new Avatars());
            inboxDto.setAvatar(avatar.getImage());
            inboxDto.setLastMessage(message.getMessageBody());
            inboxDto.setLastMessageTime(message.getDate());
            inboxDtoList.add(inboxDto);
        }
        InboxResponse inboxResponse = new InboxResponse();
        InboxResponseBody body = new InboxResponseBody();
        body.setInboxList(inboxDtoList);
        inboxResponse.setBody(new BaseBody<>(body));
        inboxResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return inboxResponse;
    }

    private void setAchievementAndSendNotification(Gamer gamer, Achievements achievement) {
        gamer.getGamerEarnedAchievements().add(achievement);
        SendNotificationTokenRequest tokenRequest = new SendNotificationTokenRequest();
        tokenRequest.setToken(gamer.getFcmToken());
        tokenRequest.setTitle(Constants.ACHIEVEMENT_TITLE);
        tokenRequest.setBody(String.format(Constants.ACHIEVEMENT_BODY, achievement.getAchievementName()));
        try {
            notificationService.sendToToken(tokenRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void mapGamesToDto(List<Games> gamesList, List<GamesDto> gamesDtoList) {
        for (Games games : gamesList) {
            GamesDto gamesDto = new GamesDto();
            BeanUtils.copyProperties(games, gamesDto);
            gamesDtoList.add(gamesDto);
        }
    }

    private void mapFriendsToDto(Set<Gamer> friends, List<GamerDto> friendDtoList) {
        for (Gamer friend : friends) {
            GamerDto friendDto = new GamerDto();
            friendDto.setUsername(friend.getGamerUsername());
            friendDto.setAge(friend.getAge());
            friendDto.setCountry(friend.getCountry());
            friendDto.setAvatar(friend.getAvatar().toString());
            friendDtoList.add(friendDto);
        }
    }
}
