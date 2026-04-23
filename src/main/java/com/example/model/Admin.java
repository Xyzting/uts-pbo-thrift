package com.example.model;

public class Admin extends User {
    public Admin() {}

    public Admin(String username, String password, String namaLengkap) {
        super(username, password, namaLengkap);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
