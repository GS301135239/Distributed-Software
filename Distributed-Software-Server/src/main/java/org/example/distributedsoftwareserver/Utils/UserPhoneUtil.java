package org.example.distributedsoftwareserver.Utils;

import java.util.regex.Pattern;
public class UserPhoneUtil {

    public static boolean isValidPhoneNumber(String phone) {
        if(phone == null || phone.length() != 11) {
            return false;
        }

        String regex = "^1[3-9]\\d{9}$";
        return Pattern.matches(regex, phone);
    }
}
