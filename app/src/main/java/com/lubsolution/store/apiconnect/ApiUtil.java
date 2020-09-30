package com.lubsolution.store.apiconnect;

import com.lubsolution.store.BuildConfig;
import com.lubsolution.store.models.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macos on 9/14/17.
 */

public class ApiUtil {
    public final static String DEFAULT_RANGE = "?page=%d&size=%d";

//    public final static String BASE_URL = BuildConfig.DEBUG_FLAG ? "http://192.168.1.29/" : BuildConfig.SERVER_URL;
    public final static String BASE_URL = BuildConfig.SERVER_URL;
    public final static String DMS_HOST_LINK(String id){
        return "http://dmslub.com?id=" + id;
    }
    public final static String MAP_API = "https://maps.googleapis.com/maps/api/";

    public final static String GOOGLESHEET_CREDENTIALS_FILE_PATH = "/credentials.json";
    public final static String GOOGLESHEET_TOKENS_PATH = "tokens";
//    public final static String STATISTICAL_SHEET_KEY = Util.getInstance().getCurrentActivity().getResources().getString(R.string.sheet_key1);
    public final static String STATISTICAL_SHEET_TAB = "%s!A%d:z";
    public final static String STATISTICAL_SHEET_TAB1 = "Sheet1!A%d:z";
    public final static String STATISTICAL_SHEET_TAB2 = "Sheet2!A%d:z";
//    public final static String SCANNER_SHEET_KEY = Util.getInstance().getCurrentActivity().getResources().getString(R.string.sheet_key2);
    public final static String SCANNER_DISTRIBUTOR_TAB = "DISTRIBUTOR!A1:z";
    public final static String SCANNER_CODE_TAB = "CODE!A%d:z";

    public final static String PROVINCES = BASE_URL + "store/token/util/provinces";
    public final static String DISTRICTS = BASE_URL + "store/token/util/districts?provinceid=";
//Login
    public final static String LOGIN_PARAM = "phone=%s&password=%s&fcm_token=%s";
    public final static String LOGIN(){
        return BASE_URL + "store/token/system/Login";
    }

    public final static String CHECK_LOGIN(){
        return BASE_URL + "store/token/system/CheckLogin";
    }

//Logout
    public final static String LOGOUT(){
        return BASE_URL + "store/token/system/Logout";
    }
//PRODUCT GROUP
    public final static String PRODUCT_GROUPS(){
        return BASE_URL + "store/token/productgroup/ProductgroupList";
    }

    public final static String PRODUCTGROUP_CREATE_PARAM = "%sname=%s";
    public final static String PRODUCT_GROUP_NEW(){
        return BASE_URL + "store/token/productgroup/ProductgroupNew";
    }

    public final static String PRODUCT_GROUP_DELETE(){
        return BASE_URL + "store/token/productgroup/ProductgroupDelete?id=";
    }
//PRODUCT
    public final static String PRODUCTS(){
        return BASE_URL + "store/token/product/ProductList";
    }

    public final static String PRODUCT_CREATE_PARAM = "%sname=%s&promotion=%s&unitPrice=%s&purchasePrice=%s&volume=%s&productGroup.id=%d&image=%s&basePrice=%s&unitInCarton=%s";
    public final static String PRODUCT_NEW(){
        return BASE_URL + "store/token/product/ProductNew";
    }

    public final static String PRODUCT_DELETE(){
        return BASE_URL + "store/token/product/ProductDelete?id=";
    }
    public final static String PRODUCT_LASTEST(){
        return BASE_URL + "store/token/system/LastProductUpdate?from=%s&to=%s";
    }

    public final static String WAREHOUSES(){
        return BASE_URL + "store/token/warehouse/WarehouseList";
    }

    public final static String WAREHOUSE_CREATE_PARAM = "%sname=%s&user_id=%d&isMaster=%d";
    public final static String WAREHOUSE_NEW(){
        return BASE_URL + "store/token/warehouse/WarehouseNew";
    }


    public final static String INVENTORIES(){
        return BASE_URL + "store/token/warehouse/InventoryList?id=%d";
    }
    public final static String INVENTORY_EDIT_QUANTITY(){
        return BASE_URL + "store/token/warehouse/InventoryEditQuantity";
    }
    public final static String INVENTORY_EDIT_QUANTITY_PARAM = "id=%d&quantity=%s";

    public final static String IMPORT_NEW(){
        return BASE_URL + "store/token/warehouse/ImportNew";
    }
    public final static String IMPORTS(){
        return BASE_URL + "store/token/warehouse/ImportList?page=%d&size=%d&warehouse_id=%d";
    }
    public final static String IMPORT_DELETE(){
        return BASE_URL + "store/token/warehouse/ImportDelete?id=";
    }

    public final static String USERS(){
        return BASE_URL + "store/token/user/UserList";
    }

    public final static String USER_CREATE_PARAM = "%sdisplayName=%s&gender=%d&email=%s&phone=%s&role=%d&image=%s&warehouse_id=%d&warehouse_name=%s";
    public final static String USER_NEW(){
        return BASE_URL + "store/token/user/UserNew";
    }

    public final static String USER_CHANGE_PASS_PARAM = "id=%d&current_password=%s&new_password=%s";
    public final static String USER_CHANGE_PASS(){
        return BASE_URL + "store/token/user/UserChangePassword";
    }
    public final static String USER_DEFAULT_PASS(){
        return BASE_URL + "store/token/user/UserDefaultPassword?user_id=%d";
    }

    public final static String USER_DEFAULT_PASS_PARAM = "user_id=%d";

    public final static String STATUS(){
        return BASE_URL + "store/token/statu/StatusList";
    }
    public final static String STATUS_CREATE_PARAM = "%sname=%s&color=%s&defaultStatus=%s";

