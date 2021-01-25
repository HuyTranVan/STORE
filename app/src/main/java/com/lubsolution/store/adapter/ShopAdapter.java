package com.lubsolution.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lubsolution.store.R;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackLong;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.CustomBottomDialog;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.Util;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import static com.lubsolution.store.activities.BaseActivity.createPostParam;

/**
 * Created by tranhuy on 5/24/17.
 */

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private BaseModel mGroup;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;

    public ShopAdapter(List<BaseModel> list, CallbackObject listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.mData = list;

    }

    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_shop_item, parent, false);
        return new ShopViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ShopViewHolder holder, final int position) {
        holder.line.setVisibility(position == mData.size() -1? View.GONE : View.VISIBLE);
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvLocation.setText(String.format("%s - %s",
                mData.get(position).getBaseModel("district").getString("name"),
                mData.get(position).getBaseModel("province").getString("name")));

        if (!Util.checkImageNull(mData.get(position).getString("image"))) {
            Glide.with(mContext).load(mData.get(position).getString("image")).centerCrop().into(holder.imageDistributor);

        } else {
            Glide.with(mContext).load(R.drawable.lub_logo_red_50).centerCrop().into(holder.imageDistributor);

        }

        if (mData.get(position).getLong("valid_to") - Util.CurrentTimeStamp() >0){
            holder.swActive.setChecked(true);
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(Util.DateDisplay(mData.get(position).getLong("valid_to") , "dd.MM.yyyy"));

        }else {
            holder.swActive.setChecked(false);
            holder.tvDate.setVisibility(View.GONE);

        }
        holder.mCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.swActive.isChecked()){
                    CustomBottomDialog.selectDate(String.format("%s >>> ACTIVE", mData.get(position).getString("name")),
                            Util.CurrentTimeStamp(),
                            new CallbackLong() {
                                @Override
                                public void onResponse(Long value) {
                                    if (value <= Util.CurrentTimeStamp()){
                                        Util.showToast("Chọn sai thời gian");

                                    }else {
                                        String message = String.format("Kích hoạt tài khoản %s đến %s",
                                                mData.get(position).getString("name"),
                                                Util.DateHourString(value));
                                        CustomCenterDialog.alertWithCancelButton(null,
                                                message,
                                                "đồng ý",
                                                "quay lại",
                                                new CallbackBoolean() {
                                                    @Override
                                                    public void onRespone(Boolean result) {
                                                        if (result){
                                                            updateActiveShop(mData.get(position),value,  new CallbackObject() {
                                                                @Override
                                                                public void onResponse(BaseModel object) {
                                                                    mData.get(position).put("valid_to", object.getLong("valid_to"));
                                                                    notifyItemChanged(position);
                                                                }
                                                            });

                                                        }
                                                    }
                                                });
                                    }

                                }
                            });




                }else {
                    CustomCenterDialog.alertWithCancelButton("Deactive!!",
                            String.format("Tạm ngừng hoạt động %s từ %s", mData.get(position).getString("name"), Util.CurrentMonthYearHour()),
                            "đồng ý",
                            "quay lại",
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result){
                                        updateActiveShop(mData.get(position),0,  new CallbackObject() {
                                            @Override
                                            public void onResponse(BaseModel object) {
                                                mData.get(position).put("valid_to", object.getLong("valid_to"));
                                                notifyItemChanged(position);
                                            }
                                        });

                                    }
                                }
                            });


                }

            }
        });

        holder.rlParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onResponse(mData.get(position));

            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class ShopViewHolder extends RecyclerView.ViewHolder {
        private View line;
        private TextView tvName, tvLocation, mCover, tvDate;
        private RelativeLayout rlParent;
        private ImageView imageDistributor;
        private SwitchButton swActive;

        public ShopViewHolder(View itemView) {
            super(itemView);
            line = itemView.findViewById(R.id.line);
            tvName = (TextView) itemView.findViewById(R.id.shop_item_name);
            tvLocation = (TextView) itemView.findViewById(R.id.shop_item_location);
            rlParent = (RelativeLayout) itemView.findViewById(R.id.shop_item_parent);
            imageDistributor = itemView.findViewById(R.id.shop_item_image);
            swActive = itemView.findViewById(R.id.shop_item_active);
            mCover = itemView.findViewById(R.id.shop_item_active_cover);
            tvDate = itemView.findViewById(R.id.shop_item_active_date);
        }

    }

    public List<BaseModel> getmData(){
        return mData;
    }

    public void updateDistributor(BaseModel distributor){
        boolean checkExist = false;
        for (int i=0; i< mData.size(); i++){
            if (mData.get(i).getInt("id") == distributor.getInt("id")){
                mData.remove(i);
                mData.add(i, distributor);
                checkExist = true;
                notifyItemChanged(i);
                break;
            }
        }

        if (!checkExist){
            mData.add(distributor);
            notifyItemInserted(mData.size() -1);
        }
    }

    private void updateActiveShop(BaseModel distributor, long time, CallbackObject listener){
        BaseModel param = createPostParam(ApiUtil.ACTIVE_NEW(),
                String.format(ApiUtil.ACTIVE_CREATE_PARAM, distributor.getInt("id"), time),
                false,
                false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onResponse(result);
            }

            @Override
            public void onError(String error) {

            }
        },1).execute();

    }

}
