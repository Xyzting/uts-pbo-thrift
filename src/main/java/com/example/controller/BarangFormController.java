package com.example.controller;

import com.example.exception.ValidationException;
import com.example.model.Barang;
import com.example.model.Kategori;
import com.example.model.Kondisi;
import com.example.model.Ukuran;
import com.example.service.AudioManager;
import com.example.service.InventoryService;
import com.example.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class BarangFormController {

    @FXML private Label titleLabel;
    @FXML private TextField kodeField;
    @FXML private TextField namaField;
    @FXML private ComboBox<Kategori> kategoriBox;
    @FXML private TextField brandField;
    @FXML private ComboBox<Ukuran> ukuranBox;
    @FXML private ComboBox<Kondisi> kondisiBox;
    @FXML private TextField hargaBeliField;
    @FXML private TextField hargaJualField;
    @FXML private Spinner<Integer> stokSpinner;
    @FXML private ImageView previewImage;
    @FXML private Label fileNameLabel;
    @FXML private Label errorLabel;

    private InventoryService service;
    private Barang existing;
    private String pathGambar;

    @FXML
    public void initialize() {
        kategoriBox.setItems(FXCollections.observableArrayList(Kategori.values()));
        ukuranBox.setItems(FXCollections.observableArrayList(Ukuran.values()));
        kondisiBox.setItems(FXCollections.observableArrayList(Kondisi.values()));
        stokSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999, 1));
        errorLabel.setText("");
    }

    public void setContext(InventoryService service, Barang existing) {
        this.service = service;
        this.existing = existing;
        try {
            if (existing == null) {
                titleLabel.setText("Tambah Barang");
                kodeField.setText(service.generateNextKode());
            } else {
                titleLabel.setText("Edit Barang");
                kodeField.setText(existing.getKode());
                namaField.setText(existing.getNama());
                kategoriBox.setValue(existing.getKategori());
                brandField.setText(existing.getBrand());
                ukuranBox.setValue(existing.getUkuran());
                kondisiBox.setValue(existing.getKondisi());
                hargaBeliField.setText(String.valueOf((long) existing.getHargaBeli()));
                hargaJualField.setText(String.valueOf((long) existing.getHargaJual()));
                stokSpinner.getValueFactory().setValue(existing.getStok());
                pathGambar = existing.getPathGambar();
                fileNameLabel.setText(pathGambar == null ? "Belum ada gambar" : pathGambar);
                loadPreview(pathGambar);
            }
        } catch (IOException e) {
            errorLabel.setText("Gagal menginisialisasi form: " + e.getMessage());
        }
    }

    @FXML
    private void pilihGambar() {
        AudioManager.getInstance().playSFX("click");
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pilih Gambar Barang");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        Stage stage = (Stage) kodeField.getScene().getWindow();
        File picked = chooser.showOpenDialog(stage);
        if (picked == null) return;
        try {
            Path targetDir = Path.of("src/main/resources/com/example/images/products");
            Files.createDirectories(targetDir);
            String ext = picked.getName().substring(picked.getName().lastIndexOf('.'));
            String newName = UUID.randomUUID() + ext;
            Path target = targetDir.resolve(newName);
            Files.copy(picked.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            pathGambar = newName;
            fileNameLabel.setText(newName);
            loadPreview(newName);
        } catch (IOException e) {
            AudioManager.getInstance().playSFX("error");
            AlertHelper.showError("Error", "Gagal menyalin gambar: " + e.getMessage());
        }
    }

    private void loadPreview(String fileName) {
        if (fileName == null) {
            previewImage.setImage(null);
            return;
        }
        Path p = Path.of("src/main/resources/com/example/images/products", fileName);
        if (Files.exists(p)) {
            previewImage.setImage(new Image(p.toUri().toString()));
        } else {
            var url = getClass().getResource("/com/example/images/products/" + fileName);
            previewImage.setImage(url != null ? new Image(url.toExternalForm()) : null);
        }
    }

    @FXML
    private void handleSimpan() {
        AudioManager.getInstance().playSFX("click");
        errorLabel.setText("");
        try {
            double hargaBeli = parseDouble(hargaBeliField.getText(), "Harga beli");
            double hargaJual = parseDouble(hargaJualField.getText(), "Harga jual");

            Barang b = new Barang(
                kodeField.getText(),
                namaField.getText() == null ? "" : namaField.getText().trim(),
                kategoriBox.getValue(),
                brandField.getText() == null ? "" : brandField.getText().trim(),
                ukuranBox.getValue(),
                kondisiBox.getValue(),
                hargaBeli,
                hargaJual,
                stokSpinner.getValue(),
                pathGambar == null ? "placeholder.png" : pathGambar
            );

            if (existing == null) {
                service.create(b);
            } else {
                service.update(b);
            }
            AudioManager.getInstance().playSFX("success");
            close();
        } catch (ValidationException ex) {
            errorLabel.setText(ex.getMessage());
            AudioManager.getInstance().playSFX("error");
        } catch (NumberFormatException ex) {
            errorLabel.setText(ex.getMessage());
            AudioManager.getInstance().playSFX("error");
        } catch (IOException ex) {
            errorLabel.setText("Gagal menyimpan: " + ex.getMessage());
            AudioManager.getInstance().playSFX("error");
        }
    }

    private double parseDouble(String s, String fieldName) {
        if (s == null || s.isBlank()) throw new NumberFormatException(fieldName + " wajib diisi");
        try {
            return Double.parseDouble(s.replace(".", "").replace(",", ""));
        } catch (NumberFormatException e) {
            throw new NumberFormatException(fieldName + " harus angka");
        }
    }

    @FXML
    private void handleBatal() {
        AudioManager.getInstance().playSFX("click");
        close();
    }

    private void close() {
        Stage stage = (Stage) kodeField.getScene().getWindow();
        stage.close();
    }
}
