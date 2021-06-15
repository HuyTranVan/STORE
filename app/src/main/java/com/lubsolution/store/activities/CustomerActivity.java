package com.lubsolution.store.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lubsolution.store.R;
import com.lubsolution.store.adapter.ViewpagerCustomerAdapter;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.CallbackString;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.libraries.FitScrollWithFullscreen;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.Product;
import com.lubsolution.store.models.Shop;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.DataUtil;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener, CallbackObject {
    private ImageView btnBack;
    protected Button btnSubmit;
    private TextView tvCheckInStatus, tvTime, tvTrash, tvPrint;
    protected TextView tvTitle, tvIconKind, tvDebt, tvPaid, tvTotal, tvBDF, btnShopCart, tvFilter, tvFilterIcon;
    private RelativeLayout coParent;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private HorizontalScrollView scrollOverView;
    private SmoothProgressBar smLoading;
    private RelativeLayout rlStatusGroup;
    private LinearLayout lnFilter;

    protected BaseModel currentCustomer = null;
    protected List<BaseModel> listBills = new ArrayList<>();
    protected List<BaseModel> listBillDetail = new ArrayList<>();
    protected List<BaseModel> listDebtBill = new ArrayList<>();
    private long countTime;
    private Fragment mFragment;
    protected List<String> mYears = new ArrayList<>();
    private ViewpagerCustomerAdapter pageAdapter;
    protected BaseModel currentBill, tempBill = null;
    protected Double currentDebt = 0.0;
    private Handler mHandlerUpdateCustomer = new Handler();
    private boolean haschange = false;
    private int currentPosition = 0;
    private long start = Util.TimeStamp1(Util.Current01MonthYear());
    private int currentCheckedTime = 1;
    protected List<BaseModel> listInitialProduct = new ArrayList<>();
    protected List<BaseModel> listProducts = new ArrayList<>();
    protected List<BaseModel> listProductGroups = new ArrayList<>();

//    protected CustomerBillsFragment billsFragment;
    protected CustomerCartFragment cartFragment;
    protected CustomerInfoFragment infoFragment;

//    protected CustomerProductFragment productFragment;
//    protected CustomerPaymentFragment paymentFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            FitScrollWithFullscreen.assistActivity(this, 1);

        }

    }

    Thread threadShowTime = new Thread() {
        @Override
        public void run() {
            try {
                while (!threadShowTime.isInterrupted()) {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTime.setText(Util.MinuteString(countTime));
                            countTime += 1000;

                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };

    @Override
    public int getResourceLayout() {
        return R.layout.activity_customer;
    }

    @Override
    public int setIdContainer() {
        return R.id.customer_parent;
    }

    @Override
    public void findViewById() {
        coParent = (RelativeLayout) findViewById(R.id.customer_parent);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            coParent.setPadding(0, 0, 0, Util.getNavigationBarHeight());
        }

        btnBack = (ImageView) findViewById(R.id.icon_back);
        btnShopCart = (TextView) findViewById(R.id.customer_shopcart);
        tvTrash = (TextView) findViewById(R.id.icon_more);
        tvTitle = (TextView) findViewById(R.id.customer_title);
        tvIconKind = findViewById(R.id.icon_kind);
        tvDebt = (TextView) findViewById(R.id.customer_debt);
        tvCheckInStatus = findViewById(R.id.customer_checkin_status);
        tvTime = findViewById(R.id.customer_checkin_time);
        rlStatusGroup = findViewById(R.id.customer_checkin_status_group);
        viewPager = findViewById(R.id.customer_viewpager);
        tabLayout = findViewById(R.id.customer_tabs);
        //tvDebt = findViewById(R.id.customer_debt);
        tvPaid = findViewById(R.id.customer_paid);
        tvTotal = findViewById(R.id.customer_total);
        tvBDF = findViewById(R.id.customer_bdf);
        scrollOverView = findViewById(R.id.customer_overview);
        smLoading = findViewById(R.id.customer_loading);
        //tvAddress = findViewById(R.id.customer_address);
        lnFilter = findViewById(R.id.customer_filter);
        tvFilter = findViewById(R.id.customer_filter_text);
        tvPrint = findViewById(R.id.customer_print);
        tvFilterIcon = findViewById(R.id.customer_filter_icon_close);

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        tvTrash.setOnClickListener(this);
        tvPrint.setOnClickListener(this);
        tvFilterIcon.setOnClickListener(this);
        btnShopCart.setOnClickListener(this);
        tvDebt.setOnClickListener(this);
        tvPaid.setOnClickListener(this);
        tvPaid.setOnLongClickListener(this);
        lnFilter.setOnClickListener(this);
        Util.textViewEvent(tvFilter, new CallbackString() {
            @Override
            public void Result(String s) {
                if (!s.equals("")) {
                    tvFilter.setPadding(Util.convertSdpToInt(R.dimen._15sdp), 0, 0, 0);
                } else {
                    tvFilter.setPadding(0, 0, 0, 0);
                }
            }
        });

    }

    @Override
    public void initialData() {
        loadCustomerDetail(CustomSQL.getInt(Constants.CUSTOMER_ID));

        tvTrash.setVisibility(Util.isAdmin()? View.VISIBLE : View.GONE);
        rlStatusGroup.setVisibility(View.GONE);

        //countTime = Util.CurrentTimeStamp() - CustomSQL.getLong(Constants.CHECKIN_TIME);

        setupViewPager(viewPager);
        //tabLayout.setupWithViewPager(viewPager);
        updateTabTitle();

        loadListProductGroup();
        loadListProduct();

    }

    private void loadCustomerDetail(int id){
        smLoading.setVisibility(View.VISIBLE);
        BaseModel param = createGetParam(ApiUtil.CUSTOMER_GETDETAIL() + id, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                CustomSQL.setBaseModel(Constants.CUSTOMER, result);
                smLoading.setVisibility(View.INVISIBLE);

                updateView(result);
                viewPager.setCurrentItem(result.getBaseModel("temp").getInt("bill_id") ==0? 1 : 0, true);

            }

            @Override
            public void onError(String error) {
                smLoading.setVisibility(View.INVISIBLE);

            }
        },0).execute();



    }

    private void updateView(BaseModel customer) {
        currentCustomer = customer;
        updateTitle(currentCustomer);
        updateOverview(listBills);

        infoFragment.reloadInfo();
//        billsFragment.updateList();
//        productFragment.updateList();
//        paymentFragment.updateList();


        //createRVFilter();



//        if (currentCustomer.hasKey(Constants.TEMPBILL)) {
//            tempBill = currentCustomer.getBaseModel(Constants.TEMPBILL);
//            viewPager.setCurrentItem(1, true);
//
//        } else {
//            tempBill = null;
//        }
//
//        listDebtBill = new ArrayList<>(DataUtil.array2ListObject(currentCustomer.getString(Constants.DEBTS)));
//        listBills = new ArrayList<>(DataUtil.array2ListObject(currentCustomer.getString(Constants.BILLS)));
//        listBillDetail = new ArrayList<>(DataUtil.getAllBillDetail(listBills));
//        listCheckins = new ArrayList<>(DataUtil.array2ListBaseModel(currentCustomer.getJSONArray(Constants.CHECKINS)));
//
//        updateBillTabNotify(tempBill != null ? true : false, listBills.size());
//


        //currentDebt = Util.getTotalDebt(listBills);



    }

    protected void updateTitle(BaseModel customer){
        tvIconKind.setText(customer.getBaseModel("vehicle").getInt("kind_id") == 0 ? Util.getIcon(R.string.icon_motocycle) : Util.getIcon(R.string.icon_car));
        tvTitle.setText(Util.FormatPlate(currentCustomer.getString("plate_number")));

    }

    protected void updateOverview(List<BaseModel> list) {
        setFilterVisibility();
//        setPrintIconVisibility();
//        if (list.size() > 0) {
//            BaseModel object = Util.getTotal(list);
//            tvTotal.setText(String.format("Tổng: %s", Util.FormatMoney(object.getDouble("total"))));
//            tvDebt.setText(String.format("Nợ: %s", Util.FormatMoney(object.getDouble("debt"))));
//            tvPaid.setText(String.format("Trả: %s", Util.FormatMoney(object.getDouble("paid"))));
//            tvBDF.setText(String.format("BDF:%s ", DataUtil.defineBDFPercent(listBillDetail)) + "%");
//
//        } else {
//            tvTotal.setText("...");
//            tvDebt.setText("...");
//            tvPaid.setText("...");
//            tvBDF.setText("...");
//        }
    }

    public void setupViewPager(ViewPager viewPager) {
        pageAdapter = new ViewpagerCustomerAdapter(getSupportFragmentManager());
        pageAdapter.addFragment(Fragment.instantiate(this, CustomerCartFragment.class.getName()), "Dịch vụ", 0);
        pageAdapter.addFragment(Fragment.instantiate(this, CustomerInfoFragment.class.getName()), "Thông tin", 0);




//        pageAdapter.addFragment(Fragment.instantiate(this, DatePickerFragment.class.getName()), "Hóa đơn", 0);
//        pageAdapter.addFragment(Fragment.instantiate(this, CustomerInfoFragment.class.getName()), "Sản Phẩm", 0);
//        pageAdapter.addFragment(Fragment.instantiate(this, "CustomerInfoFragment.class.getName()"), "Tiền mặt", 0);

        viewPager.setAdapter(pageAdapter);



        //viewPager.setOffscreenPageLimit(4);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Util.hideKeyboard(tabLayout);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                setPrintIconVisibility();
                setFilterVisibility();

                //btnShopCart.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void updateTabTitle() {
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pageAdapter.getTabView(i));
        }
        tabLayout.requestFocus();
    }

    protected void updateServiceTabNotify(int billAmount) {
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        tab.setCustomView(null);
        tab.setCustomView(pageAdapter.getNotifyBaged(0, billAmount, false));

    }

    private void addYearToList(String year) {
        if (mYears.size() == 0) {
            mYears.add(0, Constants.ALL_FILTER);
        }
        if (!mYears.get(mYears.size() - 1).equals(year)) {
            mYears.add(year);
        }
    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();

                break;

//            case R.id.icon_more:
//                if (listBills.size() > 0) {
//                    Util.showToast("Không thể xóa khách hàng đã có hóa đơn");
//
//                } else if (listCheckins.size() > 0) {
//                    Util.showToast("Không thể xóa khách hàng có checkin");
//
//                } else {
//                    deleteCustomer();
//
//                }
//
//                break;

            case R.id.customer_shopcart:
//                pageAdapter.addFragment( Fragment.instantiate(this, CustomerCartFragment.class.getName()), "Dịch vụ", 0, 0);
//                pageAdapter.addFragment( Fragment.instantiate(this, CustomerCartFragment.class.getName()), "Dịch vụ", 0);
//                pageAdapter.notifyDataSetChanged();
                //viewPager.setCurrentItem(1);
//                updateTabTitle();
                //setupTabLayout(tabLayout);

                changeFragment(new CartSelectFragment(), true);

//                if (tempBill != null) {
//                    Util.showSnackbar("Có 1 đơn hàng chưa hoàn thành nên không thể tạo hóa đơn mới", null, null);
//
//                } else {
//                    CustomSQL.setListBaseModel(Constants.BILL_DETAIL, listBillDetail);
//                    openShopCartScreen();
//                }

                break;

            case R.id.customer_debt:
                if (currentDebt > 0) {
                    //showDialogPayment();
                }

                break;

            case R.id.customer_paid:
                if (listBills.size() > 0) {
                    CustomCenterDialog.alert("Chiết khấu", "Nhấn giữ vào biểu tường này để nhập số tiền chiết khấu cho khách hàng", "đồng ý");

                }

                break;

            case R.id.customer_filter:
                Bundle bundle = new Bundle();
                bundle.putInt("position", currentCheckedTime);
                bundle.putLong("start_time", start);

                changeFragment(new DatePickerFragment(), bundle,true);

                break;

            case R.id.customer_filter_icon_close:
                if (tvFilter.getVisibility() == View.GONE) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("position", currentCheckedTime);
                    bundle1.putLong("start_time", start);
                    changeFragment(new DatePickerFragment(), bundle1, true);

                } else {
                    tvFilterIcon.setText(Util.getIcon(R.string.icon_calendar));
                    tvFilter.setVisibility(View.GONE);
                    filterBillByRange(0, 0);
                }

                break;

            case R.id.customer_print:
                //printDebtBills();

                break;


        }
    }

    protected void returnPreviousScreen(BaseModel customer) {
        Intent returnIntent = new Intent();
        //customer.removeKey("bills");
        returnIntent.putExtra(Constants.CUSTOMER, customer != null ? customer.BaseModelstoString() : null);
        //returnIntent.putExtra(Constants.RELOAD_DATA, haschange);
        setResult(Constants.RESULT_CUSTOMER_ACTIVITY, returnIntent);
        Util.getInstance().getCurrentActivity().overridePendingTransition(R.anim.nothing, R.anim.slide_down);
        Util.getInstance().getCurrentActivity().finish();

    }

    protected void openShopCartScreen() {
        //Transaction.gotoShopCartActivity(DataUtil.convertListObject2Array(listDebtBill).toString());

    }

    private void deleteCustomer() {
        CustomCenterDialog.alertWithCancelButton(null, String.format("Xóa khách hàng %s", tvTitle.getText().toString()), "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
//                BaseModel param = createGetParam(ApiUtil.CUSTOMER_DELETE() + currentCustomer.getString("id"), false);
//                new GetPostMethod(param, new NewCallbackCustom() {
//                    @Override
//                    public void onResponse(BaseModel result, List<BaseModel> list) {
//                        currentCustomer.put("deleted", true);
//                        CustomSQL.setString(Constants.CUSTOMER, currentCustomer.BaseModelstoString());
//
//                        Util.showToast("Xóa thành công!");
//                        returnPreviousScreen(currentCustomer);
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                }, 1).execute();

            }
        });

    }

    @Override
    public void onBackPressed() {
        mFragment = getSupportFragmentManager().findFragmentById(R.id.customer_parent);
        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        }else if (mFragment != null && mFragment instanceof CartSelectFragment) {
            getSupportFragmentManager().popBackStack();

        }
