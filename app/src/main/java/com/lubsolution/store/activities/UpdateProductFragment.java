package com.lubsolution.store.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.lubsolution.store.R;
import com.lubsolution.store.adapter.ListPriceAdapter;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.apiconnect.apiserver.UploadCloudaryMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackInt;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.CallbackString;
import com.lubsolution.store.callback.CallbackUri;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.customviews.CInputForm;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.Product;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomBottomDialog;
import com.lubsolution.store.utils.CustomCenterDialog;
import com.lubsolution.store.utils.CustomInputDialog;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.lubsolution.store.activities.BaseActivity.createPostParam;
import static com.lubsolution.store.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static com.lubsolution.store.utils.Constants.REQUEST_IMAGE_CAPTURE;

//import wolve.dms.libraries.FileUploader;

/**
 * Created by macos on 9/16/17.
 */

public class UpdateProductFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView btnBack;
    private CInputForm edName, edUnitPrice, edNetPrice, edGroup, edVolume, edBasePrice;
    private Button btnSubmit;
    private CircleImageView imgProduct;
    private RadioGroup rdRecommend;
    private RadioButton rdMoto, rdAll, rdCar;
    private RecyclerView rvListPrice;
    private LinearLayout btnAddPrice, lnListPriceGroup;


    private BaseModel currentProduct;
    private ProductActivity mActivity;
    private List<BaseModel> mListPrice = new ArrayList<>();
    private Uri imageChangeUri;
    private ListPriceAdapter priceAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product, container, false);
