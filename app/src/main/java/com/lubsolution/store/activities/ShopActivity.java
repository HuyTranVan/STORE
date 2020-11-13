package com.lubsolution.store.activities;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lubsolution.store.R;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.apiconnect.apiserver.UploadCloudaryMethod;
import com.lubsolution.store.callback.CallbackString;
import com.lubsolution.store.callback.CallbackUri;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.customviews.CInputForm;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.Shop;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomBottomDialog;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.Transaction;
import com.lubsolution.store.utils.Util;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.List;

import static com.lubsolution.store.utils.Constants.REQUEST_CHOOSE_IMAGE;
import static com.lubsolution.store.utils.Constants.REQUEST_IMAGE_CAPTURE;


/**
 * Created by macos on 9/16/17.
 */

public class ShopActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnBack;
    private CInputForm tvCompany, tvPhone, tvAddress, tvSite, tvThanks;
    private Button btnSubmit;
    private TextView tvTitle;
    private RelativeLayout rlImage;
    private ImageView image;

    private BaseModel currentShop;
    private Uri imageChangeUri;
    private String imgURL;

    @Override
    public int getResourceLayout() {
        return R.layout.activity_shop;
    }

    @Override
    public int setIdContainer() {
        return R.id.shop_parent;
    }

    @Override
    public void findViewById() {
        btnBack = (ImageView) findViewById(R.id.icon_back);
        tvCompany = findViewById(R.id.shop_company_name);
        tvAddress = findViewById(R.id.shop_company_add);
        tvPhone = findViewById(R.id.shop_company_hotline);
        tvSite = findViewById(R.id.shop_company_web);
        tvThanks = findViewById(R.id.shop_company_thanks);
        btnSubmit = findViewById(R.id.shop_submit);
        tvTitle = findViewById(R.id.shop_title);
        rlImage = findViewById(R.id.shop_image_parent);
        image = findViewById(R.id.shop_image);

    }

    @Override
    public void initialData() {
        BaseModel param = createGetParam(ApiUtil.SHOP_DETAIL() + Shop.getShopId(), false);
        new GetPostMethod(param, new NewCallbackCustom() {
            @Override
            public void onResponse(BaseModel result, List<BaseModel> list) {
                currentShop = result;
                updateView(currentShop);
            }

            @Override
            public void onError(String error) {

            }
        }, 1).execute();


    }

    private void updateView(BaseModel content) {
        tvCompany.setText(content.getString("company"));
        tvAddress.setText(content.getString("address"));
        tvPhone.setText(Util.FormatPhone(content.getString("phone")));
        tvSite.setText(content.getString("website"));
        tvThanks.setText(content.getString("thanks"));
        tvTitle.setText(content.getString("name"));
        if (!Util.checkImageNull(content.getString("image"))){
            Glide.with(this).load(content.getString("image")).fitCenter().into(image);
            imgURL = content.getString("image");

        } else {
            Glide.with(this).load(R.drawable.lub_logo_red).fitCenter().into(image);
            imgURL = "";

        }

    }

    @Override
    public void addEvent() {
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        rlImage.setOnClickListener(this);
        phoneEvent();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Transaction.gotoHomeActivityRight(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_back:
                onBackPressed();
                break;

            case R.id.shop_submit:
                submitShop();
                break;

            case R.id.shop_image_parent:
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
                                Glide.with(ShopActivity.this).load(R.drawable.lub_logo_red).fitCenter().into(image);
                            }
                        });
                break;


        }
    }

    private void submitShop() {
        if (imageChangeUri != null) {
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
                CustomSQL.setBaseModel(Constants.SHOP, result);
                Transaction.gotoHomeActivityRight(true);
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
                        .start(this);

            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            CropImage.activity(imageChangeUri)
                    .setAspectRatio(1,1)
                    .setMaxZoom(10)
                    .setRequestedSize(512, 512)
                    .start(this);

        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageChangeUri = result.getUri();
                Glide.with(this).load(imageChangeUri).centerCrop().into(image);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Util.showToast(result.getError().getMessage());

            }
        }
//        else if (data != null && requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
//            Glide.with(this).load(imageChangeUri).centerCrop().into(image);

//        }



    }

//    private void startImageChooser() {
//        imageChangeUri = Uri.fromFile(Util.getOutputMediaFile());
//        if (Build.VERSION.SDK_INT <= 19) {
//            Intent i = new Intent();
//            i.setType("image/*");
//            i.setAction(Intent.ACTION_GET_CONTENT);
//            i.addCategory(Intent.CATEGORY_OPENABLE);
//            startActivityForResult(i, REQUEST_CHOOSE_IMAGE);
//
//        } else if (Build.VERSION.SDK_INT > 19) {
//            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
//        }
//    }

//    public void startCamera() {
//        imageChangeUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", Util.getOutputMediaFile());
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageChangeUri );
//        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//
//    }


    private void phoneEvent() {
        tvPhone.addTextChangePhone(new CallbackString() {
            @Override
            public void Result(String s) {

            }
        });
    }


}
