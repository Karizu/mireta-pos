package com.boardinglabs.mireta.standalone.component.network.entities;


import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemPost;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;

import java.util.List;

public class NewTransactionPost {
    public long total_discount;
    public String location_id;
    public String transaction_code;
    public long total_qty;
    public long total_price;
    public int payment_type;
    public String payment_method;
    public int status;
    public String location_operation_id;
    public String transaction_type;
    public String payment_account;
    public List<ItemPost> items;

    public NewTransactionPost(String location_id, String transaction_code, long total_qty, long total_discount, String total_price, int payment_type, String payment_method, int status, List<ItemPost> items, String location_operation_id, String transaction_type, String payment_account) {
        this.location_id = location_id;
        this.location_operation_id = location_operation_id;
        this.transaction_code = transaction_code;
        this.payment_type = payment_type;
        this.payment_method = payment_method;
        this.status = status;
        this.total_price = Long.parseLong(total_price);
        this.total_qty = total_qty;
        this.transaction_type = transaction_type;
        this.total_discount = total_discount;
        this.items = items;
        this.payment_account = payment_account;
    }
}
