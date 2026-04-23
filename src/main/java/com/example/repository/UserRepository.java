package com.example.repository;

import com.example.model.Admin;
import com.example.model.Kasir;
import com.example.model.User;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserRepository extends JsonRepository<User> {
    public UserRepository() {
        super("users.json", new TypeToken<List<User>>() {});
    }

    @Override
    protected List<User> seed() {
        return List.of(
            new Admin("admin", "admin123", "Admin Toko"),
            new Kasir("kasir", "kasir123", "Kasir Satu")
        );
    }

    public Optional<User> findByUsername(String username) throws IOException {
        return findAll().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }
}
