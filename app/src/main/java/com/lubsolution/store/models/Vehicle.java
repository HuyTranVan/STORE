package com.lubsolution.store.models;

import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.DataUtil;
import com.lubsolution.store.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Vehicle extends BaseModel{
    static List<BaseModel> mListVehicles = null;

    public Vehicle() {
        jsonObject = new JSONObject();
    }

    public Vehicle(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public Vehicle(String objOrder) {
        try {
            jsonObject = new JSONObject(objOrder);
        } catch (JSONException e) {
//            e.printStackTrace();
            jsonObject = new JSONObject();
        }
    }

    public String VehicletoString() {
        return jsonObject.toString();
    }

    public static List<String> getBrandListString(){
        List<BaseModel> brands = DataUtil.array2ListObject(CustomSQL.getString(Constants.VEHICLEBRAND_LIST));
        List<String> results = new ArrayList<>();
        for (int i=0; i<brands.size(); i++){
            results.add(brands.get(i).getString("name"));


        }
        return results;
    }

//    public static List<String> getBrandList(List<BaseModel> brands){
//        //List<BaseModel> brands = DataUtil.array2ListObject(CustomSQL.getString(Constants.VEHICLEBRAND_LIST));
//        List<String> results = new ArrayList<>();
//        for (int i=0; i<brands.size(); i++){
//            results.add(brands.get(i).getString("name"));
//
//
//        }
//        return results;
//    }

//    public static List<String> getBrandList(List<BaseModel> brands){
//        //List<BaseModel> brands = DataUtil.array2ListObject(CustomSQL.getString(Constants.VEHICLEBRAND_LIST));
//        List<String> results = new ArrayList<>();
//        for (int i=0; i<brands.size(); i++){
//            results.add(brands.get(i).getString("name"));
//
//
//        }
//        return results;
//    }

    public static void saveVehicleList(JSONArray vehiclebrand) {
        List<BaseModel> groups = DataUtil.array2ListBaseModel(vehiclebrand);
        CustomSQL.setString(Constants.VEHICLEBRAND_LIST, vehiclebrand.toString());

        List<BaseModel> mProducts = new ArrayList<>();
//        try {
            for (int i = 0; i < groups.size(); i++) {
                mProducts.addAll(groups.get(i).getList("vehicles"));
//                JSONObject object = groups.getJSONObject(i);
//                JSONArray arrayProduct = object.getJSONArray("product");
//
//                for (int ii = 0; ii < arrayProduct.length(); ii++) {
//                    mProducts.add(new BaseModel(arrayProduct.getJSONObject(ii)));
//                }

            }

            Util.getInstance().stopLoading(true);
            CustomSQL.setListBaseModel(Constants.PRODUCT_LIST, mProducts);

//            new DownloadImageMethod(mProducts, "image", "PRODUCT", new CallbackListObject() {
//                @Override
//                public void onResponse(List<BaseModel> list) {
//
//                }
//            }).execute();
//
//
//        } catch (JSONException e) {
//
//        }



        mListVehicles = null;
    }

//    public static List<BaseModel> getProductGroupList() {
//        List<BaseModel> mProductGroups = new ArrayList<>();
//
//        if (mListProductGroups == null) {
//            try {
//                JSONArray array = new JSONArray(CustomSQL.getString(Constants.PRODUCTGROUP_LIST));
//                for (int i = 0; i < array.length(); i++) {
//                    BaseModel productGroup = new BaseModel(array.getJSONObject(i));
//                    mProductGroups.add(productGroup);
//                }
//
//            } catch (JSONException e) {
//                return mProductGroups;
//            }
//
//        }
//
//        DataUtil.sortProductGroup(mProductGroups, false);
//        return mProductGroups;
//    }

}
