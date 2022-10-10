package com.example.java.models;

public class Contract {
    public String name;
    public long contract_definition_id;
    public String vendor_email;
    public String message_title;
    public String message_body;
    public boolean keep_on_move;
    public long auto_close;

    public Contract(String name, long contract_definition_id, String vendor_email, String message_title, String message_body, long auto_close) {
        this.name = name;
        this.contract_definition_id = contract_definition_id;
        this.vendor_email = vendor_email;
        this.message_title = message_title;
        this.message_body = message_body;
        this.auto_close = auto_close;
    }
}
