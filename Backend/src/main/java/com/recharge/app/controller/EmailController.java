//package com.recharge.app.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import com.recharge.app.service.EmailService;
//
//@RestController
//@RequestMapping("/email")
//public class EmailController {
//
//    @Autowired
//    private EmailService emailService;
//
//    @PostMapping("/send")
//    public String sendEmail(@RequestParam String to, 
//                            @RequestParam String subject, 
//                            @RequestParam String body) {
//        emailService.sendEmail(to, subject, body);
//        return "Email sent successfully to " + to;
//    }
//}