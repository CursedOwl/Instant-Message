package com.im.feign.client;


import com.im.feign.entity.RedBag;
import com.im.feign.entity.ResponseEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="deal",url = "http://localhost:8082")
public interface DealClient {

    @GetMapping("/deal/red")
    public ResponseEntity sendRedBag(@RequestBody RedBag redBag);

    @GetMapping("/deal/grab")
    public ResponseEntity grabRedBag(Long account,Long redBagId);

}
