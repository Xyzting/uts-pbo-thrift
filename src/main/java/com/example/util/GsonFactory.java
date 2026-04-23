package com.example.util;

import com.example.adapter.LocalDateTimeAdapter;
import com.example.adapter.UserTypeAdapter;
import com.example.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;

public class GsonFactory {
    private static final Gson INSTANCE = new GsonBuilder()
            .registerTypeAdapter(User.class, new UserTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    private GsonFactory() {}

    public static Gson get() {
        return INSTANCE;
    }
}
