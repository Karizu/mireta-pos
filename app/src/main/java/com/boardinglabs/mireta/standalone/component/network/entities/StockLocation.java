package com.boardinglabs.mireta.standalone.component.network.entities;

import com.boardinglabs.mireta.standalone.component.network.entities.Items.Brand;
import com.boardinglabs.mireta.standalone.component.network.entities.Trx.Location;

public class StockLocation {
    public String id;
    public String location_id;
    public String user_id;
    public String brand_id;
    public boolean add_stock_allowed;
    public boolean point_of_sale_allowed;
    public String name;
    public String address;
    public String telp;
    public Location location;
    public Business business;
    public Brand brand;
}
