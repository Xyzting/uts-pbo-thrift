package com.example.model;

public class Barang {
    private String kode;
    private String nama;
    private Kategori kategori;
    private String brand;
    private Ukuran ukuran;
    private Kondisi kondisi;
    private double hargaBeli;
    private double hargaJual;
    private int stok;
    private String pathGambar;

    public Barang() {}

    public Barang(String kode, String nama, Kategori kategori, String brand,
                  Ukuran ukuran, Kondisi kondisi, double hargaBeli, double hargaJual,
                  int stok, String pathGambar) {
        this.kode = kode;
        this.nama = nama;
        this.kategori = kategori;
        this.brand = brand;
        this.ukuran = ukuran;
        this.kondisi = kondisi;
        this.hargaBeli = hargaBeli;
        this.hargaJual = hargaJual;
        this.stok = stok;
        this.pathGambar = pathGambar;
    }

    public String getKode() { return kode; }
    public void setKode(String kode) { this.kode = kode; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public Kategori getKategori() { return kategori; }
    public void setKategori(Kategori kategori) { this.kategori = kategori; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Ukuran getUkuran() { return ukuran; }
    public void setUkuran(Ukuran ukuran) { this.ukuran = ukuran; }

    public Kondisi getKondisi() { return kondisi; }
    public void setKondisi(Kondisi kondisi) { this.kondisi = kondisi; }

    public double getHargaBeli() { return hargaBeli; }
    public void setHargaBeli(double hargaBeli) { this.hargaBeli = hargaBeli; }

    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public String getPathGambar() { return pathGambar; }
    public void setPathGambar(String pathGambar) { this.pathGambar = pathGambar; }
}
