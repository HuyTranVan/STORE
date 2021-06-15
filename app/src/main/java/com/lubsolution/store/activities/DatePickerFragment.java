package com.lubsolution.store.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.lubsolution.store.R;
import com.lubsolution.store.adapter.ViewpagerDatePickerAdapter;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.utils.Util;


/**
 * Created by macos on 9/16/17.
 */

public class DatePickerFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private Button btnSubmit;
    private TextView tvTitle;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private ViewpagerDatePickerAdapter viewpagerAdapter;
    private int currentPosition = 0;
    private String[] titles = new String[]{"CHỌN NGÀY", "CHỌN THÁNG", "CHỌN NĂM"};
    private long starttime;
    private CallbackObject onDataPass;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_date_picker, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentPosition = bundle.getInt("position", 0);
            starttime = bundle.getLong("start_time", Util.CurrentTimeStamp());
        }
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager();
    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

    }

    private void initializeView() {
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        btnSubmit = (Button) view.findViewById(R.id.date_picker_submit);
        tvTitle = view.findViewById(R.id.date_picker_title);
        viewPager = (ViewPager) view.findViewById(R.id.date_picker_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.date_picker_tabs);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();

                break;

            case R.id.date_picker_submit:
                switch (currentPosition) {
                    case 0:
                        onDataPass.onResponse(viewpagerAdapter.getDateSelected());
                        break;

                    case 1:
                        onDataPass.onResponse(viewpagerAdapter.getMonthSelected());
                        break;

                    case 2:
                        onDataPass.onResponse(viewpagerAdapter.getYearSelected());
                        break;

                }

                getActivity().getSupportFragmentManager().popBackStack();

                break;


        }
    }

    private void setupViewPager() {
        viewpagerAdapter = new ViewpagerDatePickerAdapter(starttime,currentPosition,  titles);
        viewPager.setAdapter(viewpagerAdapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0; i < titles.length; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            View customView = LayoutInflater.from(getActivity()).inflate(R.layout.view_tab_text, null);
            TextView textTitle = (TextView) customView.findViewById(R.id.tabTitle);

            textTitle.setText(titles[i]);

            tab.setCustomView(customView);

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPass = (CallbackObject) context;
    }


}
