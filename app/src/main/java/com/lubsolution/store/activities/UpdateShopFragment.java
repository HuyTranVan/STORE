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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lubsolution.store.R;
import com.lubsolution.store.adapter.AdminsAdapter;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.apiconnect.apiserver.UploadCloudaryMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackListObject;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.CallbackString;
import com.lubsolution.store.callback.CallbackUri;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.customviews.CInputForm;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.Shop;
import com.lubsolution.store.models.User;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomBottomDialog;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.lubsolution.store.activities.BaseActivity.createGetParam;
import static com.lubsolution.store.activities.BaseActivity.createPostParam;
import static com.lubsolution.store.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static com.lubsolution.store.utils.Constants.REQUEST_IMAGE_CAPTURE;


/**
 * Created by macos on 9/16/17.
 */

public class UpdateShopFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm tvCompany, tvPhone, tvAddress, tvSite, tvThanks, tvName, tvProvince, tvDistrict;
    private Button btnSubmit;
    private TextView tvTitle, tvAddTitle, tvExpire;
    private RelativeLayout rlImage;
    private ImageView image;
    private LinearLayout btnAdminNew, rlAdmin;
    private RecyclerView rvAdmin;
    private ProgressBar progressBar;

    private BaseModel currentShop;
    private Uri imageChangeUri;
    private String imgURL;
    private List<BaseModel> mProvinces = new ArrayList<>();
    private List<BaseModel> mUsers = new ArrayList<>();
    private CallbackObject onDataPass;
    private AdminsAdapter adminAdapter;
    private String TEXT_NEW_ADMIN = "tạo quản trị viên";
    private String TEXT_NEW_USER = "tạo nhân viên";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onDataPass = (CallbackObject) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_add_shop, container, false);
        findViewById();

        intitialData();

        addEvent();
        return view;
    }

    private void findViewById() {
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        tvCompany = view.findViewById(R.id.add_shop_company_name);
        tvAddress = view.findViewById(R.id.add_shop_company_add);
        tvPhone = view.findViewById(R.id.add_shop_company_hotline);
        tvSite = view.findViewById(R.id.add_shop_company_web);
        tvThanks = view.findViewById(R.id.add_shop_company_thanks);
        btnSubmit = view.findViewById(R.id.add_shop_submit);
        tvTitle = view.findViewById(R.id.add_shop_title);
        rlImage = view.findViewById(R.id.add_shop_image_parent);
        tvName = view.findViewById(R.id.add_shop_name);
        tvProvince = view.findViewById(R.id.add_shop_province);
        tvDistrict = view.findViewById(R.id.add_shop_district);
        image = view.findViewById(R.id.add_shop_image);
        btnAdminNew = view.findViewById(R.id.add_shop_add_admin);
        rvAdmin = view.findViewById(R.id.add_shop_rvadmin);
        rlAdmin = view.findViewById(R.id.add_shop_admin_group);
        progressBar = view.findViewById(R.id.add_shop_progress);
        tvAddTitle = view.findViewById(R.id.add_shop_add_admin_title);
        tvExpire = view.findViewById(R.id.add_shop_expire_to);

    }

    private void intitialData() {
        String bundle = getArguments().getString(Constants.SHOP);
        if (bundle != null) {
            currentShop = new BaseModel(bundle);

        }else {
            currentShop = new BaseModel();
            currentShop.put("id", 0);
            currentShop.put("province_id", 0);
        }

        updateView(currentShop);

    }

    private void updateView(BaseModel shop) {
        tvName.setBoldStyle();
        tvName.setTextSize(Util.convertDp2PxInt(10));
        tvName.setText(shop.getString("name"));
        tvProvince.setText(shop.getBaseModel("province").getString("text"));
        tvDistrict.setText(shop.getBaseModel("district").getString("text"));
        tvCompany.setText(shop.getString("company"));
        tvAddress.setText(shop.getString("address"));
        tvPhone.setText(Util.FormatPhone(shop.getString("phone")));
        tvSite.setText(shop.getString("website"));
        tvThanks.setText(shop.getString("thanks"));
        tvTitle.setText(shop.getString("name"));
        if (!Util.checkImageNull(shop.getString("image"))){
            Glide.with(this).load(shop.getString("image")).fitCenter().into(image);
            imgURL = shop.getString("image");

        } else {
            Glide.with(this).load(R.drawable.lub_logo_red).fitCenter().into(image);
            imgURL = "";

        }

        if (shop.hasKey("valid_to")){
            tvExpire.setVisibility(View.VISIBLE);
            if (shop.getLong("valid_to") > Util.CurrentTimeStamp()){
                tvExpire.setText(String.format("Được kích hoạt đến %s", Util.DateHourString(shop.getLong("valid_to"))));
                tvExpire.setBackgroundColor(getResources().getColor(R.color.colorBlue));
            }else {
                tvExpire.setText("Tải khoản hết hạn");
                tvExpire.setBackgroundColor(getResources().getColor(R.color.colorRedTransparent));
            }


        }else {
            tvExpire.setVisibility(View.GONE);
        }

        if (User.getId() == 2){
            if (currentShop.getInt("id") >0){
                loadUsers();
                rlAdmin.setVisibility(View.VISIBLE);

            }else {
                rlAdmin.setVisibility(View.GONE);
            }

        }else {
            rlAdmin.setVisibility(View.GONE);

        }




        tvName.setFocusable(User.getId() != 2 ? false : true);
        tvProvince.setFocusable(User.getId() != 2 ? false : true);
        tvProvince.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                if (mProvinces.size() >0){
                    chooseProvince(mProvinces);

                }else {{
                    getListProvince(new CallbackListObject() {
                        @Override
                        public void onResponse(List<BaseModel> list) {
                            mProvinces = list;
                            chooseProvince(mProvinces);
                        }
                    });

                }}


            }
        });
        tvDistrict.setFocusable(User.getId() != 2 ? false : true);
        tvDistrict.setVisibility(currentShop.getInt("province_id") == 0 ? View.GONE : View.VISIBLE);
        tvDistrict.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                getListDistrict(currentShop.getInt("province_id"), new CallbackListObject() {
                    @Override
                    public void onResponse(List<BaseModel> list) {
                        chooseDistrict(list);
                    }
                });
            }
        });
    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        rlImage.setOnClickListener(this);
        btnAdminNew.setOnClickListener(this);
        phoneEvent();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                getActivity().onBackPressed();
                break;

            case R.id.add_shop_submit:
                submitShop();
                break;

            case R.id.add_shop_add_admin:
                dialogNewAdmin();
                break;

            case R.id.add_shop_image_parent:
                CustomBottomDialog.choiceThreeOption(getString(R.string.icon_image),
                        "Chọn ảnh thư viện",
                        getString(R.string.icon_camera),
                        "Chụp ảnh",
                        getString(R.string.icon_empty),
                        "Mặc định",
                        new CustomBottomDialog.ThreeMethodListener() {
                            @Override
                            public void Method1(Boolean one) {
                                Transaction.startImageChooser(null, new CallbackUri() {
                                    @Override
                                    public void uriRespone(Uri uri) {
                                        imageChangeUri = uri;
                                    }
                                });
                            }

                            @Override
                            public void Method2(Boolean two) {
                                Transaction.startCamera(null, new CallbackUri() {
                                    @Override
                                    public void uriRespone(Uri uri) {
                                        imageChangeUri = uri;
                                    }
                                });
                            }

                            @Override
                            public void Method3(Boolean three) {
                                imgURL = "";
                                Glide.with(UpdateShopFragment.this).load(R.drawable.lub_logo_red).fitCenter().into(image);
                            }
                        });
                break;

        }
    }

    private void submitShop() {
        if (tvName.getText().toString().trim().equals("")){
            Util.showSnackbar("Chưa nhập tên nhà phân phối!", null, null);

        }else if (tvProvince.getText().toString().trim().equals("")){
            Util.showSnackbar("Chưa nhập tỉnh/ thành phố!", null, null);

        }else if (tvPhone.getText().toString().trim().equals("")){
            Util.showSnackbar("Chưa nhập hotline!", null, null);

        }else if (imageChangeUri != null) {
            new UploadCloudaryMethod(Util.getRealPathFromCaptureURI(imageChangeUri), new CallbackString() {
                @Override
                public void Result(String url) {
                    updateShop(url);
                }

            }).execute();

        } else {
            updateShop(imgURL);

        }

    }

    private void updateShop(String imageLink) {
        BaseModel param = createPostParam(ApiUtil.SHOP_NEW(),
                String.format(ApiUtil.SHOP_CREATE_PARAM, currentShop.getInt("id") == 0 ? "" : String.format("id=%s&", currentShop.getString("id")),
                        Util.encodeString(tvName.getText().toString()),
                        currentShop.getInt("province_id"),
                        currentShop.getInt("district_id"),
                        Util.encodeString(tvCompany.getText().toString()),
                        Util.encodeString(tvAddress.getText().toString()),
                        Util.encodeString(tvPhone.getText().toString().replace(".", "")),
                        Util.encodeString(tvSite.getText().toString()),
                        Util.encodeString(tvThanks.getText().toString()),
                        imageLink),
                false,
                false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                currentShop = result;
                if (result.getInt("id") == Shop.getId()){
                    CustomSQL.setBaseModel(Constants.SHOP, result);
                }
                updateView(currentShop);
                if (result.getInt("user") < 1){
                    CustomCenterDialog.alertWithCancelButton("Admin!!!",
                            "Tạo tài khoản Admin để kích hoạt NPP " + currentShop.getString("name"),
                            "đồng ý",
                            "để sau", new CallbackBoolean() {
                                @Override
                                public void onRespone(Boolean result) {
                                    if (!result){
                                        onDataPass.onResponse(currentShop);
                                        getActivity().onBackPressed();
                                    }
                                }
                            });

                }else {
                    onDataPass.onResponse(currentShop);
                    getActivity().onBackPressed();
                }




            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHOOSE_IMAGE) {
            if (data != null) {
                CropImage.activity(Uri.parse(data.getData().toString()))
                        .setAspectRatio(1,1)
                        .setMaxZoom(10)
                        .setRequestedSize(512, 512)
                        .start(getActivity(), UpdateShopFragment.this);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            CropImage.activity(imageChangeUri)
                    .setAspectRatio(1,1)
                    .setMaxZoom(10)
                    .setRequestedSize(512, 512)
                    .start(getActivity(), UpdateShopFragment.this);

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageChangeUri = result.getUri();
                Glide.with(this).load(imageChangeUri).centerCrop().into(image);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Util.showToast(result.getError().getMessage());

            }
        }

    }



    private void phoneEvent() {
        tvPhone.addTextChangePhone(new CallbackString() {
            @Override
            public void Result(String s) {

            }
        });
    }

    private void chooseProvince(List<BaseModel> list){
        CustomBottomDialog.choiceListObject("CHỌN TỈNH / THÀNH PHỐ", list, "text", new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                tvProvince.setText(object.getString("text"));
                currentShop.put("province_id", object.getInt("provinceid"));
                tvDistrict.setVisibility(currentShop.getInt("province_id") == 0 ? View.GONE : View.VISIBLE);

            }
        }, null);
    }

    private void chooseDistrict(List<BaseModel> list){
        CustomBottomDialog.choiceListObject("CHỌN QUẬN / HUYỆN", list, "text", new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                tvDistrict.setText(object.getString("text"));
                currentShop.put("district_id", object.getInt("districtid"));
                //tvDistrict.setVisibility(currentShop.getInt("province_id") == 0 ? View.GONE : View.VISIBLE);

            }
        }, null);
    }

    private void getListProvince(CallbackListObject listener){
        BaseModel param = createGetParam(ApiUtil.PROVINCES(), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onResponse(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();

    }

    private void getListDistrict(int province_id, CallbackListObject listener){
        BaseModel param = createGetParam(ApiUtil.DISTRICTS() + province_id, true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                listener.onResponse(list);
            }

            @Override
            public void onError(String error) {

            }
        }, 0).execute();

    }

    private void dialogNewAdmin(){
        CustomCenterDialog.showDialogNewAdmin(mUsers.size() >0 ? "tạo nhân viên" : "tạo quản trị viên",
                currentShop.getInt("id"),
                mUsers.size() >0 ? 3 : 1,
                mUsers.size() >0?  false : true,
                new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        mUsers.add(object);
                        updateView(currentShop);
                        Util.showToast("Tạo nhân viên thành công");


                    }
                });
    }

    private void loadUsers(){
        progressBar.setVisibility(View.VISIBLE);
        BaseModel param = createGetParam(String.format("%s?shop_id=%d", ApiUtil.USERS(), currentShop.getInt("id")), true);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                progressBar.setVisibility(View.GONE);
                mUsers = list;

                if (mUsers.size() >0){
                    tvAddTitle.setText(TEXT_NEW_USER);
                    rvAdmin.setVisibility(View.VISIBLE);
                    adminAdapter = new AdminsAdapter(mUsers);
                    Util.createLinearRV(rvAdmin, adminAdapter);

                }else {
                    tvAddTitle.setText(TEXT_NEW_ADMIN);
                    rvAdmin.setVisibility(View.GONE);

                }

            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
            }
        }, 0).execute();
    }


}
