package com.example.exception;

public class StokTidakCukupException extends RuntimeException {
    private final String namaBarang;
    private final int stokTersedia;

    public StokTidakCukupException(String namaBarang, int stokTersedia) {
        super("Stok " + namaBarang + " tinggal " + stokTersedia);
        this.namaBarang = namaBarang;
        this.stokTersedia = stokTersedia;
    }

    public String getNamaBarang() { return namaBarang; }
    public int getStokTersedia() { return stokTersedia; }
}
