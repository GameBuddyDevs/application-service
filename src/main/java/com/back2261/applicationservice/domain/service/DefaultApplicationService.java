package com.back2261.applicationservice.domain.service;

import com.back2261.applicationservice.infrastructure.entity.*;
import com.back2261.applicationservice.infrastructure.repository.*;
import com.back2261.applicationservice.interfaces.dto.*;
import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.request.SendNotificationTokenRequest;
import com.back2261.applicationservice.interfaces.response.*;
import io.github.GameBuddyDevs.backendlibrary.base.BaseBody;
import io.github.GameBuddyDevs.backendlibrary.base.Status;
import io.github.GameBuddyDevs.backendlibrary.enums.TransactionCode;
import io.github.GameBuddyDevs.backendlibrary.exception.BusinessException;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageBody;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;
import io.github.GameBuddyDevs.backendlibrary.service.JwtService;
import io.github.GameBuddyDevs.backendlibrary.util.Constants;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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
    private final NotificationService notificationService;

    @Override
    public UserInfoResponse getUserInfo(String userId) {
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
            communityDto.setCommunityId(String.valueOf(community.getCommunityId()));
            communityDto.setIsOwner(community.getOwner().getUserId().equals(userId));
            communityDtoList.add(communityDto);
        });
        Set<Gamer> friends = gamer.getFriends();
        List<GamerDto> friendsDtoList = new ArrayList<>();
        mapFriendsToDto(friends, friendsDtoList);

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
        body.setFriends(friendsDtoList);
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
        return getGamesResponse(gamesList);
    }

    @Override
    public GamesResponse getPopularGames() {
        List<Games> gamesList = gamesRepository.findAllByIsPopularTrueOrderByAvgVoteDesc();
        return getGamesResponse(gamesList);
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

    private GamesResponse getGamesResponse(List<Games> gamesList) {
        GamesResponse gamesResponse = new GamesResponse();
        GamesResponseBody body = new GamesResponseBody();
        List<GamesDto> gamesDtoList = new ArrayList<>();
        mapGamesToDto(gamesList, gamesDtoList);
        body.setGames(gamesDtoList);
        gamesResponse.setBody(new BaseBody<>(body));
        gamesResponse.setStatus(new Status(TransactionCode.DEFAULT_100));
        return gamesResponse;
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
            BeanUtils.copyProperties(friend, friendDto);
            friendDto.setUsername(friend.getGamerUsername());
            String avatar = avatarsRepository
                    .findById(friend.getAvatar())
                    .orElse(new Avatars())
                    .getImage();
            friendDto.setAvatar(avatar);
            friendDtoList.add(friendDto);
        }
    }
}
