package com.lubsolution.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lubsolution.store.R;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackInt;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.Util;

import java.util.List;

import static com.lubsolution.store.activities.BaseActivity.createGetParam;


/**
 * Created by tranhuy on 9/30/16.
 */
public class ListPriceAdapter extends RecyclerView.Adapter<ListPriceAdapter.ListPriceViewHolder> {
    private List<BaseModel> mData;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private CallbackInt mCountListener;

    public ListPriceAdapter(List<BaseModel> list, CallbackInt listener) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mData = list;
        this.mCountListener = listener;

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<BaseModel> getmData(){
        return  mData;
    }

    @Override
    public ListPriceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_listprice_item, parent, false);
        return new ListPriceViewHolder(itemView);
    }

    public class ListPriceViewHolder extends RecyclerView.ViewHolder {
        private TextView text, price;
        private View line;
        private RelativeLayout mParent;

        public ListPriceViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.listprice_item_text);
            line = (View) itemView.findViewById(R.id.line);
            price = itemView.findViewById(R.id.listprice_item_price);
            mParent = itemView.findViewById(R.id.listprice_item_parent);


        }
    }


    @Override
    public void onBindViewHolder(final ListPriceViewHolder holder, final int position) {
        holder.line.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);
        if (Util.isEmpty(mData.get(position).getString("note"))){
            holder.text.setText(mData.get(position).getString("volume"));

        }else {
            holder.text.setText(String.format("%s (%s)",mData.get(position).getString("note"),  mData.get(position).getString("volume")));
        }
        holder.price.setText(Util.getIconString(R.string.icon_dollar, "  ", Util.FormatMoney(mData.get(position).getDouble("unitPrice"))));

        if (Util.isAdmin()){
            holder.mParent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    CustomCenterDialog.alertWithCancelButton(null, "Bạn muốn xóa giá " + holder.text.getText().toString(), "ĐỒNG Ý", "HỦY", new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            if (result) {
                                BaseModel param = createGetParam(ApiUtil.LISTPRICE_DELETE() + mData.get(position).getString("id"), false);
                                new GetPostMethod(param, new NewCallbackCustom() {
                                    @Override
                                    public void onResponse(BaseModel result, List<BaseModel> list) {
                                        if (result.getBoolean("deleted")){
                                            notifyItemRemoved(position);
                                            mData.remove(position);
                                            mCountListener.onResponse(mData.size());

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

    }

    public void addItem(BaseModel object){
        mData.add( object);
        notifyDataSetChanged();
        mCountListener.onResponse(mData.size());
    }

}

