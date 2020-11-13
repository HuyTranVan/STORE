package com.lubsolution.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lubsolution.store.R;
import com.lubsolution.store.models.BaseModel;

import java.util.ArrayList;
import java.util.List;

public class ObjectAdapter extends ArrayAdapter {
    private Context mContext;
    private List<BaseModel> mList = new ArrayList<>();
    private String mKey;
    private int mResource;
    //private View mView;

    public ObjectAdapter(@NonNull Context context, int resource, @NonNull List objects, String key) {
        super(context, resource, objects);
        mContext = context;
        mList = objects;
        mKey = key;
        mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //View listItem = convertView;
//        if(listItem == null)
        View itemView = LayoutInflater.from(mContext).inflate(mResource, parent,false);

        BaseModel item = mList.get(position);

        TextView name = (TextView) itemView.findViewById(R.id.text);
        name.setText(item.getString(mKey));

//        TextView release = (TextView) listItem.findViewById(R.id.textView_release);
//        release.setText(currentMovie.getmRelease());

        return itemView;
    }
}
