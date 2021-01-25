package com.lubsolution.store.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.lubsolution.store.R;
import com.lubsolution.store.adapter.ShopAdapter;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by macos on 9/16/17.
 */

public class ShopListActivity extends BaseActivity implements View.OnClickListener, CallbackObject {
    private ImageView btnBack;
    private RecyclerView rvDistributor;
    private ShopAdapter shopAdapter;
    private FloatingActionButton btnAddShop;

    public List<BaseModel> shops;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_shop;
    }

    @Override
    public int setIdContainer() {
        return R.id.shop_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        rvDistributor = (RecyclerView) findViewById(R.id.shop_rvgroup);
        btnAddShop = findViewById(R.id.shop_add_new);

    }

    @Override
    public void initialData() {
        loadShops();

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnAddShop.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.shop_add_new:
                openFragmentNewShop(null);

                break;

        }
    }

    @Override
    public void onBackPressed() {
        Fragment mFragment = getSupportFragmentManager().findFragmentById(R.id.shop_parent);
        if (mFragment != null && mFragment instanceof UpdateShopFragment) {
            getSupportFragmentManager().popBackStack();

        } else {
            Transaction.gotoHomeActivityRight(true);
        }
    }

    protected void loadShops() {
        BaseModel param = createGetParam(ApiUtil.SHOPS(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                createRVShop(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }


    private void createRVShop(List<BaseModel> list) {
        shops = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            shops.add(list.get(i));

        }
        shopAdapter = new ShopAdapter(shops, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                openFragmentNewShop(object.BaseModelstoString());
            }
        });
        Util.createLinearRV(rvDistributor, shopAdapter);

    }

    private void openFragmentNewShop(String shop) {
        UpdateShopFragment groupFragment = new UpdateShopFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SHOP, shop);
        changeFragment(groupFragment, bundle, true);
    }

    @Override
    public void onResponse(BaseModel object) {
        shopAdapter.updateDistributor(object);
    }
}
