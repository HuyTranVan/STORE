package com.lubsolution.store.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lubsolution.store.R;
import com.lubsolution.store.adapter.VehicleBrandAdapter;
import com.lubsolution.store.adapter.VehicleGroupNameAdapter;
import com.lubsolution.store.adapter.VehicleKindAdapter;
import com.lubsolution.store.adapter.ViewpagerMultiListAdapter;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackInt;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class VehicleActivity extends BaseActivity implements View.OnClickListener, CallbackObject {
    private ImageView btnBack;
    private TextView tvTitle;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Fragment mFragment;
    private List<RecyclerView.Adapter> listadapter;
    private ViewpagerMultiListAdapter viewpagerAdapter;
    private VehicleBrandAdapter brandAdapter;
    private VehicleKindAdapter kindAdapter;
    private VehicleGroupNameAdapter nameAdapter;
    protected BaseModel mVehicle = new BaseModel();

    @Override
    public int getResourceLayout() {
        return R.layout.activity_vehicle;
    }

    @Override
    public int setIdContainer() {
        return R.id.vehicle_parent;
    }

    @Override
    public void findViewById() {
        btnBack = findViewById(R.id.icon_back);
        tvTitle = findViewById(R.id.vehicle_title);
        viewPager = findViewById(R.id.vehicle_viewpager);
        tabLayout = findViewById(R.id.vehicle_tabs);
    }

    @Override
    public void initialData() {

        BaseModel param = createGetParam(ApiUtil.VEHICLES(), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                mVehicle = result;
                tabLayout.setupWithViewPager(viewPager);
                setupViewPager(mVehicle);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.icon_back:
                onBackPressed();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mFragment = getSupportFragmentManager().findFragmentById(R.id.vehicle_parent);

        if (Util.getInstance().isLoading()) {
            Util.getInstance().stopLoading(true);

        } else if (mFragment != null && mFragment instanceof UpdateVehicleBrandFragment) {
            getSupportFragmentManager().popBackStack();

        }else if (mFragment != null && mFragment instanceof UpdateVehicleNameFragment) {
            getSupportFragmentManager().popBackStack();

        }else {
            Transaction.gotoHomeActivityRight(true);
        }

    }

    private void setupViewPager(BaseModel mVehicle){
        listadapter = new ArrayList<>();
        nameAdapter = new VehicleGroupNameAdapter(mVehicle.getList("brandWithVehicle"),
                mVehicle.getList("brands"), new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.VEHICLENAME, object == null ? null : object.BaseModelstoString());
//                bundle.putString(Constants.VEHICLE, mVehicle.BaseModelstoString());
                changeFragment(new UpdateVehicleNameFragment(), true);
            }
        }, new CallbackInt() {
            @Override
            public void onResponse(int value) {
                updateTabName(value);
            }
        });

        brandAdapter = new VehicleBrandAdapter(mVehicle.getList("brands"), new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BRAND, object == null ? null : object.BaseModelstoString());
                changeFragment(new UpdateVehicleBrandFragment(), bundle, true);


            }
        }, new CallbackInt() {
            @Override
            public void onResponse(int value) {
                updateTabBrand(value);
            }
        });

        listadapter.add(0, nameAdapter);
        listadapter.add(1, brandAdapter);

        viewpagerAdapter = new ViewpagerMultiListAdapter(listadapter, null, null);
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setOffscreenPageLimit(2);

        for (int i = 0; i < createTabTitle().size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(this).inflate(R.layout.view_tab_default, null);
            TextView tabNotify = (TextView) customView.findViewById(R.id.tabNotify);
            TextView tabText = (TextView) customView.findViewById(R.id.tabTitle);

            tabText.setText(createTabTitle().get(i));
            tabNotify.setVisibility(View.GONE);
            if (i ==0){
                if (nameAdapter.getVehicleTotal() > 0 ){
                    tabNotify.setVisibility(View.VISIBLE);
                    tabNotify.setText(String.valueOf(nameAdapter.getVehicleTotal()));
                }else {

                }

            }else if (i == 1){
                if (brandAdapter.mData.size() > 0 ){
                    tabNotify.setVisibility(View.VISIBLE);
                    tabNotify.setText(String.valueOf(brandAdapter.mData.size() -1));
                }else {

                }

            }

            tab.setCustomView(customView);
        }



    }

    private void updateTabName(int number){
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        View customView = LayoutInflater.from(this).inflate(R.layout.view_tab_default, null);
        TextView tabNotify = (TextView) customView.findViewById(R.id.tabNotify);
        TextView tabText = (TextView) customView.findViewById(R.id.tabTitle);

        tabText.setText(createTabTitle().get(0));
        if (number > 0 ){
            tabNotify.setVisibility(View.VISIBLE);
            tabNotify.setText(String.valueOf(number));
        }else {
            tabNotify.setVisibility(View.GONE);
        }
        tab.setCustomView(null);
        tab.setCustomView(customView);
    }

    private void updateTabBrand(int number){
        TabLayout.Tab tab = tabLayout.getTabAt(1);
        View customView = LayoutInflater.from(this).inflate(R.layout.view_tab_default, null);
        TextView tabNotify = (TextView) customView.findViewById(R.id.tabNotify);
        TextView tabText = (TextView) customView.findViewById(R.id.tabTitle);

        tabText.setText(createTabTitle().get(1));
        if (number > 0 ){
            tabNotify.setVisibility(View.VISIBLE);
            tabNotify.setText(String.valueOf(number));
        }else {
            tabNotify.setVisibility(View.GONE);
        }
        tab.setCustomView(null);
        tab.setCustomView(customView);
    }

    private List<String> createTabTitle() {
        List<String> titles = new ArrayList<>();
        titles.add(0, "TÊN XE");
        titles.add(1, "HÃNG XE");

        return titles;
    }


    @Override
    public void onResponse(BaseModel object) {
        if(object.hasKey(Constants.BRAND) && object.getBoolean(Constants.BRAND) ){
            brandAdapter.updateItem(object);

        }else if(object.hasKey(Constants.KIND)&& object.getBoolean(Constants.KIND) ){
            kindAdapter.updateItem(object);

        }else if(object.hasKey(Constants.VEHICLENAME) && object.getBoolean(Constants.VEHICLENAME)){
            nameAdapter.updateItem(object);
            //initialData();

        }

    }
}
