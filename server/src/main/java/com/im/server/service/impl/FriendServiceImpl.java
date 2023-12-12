package com.im.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.im.server.dto.UserDto;
import com.im.server.entity.Request;
import com.im.server.entity.User;
import com.im.server.mapper.FriendMapper;
import com.im.server.mapper.RequestMapper;
import com.im.server.service.FriendService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private RequestMapper requestMapper;

    @Override
    public List<UserDto> getFriends(Integer userId,Integer offset) {
        return friendMapper.selectFriends(userId,offset)
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        requestMapper.insertRequest(id,friendId);
    }

    @Override
    public List<Request> getFriendRequest(Integer id,Integer offset) {
        return requestMapper.selectRequest(id, offset);
    }

    @Override
    public void accept(Integer id, Integer friend) {
        requestMapper.updateState(id,friend,1);
        friendMapper.insert(id,friend);
        friendMapper.insert(friend,id);
    }

    public void reject(Integer id , Integer friend){
        requestMapper.updateState(id,friend,2);
    }
}
