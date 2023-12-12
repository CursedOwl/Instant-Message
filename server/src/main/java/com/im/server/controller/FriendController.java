package com.im.server.controller;

import com.im.server.dto.UserDto;
import com.im.server.entity.Request;
import com.im.server.entity.ResponseEntity;
import com.im.server.service.FriendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @GetMapping("/friends")
    public ResponseEntity getFriends(@RequestParam("id") Integer id, @RequestParam("offset") Integer offset) {
        List<UserDto> friends = friendService.getFriends(id,offset);
        return ResponseEntity.ok(friends,friends.size());
    }

    @GetMapping("/add")
    public ResponseEntity addFriend(@RequestParam("id") Integer id, @RequestParam("friend") Integer friend) {
        friendService.addFriend(id,friend);
        return ResponseEntity.ok();
    }

    @GetMapping("/request")
    public ResponseEntity getFriendRequest(@RequestParam("id") Integer id, @RequestParam("offset") Integer offset) {
        List<Request> friendRequest = friendService.getFriendRequest(id,offset);
        return ResponseEntity.ok(friendRequest,friendRequest.size());
    }

    @GetMapping("/accept")
    public ResponseEntity acceptFriend(@RequestParam("id")Integer id,@RequestParam("friend") Integer friend){
        friendService.accept(id,friend);
        return ResponseEntity.ok();
    }

    @GetMapping("/reject")
    public ResponseEntity rejectFriend(@RequestParam("id")Integer id,@RequestParam("friend") Integer friend){
        friendService.reject(id,friend);
        return ResponseEntity.ok();
    }



}
