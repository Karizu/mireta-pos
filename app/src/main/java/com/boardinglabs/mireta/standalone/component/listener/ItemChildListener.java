package com.boardinglabs.mireta.standalone.component.listener;

import com.boardinglabs.mireta.standalone.component.network.entities.ItemVariants.ItemVariants;

public interface ItemChildListener {
    void itemClicked(int position, ItemVariants itemVariants);
    void itemDeleted(int position, ItemVariants itemVariants);
    void itemAdd(int position, ItemVariants itemVariants);
    void itemMinus(int position, ItemVariants itemVariants);
}
