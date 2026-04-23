package com.example.repository;

import com.example.util.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class JsonRepository<T> {
    protected final Path file;
    protected final Type listType;
    protected final Gson gson;
    protected List<T> cache;

    protected JsonRepository(String fileName, TypeToken<List<T>> typeToken) {
        this.file = Path.of("data", fileName);
        this.listType = typeToken.getType();
        this.gson = GsonFactory.get();
        this.cache = null;
    }

    public List<T> findAll() throws IOException {
        if (cache == null) {
            loadFromDisk();
        }
        return new ArrayList<>(cache);
    }

    protected void loadFromDisk() throws IOException {
        if (!Files.exists(file)) {
            cache = new ArrayList<>(seed());
            saveToDisk();
            return;
        }
        String json = Files.readString(file);
        if (json.isBlank()) {
            cache = new ArrayList<>();
            return;
        }
        List<T> loaded = gson.fromJson(json, listType);
        cache = loaded != null ? new ArrayList<>(loaded) : new ArrayList<>();
    }

    public void saveAll(List<T> items) throws IOException {
        this.cache = new ArrayList<>(items);
        saveToDisk();
    }

    protected void saveToDisk() throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, gson.toJson(cache, listType));
    }

    protected List<T> seed() {
        return Collections.emptyList();
    }

    public void reload() throws IOException {
        cache = null;
        loadFromDisk();
    }
}
