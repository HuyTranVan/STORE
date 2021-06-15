package com.lubsolution.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

import static com.lubsolution.store.activities.BaseActivity.createGetParam;

/**
 * Created by tranhuy on 5/24/17.
 */

public class WaitingListAdapter extends RecyclerView.Adapter<WaitingListAdapter.StatisticalBillsViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackObject mListener;

    public WaitingListAdapter(List<BaseModel> data, CallbackObject listener) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.mData = data;


    }

    @Override
    public StatisticalBillsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_waiting_list_item, parent, false);
        return new StatisticalBillsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StatisticalBillsViewHolder holder, final int position) {
        BaseModel customer = mData.get(position).getBaseModel("customer");
        holder.tvPlate.setText(Util.FormatPlate(customer.getString("plate_number")).replace(" ", "\n"));
        holder.tvBrand.setText(customer.getBaseModel("vehicle").getBaseModel("brand").getString("name"));
        holder.tvVehicle.setText(customer.getBaseModel("vehicle").getString("name"));

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onResponse(mData.get(position));
            }
        });

        holder.lnParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xóa xe chờ " + Util.FormatPlate(customer.getString("plate_number")), "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
                    @Override
                    public void onRespone(Boolean result) {
                        if (result) {
                            BaseModel param = createGetParam(ApiUtil.TEMP_CUSTOMER_DELETE() + mData.get(position).getString("id"), false);
                            new GetPostMethod(param, new NewCallbackCustom() {
                                @Override
                                public void onResponse(BaseModel result, List<BaseModel> list) {
                                    if (result.getBoolean("deleted")){
                                        mData.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemChanged(position);

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

    public class StatisticalBillsViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPlate, tvBrand, tvVehicle;
        private View vLine;
        private CardView lnParent;

        public StatisticalBillsViewHolder(View itemView) {
            super(itemView);
            lnParent = itemView.findViewById(R.id.waiting_list_parent);
            tvPlate = itemView.findViewById(R.id.waiting_list_plate);
            tvBrand = itemView.findViewById(R.id.waiting_list_brand);
            tvVehicle = itemView.findViewById(R.id.waiting_list_vehicle);



        }

    }

    public void updateList(List<BaseModel> list){
        for(BaseModel item: list){
            boolean dup = false;
            for (int i=0; i< mData.size(); i++){
                if (item.getInt("id") == mData.get(i).getInt("id")
                        && item.getLong("updateAt") != mData.get(i).getLong("updateAt")) {
                       mData.remove(i);
                       mData.add(i, item);
                       notifyItemChanged(i);
                       dup = true;

                       break;

                }

            }

            if (!dup){
                mData.add(item);
                notifyItemInserted(mData.size() -1);
            }


        }

    }

    public void updateItem(BaseModel customer){
        for (int i=0; i< mData.size(); i++){
            if (customer.getInt("id") == mData.get(i).getBaseModel("customer").getInt("id")){
                //mData.get(i).removeKey("customer");
                mData.get(i).putBaseModel("customer", customer);
                notifyItemChanged(i, mData.get(i));
                break;

            }

        }

    }


}
