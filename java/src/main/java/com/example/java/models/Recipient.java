package com.example.java.models;

public class Recipient {
    public String civility;
    public String firstname;
    public String lastname;
    public String address_1;
    public String cell_phone;
    public String email;
    public long signature_mode;

    public Recipient(String civility, String firstname, String lastname, String address_1, String cell_phone, String email, long signature_mode) {
        this.civility = civility;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address_1 = address_1;
        this.cell_phone = cell_phone;
        this.email = email;
        this.signature_mode = signature_mode;
    }
}
