package com.lubsolution.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lubsolution.store.R;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackInt;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.CustomInputDialog;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tranhuy on 5/24/17.
 */

public class VehicleGroupNameAdapter extends RecyclerView.Adapter<VehicleGroupNameAdapter.ItemViewHolder> {
    private List<BaseModel> baseData = new ArrayList<>();
    private List<BaseModel> mData = new ArrayList<>();
    private List<VehicleNameAdapter> mVehicleAdapter;
    private List<List<BaseModel>> mVehicle = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;
    private CallbackInt mSize;
    //private RecyclerView mRecyclerView;
    private int numberOfVehicle =0;

    public VehicleGroupNameAdapter(List<BaseModel> list, List<BaseModel> baselist, CallbackObject listener, CallbackInt size) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.mData = list;
        this.baseData = baselist;
        this.mSize = size;
        this.mVehicleAdapter = new ArrayList<>();

        for (BaseModel item: mData){
            List<BaseModel> items = new ArrayList<>(item.getList("vehicles"));
            mVehicle.add(items);
            numberOfVehicle += items.size();

        }

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_vehiclegroup_item, parent, false);
        return new ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        mVehicleAdapter.add(position, new VehicleNameAdapter(mVehicle.get(position), new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                CustomInputDialog.createNewVehicle(holder.rvGroup, object, mData.get(position),  new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        updateItem(object, position);
                    }
                });

            }
        }, new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                if (result){
                    notifyItemChanged(position);
                    numberOfVehicle -= 1;
                    mSize.onResponse(numberOfVehicle);
                }
            }
        }));
        Util.createLinearRV(holder.rvGroup, mVehicleAdapter.get(position));

        holder.tvNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomInputDialog.createNewVehicle(view, null, mData.get(position), new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        insertItem(object);
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvNew;
        private RecyclerView rvGroup;
        private LinearLayout lnParent;

        public ItemViewHolder(View itemView) {
            super(itemView);
            lnParent = itemView.findViewById(R.id.vehiclegroup_item_parent);
            tvName = itemView.findViewById(R.id.vehiclegroup_item_name);
            tvNew = itemView.findViewById(R.id.vehiclegroup_item_new);
            rvGroup = itemView.findViewById(R.id.vehiclegroup_item_rv);

        }
    }

    private void insertItem(BaseModel item){
        for (int i=0; i<mData.size(); i++){
            if (item.getInt("brand_id") == mData.get(i).getInt("id")){
                mVehicle.get(i).add(0, item);

                notifyItemChanged(i);
                numberOfVehicle += 1;
                mSize.onResponse(numberOfVehicle);
                break;
            }


        }

    }

    public void updateItem(BaseModel object, int pos){
        mVehicleAdapter.get(pos).updateItem(object);

    }

    public void updateBrand(BaseModel brand){
        boolean check = false;
        for (int i=0; i< mData.size(); i++ ){
            if (brand.getInt("id") == mData.get(i).getInt("id")){
                mData.get(i).put("name", brand.getString("name"));
                mData.get(i).put("image", brand.getString("image"));

                notifyItemChanged(i);
                check = true;
                break;

            }

        }

        if (!check){
            mData.add(brand);
            mVehicle.add(new ArrayList<>());
            notifyItemInserted(mData.size() -1);
        }

    }

    public int getVehicleTotal(){
        return numberOfVehicle;
    }



}
