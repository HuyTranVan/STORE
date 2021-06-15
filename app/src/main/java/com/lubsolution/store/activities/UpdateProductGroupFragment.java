package com.lubsolution.store.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lubsolution.store.R;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.customviews.CInputForm;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.Util;

import java.util.List;

import static com.lubsolution.store.activities.BaseActivity.createPostParam;


/**
 * Created by macos on 9/16/17.
 */

public class UpdateProductGroupFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edInput;
    private Button btnSubmit;

    private BaseModel productGroup;
    private ProductGroupActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_productgroup, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.PRODUCTGROUP);
        if (bundle != null) {
            btnSubmit.setText("cập nhật");
            productGroup = new BaseModel(bundle);
            edInput.setText(productGroup.getString("name"));

        }else {
            btnSubmit.setText("tạo mới");
            productGroup = new BaseModel();
            productGroup.put("id", 0);
        }

        Util.showKeyboardEditTextDelay(edInput.getEdInput());


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void initializeView() {
        edInput = (CInputForm) view.findViewById(R.id.add_productgroup_input);
        btnSubmit = (Button) view.findViewById(R.id.add_productgroup_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);

        mActivity = (ProductGroupActivity) getActivity();
    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                Util.hideKeyboard(v);
                mActivity.onBackPressed();
                break;

            case R.id.add_productgroup_submit:
                if (edInput.getText().toString().trim().equals("")) {
                    Util.showSnackbar("Tên nhóm không được để trống!", null, null);

                } else {
                    BaseModel param = createPostParam(ApiUtil.PRODUCT_GROUP_NEW(),
                            String.format(ApiUtil.PRODUCTGROUP_CREATE_PARAM, productGroup.getInt("id") == 0 ? "" : "id=" + productGroup.getString("id") + "&",
                                    Util.encodeString(edInput.getText().toString())),
                            false,
                            false);
                    new GetPostMethod(param, new NewCallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result, List<BaseModel> list) {
                            getActivity().getSupportFragmentManager().popBackStack();
                            mActivity.loadProductGroup();
                        }

                        @Override
                        public void onError(String error) {

                        }
                    }, 1).execute();

                    break;
                }
        }
    }
}
