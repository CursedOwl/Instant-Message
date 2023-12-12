package com.im.server.service;


import com.im.server.dto.UserDto;
import com.im.server.entity.Request;
import com.im.server.entity.User;

import java.util.List;

public interface FriendService {
    List<UserDto> getFriends(Integer userId,Integer offset);

    void addFriend(Integer id, Integer friendId);

    List<Request> getFriendRequest(Integer id,Integer offset);

    void accept(Integer id, Integer friend);

    void reject(Integer id, Integer friend);
}
