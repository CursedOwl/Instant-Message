package com.im.server.service;


import com.im.server.dto.UserDto;
import com.im.server.entity.User;

import java.util.List;

public interface FriendService {
    public List<UserDto> getFriends(Integer userId,Integer offset);

    void addFriend(Integer id, Integer friendId);
}
