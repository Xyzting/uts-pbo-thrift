package com.example.model;

public class ItemTransaksi {
    private String kodeBarang;
    private String namaBarang;
    private double hargaBeli;
    private double hargaJual;
    private int qty;
    private double subtotal;

    public ItemTransaksi() {}

    public ItemTransaksi(String kodeBarang, String namaBarang, double hargaBeli,
                         double hargaJual, int qty) {
        this.kodeBarang = kodeBarang;
        this.namaBarang = namaBarang;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.qty = qty;
        this.subtotal = hargaJual * qty;
    }

    public double profit() {
        return (hargaJual - hargaBeli) * qty;
    }

    public String getKodeBarang() { return kodeBarang; }
    public void setKodeBarang(String kodeBarang) { this.kodeBarang = kodeBarang; }

    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }

    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }

    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }

    public int getQty() { return qty; }
    public void setQty(int qty) {
        this.qty = qty;
        this.subtotal = hargaJual * qty;
    }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
