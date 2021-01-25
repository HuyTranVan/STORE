package com.lubsolution.store.activities;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ferfalk.simplesearchview.SimpleSearchView;
import com.github.clans.fab.FloatingActionButton;
import com.lubsolution.store.R;
import com.lubsolution.store.adapter.WaitingListAdapter;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.Shop;
import com.lubsolution.store.utils.CustomInputDialog;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;

import java.util.List;

public class MainShopActivity extends BaseActivity implements View.OnClickListener {
   private TextView tvTitle, tvSearch, tvScan;
   private ImageView btnBack;
   private RecyclerView rvCustomer;
   private FloatingActionButton btnNew;
   private SimpleSearchView mSearch;
   private WaitingListAdapter adapter;

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

    }

    @Override
    public void initialData() {
        tvTitle.setText(Shop.getName());
        mSearch.setBackIconColor(getResources().getColor(R.color.black_text_color_hint));
        createWaitingList();


    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnNew.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
       // mSearch


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
                //changeFragment(new AddNewCustomerFragment(), false);
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
        BaseModel param = createGetParam(ApiUtil.CUSTOMER_WAITING_LIST(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                createRVCustomer(list);
            }

            @Override
            public void onError(String error) {

            }
        },1).execute();

    }

    private void createRVCustomer(List<BaseModel> list){
        adapter = new WaitingListAdapter(list, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {

            }
        });
        Util.createGridRV(rvCustomer, adapter, 2);

    }




}
