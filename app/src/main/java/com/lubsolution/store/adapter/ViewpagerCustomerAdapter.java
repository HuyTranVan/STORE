package com.lubsolution.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.lubsolution.store.R;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ViewpagerCustomerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;

    private List<Fragment> mFragments = new ArrayList<>();
    private List<Integer> mFragmentCount = new ArrayList<>();
    private List<String> mFragmentText = new ArrayList<>();

    public ViewpagerCustomerAdapter(FragmentManager fm) {
        super(fm);
        this.mContext = Util.getInstance().getCurrentActivity();
    }

    public void addFragment(Fragment fragment, String text, int count) {
        mFragments.add(fragment);
        mFragmentCount.add(count);
        mFragmentText.add(text);
        notifyDataSetChanged();
    }

//    public void addFragment1(Fragment fragment, String text, int count, int position) {
//        mFragments.add(position, Fragment.instantiate(mContext, fragment.getName()));
//        mFragmentCount.add(position, count);
//        mFragmentText.add(position, text);
//        //notifyDataSetChanged();
//
//    }

    public void addCountNotify(int count, int position) {
        //mFragmentNotify.remove(position);
        //mFragmentNotify.add(position, count);
        //notifyDataSetChanged();
    }

    public void replaceFragment(Fragment fragment, String title, String text, int count, int position) {
        mFragments.remove(position);
        mFragments.add(position, fragment);

        //mFragmentNotify.remove(position);
        //mFragmentNotify.add(position, count);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public View getTabView(int position) {
        View tab = LayoutInflater.from(mContext).inflate(R.layout.view_tab_customer, null);
        TextView tvTitle = (TextView) tab.findViewById(R.id.tabTitle);
        TextView tvNotifyCount = (TextView) tab.findViewById(R.id.tabNotifyCount);
        TextView tvNotify = (TextView) tab.findViewById(R.id.tabNotify);

        tvTitle.setText(mFragmentText.get(position));
        if (mFragmentCount.get(position) != 0) {
            tvNotifyCount.setVisibility(View.VISIBLE);
            tvNotifyCount.setText(String.valueOf(mFragmentCount.get(position)));
        } else {
            tvNotifyCount.setVisibility(View.GONE);
        }

        if (position == 0) {
            tab.setSelected(true);
        }
        return tab;
    }

    public View getNotifyBaged(int position, int count, boolean highlight) {
        View tab = LayoutInflater.from(mContext).inflate(R.layout.view_tab_customer, null);
        TextView tvTitle = (TextView) tab.findViewById(R.id.tabTitle);
        TextView tvNotifyCount = (TextView) tab.findViewById(R.id.tabNotifyCount);
        TextView tvNotify = (TextView) tab.findViewById(R.id.tabNotify);

        tvTitle.setText(mFragmentText.get(position));

        if (highlight) {
            tvNotifyCount.setVisibility(View.GONE);
            tvNotify.setVisibility(View.VISIBLE);

        } else {
            tvNotifyCount.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
            tvNotifyCount.setText(String.valueOf(count));
            tvNotify.setVisibility(View.GONE);
        }

        return tab;

    }


}