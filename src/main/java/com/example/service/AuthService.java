package com.example.service;

import com.example.exception.AuthException;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.util.SessionManager;
import java.io.IOException;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password) throws AuthException, IOException {
        if (username == null || username.isBlank()) {
            throw new AuthException("Username tidak boleh kosong");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthException("Password tidak boleh kosong");
        }
        Optional<User> found = userRepository.findByUsername(username.trim());
        if (found.isEmpty()) {
            throw new AuthException("Username tidak ditemukan");
        }
        User user = found.get();
        if (!user.getPassword().equals(password)) {
            throw new AuthException("Password salah");
        }
        SessionManager.setCurrentUser(user);
        return user;
    }

    public void logout() {
        SessionManager.clear();
    }
}
