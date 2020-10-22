package com.lubsolution.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lubsolution.store.R;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by tranhuy on 5/24/17.
 */

public class VehicleBrandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public VehicleBrandAdapter(List<BaseModel> list, CallbackObject listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;

        this.mData = list;
        mData.add(0,null);


    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_vehiclebrand_item, parent, false);
            return new VehicleBrandAdapter.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_new, parent, false);
            return new VehicleBrandAdapter.SetNewViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof VehicleBrandAdapter.ItemViewHolder) {
            setItemRows((VehicleBrandAdapter.ItemViewHolder) viewHolder, position);

        } else if (viewHolder instanceof VehicleBrandAdapter.SetNewViewHolder) {
            setNewItem((VehicleBrandAdapter.SetNewViewHolder) viewHolder, position);

        }

    }

    private void setItemRows(VehicleBrandAdapter.ItemViewHolder holder, int position) {
        holder.tvName.setText(mData.get(position).getString("name"));

        if (!Util.checkImageNull(mData.get(position).getString("image"))) {
            Glide.with(mContext).load(mData.get(position).getString("image")).centerCrop().into(holder.image);
        }


        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResponse(mData.get(position));
            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private CircleImageView image;
        private View vLine;
        private RelativeLayout lnParent;

        public ItemViewHolder(View itemView) {
            super(itemView);
            lnParent = itemView.findViewById(R.id.vehiclebrand_item_parent);
            image = itemView.findViewById(R.id.vehiclebrand_item_image);
            tvName = itemView.findViewById(R.id.vehiclebrand_item_name);
            vLine = itemView.findViewById(R.id.seperateline);


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

    private void setNewItem(VehicleBrandAdapter.SetNewViewHolder holder, int position) {
        holder.tvName.setText("Thêm hãng xe");
        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResponse(null);
            }
        });
    }

    public void updateItem(BaseModel object){
        boolean check = false;
        for (int i=1; i<mData.size(); i++){
            if (object.getInt("id") == mData.get(i).getInt("id")){
                mData.remove(i);
                mData.add(i, object);
                notifyItemChanged(i);

                check = true;
                break;
            }
        }
        if (!check){
            mData.add(object);
            notifyItemChanged(mData.size());
        }
    }


}
