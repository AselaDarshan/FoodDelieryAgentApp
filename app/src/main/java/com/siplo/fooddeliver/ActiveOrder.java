package com.siplo.fooddeliver;

import android.util.Log;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asela on 6/15/17.
 */
public class ActiveOrder extends SugarRecord  {
    String tableId;



    Double total;
    String phoneNumber;
    String location;

    boolean isCompleted;
    @Ignore
    List<ActiveOrderItem> itemList;


    public ActiveOrder(){}
    public ActiveOrder(String tableId,String phoneNumber,String location) {
        this.tableId = tableId;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.isCompleted = false;
        this.itemList = new ArrayList<ActiveOrderItem>();
        this.total=0.0;


    }
    public List<ActiveOrderItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<ActiveOrderItem> itemList) {
        this.itemList = itemList;
    }
    public void changeState(String id,String state){
        for(ActiveOrderItem item:itemList){
            Log.d("active_order","item id"+id);
            if(item.itemId.equals(id)){
                item.state = state;
                Log.d("active_order","state changed: "+id+" "+state);
                return;
            }
        }
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

}
