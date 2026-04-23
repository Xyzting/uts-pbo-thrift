package com.example.adapter;

import com.example.model.Admin;
import com.example.model.Kasir;
import com.example.model.User;
import com.google.gson.*;
import java.lang.reflect.Type;

public class UserTypeAdapter implements JsonSerializer<User>, JsonDeserializer<User> {
    @Override
    public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", src instanceof Admin ? "Admin" : "Kasir");
        obj.addProperty("username", src.getUsername());
        obj.addProperty("password", src.getPassword());
        obj.addProperty("namaLengkap", src.getNamaLengkap());
        return obj;
    }

    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("type").getAsString();
        String username = obj.get("username").getAsString();
        String password = obj.get("password").getAsString();
        String namaLengkap = obj.has("namaLengkap") ? obj.get("namaLengkap").getAsString() : "";
        if ("Admin".equals(type)) {
            return new Admin(username, password, namaLengkap);
        }
        return new Kasir(username, password, namaLengkap);
    }
}
