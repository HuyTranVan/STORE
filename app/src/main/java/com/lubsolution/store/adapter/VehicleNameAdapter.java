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
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.lubsolution.store.activities.BaseActivity.createGetParam;

/**
 * Created by tranhuy on 5/24/17.
 */

public class VehicleNameAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;
    private CallbackBoolean mDelete;

    public VehicleNameAdapter(List<BaseModel> list, CallbackObject listener, CallbackBoolean delete) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.mDelete = delete;

        this.mData = list;


    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_vehiclename_item, parent, false);
        return new VehicleNameAdapter.ItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        setItemRows((VehicleNameAdapter.ItemViewHolder) viewHolder, position);

    }

    private void setItemRows(VehicleNameAdapter.ItemViewHolder holder, int position) {
        holder.vLine.setVisibility(position == mData.size() -1 ? View.GONE : View.VISIBLE);
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvKind.setText(mData.get(position).getBaseModel("kind").getString("name"));

        if (!Util.checkImageNull(mData.get(position).getString("image"))) {
            Glide.with(mContext).load(mData.get(position).getString("image")).centerCrop().into(holder.image);
        }

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResponse(mData.get(position));
            }
        });

        holder.lnParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CustomCenterDialog.alertWithCancelButton(null,
                        String.format("Bạn muốn xóa xe %s %s" ,
                                mData.get(position).getBaseModel("brand").getString("name"),
                                mData.get(position).getString("name")
                                ), "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result) {
                            BaseModel param = createGetParam(ApiUtil.VEHICLE_DELETE() + mData.get(position).getString("id"), false);
                            new GetPostMethod(param, new NewCallbackCustom() {
                                @Override
                                public void onResponse(BaseModel re, List<BaseModel> list) {
                                    if (re.getBoolean("deleted")){
                                        notifyItemRemoved(position);
                                        mData.remove(position);
                                        mDelete.onRespone(true);

                                    }
                                }

                                @Override
                                public void onError(String error) {

                                }
                            }, 1).execute();

                        }

                    }
                });



                return true;
            }
        });

    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvKind;
        private CircleImageView image;
        private View vLine;
        private RelativeLayout lnParent;

        public ItemViewHolder(View itemView) {
            super(itemView);
            lnParent = itemView.findViewById(R.id.vehiclename_item_parent);
            image = itemView.findViewById(R.id.vehiclename_item_image);
            tvName = itemView.findViewById(R.id.vehiclename_item_name);
            tvKind = itemView.findViewById(R.id.vehiclename_item_kind);
            vLine = itemView.findViewById(R.id.seperateline);

        }
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
