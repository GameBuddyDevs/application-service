package com.back2261.applicationservice.domain.service;

import com.back2261.applicationservice.interfaces.request.FriendRequest;
import com.back2261.applicationservice.interfaces.request.MessageRequest;
import com.back2261.applicationservice.interfaces.response.FriendsResponse;
import com.back2261.applicationservice.interfaces.response.GamesResponse;
import com.back2261.applicationservice.interfaces.response.KeywordsResponse;
import io.github.GameBuddyDevs.backendlibrary.interfaces.DefaultMessageResponse;

public interface ApplicationService {

    KeywordsResponse getKeywords();

    GamesResponse getGames();

    FriendsResponse getFriends(String token);

    FriendsResponse getWaitingFriends(String token);

    FriendsResponse getBlockedFriends(String token);

    DefaultMessageResponse acceptFriend(FriendRequest addFriendRequest, String token);

    DefaultMessageResponse rejectFriend(FriendRequest rejectFriendRequest, String token);

    DefaultMessageResponse removeFriend(FriendRequest removeFriendRequest, String token);

    DefaultMessageResponse blockUser(FriendRequest blockFriendRequest, String token);

    DefaultMessageResponse unblockUser(FriendRequest unblockFriendRequest, String token);

    DefaultMessageResponse sendFriendRequest(FriendRequest sendFriendRequest, String token);

    DefaultMessageResponse saveMessageToMongo(MessageRequest messageRequest);
}
