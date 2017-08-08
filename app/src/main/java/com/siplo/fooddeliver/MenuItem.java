package com.siplo.fooddeliver;

import com.orm.SugarRecord;

/**
 * Created by asela on 5/17/17.
 */
public class MenuItem extends SugarRecord<MenuItem> {


    int menuId;
    int category;
    String name;
    double price;
    String imageName;
    String options;//"option_name:value,option_name:value.."
    int prepearedIn;

    public MenuItem()
    {
        this.category = 0;

        this.name = "name";
        this.price = 0.0;
        this.imageName ="";
        this.prepearedIn = 0;
    }

    public MenuItem(int menu_id,int category,String name, double price,String imageName,int prepearedIn,String options) {
        this.name = name;
        this.price = price;
        this.menuId = menu_id;
        this.category = category;
        this.imageName = imageName;
        this.prepearedIn = prepearedIn;
        this.options = options;
    }
}