//        else if (mFragment != null && mFragment instanceof CustomerReturnFragment) {
//            getSupportFragmentManager().popBackStack();
//
//        } else if (mFragment != null && mFragment instanceof CustomerEditMapFragment) {
//            getSupportFragmentManager().popBackStack();
//
//        } else if (mFragment != null && mFragment instanceof CustomerCheckinFragment) {
//            getSupportFragmentManager().popBackStack();
//
//        }
        else if (mFragment != null && mFragment instanceof DatePickerFragment) {
            getSupportFragmentManager().popBackStack();

        } else if (tabLayout.getSelectedTabPosition() != 0) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();

        } else {
            returnPreviousScreen(currentCustomer);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Util.getInstance().setCurrentActivity(this);
        if (requestCode == Constants.RESULT_SHOPCART_ACTIVITY) {
            BaseModel data = new BaseModel(intent.getStringExtra(Constants.SHOP_CART_ACTIVITY));
            if (data.hasKey(Constants.RELOAD_DATA) && data.getBoolean(Constants.RELOAD_DATA)) {
                reloadCustomer(CustomSQL.getBaseModel(Constants.CUSTOMER).getString("id"));

            }

        } else if (requestCode == Constants.RESULT_PRINTBILL_ACTIVITY) {
            BaseModel data = new BaseModel(intent.getStringExtra(Constants.PRINT_BILL_ACTIVITY));
            if (data.hasKey(Constants.RELOAD_DATA) && data.getBoolean(Constants.RELOAD_DATA)) {
                reloadCustomer(CustomSQL.getBaseModel(Constants.CUSTOMER).getString("id"));

            }

        } else {
            onResume();

        }

    }

    protected void saveCustomerToLocal(String key, Object value) {
        currentCustomer.put(key, value);

        mHandlerUpdateCustomer.removeCallbacks(mUpdateTask);
        mHandlerUpdateCustomer.postDelayed(mUpdateTask, 1000);

    }

    private Runnable mUpdateTask = new Runnable() {
        @Override
        public void run() {
            submitCustomer(currentCustomer, new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {
                    CustomSQL.setBaseModel(Constants.CUSTOMER, currentCustomer);
                }
            });

        }
    };

    protected void submitCustomer(BaseModel customer,  CallbackBoolean listener) {
        smLoading.setVisibility(View.VISIBLE);

        String param = String.format(ApiUtil.CUSTOMER_CREATE_PARAM,
                String.format("id=%s&", customer.getString("id")),
                Util.encodeString(customer.getString("name")),//name
                Util.encodeString(customer.getString("address")), //address
                Util.encodeString(customer.getString("phone")), //phone
                Util.encodeString(customer.getString("note")), //note
                customer.getInt("district_id"), //district
                customer.getInt("province_id"), //province
                0,
                Shop.getId(),//DistributorId
                Util.encodeString(customer.getString("plate_number")),
                customer.getInt("vehicle_id")
        );
//
        new GetPostMethod(createPostParam(ApiUtil.CUSTOMER_NEW(), param, false, false),
                new NewCallbackCustom() {
                    @Override
                    public void onResponse(BaseModel result, List<BaseModel> list) {
                        smLoading.setVisibility(View.INVISIBLE);
                        listener.onRespone(true);
                    }

                    @Override
                    public void onError(String error) {
                        smLoading.setVisibility(View.INVISIBLE);
                        Util.showSnackbar("Không thể lưu thay đổi của khách hàng", null, null);
                    }
                }, 0).execute();

    }

