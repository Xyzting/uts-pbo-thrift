package com.example.controller;

import com.example.exception.StokTidakCukupException;
import com.example.exception.ValidationException;
import com.example.model.Barang;
import com.example.model.ItemTransaksi;
import com.example.model.Kategori;
import com.example.model.MetodeBayar;
import com.example.model.Transaksi;
import com.example.repository.BarangRepository;
import com.example.repository.TransaksiRepository;
import com.example.service.AudioManager;
import com.example.service.InventoryService;
import com.example.service.KasirService;
import com.example.util.AlertHelper;
import com.example.util.RupiahFormatter;
import com.example.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KasirController {

    @FXML private TextField searchField;
    @FXML private ComboBox<Kategori> kategoriFilter;
    @FXML private FlowPane produkPane;
    @FXML private VBox cartList;
    @FXML private Label subtotalLabel;
    @FXML private Label totalLabel;
    @FXML private Label kembalianLabel;
    @FXML private TextField diskonField;
    @FXML private ComboBox<String> diskonModeBox;
    @FXML private ComboBox<MetodeBayar> metodeBox;
    @FXML private TextField bayarField;
    @FXML private Label errorLabel;
    @FXML private Button checkoutButton;

    private final BarangRepository barangRepo = new BarangRepository();
    private final InventoryService inventoryService = new InventoryService(barangRepo);
    private final KasirService kasirService = new KasirService(barangRepo, new TransaksiRepository());

    private final ObservableList<ItemTransaksi> cart = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ObservableList<Kategori> kats = FXCollections.observableArrayList();
        kats.add(null);
        kats.addAll(Kategori.values());
        kategoriFilter.setItems(kats);
        kategoriFilter.getSelectionModel().select(null);

        diskonModeBox.setItems(FXCollections.observableArrayList("%", "Rp"));
        diskonModeBox.getSelectionModel().select(0);

        metodeBox.setItems(FXCollections.observableArrayList(MetodeBayar.values()));
        metodeBox.getSelectionModel().select(MetodeBayar.TUNAI);

        searchField.textProperty().addListener((o, a, b) -> loadProduk());
        kategoriFilter.valueProperty().addListener((o, a, b) -> loadProduk());
        diskonField.textProperty().addListener((o, a, b) -> recalc());
        diskonModeBox.valueProperty().addListener((o, a, b) -> recalc());
        bayarField.textProperty().addListener((o, a, b) -> recalc());

        errorLabel.setText("");
        loadProduk();
        recalc();
    }

    private void loadProduk() {
        produkPane.getChildren().clear();
        try {
            List<Barang> data;
            Kategori kat = kategoriFilter.getValue();
            if (kat != null) {
                data = inventoryService.filterByKategori(kat);
            } else {
                data = inventoryService.findAll();
            }
            String kw = searchField.getText();
            if (kw != null && !kw.isBlank()) {
                String lc = kw.toLowerCase();
                data = data.stream()
                        .filter(b ->
                            b.getNama().toLowerCase().contains(lc) ||
                            b.getBrand().toLowerCase().contains(lc))
                        .collect(Collectors.toList());
            }
            for (Barang b : data) {
                produkPane.getChildren().add(buildCard(b));
            }
        } catch (IOException e) {
            AlertHelper.showError("Error", "Gagal memuat produk: " + e.getMessage());
        }
    }

    private VBox buildCard(Barang b) {
        ImageView iv = new ImageView();
        iv.setFitWidth(140);
        iv.setFitHeight(140);
        iv.setPreserveRatio(true);
        loadBarangImage(iv, b.getPathGambar());

        Label nama = new Label(b.getNama());
        nama.setWrapText(true);
        nama.setMaxWidth(140);
        nama.setStyle("-fx-font-weight: bold;");
        Label brandUkuran = new Label(b.getBrand() + " • " + b.getUkuran().name());
        brandUkuran.setStyle("-fx-text-fill: #718096; -fx-font-size: 11px;");
        Label harga = new Label(RupiahFormatter.format(b.getHargaJual()));
        harga.setStyle("-fx-text-fill: #4F46E5; -fx-font-weight: bold;");
        Label stok = new Label("Stok: " + b.getStok());
        stok.setStyle("-fx-font-size: 11px;");

        Button addBtn = new Button("+ Keranjang");
        addBtn.getStyleClass().add("primary-button");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setDisable(b.getStok() <= 0);
        addBtn.setOnAction(e -> addToCart(b));

        VBox card = new VBox(6, iv, nama, brandUkuran, harga, stok, addBtn);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(160);
        return card;
    }

    private void loadBarangImage(ImageView iv, String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        Path p = Path.of("src/main/resources/com/example/images/products", fileName);
        if (Files.exists(p)) {
            iv.setImage(new Image(p.toUri().toString()));
            return;
        }
        var url = getClass().getResource("/com/example/images/products/" + fileName);
        if (url != null) iv.setImage(new Image(url.toExternalForm()));
    }

    private void addToCart(Barang b) {
        AudioManager.getInstance().playSFX("add-cart");
        ItemTransaksi existing = cart.stream()
                .filter(it -> it.getKodeBarang().equals(b.getKode()))
                .findFirst().orElse(null);
        if (existing != null) {
            if (existing.getQty() >= b.getStok()) {
                AudioManager.getInstance().playSFX("error");
                AlertHelper.showError("Stok", "Stok " + b.getNama() + " tinggal " + b.getStok());
                return;
            }
            existing.setQty(existing.getQty() + 1);
        } else {
            cart.add(new ItemTransaksi(b.getKode(), b.getNama(), b.getHargaBeli(), b.getHargaJual(), 1));
        }
        renderCart();
        recalc();
    }

    private void renderCart() {
        cartList.getChildren().clear();
        for (ItemTransaksi it : cart) {
            HBox row = new HBox(8);
            row.setStyle("-fx-padding: 6 0; -fx-border-color: #E2E8F0 transparent transparent transparent; -fx-border-width: 1 0 0 0;");
            VBox info = new VBox(2);
            Label nama = new Label(it.getNamaBarang());
            nama.setStyle("-fx-font-weight: bold;");
            Label harga = new Label(RupiahFormatter.format(it.getHargaJual()) + " × " + it.getQty());
            harga.setStyle("-fx-text-fill: #718096; -fx-font-size: 11px;");
            Label subtotal = new Label(RupiahFormatter.format(it.getSubtotal()));
            subtotal.setStyle("-fx-text-fill: #4F46E5;");
            info.getChildren().addAll(nama, harga, subtotal);

            Button minus = new Button("-");
            Button plus = new Button("+");
            Button hapus = new Button("✕");
            minus.getStyleClass().add("ghost-button");
            plus.getStyleClass().add("ghost-button");
            hapus.getStyleClass().add("ghost-button");

            minus.setOnAction(e -> {
                if (it.getQty() > 1) {
                    it.setQty(it.getQty() - 1);
                } else {
                    cart.remove(it);
                }
                AudioManager.getInstance().playSFX("click");
                renderCart();
                recalc();
            });
            plus.setOnAction(e -> {
                try {
                    Barang b = inventoryService.findByKode(it.getKodeBarang()).orElseThrow();
                    if (it.getQty() >= b.getStok()) {
                        AudioManager.getInstance().playSFX("error");
                        return;
                    }
                    it.setQty(it.getQty() + 1);
                    AudioManager.getInstance().playSFX("click");
                    renderCart();
                    recalc();
                } catch (IOException ex) {
                    AlertHelper.showError("Error", ex.getMessage());
                }
            });
            hapus.setOnAction(e -> {
                cart.remove(it);
                AudioManager.getInstance().playSFX("click");
                renderCart();
                recalc();
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            row.getChildren().addAll(info, spacer, minus, plus, hapus);
            cartList.getChildren().add(row);
        }
        if (cart.isEmpty()) {
            Label empty = new Label("Keranjang kosong");
            empty.getStyleClass().add("placeholder");
            cartList.getChildren().add(empty);
        }
    }

    private void recalc() {
        double subtotal = kasirService.hitungSubtotal(new ArrayList<>(cart));
        double diskon = parseDoubleSafe(diskonField.getText());
        boolean persen = "%".equals(diskonModeBox.getValue());
        double potongan = kasirService.hitungPotongan(subtotal, diskon, persen);
        double total = kasirService.hitungTotal(subtotal, potongan);
        double bayar = parseDoubleSafe(bayarField.getText());
        double kembalian = Math.max(0, bayar - total);

        subtotalLabel.setText(RupiahFormatter.format(subtotal));
        totalLabel.setText(RupiahFormatter.format(total));
        kembalianLabel.setText(RupiahFormatter.format(kembalian));
    }

    private double parseDoubleSafe(String s) {
        if (s == null || s.isBlank()) return 0;
        try {
            return Double.parseDouble(s.replace(".", "").replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FXML
    private void handleCheckout() {
        AudioManager.getInstance().playSFX("click");
        errorLabel.setText("");
        try {
            if (cart.isEmpty()) throw new ValidationException("Keranjang kosong");
            double diskon = parseDoubleSafe(diskonField.getText());
            boolean persen = "%".equals(diskonModeBox.getValue());
            double bayar = parseDoubleSafe(bayarField.getText());
            MetodeBayar metode = metodeBox.getValue();

            List<ItemTransaksi> items = new ArrayList<>();
            for (ItemTransaksi it : cart) {
                items.add(new ItemTransaksi(it.getKodeBarang(), it.getNamaBarang(),
                        it.getHargaBeli(), it.getHargaJual(), it.getQty()));
            }

            String username = SessionManager.getCurrentUser() != null
                    ? SessionManager.getCurrentUser().getUsername() : "unknown";
            Transaksi trx = kasirService.buatTransaksi(items, diskon, persen, bayar, metode, username);
            AudioManager.getInstance().playSFX("success");
            showStruk(trx);
            cart.clear();
            renderCart();
            diskonField.clear();
            bayarField.clear();
            recalc();
            loadProduk();
        } catch (StokTidakCukupException ex) {
            errorLabel.setText(ex.getMessage());
            AudioManager.getInstance().playSFX("error");
        } catch (ValidationException ex) {
            errorLabel.setText(ex.getMessage());
            AudioManager.getInstance().playSFX("error");
        } catch (IOException ex) {
            errorLabel.setText("Gagal menyimpan transaksi: " + ex.getMessage());
            AudioManager.getInstance().playSFX("error");
        }
    }

    private void showStruk(Transaksi trx) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(trx.getId()).append("\n");
        sb.append("Tanggal: ").append(trx.getTanggal().toString().replace("T", " ")).append("\n");
        sb.append("Kasir: ").append(trx.getKasirUsername()).append("\n");
        sb.append("------------------------------\n");
        for (ItemTransaksi it : trx.getItems()) {
            sb.append(it.getNamaBarang()).append(" ")
              .append(it.getQty()).append("×").append(RupiahFormatter.format(it.getHargaJual()))
              .append(" = ").append(RupiahFormatter.format(it.getSubtotal())).append("\n");
        }
        sb.append("------------------------------\n");
        sb.append("Subtotal: ").append(RupiahFormatter.format(trx.getSubtotal())).append("\n");
        if (trx.getDiskon() > 0) {
            sb.append("Diskon: ").append(trx.isDiskonPersen() ? trx.getDiskon() + "%" : RupiahFormatter.format(trx.getDiskon())).append("\n");
        }
        sb.append("Total: ").append(RupiahFormatter.format(trx.getTotal())).append("\n");
        sb.append("Metode: ").append(trx.getMetodeBayar()).append("\n");
        if (trx.getMetodeBayar() == MetodeBayar.TUNAI) {
            sb.append("Bayar: ").append(RupiahFormatter.format(trx.getBayar())).append("\n");
            sb.append("Kembalian: ").append(RupiahFormatter.format(trx.getKembalian())).append("\n");
        }
        AlertHelper.showInfo("Struk Transaksi", sb.toString());
    }
}
