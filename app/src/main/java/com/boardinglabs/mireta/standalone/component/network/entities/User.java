package com.boardinglabs.mireta.standalone.component.network.entities;

import com.google.android.gms.wallet.LineItem;

public class User extends BaseEntity{
    public String first_name;
    public String last_name;
    public String fullname;
    public String username;
    public String email;
    public boolean user_is_active;
    public boolean user_is_block;
    public String user_last_login;
    public boolean user_change_password;
    public boolean user_is_pristine;
    public Business business;
    public Role role;
    public StockLocation user_location;
}
