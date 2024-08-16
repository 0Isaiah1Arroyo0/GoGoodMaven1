package com.yourdomain;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;

public class TwilioService {
    // Twilio credentials
    public static final String ACCOUNT_SID = "ACb4ce8fe448313ce3b953d1956a0e5a1e";
    public static final String AUTH_TOKEN = "23de67d8de291c63dad9a42665a5456b";
    public static final String SERVICE_SID = "VA02c35f07c277b10caf088627d4f746ff";

    public TwilioService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendVerificationCode(String phoneNumber) {
        Verification verification = Verification.creator(SERVICE_SID, phoneNumber, "sms").create();
        System.out.println(verification.getStatus());
    }

    public boolean verifyCode(String phoneNumber, String code) {
        VerificationCheck verificationCheck = VerificationCheck.creator(SERVICE_SID, phoneNumber)
                .setTo(code)
                .create();
        return "approved".equals(verificationCheck.getStatus());
    }
}
