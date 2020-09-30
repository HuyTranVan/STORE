package com.lubsolution.store.callback;

import com.lubsolution.store.models.BaseModel;

import java.util.List;


public interface CallbackListObject {
    void onResponse(List<BaseModel> list);
}
