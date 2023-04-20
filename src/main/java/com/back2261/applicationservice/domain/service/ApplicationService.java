package com.back2261.applicationservice.domain.service;

import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.request.MessageRequest;
import com.back2261.applicationservice.interfaces.response.*;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;

public interface ApplicationService {

    KeywordsResponse getKeywords();

    GamesResponse getGames();

    AvatarsResponse getAvatars(String token);

    AchievementResponse getAchievements(String token);

    DefaultMessageResponse collectAchievement(String token, String achievementId);

    MarketplaceResponse getMarketplace();

    DefaultMessageResponse buyItem(String token, String itemId);

    FriendsResponse getFriends(String token);

    FriendsResponse getWaitingFriends(String token);

    FriendsResponse getBlockedFriends(String token);

    DefaultMessageResponse acceptFriend(FriendRequest addFriendRequest, String token);

    DefaultMessageResponse rejectFriend(FriendRequest rejectFriendRequest, String token);

    DefaultMessageResponse removeFriend(FriendRequest removeFriendRequest, String token);

    DefaultMessageResponse blockUser(FriendRequest blockFriendRequest, String token);

    DefaultMessageResponse unblockUser(FriendRequest unblockFriendRequest, String token);

    DefaultMessageResponse sendFriendRequest(FriendRequest sendFriendRequest, String token);

    DefaultMessageResponse saveMessageToMongo(String token, MessageRequest messageRequest);

    ConversationResponse getUserConversation(String token, FriendRequest friendRequest);
}
