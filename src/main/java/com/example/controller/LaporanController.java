package com.example.controller;

import com.example.model.ItemTransaksi;
import com.example.model.Transaksi;
import com.example.repository.TransaksiRepository;
import com.example.service.AudioManager;
import com.example.service.LaporanService;
import com.example.util.AlertHelper;
import com.example.util.RupiahFormatter;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LaporanController {

    @FXML private DatePicker fromPicker;
    @FXML private DatePicker toPicker;
    @FXML private Label totalSalesLabel;
    @FXML private Label jumlahTrxLabel;
    @FXML private Label totalProfitLabel;
    @FXML private Label bestSellerLabel;
    @FXML private TableView<Transaksi> trxTable;
    @FXML private TableColumn<Transaksi, String> colId;
    @FXML private TableColumn<Transaksi, String> colTanggal;
    @FXML private TableColumn<Transaksi, String> colKasir;
    @FXML private TableColumn<Transaksi, Integer> colItems;
    @FXML private TableColumn<Transaksi, String> colTotal;
    @FXML private TableColumn<Transaksi, String> colMetode;

    private final LaporanService service = new LaporanService(new TransaksiRepository());
    private final ObservableList<Transaksi> data = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        LocalDate today = LocalDate.now();
        fromPicker.setValue(today.minusDays(30));
        toPicker.setValue(today);

        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colTanggal.setCellValueFactory(c -> new SimpleStringProperty(DATE_FMT.format(c.getValue().getTanggal())));
        colKasir.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getKasirUsername()));
        colItems.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getItems().size()));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(RupiahFormatter.format(c.getValue().getTotal())));
        colMetode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMetodeBayar().name()));

        trxTable.setRowFactory(tv -> {
            TableRow<Transaksi> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    AudioManager.getInstance().playSFX("click");
                    showDetail(row.getItem());
                }
            });
            return row;
        });

        trxTable.setItems(data);
        terapkan();
    }

    @FXML
    private void handleTerapkan() {
        AudioManager.getInstance().playSFX("click");
        terapkan();
    }

    private void terapkan() {
        try {
            LocalDate from = fromPicker.getValue();
            LocalDate to = toPicker.getValue();
            if (from == null || to == null || from.isAfter(to)) {
                AlertHelper.showError("Error", "Range tanggal tidak valid");
                return;
            }
            List<Transaksi> list = service.filterByRange(from, to);
            data.setAll(list);
            totalSalesLabel.setText(RupiahFormatter.format(service.totalSales(list)));
            totalProfitLabel.setText(RupiahFormatter.format(service.totalProfit(list)));
            jumlahTrxLabel.setText(String.valueOf(service.jumlahTransaksi(list)));
            bestSellerLabel.setText(service.bestSeller(list).orElse("-"));
        } catch (IOException e) {
            AlertHelper.showError("Error", "Gagal memuat laporan: " + e.getMessage());
        }
    }

    private void showDetail(Transaksi trx) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(trx.getId()).append("\n");
        sb.append("Tanggal: ").append(DATE_FMT.format(trx.getTanggal())).append("\n");
        sb.append("Kasir: ").append(trx.getKasirUsername()).append("\n");
        sb.append("Metode: ").append(trx.getMetodeBayar()).append("\n");
        sb.append("--------------------------------\n");
        for (ItemTransaksi it : trx.getItems()) {
            sb.append(it.getNamaBarang())
              .append(" (").append(it.getKodeBarang()).append(") ")
              .append(it.getQty()).append("× ")
              .append(RupiahFormatter.format(it.getHargaJual()))
              .append(" = ").append(RupiahFormatter.format(it.getSubtotal()))
              .append("\n");
        }
        sb.append("--------------------------------\n");
        sb.append("Subtotal: ").append(RupiahFormatter.format(trx.getSubtotal())).append("\n");
        if (trx.getDiskon() > 0) {
            sb.append("Diskon: ")
              .append(trx.isDiskonPersen() ? trx.getDiskon() + "%" : RupiahFormatter.format(trx.getDiskon()))
              .append("\n");
        }
        sb.append("Total: ").append(RupiahFormatter.format(trx.getTotal())).append("\n");
        sb.append("Profit: ").append(RupiahFormatter.format(trx.totalProfit())).append("\n");
        AlertHelper.showInfo("Detail Transaksi", sb.toString());
    }
}
