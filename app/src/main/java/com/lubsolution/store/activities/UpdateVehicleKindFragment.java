package com.lubsolution.store.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lubsolution.store.R;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.customviews.CInputForm;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

import static com.lubsolution.store.activities.BaseActivity.createPostParam;

/**
 * Created by macos on 9/16/17.
 */

public class UpdateVehicleKindFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edName;
    private Button btnSubmit;
    private TextView tvDelete;

    private BaseModel kindObject;
    private List<String> listBoolean = new ArrayList<>();
    private Uri imageChangeUri;
    private CallbackObject onDataPass;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPass = (CallbackObject) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_vehiclekind, container, false);
//        FitScrollWithFullscreen.assistActivity(getActivity(), 1);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.KIND);
        if (bundle != null) {
            tvDelete.setVisibility(Util.isAdmin()? View.VISIBLE : View.GONE);
            btnSubmit.setText("CẬP NHẬT");
            kindObject = new BaseModel(bundle);
            edName.setText(kindObject.getString("name"));


//            if (!Util.checkImageNull(brandObject.getString("image"))) {
//                Glide.with(UpdateVehicleKindFragment.this).load(brandObject.getString("image")).centerCrop().into(imgBrand);
//
//            } else {
//                Glide.with(this).load(R.drawable.ic_wolver).centerCrop().into(imgBrand);
//
//            }

        } else {
            btnSubmit.setText("TẠO MỚI");
            kindObject = new BaseModel();
            kindObject.put("id", 0);

        }
        Util.showKeyboardEditTextDelay(edName.getEdInput());



    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
    }

    private void initializeView() {
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        edName = (CInputForm) view.findViewById(R.id.new_kind_name);
        btnSubmit = view.findViewById(R.id.new_kind_submit);
        tvDelete = view.findViewById(R.id.icon_delete);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            case R.id.new_kind_submit:
                submitKind();
                break;

            case R.id.icon_delete:

                break;


        }
    }


    private void submitKind() {
        if (edName.getText().toString().trim().equals("")) {
            Util.showSnackbar("Tên loại xe không được rỗng!", null, null);

        } else {
            BaseModel param = createPostParam(ApiUtil.KIND_NEW(),
                    String.format(ApiUtil.KIND_CREATE_PARAM,
                            kindObject.getInt("id") == 0 ? "" : "id=" + kindObject.getString("id") + "&",
                            Util.encodeString(edName.getText().toString().trim())),
                    false, false);
            new GetPostMethod(param, new NewCallbackCustom() {
                @Override
                public void onResponse(BaseModel result, List<BaseModel> list) {
                    getActivity().getSupportFragmentManager().popBackStack();
                    result.put(Constants.KIND, true);
                    onDataPass.onResponse(result);
                }

                @Override
                public void onError(String error) {

                }
            },1).execute();


        }
    }



//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_CHOOSE_IMAGE) {
//            if (data != null) {
//                CropImage.activity(Uri.parse(data.getData().toString()))
//                        .setAspectRatio(1,1)
//                        .setMaxZoom(10)
//                        .setRequestedSize(512, 512)
//                        .start(getActivity(),  UpdateVehicleKindFragment.this);
//
//            }
//
//        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
//            CropImage.activity(imageChangeUri)
//                    .setAspectRatio(1,1)
//                    .setMaxZoom(10)
//                    .setRequestedSize(512, 512)
//                    .start(getActivity(),  UpdateVehicleKindFragment.this);
//
//        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                imageChangeUri = result.getUri();
//                Glide.with(this).load(imageChangeUri).centerCrop().into(imgBrand);
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Util.showToast(result.getError().getMessage());
//
//            }
//        }



//    }


}
