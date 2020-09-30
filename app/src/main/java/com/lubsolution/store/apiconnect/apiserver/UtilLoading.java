package com.lubsolution.store.apiconnect.apiserver;

import android.graphics.Color;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.Util;

public class UtilLoading {
    private static UtilLoading util;
    private KProgressHUD cDialog;

    public static synchronized UtilLoading getInstance() {
        if (util == null)
            util = new UtilLoading();

        return util;
    }

    public boolean isLoading() {
        if (cDialog != null && cDialog.isShowing())
            return true;
        return false;
    }

//    public void showLoading() {
//        createLoading("Đang xử lý...");
//    }

    public void showLoading(String text) {
        int times = CustomSQL.getInt(Constants.LOADING_TIMES) + 1;

        if (times >0){
            CustomSQL.setInt(Constants.LOADING_TIMES, times);
            createLoading(text);
        }


    }

    public void showLoading(int times) {
        if (times >0) {
            CustomSQL.setInt(Constants.LOADING_TIMES, times);
            createLoading("Đang xử lý...");

        }else {
            stopLoading();
        }

    }

//    public void showLoading(boolean show) {
//        if (show) {
//            int times = CustomSQL.getInt(Constants.LOADING_TIMES) + 1;
//
//            if (times >0){
//                CustomSQL.setInt(Constants.LOADING_TIMES, times);
//                createLoading("Đang xử lý...");
//            }
//
//
//        }else {
//            stopLoading();
//        }
//
//    }

    public void createLoading(final String message) {
        if (!isLoading()){
            cDialog = KProgressHUD.create(Util.getInstance().getCurrentActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setDetailsLabel(message)
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setBackgroundColor(Color.parseColor("#40000000"))
                    .setDimAmount(0.5f)
                    .show();
        }

    }

    public void stopLoading() {
        int times = CustomSQL.getInt(Constants.LOADING_TIMES);
        if (times ==  0 || times ==1){
            CustomSQL.setInt(Constants.LOADING_TIMES, 0);
            if (cDialog != null && cDialog.isShowing() || cDialog != null){
                cDialog.dismiss();
                cDialog = null;
            }

        }else {
            CustomSQL.setInt(Constants.LOADING_TIMES, times - 1);

        }

    }



}
