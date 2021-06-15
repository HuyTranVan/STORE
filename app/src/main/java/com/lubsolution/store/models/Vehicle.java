package com.lubsolution.store.models;

import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.DataUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Vehicle extends BaseModel{
    static List<BaseModel> mListVehicles = null;
    static List<BaseModel> mListBrand = null;


    //kind_id = 0 ? xe may : o to

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
        List<BaseModel> brands = DataUtil.array2ListObject(CustomSQL.getString(Constants.BRAND_LIST));
        List<String> results = new ArrayList<>();
        for (int i=0; i<brands.size(); i++){
            results.add(brands.get(i).getString("name"));


        }
        return results;
    }

    public static List<String> getVehicleList(List<BaseModel> brands){
        //List<BaseModel> brands = DataUtil.array2ListObject(CustomSQL.getString(Constants.VEHICLEBRAND_LIST));
        List<String> results = new ArrayList<>();
        for (int i=0; i<brands.size(); i++){
            results.add(brands.get(i).getString("name"));


        }
        return results;
    }

    public static List<BaseModel> getVehicleList(BaseModel brand){
        List<BaseModel> vehicles = CustomSQL.getListObject(Constants.VEHICLE_LIST);
        List<BaseModel> results = new ArrayList<>();
        for (int i=0; i<vehicles.size(); i++){
            if (brand.getInt("id") == vehicles.get(i).getInt("brand_id")){
                results.add(vehicles.get(i));
            }



        }
        return results;
    }

    public static List<BaseModel> getVehicleListWithNew(BaseModel brand){
        List<BaseModel> vehicles = CustomSQL.getListObject(Constants.VEHICLE_LIST);
        List<BaseModel> results = new ArrayList<>();
        for (int i=0; i<vehicles.size(); i++){
            if (brand.getInt("id") == vehicles.get(i).getInt("brand_id")){
                results.add(vehicles.get(i));
            }

        }
        BaseModel itemNew = new BaseModel();
        itemNew.put("id", 0);
        itemNew.put("name", "TẠO XE MỚI");

        results.add(0, itemNew);
        return results;
    }



    public static void saveVehicleList(JSONArray array){
        CustomSQL.setString(Constants.VEHICLE_LIST, array.toString());
        mListVehicles = null;
    }

    public static void saveBrandList(JSONArray brands) {
        CustomSQL.setString(Constants.BRAND_LIST, brands.toString());
       //List<BaseModel> groups = DataUtil.array2ListBaseModel(vehiclebrand);
        mListBrand = null;
//
//        List<BaseModel> mProducts = new ArrayList<>();
////        try {
//            for (int i = 0; i < groups.size(); i++) {
//                mProducts.addAll(groups.get(i).getList("vehicles"));
////                JSONObject object = groups.getJSONObject(i);
////                JSONArray arrayProduct = object.getJSONArray("product");
////
////                for (int ii = 0; ii < arrayProduct.length(); ii++) {
////                    mProducts.add(new BaseModel(arrayProduct.getJSONObject(ii)));
////                }
//
//            }
//
//            Util.getInstance().stopLoading(true);
//            CustomSQL.setListBaseModel(Constants.PRODUCT_LIST, mProducts);
//
////            new DownloadImageMethod(mProducts, "image", "PRODUCT", new CallbackListObject() {
////                @Override
////                public void onResponse(List<BaseModel> list) {
////
////                }
////            }).execute();
////
////
////        } catch (JSONException e) {
////
//        }





    }

}
