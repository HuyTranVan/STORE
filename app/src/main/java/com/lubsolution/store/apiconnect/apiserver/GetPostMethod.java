package com.lubsolution.store.apiconnect.apiserver;

import android.os.AsyncTask;
import android.view.View;

import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.Distributor;
import com.lubsolution.store.models.User;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.lubsolution.store.apiconnect.ApiUtil.getResponeArraySuccess;
import static com.lubsolution.store.apiconnect.ApiUtil.getResponeObjectSuccess;
import static com.lubsolution.store.apiconnect.ApiUtil.responeIsSuccess;


/**
 * Created by tranhuy on 7/22/16.
 */
public class GetPostMethod extends AsyncTask<String, Void, String> {
    private NewCallbackCustom mListener = null;
    private BaseModel mParam;
    private GetPostMethod main;

    public GetPostMethod(BaseModel param, NewCallbackCustom listener, int loadingtimes) {
        this.mListener = listener;
        this.mParam = param;
        this.main = this;

        UtilLoading.getInstance().showLoading(loadingtimes);

        if (!Util.checkInternetConnection()){
            main.cancel(true);
            Util.showLongSnackbar("No internet!",
                    "Try again",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new GetPostMethod(mParam, mListener, loadingtimes);
                        }
                    });

        }

    }

    @Override
    protected String doInBackground(String... s) {
        StringBuffer response = null;
        URL url = null;
        HttpURLConnection con = null;
        try {
            if (mParam.getString("method").equals("POST")){
                url = new URL(mParam.getString("url"));
                con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000);
                con.setConnectTimeout(10000);
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Content-Type", mParam.getBoolean("isjson") ? "application/json" : "application/x-www-form-urlencoded");
                con.setRequestProperty("x-wolver-accesstoken", User.getToken());
                con.setRequestProperty("x-wolver-accessid", User.getUserId());
                con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

                DataOutputStream wr = null;
                BufferedReader in = null;
                con.setDoOutput(true);


                wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(mParam.getString("param"));
                wr.flush();

                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                wr.close();

            }else if (mParam.getString("method").equals("GET") || mParam.getString("method").equals("DELETE")){
                url = new URL(mParam.getString("url"));
                con = (HttpURLConnection) url.openConnection();

                con.setReadTimeout(10000);
                con.setConnectTimeout(10000);
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("x-wolver-accesstoken", User.getToken());
                con.setRequestProperty("x-wolver-accessid", User.getUserId());
                con.setRequestProperty("x-wolver-debtid", Distributor.getDistributorId());

                BufferedReader inp = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine1;
                response = new StringBuffer();

                while ((inputLine1 = inp.readLine()) != null) {
                    response.append(inputLine1);
                }
                inp.close();
            }


        } catch (IOException e) {
            return e.toString();
        }

        return String.valueOf(response);
    }

    @Override
    protected void onPostExecute(String result) {
        UtilLoading.getInstance().stopLoading();
        if (Util.isJSONObject(result)) {
            BaseModel respone = new BaseModel(result);

            if (responeIsSuccess(respone)){
                if (mParam.getString("resultType").equals(Constants.TYPE_OBJECT)){
                    mListener.onResponse(getResponeObjectSuccess(respone), null);


                }else if (mParam.getString("resultType").equals(Constants.TYPE_ARRAY)){
                    mListener.onResponse(null, getResponeArraySuccess(respone));

                }

            }else {

                mListener.onError(respone.getString("message"));

                if (respone.getInt("status") == 203){
                    doRelogin();

                }else {
                    Constants.throwError(respone.getString("message"));
                }

            }

        }else {
            Constants.throwError(result);
            mListener.onError(result);

        }

    }



    private void doRelogin(){
        Util.showLongSnackbar("Sai TOKEN, đăng nhập lại",
                "Đăng nhập",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CustomCenterDialog.alertWithButton("Lỗi đăng nhập",
                                "Sai thông tin xác thực tài khoản, vui lòng đăng nhập lại",
                                "đồng ý",
                                new CallbackBoolean() {
                                    @Override
                                    public void onRespone(Boolean result) {
                                        if (result){
                                            Util.deleteAllImageExternalStorage();
                                            CustomSQL.clear();
                                            Transaction.gotoLoginActivityRight();
                                        }
                                    }
                                });
                    }
                });

    }

}


