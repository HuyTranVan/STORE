package com.lubsolution.store.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lubsolution.store.R;
import com.lubsolution.store.adapter.VehicleBrandAdapter;
import com.lubsolution.store.adapter.VehicleKindAdapter;
import com.lubsolution.store.adapter.VehicleNameAdapter;
import com.lubsolution.store.adapter.ViewpagerMultiListAdapter;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.Transaction;

import java.util.ArrayList;
import java.util.List;

public class VehicleActivity extends BaseActivity implements View.OnClickListener, CallbackObject {
    private ImageView btnBack;
    private TextView tvTitle;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private List<RecyclerView.Adapter> listadapter;
    private ViewpagerMultiListAdapter viewpagerAdapter;
    private VehicleBrandAdapter brandAdapter;
    private VehicleKindAdapter kindAdapter;
    private VehicleNameAdapter nameAdapter;
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
        super.onBackPressed();
        Transaction.gotoHomeActivityRight(true);
    }

    private void setupViewPager(BaseModel mVehicle) {
        listadapter = new ArrayList<>();

        nameAdapter = new VehicleNameAdapter(new ArrayList<>(), new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.VEHICLE_NAME, object == null ? null : object.BaseModelstoString());
                bundle.putString(Constants.VEHICLE, mVehicle.BaseModelstoString());
                changeFragment(new UpdateVehicleNameFragment(), bundle, true);
            }
        });

        brandAdapter = new VehicleBrandAdapter(mVehicle.getList("brands"), new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BRAND, object == null ? null : object.BaseModelstoString());
                changeFragment(new UpdateVehicleBrandFragment(), bundle, true);


            }
        });

        kindAdapter = new VehicleKindAdapter(mVehicle.getList("kinds"), new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                Bundle bundle = new Bundle();
                bundle.putString(Constants.KIND, object == null ? null : object.BaseModelstoString());
                changeFragment(new UpdateVehicleKindFragment(), bundle, true);
            }
        });

        listadapter.add(0, nameAdapter);
        listadapter.add(1, brandAdapter);
        listadapter.add(2, kindAdapter);

        viewpagerAdapter = new ViewpagerMultiListAdapter(listadapter, null, null);
        viewPager.setAdapter(viewpagerAdapter);
        //viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0; i < createTabTitle().size(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(this).inflate(R.layout.view_tab_default, null);
            TextView tabNotify = (TextView) customView.findViewById(R.id.tabNotify);
            TextView tabText = (TextView) customView.findViewById(R.id.tabTitle);

            tabText.setText(createTabTitle().get(i));
            tabNotify.setVisibility(View.GONE);
//            if (mActivity.listInventoryDetail.get(i).getList("inventories").size() >0){
//                tabTextNotify.setVisibility(View.VISIBLE);
//                tabTextNotify.setText(String.valueOf(mActivity.listInventoryDetail.get(i).getList("inventories").size()));
//            }else {
//                tabTextNotify.setVisibility(View.GONE);
//            }


            tab.setCustomView(customView);
        }


    }

    private List<String> createTabTitle() {
        List<String> titles = new ArrayList<>();
        titles.add(0, "TÊN XE");
        titles.add(1, "HÃNG XE");
        titles.add(2, "LOẠI XE");
        return titles;
    }


    @Override
    public void onResponse(BaseModel object) {
        if(object.hasKey(Constants.BRAND) && object.getBoolean(Constants.BRAND)){
            brandAdapter.updateItem(object);

        }else if(object.hasKey(Constants.KIND) && object.getBoolean(Constants.KIND)){
            kindAdapter.updateItem(object);
        }

    }
}
