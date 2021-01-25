package com.lubsolution.store.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.lubsolution.store.R;
import com.lubsolution.store.adapter.HomeAdapter;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackClickAdapter;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.ProductGroup;
import com.lubsolution.store.models.User;
import com.lubsolution.store.models.Vehicle;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomBottomDialog;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by macos on 9/15/17.
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener, CallbackClickAdapter, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rvItems;
    private CircleImageView imgUser;
    private TextView tvFullname, tvCash, tvMonth, tvHaveNewProduct,tvRole ;
    private LinearLayout lnUser;
    private SwipeRefreshLayout swipeRefreshLayout;

    protected List<BaseModel> listTempBill = new ArrayList<>();
    protected List<BaseModel> listTempImport = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getResourceLayout() {
        return R.layout.activity_home;
    }

    @Override
    public int setIdContainer() {
        return R.id.home_parent;
    }

    @Override
    public void findViewById() {
        rvItems = (RecyclerView) findViewById(R.id.home_rvitems);
        imgUser = findViewById(R.id.home_icon);
        tvFullname = findViewById(R.id.home_fullname);
        lnUser = findViewById(R.id.home_user);
        tvCash = findViewById(R.id.home_cash);
        tvRole = findViewById(R.id.home_role);
        tvMonth = findViewById(R.id.home_month);
        tvHaveNewProduct = findViewById(R.id.home_new_product);
        swipeRefreshLayout = findViewById(R.id.home_refresh);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void initialData() {
        Util.getInstance().setCurrentActivity(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueDark));
        tvFullname.setText( User.getFullName());
        String role = User.getCurrentRoleString();
        tvRole.setText(  role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase());
        tvMonth.setText(String.format("Thực thu %s:", Util.CurrentMonthYear()));
        if (!Util.checkImageNull(User.getImage())) {
            Glide.with(this).load(User.getImage()).centerCrop().into(imgUser);
        }

        checkPermission();

    }

    private void createListItem() {
        HomeAdapter adapter = new HomeAdapter(HomeActivity.this);
        Util.createGridRV(rvItems, adapter, 3);

        if (CustomSQL.getBoolean(Constants.LOGIN_SUCCESS)) {
            CustomSQL.setBoolean(Constants.LOGIN_SUCCESS, false);

            swipeRefreshLayout.setRefreshing(true);
            loadCurrentData(new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    checkNewProductUpdated(new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            suggestChangePassword();
                        }
                    }, 0, true);


                }

            },2);


        }else {
            checkNewProductUpdated(null, 1, true);

        }
    }

    private void loadOverView() {
        List<BaseModel> params = new ArrayList<>();
        long start = Util.TimeStamp1(Util.Current01MonthYear());
        long end = Util.TimeStamp1(Util.Next01MonthYear());

//        params.add(DataUtil.createListPaymentParam(start, end));
//        SystemConnect.loadListObject(params, new CallbackCustomListList() {
//            @Override
//            public void onResponse(List<List<BaseModel>> results) {
//                double paid = DataUtil.sumValueFromList(results.get(0), "paid");
//                tvCash.setText(Util.getStringIcon(Util.FormatMoney(paid), "    ", R.string.icon_usd));
//
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, true);
    }

    @Override
    public void addEvent() {
        lnUser.setOnClickListener(this);
        tvHaveNewProduct.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_user:
                Transaction.gotoSettingActivity();

                break;

            case R.id.home_new_product:
                loadCurrentData(new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result){
                            Util.showToast("Đồng bộ thành công");
                        }
                    }
                }, 1);

                break;



        }
    }

    @Override
    public void onBackPressed() {
        mFragment = getSupportFragmentManager().findFragmentById(R.id.home_parent);

        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        } else if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);

        } else {
            if (doubleBackToExitPressedOnce) {
                this.finish();
            }

            this.doubleBackToExitPressedOnce = true;
            Util.showToast("Ấn Back để thoát khỏi ứng dụng");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1500);

        }
    }

    @Override
    public void onRespone(String data, int position) {
        switch (position) {
            case 0:
                Transaction.gotoMainShopActivity();

                break;

            case 1:
//                if (User.checkUserWarehouse())
//                    Transaction.gotoStatisticalActivity();

                break;

            case 2:
//                Transaction.gotoWarehouseActivity();

                break;

            case 3:
                Util.showToast("Chưa hỗ trợ");

                break;

            case 4:
                choiceSetupItem();


                break;

            case 5:
                if (CustomSQL.getBoolean(Constants.IS_ADMIN)) {
                    Transaction.gotoShopActivity();
                }

                break;
        }


    }

    private void loadCurrentData(CallbackBoolean listener, int load) {
        BaseModel param = createGetParam(ApiUtil.CATEGORIES(), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                ProductGroup.saveProductGroupList(result.getJSONArray("ProductGroup"));
                Vehicle.saveBrandList(result.getJSONArray("Brands"));
                Vehicle.saveVehicleList(result.getJSONArray("Vehicles"));

                CustomSQL.setLong(Constants.LAST_PRODUCT_UPDATE, result.getLong("LastProductUpdate"));
                tvHaveNewProduct.setVisibility(View.GONE);

                if (listener != null){
                    listener.onRespone(true);
                }

            }

            @Override
            public void onError(String error) {

            }
        }, load).execute();

    }

    private void choiceSetupItem() {
        CustomBottomDialog.choiceListObject(null ,
                Constants.homeSettingSetup(),
                "text",
                new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        switch (object.getInt("position")) {
//                            case 0:
//                                if (CustomSQL.getBoolean(Constants.IS_ADMIN)) {
//                                    Transaction.gotoShopActivity();
//                                }
//                                break;

                            case 0:
                                if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                                    //Transaction.gotoUserActivity(false);
                                }
                                break;

                            case 1:
                                if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                                    Transaction.gotoProductGroupActivity();
                                }
                                break;

                            case 2:
                                Transaction.gotoProductActivity();
                                break;

                            case 3:
                                if (User.getCurrentRoleId() == Constants.ROLE_ADMIN) {
                                    Transaction.gotoVehicleActivity();
                                }

                                break;

                            case 4:
                                Transaction.gotoShopActivity();

                                break;


                        }

                    }
                }, null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Util.getInstance().setCurrentActivity(this);
        mFragment = getSupportFragmentManager().findFragmentById(R.id.home_parent);