//        FitScrollWithFullscreen.assistActivity(getActivity(), 1);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        for (int i = 0; i < mActivity.listProductGroup.size(); i++) {
            mActivity.listProductGroup.get(i).put("text", mActivity.listProductGroup.get(i).getString("name"));
        }

        edGroup.setText(mActivity.listProductGroup.get(mActivity.currentPosition).getString("name"));
        edGroup.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                CustomBottomDialog.choiceListObject("CHỌN NHÓM SẢN PHẨM", mActivity.listProductGroup, "text", new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        edGroup.setText(object.getString("text"));

                    }
                }, null);

            }
        });

        String bundle = getArguments().getString(Constants.PRODUCT);
        if (bundle != null) {
            currentProduct = new BaseModel(bundle);
            lnListPriceGroup.setVisibility(View.VISIBLE);
            edName.setText(currentProduct.getString("name"));
            edUnitPrice.setText(Util.FormatMoney(currentProduct.getDouble("unitPrice")));
            edNetPrice.setText(Util.FormatMoney(currentProduct.getDouble("netPrice")));
            edBasePrice.setText(Util.FormatMoney(currentProduct.getDouble("basePrice")));

            edGroup.setText(currentProduct.getBaseModel("productGroup").getString("name"));
            edVolume.setText(currentProduct.getString("volume"));

            if (currentProduct.getInt("forMoto") == currentProduct.getInt("forCar")){
                rdRecommend.check(R.id.add_product_filter_all);
            }else if (currentProduct.getInt("forMoto") == 1){
                rdRecommend.check(R.id.add_product_filter_moto);
            }else {
                rdRecommend.check(R.id.add_product_filter_car);
            }

            mListPrice = currentProduct.getList("listPrice");
            if (!Util.checkImageNull(currentProduct.getString("image")))
                Glide.with(UpdateProductFragment.this).load(currentProduct.getString("image")).centerCrop().into(imgProduct);


        } else {
            currentProduct = new Product(new JSONObject());
            lnListPriceGroup.setVisibility(View.GONE);
            mListPrice = new ArrayList<>();
            currentProduct.put("id", 0);

        }

        edBasePrice.setVisibility(Util.isAdmin() ? View.VISIBLE : View.GONE);
        createRVListPrice();

    }

    private void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        imgProduct.setOnClickListener(this);
        edGroup.setOnClickListener(this);
        btnAddPrice.setOnClickListener(this);
        edUnitPrice.textMoneyEvent(null, null);
        edNetPrice.textMoneyEvent(null, null);
        edBasePrice.textMoneyEvent(null, null);

    }

    private void initializeView() {
        mActivity = (ProductActivity) getActivity();
        edName = (CInputForm) view.findViewById(R.id.add_product_name);
        edUnitPrice = (CInputForm) view.findViewById(R.id.add_product_unit_price);
        edNetPrice = (CInputForm) view.findViewById(R.id.add_product_purchase_price);
        edGroup = (CInputForm) view.findViewById(R.id.add_product_group);
        edVolume = (CInputForm) view.findViewById(R.id.add_product_volume);
        rdRecommend = view.findViewById(R.id.add_product_filter);
        rdAll = view.findViewById(R.id.add_product_filter_all);
        rdCar = view.findViewById(R.id.add_product_filter_car);
        rdMoto = view.findViewById(R.id.add_product_filter_moto);
        edBasePrice = (CInputForm) view.findViewById(R.id.add_product_distributor_price);
        btnSubmit = (Button) view.findViewById(R.id.add_product_submit);
        btnBack = (ImageView) view.findViewById(R.id.icon_back);
        imgProduct = (CircleImageView) view.findViewById(R.id.add_product_image);
        rvListPrice = view.findViewById(R.id.add_product_rvlistprice);
        btnAddPrice = view.findViewById(R.id.add_product_listprice_add);
        lnListPriceGroup = view.findViewById(R.id.add_product_listprice_group);

    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.icon_back:
                mActivity.onBackPressed();
                break;

            case R.id.add_product_submit:
                submitProduct();
                break;

            case R.id.add_product_image:
                choiceGalleryCamera();
                break;

            case R.id.add_product_listprice_add:
                showDialogNewPrice();
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
                        Transaction.startImageChooser(UpdateProductFragment.this, new CallbackUri() {
                            @Override
                            public void uriRespone(Uri uri) {
                                imageChangeUri = uri;
                            }
                        });
                    }

                    @Override
                    public void Method2(Boolean two) {
                        Transaction.startCamera(UpdateProductFragment.this, new CallbackUri() {
                            @Override
                            public void uriRespone(Uri uri) {
                                imageChangeUri = uri;
                            }
                        });
                    }
                });
    }

    private int defineGroupId(String groupName) {
        int id = 0;
        for (int i = 0; i < mActivity.listProductGroup.size(); i++) {
            if (mActivity.listProductGroup.get(i).getString("name").equals(groupName)) {
                id = mActivity.listProductGroup.get(i).getInt("id");
            }
        }
        return id;
    }

    private void submitProduct() {
        if (edName.getText().toString().trim().equals("")
                || edUnitPrice.getText().toString().trim().equals("")
                || edNetPrice.getText().toString().trim().equals("")
                || edBasePrice.getText().toString().trim().equals("")) {
            CustomCenterDialog.alertWithCancelButton(null, "Vui lòng nhập đủ thông tin", "đồng ý", null, new CallbackBoolean() {
                @Override
                public void onRespone(Boolean result) {

                }
            });

        } else {
            if (imageChangeUri != null) {
                new UploadCloudaryMethod(Util.getRealPathFromCaptureURI(imageChangeUri), new CallbackString() {
                    @Override
                    public void Result(String url) {
                        updateProduct(url);
                    }

                }).execute();

            } else {
                if (currentProduct.getInt("id") == 0) {
                    updateProduct("");
                } else if (currentProduct.getString("image").startsWith("http")) {
                    updateProduct(currentProduct.getString("image"));
                } else {
                    updateProduct("");
                }

            }


        }
    }

    private void updateProduct(String urlImage) {
        BaseModel param = createPostParam(ApiUtil.PRODUCT_NEW(),
                String.format(ApiUtil.PRODUCT_CREATE_PARAM,
                        currentProduct.getInt("id") == 0 ? "" : "id=" + currentProduct.getString("id") + "&",
                        Util.encodeString(edName.getText().toString().trim()),
                        Util.valueMoney(edUnitPrice.getText().toString()),
                        Util.valueMoney(edNetPrice.getText().toString()),
                        edVolume.getText().toString().trim().replace(",", ""),
                        defineGroupId(edGroup.getText().toString().trim()),
                        urlImage,
                        Util.valueMoney(edBasePrice.getText().toString()),
                        rdMoto.isChecked() || rdAll.isChecked() ? 1 : 0,
                        rdCar.isChecked() || rdAll.isChecked() ? 1 : 0),
                false, false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                CustomCenterDialog.alertWithCancelButton2("Thành công",
                        "Cập nhật sản phẩm thành công. Tiếp tục?",
                        "Tiếp tục",
                        "Quay lại",
                        new CallbackBoolean() {
                            @Override
                            public void onRespone(Boolean re) {
                                if (!re){
                                    mActivity.reupdateProduct(result);
                                    getActivity().getSupportFragmentManager().popBackStack();

                                }
                            }
                        });

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
                        .start(mActivity,  UpdateProductFragment.this);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            CropImage.activity(imageChangeUri)
                    .setAspectRatio(1,1)
                    .setMaxZoom(10)
                    .setRequestedSize(512, 512)
                    .start(mActivity,  UpdateProductFragment.this);

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageChangeUri = result.getUri();
                Glide.with(this).load(imageChangeUri).centerCrop().into(imgProduct);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Util.showToast(result.getError().getMessage());

            }
        }



    }

    private void showDialogNewPrice(){
        CustomInputDialog.createNewListPrice(btnAddPrice, currentProduct, new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                priceAdapter.addItem(object);
            }
        });

    }

    private void createRVListPrice(){
        priceAdapter = new ListPriceAdapter(mListPrice, new CallbackInt() {
            @Override
            public void onResponse(int value) {
                currentProduct.put("numOfPrice", value);
                currentProduct.putList("listPrice", priceAdapter.getmData());
                mActivity.reupdateProduct(currentProduct);

            }
        });
        Util.createLinearRV(rvListPrice, priceAdapter);

    }



}
