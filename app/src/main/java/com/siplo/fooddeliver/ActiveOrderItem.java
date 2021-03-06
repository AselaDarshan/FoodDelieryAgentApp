package com.siplo.fooddeliver;

import com.orm.SugarRecord;

/**
 * Created by asela on 6/15/17.
 */
public class ActiveOrderItem extends SugarRecord {
    String itemName;
    String itemId;
    String qty;
    String state;
    String tableId;
    long orderId;
    double price;

    public ActiveOrderItem(String itemName, String qty, String state, String tableId, long orderId, String itemId, double price) {
        this.itemName = itemName;
        this.qty = qty;
        this.state = state;
        this.tableId = tableId;
        this.orderId = orderId;
        this.itemId = itemId;
        this.price = price;
    }

    public ActiveOrderItem(){}
}
