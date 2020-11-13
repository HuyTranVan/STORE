package com.lubsolution.store.models;

import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomSQL;

import org.json.JSONObject;

/**
 * Created by macos on 9/16/17.
 */

public class Shop extends BaseModel {

    public Shop() {
        jsonObject = null;
    }

    public Shop(JSONObject objOrder) {
        jsonObject = objOrder;
    }


    public static String getShopId() {
        String id_Shop = "";
        BaseModel currentShop = CustomSQL.getBaseModel(Constants.SHOP);

        if (currentShop != null) {
            id_Shop = String.valueOf(currentShop.getInt("id"));
        }
        return id_Shop;
    }

    public static int getId() {
        int id_Shop = 0;
        BaseModel currentShop = CustomSQL.getBaseModel(Constants.SHOP);

        if (currentShop != null) {
            id_Shop = currentShop.getInt("id");
        }
        return id_Shop;
    }

    public static String getName() {
        String name = "";
        BaseModel currentShop = CustomSQL.getBaseModel(Constants.SHOP);

        if (currentShop != null) {
            name = currentShop.getString("name");
        }
        return name;

    }

    public static String getImage() {
        BaseModel currentShop = CustomSQL.getBaseModel(Constants.SHOP);

        return currentShop.getString("image");
    }

    public static int getLocationId() {
        int location = 0;
        BaseModel currentShop = CustomSQL.getBaseModel(Constants.SHOP);

        if (currentShop != null) {
            location = currentShop.getInt("location");
//            location = 79;
        }
        return location;

    }

    public static BaseModel getObject() {
        String name = "";
        BaseModel currentShop = CustomSQL.getBaseModel(Constants.SHOP);

        if (currentShop != null) {
            return currentShop;
        }
        return new BaseModel();

    }


}
