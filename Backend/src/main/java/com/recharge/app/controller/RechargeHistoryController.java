package com.recharge.app.controller;

import com.recharge.app.model.RechargeHistory;
import com.recharge.app.service.RechargeHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recharge")
public class RechargeHistoryController {

    @Autowired
    private RechargeHistoryService rechargeHistoryService;

    @PostMapping("/create")
    public RechargeHistory createRechargeHistory(@RequestBody RechargeHistory rechargeHistory) {
        return rechargeHistoryService.saveRechargeHistory(rechargeHistory);
    }
}
