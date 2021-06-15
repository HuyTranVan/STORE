package com.lubsolution.store.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lubsolution.store.R;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by tranhuy on 5/24/17.
 */

public class ProductSelectAdapter extends RecyclerView.Adapter<ProductSelectAdapter.ProductDialogShopCartAdapterViewHolder> {
    private List<BaseModel> mData = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private CallbackViewPager mListener;
    private int count = 0;
    private int groupPosition;
//    private Boolean isPromotion;

    public interface CallbackViewPager {
        void onChoosen(int position, int count);
    }

    public ProductSelectAdapter(List<BaseModel> listChoosen, List<BaseModel> list, int groupPosition, BaseModel group, CallbackViewPager listener) {
        this.mLayoutInflater = LayoutInflater.from(Util.getInstance().getCurrentActivity());
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mListener = listener;
        this.groupPosition = groupPosition;

        int groupId = group.getInt("id");
        for (int i = 0; i < list.size(); i++) {
            int groupid = list.get(i).getBaseModel("productGroup").getInt("id");
            if (groupId == groupid && !checkHasChoosen(listChoosen, list.get(i))) {
                list.get(i).put("checked", false);
                this.mData.add(list.get(i));
            }

        }
    }

    @Override
    public ProductDialogShopCartAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_cart_product_dialog_item, parent, false);
        return new ProductDialogShopCartAdapterViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ProductDialogShopCartAdapterViewHolder holder, final int position) {
        holder.tvName.setText(mData.get(position).getString("name"));
        holder.tvUnitPrice.setText(Util.FormatMoney(mData.get(position).getDouble("unitPrice")));
        holder.tvLine.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);
        holder.lnParent.setBackgroundColor(mData.get(position).getBoolean("checked") ? Color.parseColor("#0d000000") : Color.parseColor("#ffffff"));
        if (!Util.checkImageNull(mData.get(position).getString("image_uri"))) {
            Glide.with(mContext).load(Util.getRealPathFromImageURI(Uri.parse(mData.get(position).getString("image_uri")))).centerCrop().into(holder.imgProduct);

        } else {
            Glide.with(mContext).load(R.drawable.lub_icon_round).optionalCenterInside().into(holder.imgProduct);

        }

        holder.lnParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
                if (mData.get(position).getBoolean("checked")) {
                    mData.get(position).put("checked", false);
                    holder.lnParent.setBackgroundColor(Color.parseColor("#ffffff"));
                    count -= 1;
                    mListener.onChoosen(groupPosition, count);
                    //notifyItemChanged(position);

                } else {
                    mData.get(position).put("checked", true);
                    holder.lnParent.setBackgroundColor(Color.parseColor("#50000000"));
                    //notifyItemChanged(position);
                    count += 1;
                    mListener.onChoosen(groupPosition, count);

                }
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ProductDialogShopCartAdapterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvUnitPrice, tvLine;
        private RelativeLayout lnParent;
        private CircleImageView imgProduct;

        public ProductDialogShopCartAdapterViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.shopcart_dialog_product_item_name);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.shopcart_dialog_product_item_unitprice);
            imgProduct = (CircleImageView) itemView.findViewById(R.id.shopcart_dialog_product_item_image);
            lnParent = (RelativeLayout) itemView.findViewById(R.id.shopcart_dialog_product_item_parent);
            tvLine = itemView.findViewById(R.id.shopcart_dialog_product_item_line);
        }

    }

    public List<BaseModel> getAllData() {
        return mData;
    }

    private Boolean checkHasChoosen(List<BaseModel> listChoosen, BaseModel product) {
        Boolean check = false;
        for (int i = 0; i < listChoosen.size(); i++) {
            if (listChoosen.get(i).getString("id").equals(product.getString("id"))) {
                check = true;
                break;
            }
        }
        return check;
    }

    public int getCount() {
        return count;
    }


}
