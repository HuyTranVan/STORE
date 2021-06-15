package com.lubsolution.store.models;

import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackListObject;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomSQL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.lubsolution.store.activities.BaseActivity.createGetParam;

/**
 * Created by macos on 9/16/17.
 */

public class Province extends BaseModel {
    static List<Province> mListProvinces = null;

    public Province() {
        jsonObject = null;
    }

    public Province(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String ProvincetoString() {
        return jsonObject.toString();
    }

    public static void saveProvinceList(JSONArray province) {
        CustomSQL.setString(Constants.PROVINCE_LIST, province.toString());
        mListProvinces = null;
    }

    public static void getProvincetList(CallbackListObject listener) {
        BaseModel param = createGetParam(ApiUtil.PROVINCES(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onResponse(list);
            }

            @Override
            public void onError(String error) {

            }
        },0).execute();


    }

    public static int getDistrictId(String district) {
        int id = 0;

        try {
            JSONArray array = new JSONArray(CustomSQL.getString(Constants.PROVINCE_LIST));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.getString("name").equals(district)) {
                    id = object.getInt("provinceid");
                    break;
                }
            }

        } catch (JSONException e) {
            return id;
        }

        return id;
    }
}
