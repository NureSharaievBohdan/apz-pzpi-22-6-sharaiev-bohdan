package com.example.radguardmobile.api.response;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("access")
    private String token;
    private String message;

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}