//        checkNewProductUpdated(new CallbackListObject() {
//            @Override
//            public void onResponse(List<BaseModel> list) {
//                updateTempBillVisibility(list);
//
////                if (mFragment != null && mFragment instanceof TempBillFragment) {
////                    ((TempBillFragment) mFragment).reloadData();
////
////                }
//            }
//        }, new CallbackListObject() {
//            @Override
//            public void onResponse(List<BaseModel> list) {
//                updateTempImportVisibility(list);
//            }
//        });


    }

    private void checkNewProductUpdated(CallbackBoolean listener, int load, boolean redirect) {
        long start = Util.TimeStamp1(Util.Current01MonthYear());
        long end = Util.TimeStamp1(Util.Next01MonthYear());

        BaseModel param = createGetParam(String.format(ApiUtil.PRODUCT_LASTEST() , start, end), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                swipeRefreshLayout.setRefreshing(false);
//                updateTempBillVisibility(DataUtil.listTempBill(DataUtil.array2ListObject(result.getString("tempBills"))));
//                updateTempImportVisibility(DataUtil.filterListTempImport(DataUtil.array2ListObject(result.getString("tempImport"))));
                //tvCash.setText(String.format("%s đ", Util.FormatMoney(result.getDouble("paymentInMonth"))));

                if (result.getLong("lastProductUpdate") != null && result.getLong("lastProductUpdate") > CustomSQL.getLong(Constants.LAST_PRODUCT_UPDATE)) {
                    tvHaveNewProduct.setVisibility(View.VISIBLE);
                    CustomCenterDialog.alertWithButtonCanceled("Thông tin thay đổi",
                            "Cài đặt hệ thống vừa được điều chỉnh! \n Đồng bộ lại hệ thống với thiết bị của bạn",
                            "ĐỒNG Ý",
                            true,
                            null);

                } else {
                    tvHaveNewProduct.setVisibility(View.GONE);
                }

                if (listener != null){
                    listener.onRespone(true);
                }

                if (redirect){
                    //redirectOtherScreen();
                }

            }

            @Override
            public void onError(String error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, load).execute();
    }



    @SuppressLint("WrongConstant")
    private void checkPermission() {
        Activity context = Util.getInstance().getCurrentActivity();
        if (PermissionChecker.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


            CustomCenterDialog.alertWithCancelButton("Cấp quyền truy cập!",
                    "Ứng dụng cần bạn đồng ý các quyền truy cập sau để tiếp tục",
                    "đồng ý",
                    "hủy",
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result) {
                                ActivityCompat.requestPermissions(context, new String[]{
                                        //Manifest.permission.CALL_PHONE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA,

                                }, Constants.REQUEST_PERMISSION);

                            } else {
                                logout(null);

                            }
                        }

                    });

        } else {
            createListItem();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constants.REQUEST_PERMISSION) {
            if (grantResults.length > 0) {

                boolean hasDenied = false;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        hasDenied = true;
                        break;
                    }
                }

                if (!hasDenied) {
                    createListItem();

                } else {
                    Util.showToast("Cấp quyền truy cập không thành công!");
                    CustomCenterDialog.alertWithCancelButton("Cấp quyền truy cập!",
                            "Ứng dụng chưa được cấp quyền đầy đủ",
                            "Cấp lại",
                            "hủy",
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result) {
                                        checkPermission();

                                    } else {
                                        logout(null);
                                    }

                                }

                            });

                }

            }
        }

    }


    @Override
    public void onRefresh() {
        checkNewProductUpdated(null, 0, false);
//        checkNewProductUpdated(new CallbackListObject() {
//            @Override
//            public void onResponse(List<BaseModel> list) {
//                //updateTempBillVisibility(list);
//            }
//        }, new CallbackListObject() {
//            @Override
//            public void onResponse(List<BaseModel> list) {
//                //updateTempImportVisibility(list);
//            }
//        });
        loadOverView();
    }

    protected boolean checkWarehouse() {
        BaseModel user = User.getCurrentUser();
        if (User.getCurrentUser().getInt("warehouse_id") == 0) {
            CustomCenterDialog.alert(null, "Cập nhật thông tin kho hàng nhân viên để tiếp tục thao tác", "đồng ý");
            return false;
        } else {
            return true;
        }
    }

    private void suggestChangePassword(){
        if (User.getCurrentUser().hasKey("password") && CustomSQL.getString(Constants.USER_PASSWORD).equals("Aa123456")){
            CustomCenterDialog.alertWithCancelButton("Đổi mật khẩu!",
                    "Bạn đang sử dụng mật khẩu mặc định. Vui lòng đổi sang mật khẩu khác để bảo mật hơn!!!",
                    "đồng ý",
                    "lúc khác",
                    new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result){
                                changePassword();
                            }
                        }
                    });

        }else {
            CustomSQL.removeKey(Constants.USER_PASSWORD);
        }
    }


}
