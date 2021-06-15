//package com.lubsolution.store.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.lubsolution.store.R;
//import com.lubsolution.store.callback.CallbackObject;
//import com.lubsolution.store.models.BaseModel;
//import com.lubsolution.store.utils.CustomBottomDialog;
//import com.lubsolution.store.utils.Util;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static android.view.View.GONE;
//import static android.view.View.VISIBLE;
//
///**
// * Created by tranhuy on 5/24/17.
// */
//
//public class Customer_SearchAdapter extends RecyclerView.Adapter<Customer_SearchAdapter.CustomerSearchViewHolder> {
//    private List<BaseModel> mData = new ArrayList<>();
//    private LayoutInflater mLayoutInflater;
//    private Context mContext;
//    private CallbackObject mListener;
//    private CustomBottomDialog.FourMethodListener mListerner;
//
//    public Customer_SearchAdapter(List<BaseModel> data, CallbackObject listener) {
//        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
//        this.mData = data;
//        this.mContext = Util.getInstance().getCurrentActivity();
//        this.mListener = listener;
//
//    }
//
//    @Override
//    public CustomerSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(mContext).inflate(R.layout.adapter_search_item, parent, false);
//        return new CustomerSearchViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(final CustomerSearchViewHolder holder, final int position) {
//        holder.tvLine.setVisibility(position == mData.size() - 1 ? GONE : VISIBLE);
//
//        holder.tvMainText.setText(Util.FormatPlate(mData.get(position).getString("plate_number")).replace(" ", "\n"));
//        holder.tvSecondText.setText(String.format("%s %s",
//                mData.get(position).getBaseModel("vehicle").getBaseModel("brand").getString("name"),
//                mData.get(position).getBaseModel("vehicle").getString("name")));
//        holder.tvPhone.setText(Util.FormatPhone(mData.get(position).getString("phone")));
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mData.size();
//    }
//
//    public class CustomerSearchViewHolder extends RecyclerView.ViewHolder {
//        TextView tvMainText, tvSecondText, tvPhone, tvLine, tvIcon;
//
//        public CustomerSearchViewHolder(View itemView) {
//            super(itemView);
//            tvMainText = itemView.findViewById(R.id.suggestion_maintext);
//            tvSecondText = itemView.findViewById(R.id.suggestion_secondtext);
//            tvPhone = itemView.findViewById(R.id.suggestion_contact);
//            tvIcon = itemView.findViewById(R.id.suggestion_icon);
//            tvLine = itemView.findViewById(R.id.suggestion_line);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onResponse(mData.get(getAdapterPosition()));
//
//                }
//            });
//
//        }
//
//    }
//
//}
