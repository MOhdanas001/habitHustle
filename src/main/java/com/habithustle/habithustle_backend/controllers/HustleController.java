package com.habithustle.habithustle_backend.controllers;

import com.habithustle.habithustle_backend.DTO.SearchRequest;
import com.habithustle.habithustle_backend.services.HustleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/bet")
public class HustleController
{
    @Autowired
    private HustleService hustleService;

    @PostMapping("/create")
    public Object createHustle(@RequestBody SearchRequest.BetRequestDTO req){
        return hustleService.createBet(req);
    }


}