//    protected void openReturnFragment(BaseModel bill) {
//        currentBill = bill;
//        changeFragment(new CustomerReturnFragment(), true);
//
//    }


    protected void reloadCustomer(String id) {
        haschange = true;
        BaseModel param = createGetParam(ApiUtil.CUSTOMER_GETDETAIL() + id, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                BaseModel customer = DataUtil.rebuiltCustomer(result, false);
                CustomSQL.setBaseModel(Constants.CUSTOMER, customer);
                updateView(customer);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }

    private void loadListProduct() {
        List<BaseModel> all = Product.getProductList();
        for (int i = 0; i < all.size(); i++) {
            BaseModel product = all.get(i);
            product.put("checked", false);
            listProducts.add(product);

        }

        DataUtil.sortProduct(listProducts, false);
    }

    private void loadListProductGroup() {
        listProductGroups = Product.getProductGroupList();

    }

    protected void updatelistProduct(List<BaseModel> list_product) {
        cartFragment.updatelistProduct(list_product);
        viewPager.setCurrentItem(cartFragment.adapter.getItemCount() > 0 ? 0 : 1, true);
        updateServiceTabNotify(cartFragment.adapter.getItemCount());


//        for (int i = 0; i < list_product.size(); i++) {
//            Product product = new Product(new JSONObject());
//            product.put("id", list_product.get(i).getInt("id"));
//            product.put("product_id", list_product.get(i).getInt("product_id"));
//            product.put("name", list_product.get(i).getString("name"));
//            product.put("productGroup", list_product.get(i).getString("productGroup"));
//            product.put("promotion", list_product.get(i).getBoolean("promotion"));
//            product.put("unitPrice", list_product.get(i).getDouble("unitPrice"));
//            product.put("purchasePrice", list_product.get(i).getDouble("purchasePrice"));
//            product.put("volume", list_product.get(i).getInt("volume"));
//            product.put("image", list_product.get(i).getString("image"));
//            //product.put("imageUrl", list_product.get(i).getString("imageUrl"));
//            product.put("checked", list_product.get(i).getBoolean("checked"));
//            product.put("isPromotion", false);
//            product.put("quantity", 1);
//            product.put("totalMoney", product.getDouble("unitPrice"));
//            product.put("discount", 0);
//
//            cartFragment.addItemProduct(product);
//
//        }
//        updateBDFValue();


    }

//    private void showDialogPayment() {
//        CustomCenterDialog.showDialogPayment("THANH TOÁN CÁC HÓA ĐƠN NỢ",
//                listDebtBill,
//                0.0,
//                true,
//                new CallbackListCustom() {
//                    @Override
//                    public void onResponse(List result) {
//                        postPayToServer(DataUtil.createListPaymentParam(currentCustomer.getInt("id"), result, false));
//
//                    }
//
//                    @Override
//                    public void onError(String error) {
//
//                    }
//                });
//
//    }
//
//    private void showDiscountPayment() {
//        CustomCenterDialog.showDialogDiscountPayment("TRỪ TIỀN CHIẾT KHẤU KHÁCH HÀNG",
//                "Nhập số tiền chiết khấu",
//                currentCustomer.getDouble("paid"),
//                new CallbackDouble() {
//                    @Override
//                    public void Result(Double d) {
//                        BaseModel param = createPostParam(ApiUtil.PAY_NEW(),
//                                String.format(ApiUtil.PAY_PARAM,
//                                        currentCustomer.getInt("id"),
//                                        String.valueOf(Math.round(d * -1)),
//                                        0,
//                                        User.getId(),
//                                        Util.encodeString("Trả chiết khấu"),
//                                         0,
//                                        User.getId()),
//                                false,
//                                false);
//
//                        new GetPostMethod(param, new NewCallbackCustom() {
//                            @Override
//                            public void onResponse(BaseModel result, List<BaseModel> list) {
//                                reloadCustomer(currentCustomer.getString("id"));
//                            }
//
//                            @Override
//                            public void onError(String error) {
//
//                            }
//                        }, 1).execute();
//
//                    }
//                });
//    }
//
//    private void postPayToServer(List<String> listParam) {
//        List<BaseModel> params = new ArrayList<>();
//
//        for (String itemdetail: listParam){
//            BaseModel item = createPostParam(ApiUtil.PAY_NEW(), itemdetail, false, false);
//            params.add(item);
//        }
//
//        new CustomGetPostListMethod(params, new CallbackCustomList() {
//            @Override
//            public void onResponse(List<BaseModel> results) {
//                reloadCustomer(currentCustomer.getString("id"));
//            }
//
//            @Override
//            public void onError(String error) {
//
//            }
//        }, 1).execute();
//
//    }
//
//    protected void printDebtBills() {
//        Transaction.gotoPrintBillActivity(new BaseModel(), true);
//
//    }
//
//    protected void printTempBill(BaseModel bill) {
//        Transaction.checkInventoryBeforePrintBill(bill,
//                DataUtil.array2ListObject(bill.getString(Constants.BILL_DETAIL)),
//                User.getCurrentUser().getInt("warehouse_id"));
//
//
//    }
//
//    private void checkLocation(CallbackBoolean mSuccess) {
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        tvCheckInStatus.setText("Đang kiểm tra vị trí...");
//        getCurrentLocation(new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                double distance = MapUtil.distance(location.getLatitude(), location.getLongitude(), currentCustomer.getDouble("lat"), currentCustomer.getDouble("lng"));
//                CustomSQL.setLong(Constants.CURRENT_DISTANCE, (long) distance);
//                mSuccess.onRespone(true);
//
//                if (distance < Constants.CHECKIN_DISTANCE) {
//                    tvCheckInStatus.setText(String.format("Đang trong phạm vi cửa hàng ~%sm", Math.round(distance)));
//                    threadShowTime.start();
//                    infoFragment.tvCheckin.setText(Util.getIconString(R.string.icon_district, "   ", "Checkin - Ghi chú"));
//
//                } else {
//                    tvCheckInStatus.setText(String.format("Đang bên ngoài cửa hàng ~%s", distance > 1000 ? Math.round(distance) / 1000 + "km" : Math.round(distance) + "m"));
//                    infoFragment.tvCheckin.setText(Util.getIconString(R.string.icon_note, "   ", "Thêm ghi chú"));
//                }
//
//            }
//        });
//    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.customer_paid:
                //showDiscountPayment();
                break;
        }
        return true;
    }

    private void setPrintIconVisibility() {
        if (currentPosition == 1 && listDebtBill.size() > 0) {
            tvPrint.setVisibility(View.VISIBLE);
        } else {
            tvPrint.setVisibility(View.GONE);
        }
    }

    private void setFilterVisibility() {
        if (currentPosition != 0 && listBills.size() > 0) {
            lnFilter.setVisibility(View.VISIBLE);
        } else {
            lnFilter.setVisibility(View.GONE);
        }
    }

    private void filterBillByRange(long start, long end) {
//        if (start == 0 && end == 0) {
//            billsFragment.adapter.getFilter().filter("");
//            paymentFragment.adapter.getFilter().filter("");
//            productFragment.updateListByRange("");
//        } else {
//            BaseModel model = BaseModel.put2ValueToNewObject("from", start, "to", end);
//            billsFragment.adapter.getFilter().filter(model.BaseModelstoString());
//            paymentFragment.adapter.getFilter().filter(model.BaseModelstoString());
//            productFragment.updateListByRange(model.BaseModelstoString());
//        }
//
//        viewPager.setCurrentItem(1, true);
//        updateOverview(billsFragment.adapter.getAllBill());

    }

    //get date return from fragment
    @Override
    public void onResponse(BaseModel object) {
        start = object.getLong("start");
        currentCheckedTime = object.getInt("position");

        tvFilterIcon.setText(Util.getIcon(R.string.icon_x));
        tvFilter.setVisibility(View.VISIBLE);
        tvFilter.setText(object.getString("text"));

        filterBillByRange(object.getLong("start"), object.getLong("end"));

    }
}