    public final static String STATUS_NEW(){
        return BASE_URL + "store/token/statu/StatusNew";
    }
    public final static String STATUS_DELETE(){
        return BASE_URL + "store/token/statu/StatusDelete?id=";
    }
    
    public final static String CUSTOMERS(int page, int size){
        return BASE_URL + "store/token/customer/CustomerList" + String.format(ApiUtil.DEFAULT_RANGE, page, size);
    }

    public final static String CUSTOMERS_NEAREST(String lat, String lng, int page, int size){
        return BASE_URL + "store/token/customer/CustomerNearest"
                + String.format(ApiUtil.DEFAULT_RANGE, page, size)
                + String.format("&lat=%s&lng=%s", lat, lng);
    }

    public final static String CUSTOMER_ORDERED(int user_id, int page, int size){
        return BASE_URL + "store/token/customer/CustomerOrderedList"
                + String.format(ApiUtil.DEFAULT_RANGE, page, size)
                + "&user_id=" + user_id;
    }

    public final static String CUSTOMER_CREATE_PARAM = "%sname=%s&signBoard=%s&address=%s&phone=%s&street=%s&note=%s&district=%s&province=%s&lat=%s&lng=%s&volumeEstimate=%s&shopType=%s&status_id=%d&distributor_id=%s&checkinCount=%d";
    public final static String CUSTOMER_NEW(){
        return BASE_URL + "store/token/customer/CustomerNew";
    }


    public final static String CUSTOMERS_HAVEDEBT = BASE_URL + "store/token/customer/CustomerHaveDebtList";
    //public final static String CUSTOMER_NEW = BASE_URL + "store/token/customer/CustomerNew";
    public final static String CUSTOMER_DELETE(){
        return BASE_URL + "store/token/customer/CustomerDelete?id=";
    }

    public final static String CUSTOMER_GETDETAIL(){
        return BASE_URL + "store/token/customer/CustomerDetail?id=";
    }

    public final static String CUSTOMER_TEMP_NEW_PARAM = "customer_id=%d&user_id=%d";
    public final static String CUSTOMER_TEMP_NEW(){
        return BASE_URL + "store/token/customer/CustomerTempNew";
    }

    public final static String CUSTOMER_WAITING_LIST(){
        return BASE_URL + "store/token/customer/WaitingList";
    }
    //public final static String CUSTOMER_ORDERED = BASE_URL + "store/token/customer/CustomerOrderedList";

    public final static String DISTRIBUTOR_DETAIL(){
        return BASE_URL + "store/token/distributor/DistributorDetail?id=";
    }

    public final static String DISTRIBUTOR_CREATE_PARAM = "%scompany=%s&address=%s&phone=%s&website=%s&thanks=%s&image=%s";
    public final static String DISTRIBUTOR_NEW(){
        return BASE_URL + "store/token/distributor/DistributorNew";
    }

    public final static String SCHECKIN_CREATE_PARAM = "customer_id=%d&rating=%d&note=%s&user_id=%d&nextVisit=%d&meetOwner=%d";
    public final static String CHECKIN_NEW(){
        return BASE_URL + "store/token/customer/CheckinNew";
    }
    public final static String CHECKIN_DELETE(){
        return BASE_URL + "store/token/customer/CheckinDelete?id=";
    }


    public final static String CATEGORIES(){
        return BASE_URL + "store/token/system/CategoryList";
    }
    public final static String STATISTICALS(){
        return BASE_URL + "store/token/system/StatisticalList?from=%s&to=%s";
    }
    //public final static String STATISTICAL_PARAM = "?from=%s&to=%s";

//    public final static String BILLS = BASE_URL + "store/token/bill/BillList";
//    public final static String BILLS_HAVE_PAYMENT = BASE_URL + "store/token/bill/BillListHavePayment";
    public final static String BILL_NEW(){
        return BASE_URL + "store/token/bill/BillNew";
    }
    public final static String BILL_DELETE(){
        return BASE_URL + "store/token/bill/BillDelete?id=";
    }

    public final static String PAY_PARAM = "customerId=%d&paid=%s&billId=%d&userId=%d&note=%s&payByReturn=%d&user_collect=%d";
    public final static String PAY_NEW() {
        return BASE_URL + "store/token/bill/PayNew";
    }
    public final static String PAYMENTS(){
        return BASE_URL + "store/token/bill/PaymentList?from=%s&to=%s";
    }

    public final static String PAYMENT_DELETE(){
        return BASE_URL + "store/token/bill/PaymentDelete?id=";
    }

    public final static String RESET_PASSWORD_PARAM = "phone=%s&email=%s";
    public final static String RESET_PASSWORD(){
        return BASE_URL + "store/token/system/ResetPassword";
    }


//    public final static String DEBT_NEW = BASE_URL + "store/token/bill/DebtNew";
//    public final static String DEBT_PARAM = "debt=%s&user_id=%d&customer_id=%d&distributor_id=%d";


    public static boolean responeIsSuccess(BaseModel respone) {
        if (!respone.isNull("status") && respone.getInt("status") == 200) {
            return true;

        } else {
            return false;
        }
    }

    public static BaseModel getResponeObjectSuccess(BaseModel respone) {
        return new BaseModel(respone.getJsonObject("data"));

    }

    public static List<BaseModel> getResponeArraySuccess(BaseModel respone) {
        List<BaseModel> list = new ArrayList<>();
        JSONArray array = respone.getJSONArray("data");
        try {
            for (int i = 0; i < array.length(); i++) {
                list.add(new BaseModel(array.getJSONObject(i)));
            }

        } catch (JSONException e) {
            return new ArrayList<>();
        }

        return list;

    }



}



