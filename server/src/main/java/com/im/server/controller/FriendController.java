package com.im.server.controller;

import com.im.server.dto.UserDto;
import com.im.server.entity.ResponseEntity;
import com.im.server.service.FriendService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {

    private FriendService friendService;

    @GetMapping("/{id}")
    public ResponseEntity getFriends(@PathVariable("id") Integer id, @RequestParam("offset") Integer offset) {
        List<UserDto> friends = friendService.getFriends(id,offset);
        return ResponseEntity.ok(friends,friends.size());
    }

    @GetMapping("/add/{id}")
    public ResponseEntity addFriend(@PathVariable("id") Integer id, @RequestParam("friendId") Integer friendId) {
        friendService.addFriend(id,friendId);
        return ResponseEntity.ok();
    }
}
