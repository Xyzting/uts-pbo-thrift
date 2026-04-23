package com.example.controller;

import com.example.App;
import com.example.model.User;
import com.example.service.AudioManager;
import com.example.util.AlertHelper;
import com.example.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class DashboardController {

    @FXML private StackPane contentPane;
    @FXML private Label userLabel;
    @FXML private Slider volumeSlider;
    @FXML private ToggleButton bgmToggle;
    @FXML private Button navInventory;
    @FXML private Button navKasir;
    @FXML private Button navLaporan;
    @FXML private Button navSettings;

    @FXML
    public void initialize() {
        User current = SessionManager.getCurrentUser();
        if (current != null) {
            userLabel.setText(current.getNamaLengkap() + " [" + current.getRole() + "]");
        }
        boolean isAdmin = SessionManager.isAdmin();
        navInventory.setVisible(isAdmin);
        navInventory.setManaged(isAdmin);
        navLaporan.setVisible(isAdmin);
        navLaporan.setManaged(isAdmin);

        volumeSlider.setValue(AudioManager.getInstance().getVolume());
        volumeSlider.valueProperty().addListener((obs, oldV, newV) ->
            AudioManager.getInstance().setVolume(newV.doubleValue()));

        updateBgmToggleText();
    }

    @FXML
    private void toggleBGM() {
        AudioManager am = AudioManager.getInstance();
        if (am.isBGMPlaying()) {
            am.pauseBGM();
        } else {
            am.playBGM();
        }
        am.playSFX("click");
        updateBgmToggleText();
    }

    private void updateBgmToggleText() {
        bgmToggle.setText(AudioManager.getInstance().isBGMPlaying() ? "⏸" : "▶");
        bgmToggle.setSelected(AudioManager.getInstance().isBGMPlaying());
    }

    @FXML
    private void openInventory() {
        AudioManager.getInstance().playSFX("click");
        loadView("inventory");
    }

    @FXML
    private void openKasir() {
        AudioManager.getInstance().playSFX("click");
        loadView("kasir");
    }

    @FXML
    private void openLaporan() {
        AudioManager.getInstance().playSFX("click");
        loadView("laporan");
    }

    @FXML
    private void openSettings() {
        AudioManager.getInstance().playSFX("click");
        loadView("settings");
    }

    @FXML
    private void handleLogout() {
        AudioManager.getInstance().playSFX("click");
        if (!AlertHelper.showConfirm("Logout", "Yakin mau logout?")) return;
        SessionManager.clear();
        try {
            App.setRoot("login");
        } catch (IOException e) {
            AlertHelper.showError("Error", "Gagal memuat halaman login");
        }
    }

    private void loadView(String name) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/com/example/view/" + name + ".fxml"));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            AlertHelper.showError("Error", "Gagal memuat halaman " + name + ": " + e.getMessage());
        } catch (NullPointerException e) {
            AlertHelper.showError("Belum tersedia", "Halaman " + name + " belum dibuat.");
        }
    }
}
