package com.instagram.api.user;

public class User {

    private final boolean self;
    private final String id, username;

    protected User(boolean self, String id, String username) {
        this.self = self;
        this.id = id;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public boolean isSelf() {
        return self;
    }
}
