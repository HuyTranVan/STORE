package com.lubsolution.store.activities;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.clans.fab.FloatingActionButton;
import com.lubsolution.store.R;
import com.lubsolution.store.adapter.WaitingListAdapter;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.CallbackString;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.libraries.searchview.SimpleSearchView;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.Shop;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomInputDialog;
import com.lubsolution.store.utils.DataUtil;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class MainShopActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private TextView tvTitle, tvSearch, tvScan;
    private ImageView btnBack;
    private RecyclerView rvCustomer;
    private FloatingActionButton btnNew;
    private SimpleSearchView mSearch;
    private WaitingListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Handler mHandlerSearch = new Handler();
    private String mSearchText = "";
    private List<BaseModel> mCustomers;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_mainshop;
    }

    @Override
    public int setIdContainer() {
        return R.id.mainshop_parent;
    }

    @Override
    public void findViewById() {
        tvTitle = findViewById(R.id.mainshop_title);
        tvSearch = findViewById(R.id.mainshop_search);
        btnBack = findViewById(R.id.icon_back);
        rvCustomer = findViewById(R.id.mainshop_rv);
        btnNew = findViewById(R.id.mainshop_new);
        mSearch = findViewById(R.id.searchView);
        tvScan = findViewById(R.id.mainshop_scan);
        swipeRefreshLayout = findViewById(R.id.mainshop_refresh);

    }

    @Override
    public void initialData() {
        tvTitle.setText(Shop.getName());
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorBlueDark));
        mSearch.setBackIconColor(getResources().getColor(R.color.black_text_color_hint));
        createWaitingList();
        createRVCustomer();

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnNew.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        searchEvent();



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.mainshop_search:
                mSearch.showSearch(true);
                break;
            case R.id.mainshop_new:
                CustomInputDialog.createNewCustomer(view, new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        createWaitingList();
                    }
                });
                break;


        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (mSearch.isSearchOpen()){
            mSearch.closeSearch(true);

        }else {
            Transaction.gotoHomeActivityRight(true);
        }

    }

    private void createWaitingList(){
        mCustomers = new ArrayList<>();
        swipeRefreshLayout.setRefreshing(true);
        BaseModel param = createGetParam(ApiUtil.CUSTOMER_WAITING_LIST(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                mCustomers = list;
                adapter.updateList(mCustomers);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String error){
                swipeRefreshLayout.setRefreshing(false);

            }
        },0).execute();

    }

    private void createRVCustomer(){
        adapter = new WaitingListAdapter(new ArrayList<>(), new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                Transaction.gotoCustomerActivity(object.getInt("customer_id"));

            }
        });
        Util.createGridRV(rvCustomer, adapter, 2);

    }

    private void searchEvent(){
        mSearch.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    mSearchText = newText.toLowerCase() ;
                    mHandlerSearch.removeCallbacks(delayForSerch);
                    mHandlerSearch.postDelayed(delayForSerch, 500);

                }else {
                    mSearch.updateListItem(new ArrayList<>());

                }
                return true;
            }

            @Override
            public boolean onQueryTextCleared() {
                return false;
            }
        });

        mSearch.setOnItemClick(new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                if (DataUtil.checkDuplicate(mCustomers, "customer_id", object, "id")) {
                    Util.showSnackbarError("Xe đang trong danh sách chờ");

                } else {
                    mSearch.closeSearch();
                    BaseModel param = createPostParam(ApiUtil.TEMP_CUSTOMER_NEW(),
                            ApiUtil.TEMP_CUSTOMER_NEW_PARAM + object.getString("id"),
                            false,
                            false);

                    new GetPostMethod(param, new NewCallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result, List<BaseModel> list) {
                            createWaitingList();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, 1).execute();

                }


            }
        }, new CallbackString() {
            @Override
            public void Result(String s) {


            }
        });
    }

    private Runnable delayForSerch = new Runnable() {
        @Override
        public void run() {
            BaseModel param = createPostParam(
                    ApiUtil.CUSTOMERS(1, 15),
                    "plate_number=" + Util.encodeString(mSearchText),
                    false,
                    true);

            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list) {
                    mSearch.updateListItem(list);

                }

                @Override
                public void onError(String error) {

                }
            }, 0).execute();

        }
    };


    @Override
    public void onRefresh() {
        createWaitingList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data.getStringExtra(Constants.CUSTOMER) != null && requestCode == Constants.RESULT_CUSTOMER_ACTIVITY) {
            BaseModel cust = new BaseModel(data.getStringExtra(Constants.CUSTOMER));

            adapter.updateItem(cust);

//            if (cust.hasKey("deleted") && cust.getBoolean("deleted")) {
//                removeCustomer(cust.getString("id"));
//
//            } else {
//                reUpdateMarkerDetail(cust);
//
//            }

        } else {
            onResume();
        }
    }
}
