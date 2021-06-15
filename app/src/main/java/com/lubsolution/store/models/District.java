package com.lubsolution.store.models;

import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackListObject;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.utils.DataUtil;

import org.json.JSONObject;

import java.util.List;

import static com.lubsolution.store.activities.BaseActivity.createGetParam;

/**
 * Created by macos on 9/16/17.
 */

public class District extends BaseModel {
    static List<String> mListDistricts = null;

    public District() {
        jsonObject = null;
    }

    public District(JSONObject objOrder) {
        jsonObject = objOrder;
    }

    public String DistricttoString() {
        return jsonObject.toString();
    }

    public static void getDistrictList(int province_id, CallbackListObject listener) {
        BaseModel param = createGetParam(ApiUtil.DISTRICTS() + province_id, true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                DataUtil.sortbyStringKey("name", list, false);
                listener.onResponse(list);
            }

            @Override
            public void onError(String error) {

            }
        },0).execute();


    }

}
