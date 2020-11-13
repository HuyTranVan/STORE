package com.lubsolution.store.activities;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ferfalk.simplesearchview.SimpleSearchView;
import com.github.clans.fab.FloatingActionButton;
import com.lubsolution.store.R;
import com.lubsolution.store.models.Shop;
import com.lubsolution.store.utils.Transaction;

public class MainShopActivity extends BaseActivity implements View.OnClickListener {
   private TextView tvTitle, tvSearch, tvScan;
   private ImageView btnBack;
   private RecyclerView rvCustomer;
   private FloatingActionButton btnNew;
   private SimpleSearchView mSearch;

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
                changeFragment(new AddNewCustomerFragment(), false);
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




}
