package com.lubsolution.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lubsolution.store.R;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tranhuy on 5/24/17.
 */

public class VehicleGroupNameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BaseModel> baseData = new ArrayList<>();
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;
    private RecyclerView mRecyclerView;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public VehicleGroupNameAdapter(List<BaseModel> list, CallbackObject listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.baseData = list;

        reloadBaseData(baseData);
        mData.add(0,null);
//        for(BaseModel item: list){
//            if (item.getList("vehicles").size()>0){
//                this.mData.add(item);
//            }
////            List<BaseModel> vehices = item.getList("vehicles");
////            for (BaseModel vehicleItem : vehices){
////
////            }
//        }
//
//        mData.add(0,null);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    private void reloadBaseData(List<BaseModel> list){
        this.mData = new ArrayList<>();
        for(BaseModel item: list){
            if (item.getList("vehicles").size()>0){
                this.mData.add(item);
            }

        }
//        mData.add(0,null);

    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_vehiclegroup_item, parent, false);
            return new VehicleGroupNameAdapter.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_new, parent, false);
            return new VehicleGroupNameAdapter.SetNewViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof VehicleGroupNameAdapter.ItemViewHolder) {
            setItemRows((VehicleGroupNameAdapter.ItemViewHolder) viewHolder, position);

        } else if (viewHolder instanceof VehicleGroupNameAdapter.SetNewViewHolder) {
            setNewItem((VehicleGroupNameAdapter.SetNewViewHolder) viewHolder, position);

        }

    }

    private void setItemRows(VehicleGroupNameAdapter.ItemViewHolder holder, int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        VehicleNameAdapter nameAdapter = new VehicleNameAdapter(mData.get(position).getList("vehicles"), new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                mListener.onResponse(object);
            }
        });
        Util.createLinearRV(holder.rvGroup, nameAdapter);

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private RecyclerView rvGroup;
        private LinearLayout lnParent;

        public ItemViewHolder(View itemView) {
            super(itemView);
            lnParent = itemView.findViewById(R.id.vehiclegroup_item_parent);
            tvName = itemView.findViewById(R.id.vehiclegroup_item_name);
            rvGroup = itemView.findViewById(R.id.vehiclegroup_item_rv);

        }
    }

    public static class SetNewViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        RelativeLayout lnParent;

        public SetNewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.item_new_name);

            lnParent = itemView.findViewById(R.id.item_new_parent);
        }
    }

    private void setNewItem(VehicleGroupNameAdapter.SetNewViewHolder holder, int position) {
        holder.tvName.setText("Thêm tên xe");
        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResponse(null);
            }
        });
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



}
