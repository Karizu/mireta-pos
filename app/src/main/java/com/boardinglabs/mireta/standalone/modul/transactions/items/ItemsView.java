package com.boardinglabs.mireta.standalone.modul.transactions.items;

import com.boardinglabs.mireta.standalone.component.network.entities.Item;
import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;

import java.util.List;

import okhttp3.ResponseBody;

public interface ItemsView {
    void onSuccessGetItems(List<Item> transactionItems);
    void onSuccessGetNewItems(List<ItemVariants> transactionItems);
    void onSuccessCreateTransaction(ResponseBody responseBody);
}
