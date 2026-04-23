package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Transaksi {
    private String id;
    private LocalDateTime tanggal;
    private String kasirUsername;
    private List<ItemTransaksi> items;
    private double diskon;
    private boolean diskonPersen;
    private double subtotal;
    private double total;
    private double bayar;
    private double kembalian;
    private MetodeBayar metodeBayar;

    public Transaksi() {
        this.items = new ArrayList<>();
    }

    public Transaksi(String id, LocalDateTime tanggal, String kasirUsername,
                     List<ItemTransaksi> items, double diskon, boolean diskonPersen,
                     double bayar, MetodeBayar metodeBayar) {
        this.id = id;
        this.tanggal = tanggal;
        this.kasirUsername = kasirUsername;
        this.items = items;
        this.diskon = diskon;
        this.diskonPersen = diskonPersen;
        this.bayar = bayar;
        this.metodeBayar = metodeBayar;
        recompute();
    }

    public void recompute() {
        this.subtotal = items.stream().mapToDouble(ItemTransaksi::getSubtotal).sum();
        double potongan = diskonPersen ? subtotal * diskon / 100.0 : diskon;
        this.total = Math.max(0, subtotal - potongan);
        this.kembalian = Math.max(0, bayar - total);
    }

    public double totalProfit() {
        return items.stream().mapToDouble(ItemTransaksi::profit).sum();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getTanggal() { return tanggal; }
    public void setTanggal(LocalDateTime tanggal) { this.tanggal = tanggal; }

    public String getKasirUsername() { return kasirUsername; }
    public void setKasirUsername(String kasirUsername) { this.kasirUsername = kasirUsername; }

    public List<ItemTransaksi> getItems() { return items; }
    public void setItems(List<ItemTransaksi> items) { this.items = items; }

    public double getDiskon() { return diskon; }
    public void setDiskon(double diskon) { this.diskon = diskon; }

    public boolean isDiskonPersen() { return diskonPersen; }
    public void setDiskonPersen(boolean diskonPersen) { this.diskonPersen = diskonPersen; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getBayar() { return bayar; }
    public void setBayar(double bayar) { this.bayar = bayar; }

    public double getKembalian() { return kembalian; }
    public void setKembalian(double kembalian) { this.kembalian = kembalian; }

    public MetodeBayar getMetodeBayar() { return metodeBayar; }
    public void setMetodeBayar(MetodeBayar metodeBayar) { this.metodeBayar = metodeBayar; }
}
