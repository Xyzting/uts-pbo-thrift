package com.example.controller;

import com.example.App;
import com.example.exception.AuthException;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.service.AudioManager;
import com.example.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService(new UserRepository());

    @FXML
    public void initialize() {
        errorLabel.setText("");
    }

    @FXML
    private void handleLogin() {
        AudioManager.getInstance().playSFX("click");
        errorLabel.setText("");
        try {
            User user = authService.login(usernameField.getText(), passwordField.getText());
            AudioManager.getInstance().playSFX("success");
            App.setRoot("dashboard");
        } catch (AuthException ex) {
            errorLabel.setText(ex.getMessage());
            AudioManager.getInstance().playSFX("error");
        } catch (IOException ex) {
            errorLabel.setText("Gagal membaca data pengguna");
            AudioManager.getInstance().playSFX("error");
        }
    }
}
