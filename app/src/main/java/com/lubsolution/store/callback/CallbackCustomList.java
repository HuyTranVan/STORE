package com.lubsolution.store.callback;

import com.lubsolution.store.models.BaseModel;

import java.util.List;


/**
 * Created by Engine on 12/26/2016.
 */
public interface CallbackCustomList {
    void onResponse(List<BaseModel> results);

    void onError(String error);
}