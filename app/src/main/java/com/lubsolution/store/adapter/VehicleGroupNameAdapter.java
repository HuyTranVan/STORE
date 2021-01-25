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
    private List<List<BaseModel>> mVehicle = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;
    private CallbackInt mSize;
    private RecyclerView mRecyclerView;
    private int numberOfVehicle =0;

    public VehicleGroupNameAdapter(List<BaseModel> list, List<BaseModel> baselist, CallbackObject listener, CallbackInt size) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.mData = list;
        this.baseData = baselist;
        this.mSize = size;

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
        VehicleNameAdapter nameAdapter = new VehicleNameAdapter(mVehicle.get(position), new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                mListener.onResponse(object);
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
        });
        Util.createLinearRV(holder.rvGroup, nameAdapter);

        holder.tvNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomInputDialog.createNewVehicle(view, position, baseData, new CallbackObject() {
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

    public void updateItem(BaseModel object){
        for (int i=0; i<baseData.size(); i++){
            if (object.getInt("brand_id") == baseData.get(i).getInt("id")){
                List<BaseModel> vehices = baseData.get(i).getList("vehicles");
                boolean check = false;

                for (int j =0; j<vehices.size(); j++){
                    if (object.getInt("id") == vehices.get(j).getInt("id")){
//                        vehices.remove(j);
//                        vehices.add(j, object);

                        RecyclerView recyclerView = (RecyclerView) mRecyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.vehiclegroup_item_rv);
                        VehicleNameAdapter adapter = (VehicleNameAdapter) recyclerView.getAdapter();
                        adapter.updateItem(object);

                        notifyItemChanged(i+1);

                        check = true;
                        break;
                    }
                }

                if (!check){
                    //vehices.add(object);
                    RecyclerView recyclerView = (RecyclerView) mRecyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.vehiclegroup_item_rv);
                    VehicleNameAdapter adapter = (VehicleNameAdapter) recyclerView.getAdapter();
                    adapter.updateItem(object);
                    //notifyItemChanged(mData.size());
                }
//                RecyclerView recyclerView = (RecyclerView) mRecyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.vehiclegroup_item_rv);
//                VehicleNameAdapter adapter = (VehicleNameAdapter) recyclerView.getAdapter();
//                adapter.updateItem(object);


            }
        }


        //reloadBaseData(baseData);
        //notifyDataSetChanged();

    }

    public int getVehicleTotal(){
        return numberOfVehicle;
    }



}
