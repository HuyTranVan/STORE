package com.lubsolution.store.adapter;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.lubsolution.store.R;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tranhuy on 5/24/17.
 */

public class ViewpagerMultiListAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<RecyclerView.Adapter> listAdapter = new ArrayList<>();
    private List<String> listTitle;
    private boolean[] showSearches;
    private List<View> views = new ArrayList<>();

    public ViewpagerMultiListAdapter(List<RecyclerView.Adapter> listAdapter, List<String> listtitle, boolean[] viewsearch) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.listAdapter = listAdapter;

        if (listtitle != null){
            this.listTitle = listtitle;
        }
        if (viewsearch != null){
            this.showSearches = viewsearch;
        }

    }

    @Override
    public int getCount() {
        return listAdapter.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (LinearLayout) object);
    }

    public void showSearch(int position, boolean isShow) {
        showSearches[position] = isShow;


    }

    public View getView(int position) {
        return views.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.adapter_multi_list_viewpager_item, container, false);
        views.add(view);
        RecyclerView rvList = (RecyclerView) view.findViewById(R.id.customer_viewpager_item_rv);
        RelativeLayout lnSearch = (RelativeLayout) view.findViewById(R.id.search_parent);
        EditText edSearch = view.findViewById(R.id.search_text);
        TextView tvIconSearch = view.findViewById(R.id.search_icon);

        lnSearch.setVisibility(showSearches != null && showSearches[position] ? View.VISIBLE : View.GONE);
        Util.createLinearRV(rvList, listAdapter.get(position));

        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Util.hideKeyboard(v);
                return true;
            }
        });

        tvIconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edSearch.setText("");
            }
        });

        container.addView(view);

        return view;

    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        container.refreshDrawableState();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    //fix recyclerview scroll
    RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int action = e.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };

}