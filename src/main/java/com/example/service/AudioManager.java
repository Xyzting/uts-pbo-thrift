package com.example.service;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioManager {
    private static final AudioManager INSTANCE = new AudioManager();

    private MediaPlayer bgmPlayer;
    private final Map<String, AudioClip> sfxCache = new HashMap<>();
    private double volume = 0.5;
    private boolean enabled = true;

    private AudioManager() {}

    public static AudioManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        try {
            URL bgmUrl = getClass().getResource("/com/example/audio/bgm-lofi.mp3");
            if (bgmUrl != null) {
                Media media = new Media(bgmUrl.toExternalForm());
                bgmPlayer = new MediaPlayer(media);
                bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                bgmPlayer.setVolume(volume);
                bgmPlayer.setOnError(() -> {
                    System.err.println("[AudioManager] BGM error: " + bgmPlayer.getError());
                });
            } else {
                System.err.println("[AudioManager] bgm-lofi.mp3 not found, BGM disabled");
            }

            for (String name : List.of("click", "success", "error", "add-cart")) {
                URL url = getClass().getResource("/com/example/audio/" + name + ".wav");
                if (url != null) {
                    AudioClip clip = new AudioClip(url.toExternalForm());
                    clip.setVolume(volume);
                    sfxCache.put(name, clip);
                } else {
                    System.err.println("[AudioManager] " + name + ".wav not found, SFX '" + name + "' disabled");
                }
            }
        } catch (Exception e) {
            enabled = false;
            System.err.println("[AudioManager] init failed, audio disabled: " + e.getMessage());
        }
    }

    public void playBGM() {
        if (!enabled || bgmPlayer == null) return;
        bgmPlayer.play();
    }

    public void pauseBGM() {
        if (bgmPlayer == null) return;
        bgmPlayer.pause();
    }

    public boolean isBGMPlaying() {
        return bgmPlayer != null && bgmPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public void setVolume(double v) {
        this.volume = Math.max(0, Math.min(1.0, v));
        if (bgmPlayer != null) bgmPlayer.setVolume(volume);
        sfxCache.values().forEach(c -> c.setVolume(volume));
    }

    public double getVolume() {
        return volume;
    }

    public void playSFX(String name) {
        if (!enabled) return;
        AudioClip clip = sfxCache.get(name);
        if (clip != null) clip.play();
    }

    public void dispose() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.dispose();
        }
        sfxCache.clear();
    }
}
