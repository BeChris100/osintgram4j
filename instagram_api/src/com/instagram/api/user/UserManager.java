package com.instagram.api.user;

public class UserManager {

    public static User login(String username, String password) {
        //https://www.instagram.com/api/v1/accounts/login/ajax?force_classic_login
        return new User(true, username, username);
    }

}
