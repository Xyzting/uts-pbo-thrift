package com.example.controller;

import com.example.service.AudioManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class SettingsController {

    @FXML private Slider volumeSlider;
    @FXML private Label volumeLabel;
    @FXML private Button bgmButton;

    @FXML
    public void initialize() {
        AudioManager am = AudioManager.getInstance();
        volumeSlider.setValue(am.getVolume());
        updateVolumeLabel(am.getVolume());
        updateBgmButton();
        volumeSlider.valueProperty().addListener((obs, oldV, newV) -> {
            am.setVolume(newV.doubleValue());
            updateVolumeLabel(newV.doubleValue());
        });
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
        updateBgmButton();
    }

    private void updateBgmButton() {
        bgmButton.setText(AudioManager.getInstance().isBGMPlaying() ? "⏸ Pause" : "▶ Play");
    }

    private void updateVolumeLabel(double v) {
        volumeLabel.setText(((int) (v * 100)) + "%");
    }
}
