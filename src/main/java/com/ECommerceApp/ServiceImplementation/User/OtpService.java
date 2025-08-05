package com.ECommerceApp.ServiceImplementation.User;

import com.ECommerceApp.ServiceImplementation.Order.EmailService;
import com.ECommerceApp.Util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
public class OtpService {
    @Autowired
    private EmailService mailService;
    private Map<String, Integer> otpStorage = new HashMap<>();

    public void sendOtpToEmail(String email) {
        int otp = generateOtp();
        otpStorage.put(email, otp);
        String userId = new SecurityUtils().getCurrentUserId();
        log.info("mail is: "+email+"    user id is :"+userId);
        String body = Integer.toString(otp);
        mailService.sendOtpEmail("iamanil3121@gmail.com", userId, body);
    }

    public boolean validateOtp(String email, long inputOtp) {
        Integer storedOtp = otpStorage.get(email);
        log.info("the mail is: "+email+"  input otp is: "+inputOtp+"   storedOtp is: "+storedOtp);
        return storedOtp != null && storedOtp == inputOtp;
    }

    private int generateOtp() {
        return (int)(Math.random() * 900000) + 100000;
    }

    public void clearOtp(String email) {
        otpStorage.remove(email);
    }
}

