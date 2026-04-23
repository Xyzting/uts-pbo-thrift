package com.example.model;

public class Kasir extends User {
    public Kasir() {}

    public Kasir(String username, String password, String namaLengkap) {
        super(username, password, namaLengkap);
    }

    @Override
    public String getRole() {
        return "KASIR";
    }
}
