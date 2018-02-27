package com.atik_faysal.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by USER on 2/22/2018.
 */

public class ShoppingItemModel
{
        String itemName,quantity;
        String price;

        public ShoppingItemModel(String name,String quantity,String price)
        {
                this.itemName = name;
                this.quantity = quantity;
                this.price = price;
        }

        public String getItemName() {
                return itemName;
        }

        public void setItemName(String itemName) {
                this.itemName = itemName;
        }

        public String getQuantity() {
                return quantity;
        }

        public void setQuantity(String quantity) {
                this.quantity = quantity;
        }

        public String getPrice() {
                return price;
        }

        public void setPrice(String price) {
                this.price = price;
        }
}
