package com.lubsolution.store.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.lubsolution.store.R;
import com.lubsolution.store.adapter.CustomerCartAdapter;
import com.lubsolution.store.callback.CallbackDouble;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.Product;
import com.lubsolution.store.utils.Util;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerCartFragment extends Fragment implements View.OnClickListener {
    private View view;
    private RecyclerView rvCustomerCart;

    public CustomerCartAdapter adapter;
    private CustomerActivity mActivity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_cart, container, false);


        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        createRVCart(new ArrayList<>());
    }

    private void addEvent() {


    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        mActivity.cartFragment = this;
        rvCustomerCart = (RecyclerView) view.findViewById(R.id.customer_rvcart);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }

    private void createRVCart(final List<BaseModel> list) {
        adapter = new CustomerCartAdapter(list, null, new CallbackDouble() {
            @Override
            public void Result(Double price) {
//                tvTotal.setText(Util.FormatMoney(price));
//
//                rvProducts.requestLayout();
//                rvProducts.invalidate();
//
//                lnSubmitGroup.setVisibility(adapterProducts.getAllDataProduct().size() > 0 ? View.VISIBLE : View.GONE);
//                rlCover.setVisibility(adapterProducts.getAllDataProduct().size() > 0 ? View.GONE : View.VISIBLE);
//
//                updateBDFValue();
            }
        });
        Util.createLinearRV(rvCustomerCart, adapter);

    }

    protected void updatelistProduct(List<BaseModel> list_product) {
        for (int i = 0; i < list_product.size(); i++) {
            Product product = new Product(new JSONObject());
            product.put("id", list_product.get(i).getInt("id"));
            product.put("product_id", list_product.get(i).getInt("product_id"));
            product.put("name", list_product.get(i).getString("name"));
            product.put("productGroup", list_product.get(i).getString("productGroup"));
            product.put("promotion", list_product.get(i).getBoolean("promotion"));
            product.put("unitPrice", list_product.get(i).getDouble("unitPrice"));
            product.put("purchasePrice", list_product.get(i).getDouble("purchasePrice"));
            product.put("volume", list_product.get(i).getInt("volume"));
            product.put("image", list_product.get(i).getString("image"));
            //product.put("imageUrl", list_product.get(i).getString("imageUrl"));
            product.put("checked", list_product.get(i).getBoolean("checked"));
            product.put("isPromotion", false);
            product.put("quantity", 1);
            product.put("totalMoney", product.getDouble("unitPrice"));
            product.put("discount", 0);

            adapter.addItemProduct(product);

        }
        //updateBDFValue();


    }


}
