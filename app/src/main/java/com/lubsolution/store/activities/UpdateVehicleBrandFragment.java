package com.lubsolution.store.activities;

import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.lubsolution.store.R;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.apiconnect.apiserver.UploadCloudaryMethod;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.CallbackString;
import com.lubsolution.store.callback.CallbackUri;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.customviews.CInputForm;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomBottomDialog;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.lubsolution.store.activities.BaseActivity.createPostParam;
import static com.lubsolution.store.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static com.lubsolution.store.utils.Constants.REQUEST_IMAGE_CAPTURE;

/**
 * Created by macos on 9/16/17.
 */

public class UpdateVehicleBrandFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private TextView tvDelete;
    private CInputForm edName;
    private Button btnSubmit;
    private CircleImageView imgBrand;

    private BaseModel brandObject;
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
        view = inflater.inflate(R.layout.fragment_add_vehiclebrand, container, false);
//        FitScrollWithFullscreen.assistActivity(getActivity(), 1);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.BRAND_RESULT);
        if (bundle != null) {
            tvDelete.setVisibility(Util.isAdmin()? View.VISIBLE : View.GONE);
            btnSubmit.setText("CẬP NHẬT");
            brandObject = new BaseModel(bundle);
            edName.setText(brandObject.getString("name"));

            if (!Util.checkImageNull(brandObject.getString("image"))) {
                Glide.with(UpdateVehicleBrandFragment.this).load(brandObject.getString("image")).centerCrop().into(imgBrand);

            } else {
                Glide.with(this).load(R.drawable.ic_wolver).centerCrop().into(imgBrand);

            }

        } else {
            btnSubmit.setText("TẠO MỚI");
            brandObject = new BaseModel();
            brandObject.put("id", 0);

        }
        Util.showKeyboardEditTextDelay(edName.getEdInput());


    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        imgBrand.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
    }

    private void initializeView() {
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        edName = (CInputForm) view.findViewById(R.id.new_brand_name);
        imgBrand = (CircleImageView) view.findViewById(R.id.new_brand_image);
        btnSubmit = view.findViewById(R.id.new_brand_submit);
        tvDelete = view.findViewById(R.id.icon_delete);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            case R.id.new_brand_submit:
                submitBrand();
                break;

            case R.id.new_brand_image:
                choiceGalleryCamera();
                break;

            case R.id.icon_delete:
                deleteBrand(brandObject.getInt("id"));
                break;

        }
    }

    private void choiceGalleryCamera() {
        CustomBottomDialog.choiceTwoOption(getString(R.string.icon_image),
                "Chọn ảnh thư viện",
                getString(R.string.icon_camera),
                "Chụp ảnh",
                new CustomBottomDialog.TwoMethodListener() {
                    @Override
                    public void Method1(Boolean one) {
                        Transaction.startImageChooser(UpdateVehicleBrandFragment.this, new CallbackUri() {
                            @Override
                            public void uriRespone(Uri uri) {
                                imageChangeUri = uri;
                            }
                        });
                    }

                    @Override
                    public void Method2(Boolean two) {
                        Transaction.startCamera(UpdateVehicleBrandFragment.this, new CallbackUri() {
                            @Override
                            public void uriRespone(Uri uri) {
                                imageChangeUri = uri;
                            }
                        });
                    }
                });
    }

    private void submitBrand() {
        if (edName.getText().toString().trim().equals("")) {
            Util.showSnackbar("Tên hãng xe không được rỗng!", null, null);

        } else {
            if (imageChangeUri != null) {
                new UploadCloudaryMethod(Util.getRealPathFromCaptureURI(imageChangeUri), new CallbackString() {
                    @Override
                    public void Result(String url) {
                        updateProduct(url);
                    }

                }).execute();

            } else {
                if (brandObject.getInt("id") == 0) {
                    updateProduct("");
                } else if (brandObject.getString("image").startsWith("http")) {
                    updateProduct(brandObject.getString("image"));
                } else {
                    updateProduct("");
                }

            }


        }
    }

    private void updateProduct(String urlImage) {
        BaseModel param = createPostParam(ApiUtil.BRAND_NEW(),
                String.format(ApiUtil.BRAND_CREATE_PARAM,
                        brandObject.getInt("id") == 0 ? "" : "id=" + brandObject.getString("id") + "&",
                        Util.encodeString(edName.getText().toString().trim()),
                        urlImage),
                false, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                result.put(Constants.BRAND_RESULT, true);
                onDataPass.onResponse(result);

                getActivity().getSupportFragmentManager().popBackStack();

            }

            @Override
            public void onError(String error) {

            }
        },1).execute();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_IMAGE) {
            if (data != null) {
                CropImage.activity(Uri.parse(data.getData().toString()))
                        .setAspectRatio(1,1)
                        .setMaxZoom(10)
                        .setRequestedSize(512, 512)
                        .start(getActivity(),  UpdateVehicleBrandFragment.this);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            CropImage.activity(imageChangeUri)
                    .setAspectRatio(1,1)
                    .setMaxZoom(10)
                    .setRequestedSize(512, 512)
                    .start(getActivity(),  UpdateVehicleBrandFragment.this);

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageChangeUri = result.getUri();
                Glide.with(this).load(imageChangeUri).centerCrop().into(imgBrand);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Util.showToast(result.getError().getMessage());

            }
        }

    }

    private void deleteBrand(int id){

    }


}
