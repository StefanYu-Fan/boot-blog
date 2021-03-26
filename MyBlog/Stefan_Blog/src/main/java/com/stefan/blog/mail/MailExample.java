package com.stefan.blog.mail;

/**
 * Created by wly on 2018/3/7.
 */

public class MailExample {

    public static void main (String args[]) throws Exception {
        String email = "";
        String validateCode = "";
        SendEmail.sendEmailMessage(email,validateCode);

    }
}
