package com.lubsolution.store.callback;

public interface CallbackProcess {
    void onStart();

    void onError();

    void onSuccess(String name);
}
