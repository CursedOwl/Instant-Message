package com.im.deal.web;


import com.im.deal.entity.RedBag;
import com.im.deal.entity.ResponseEntity;
import com.im.deal.service.RedBagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.util.function.Tuple2;

@Slf4j
@RestController
@RequestMapping("/deal")
public class DealController {

    @Autowired
    private RedBagService redBagService;

    @PostMapping("/red")
    public ResponseEntity send(@RequestBody RedBag redBag){
        log.info("Controller RedBag:{}",redBag);
        Tuple2<Boolean, String> cb = redBagService.sendRedBag(redBag);
        if(cb.getT1()){
            return ResponseEntity.ok();
        }else {
            return ResponseEntity.fail(cb.getT2());
        }

    }

    @GetMapping("/grab")
    public ResponseEntity grab(Long account,Long redBagId){
        redBagService.grabRedBag(account,redBagId);
        return ResponseEntity.ok();
    }
}
