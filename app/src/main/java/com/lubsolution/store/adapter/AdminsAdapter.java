package com.lubsolution.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lubsolution.store.R;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.Util;
import com.suke.widget.SwitchButton;

import java.util.List;

import static com.lubsolution.store.activities.BaseActivity.createPostParam;


/**
 * Created by tranhuy on 9/30/16.
 */
public class AdminsAdapter extends RecyclerView.Adapter<AdminsAdapter.ChoiceMethodViewHolder> {

    private List<BaseModel> mData;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    //private CustomBottomDialog.PositionListener mListener;

    public AdminsAdapter(List<BaseModel> list) {
        this.mContext = Util.getInstance().getCurrentActivity();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mData = list;

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ChoiceMethodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.adapter_admins_item, parent, false);
        return new ChoiceMethodViewHolder(itemView);
    }

    public class ChoiceMethodViewHolder extends RecyclerView.ViewHolder {
        private TextView text, icon, phone, cover;
        private View line;
        private SwitchButton swActive;
        private ProgressBar progressBar;

        public ChoiceMethodViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.list_admin_text);
            line = (View) itemView.findViewById(R.id.list_admin_line);
            icon = itemView.findViewById(R.id.list_admin_icon);
            phone = itemView.findViewById(R.id.list_admin_phone);
            cover = itemView.findViewById(R.id.list_admin_cover);
            swActive = itemView.findViewById(R.id.list_admin_active);
            progressBar = itemView.findViewById(R.id.list_admin_progress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //mListener.onResponse(getAdapterPosition());

                }
            });

        }
    }


    @Override
    public void onBindViewHolder(final ChoiceMethodViewHolder holder, final int position) {
        holder.text.setText(mData.get(position).getString("displayName"));
        holder.line.setVisibility(position == mData.size() - 1 ? View.GONE : View.VISIBLE);
        holder.phone.setText(Util.FormatPhone(mData.get(position).getString("phone")));
        if (mData.get(position).getInt("role") ==1){
            holder.icon.setText(Util.getIcon(R.string.icon_key));
        }else {
            holder.icon.setText(Util.getIcon(R.string.icon_username));
        }
        holder.swActive.setChecked(mData.get(position).getInt("active") == 1? true : false);
        holder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.swActive.isChecked()){
                    CustomCenterDialog.alertWithButton(null,
                            "Ẩn tài khoản " + mData.get(position).getString("displayName"),
                            "đồng ý",
                            new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (result){
                                        holder.progressBar.setVisibility(View.VISIBLE);
                                        updateActive(mData.get(position), 0, new CallbackBoolean() {
                                            @Override
                                            public void onRespone(Boolean result) {
                                                holder.progressBar.setVisibility(View.GONE);
                                                if (result){
                                                    mData.get(position).put("active", 0);
                                                    notifyItemChanged(position);
                                                }
                                            }
                                        });

                                    }
                                }
                            });

                }else {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    updateActive(mData.get(position), 1, new CallbackBoolean() {
                        @Override
                        public void onRespone(Boolean result) {
                            holder.progressBar.setVisibility(View.GONE);
                            if (result){
                                mData.get(position).put("active", 1);
                                notifyItemChanged(position);
                            }
                        }
                    });
                }
            }
        });

    }

    private void updateActive(BaseModel user, int active, CallbackBoolean listener){
        BaseModel param = createPostParam(ApiUtil.USER_ACTIVE(),
                String.format(ApiUtil.USER_ACTIVE_PARAM, user.getInt("id"), active), false, false);

        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onRespone(true);
            }

            @Override
            public void onError(String error) {
                listener.onRespone(false);
            }
        }, 0).execute();

    }


}

