package com.lubsolution.store.utils;

import android.graphics.Rect;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.lubsolution.store.R;
import com.lubsolution.store.apiconnect.ApiUtil;
import com.lubsolution.store.apiconnect.apiserver.GetPostMethod;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.CallbackString;
import com.lubsolution.store.callback.NewCallbackCustom;
import com.lubsolution.store.customviews.CDropdown;
import com.lubsolution.store.customviews.CInputForm;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.Shop;
import com.lubsolution.store.models.Vehicle;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnBackPressListener;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.List;

import static com.lubsolution.store.activities.BaseActivity.createPostParam;

public class CustomInputDialog {
    private static DialogPlus dialog;

    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

//    public interface ShopNameListener {
//        void onShopname(String shopname, int shoptype);
//    }

    public static void inputWarehouse(View view, final CallbackString mListener) {
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_warehouse))
                .setGravity(Gravity.BOTTOM)
                .setInAnimation(R.anim.slide_up)
                .setBackgroundColorResId(R.drawable.transparent)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .create();

        final CardView lnParent = (CardView) dialog.findViewById(R.id.input_warehouse_parent);
        final EditText edName = (EditText) dialog.findViewById(R.id.input_warehouse_name);
        TextView btnSubmit = (TextView) dialog.findViewById(R.id.input_warehouse_submit);

        edName.requestFocus();
        Util.showKeyboardDelay(edName);

        edName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!edName.getText().toString().trim().equals("")) {
                        mListener.Result(edName.getText().toString().trim());
                        handled = true;

                    } else {
                        Util.showToast("Nhập chưa đủ thông tin");
                    }

                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edName.getText().toString().trim().equals("")) {
                    mListener.Result(edName.getText().toString().trim());
                    dismissDialog();
                    Util.hideKeyboard(v);

                } else {
                    Util.showToast("Nhập chưa đủ thông tin");
                }
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                lnParent.getWindowVisibleDisplayFrame(r);

                int screenHeight = lnParent.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top) - Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._32sdp);
                int margin = Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._2sdp);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin, 0, margin, heightDifference);
                lnParent.setLayoutParams(params);


            }
        });

        dialog.show();


    }

    public static void inputPhoneNumber(View view, final CallbackString mListener) {
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_phonenumber))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.transparent)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogPlus dialog) {
                        //Util.hideKeyboard(btnNewCustomer);
                    }
                })
                .create();


        final CardView lnParent = (CardView) dialog.findViewById(R.id.input_phonenumber_parent);
        final EditText edPhone = (EditText) dialog.findViewById(R.id.input_phonenumber_phone);
        TextView btnSubmit = (TextView) dialog.findViewById(R.id.input_phonenumber_submit);

        Util.textPhoneEvent(edPhone, new CallbackString() {
            @Override
            public void Result(String s) {

            }
        });

        edPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (Util.getPhoneValue(edPhone).matches(Util.DETECT_PHONE)) {
                        mListener.Result(Util.getPhoneValue(edPhone));
                        handled = true;

                    } else {
                        Util.showToast("Sai số điện thoại");
                    }


                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Util.getPhoneValue(edPhone).matches(Util.DETECT_PHONE)) {
                    mListener.Result(Util.getPhoneValue(edPhone));
                    Util.hideKeyboard(v);

                } else {
                    Util.showToast("Sai số điện thoại");
                }

            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                lnParent.getWindowVisibleDisplayFrame(r);

                int screenHeight = lnParent.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top) - Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._32sdp);
                int margin = Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._2sdp);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin, 0, margin, heightDifference);
                lnParent.setLayoutParams(params);


            }
        });


        dialog.show();
        edPhone.requestFocus();
        Util.showKeyboard(edPhone);

    }

    public static void inputNumber(View view, final CallbackString mListener) {
        dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.view_input_number))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.transparent)
                .setInAnimation(R.anim.slide_up)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogPlus dialog) {
                        //Util.hideKeyboard(btnNewCustomer);
                    }
                })
                .create();


        final CardView lnParent = (CardView) dialog.findViewById(R.id.input_number_parent);
        final EditText edPhone = (EditText) dialog.findViewById(R.id.input_number_text);
        TextView btnSubmit = (TextView) dialog.findViewById(R.id.input_number_submit);

        Util.textPhoneEvent(edPhone, new CallbackString() {
            @Override
            public void Result(String s) {

            }
        });

        edPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mListener.Result(edPhone.getText().toString());

                }
                return handled;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.Result(edPhone.getText().toString());

            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                lnParent.getWindowVisibleDisplayFrame(r);

                int screenHeight = lnParent.getRootView().getHeight();
                int heightDifference = screenHeight - (r.bottom - r.top) - Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._32sdp);
                int margin = Util.getInstance().getCurrentActivity().getResources().getDimensionPixelSize(R.dimen._2sdp);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(margin, 0, margin, heightDifference);
                lnParent.setLayoutParams(params);


            }
        });


        dialog.show();
        edPhone.requestFocus();
        Util.showKeyboard(edPhone);

    }

    public static void createNewCustomer(View view, CallbackBoolean listener){
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.dialog_add_customer))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.bg_corner5_white)
                .setMargin(20, 20, 20, 20)
                //.setContentHeight(heigh)
                .setInAnimation(R.anim.slide_up)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                        Util.hideKeyboard(view);
//                        if (dismiss != null) {
//                            dismiss.onRespone(true);
//
//                        }
                    }
                })

                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();

        FrameLayout layoutParent = (FrameLayout) dialog.findViewById(R.id.add_customer_parent);
        EditText iPlate = (EditText) dialog.findViewById(R.id.add_customer_number);
        TextView iPlateShow = (TextView) dialog.findViewById(R.id.add_customer_number_show);
        CInputForm iPhone = (CInputForm) dialog.findViewById(R.id.add_customer_phone);
        CInputForm iUsername = (CInputForm) dialog.findViewById(R.id.add_customer_name);
        CDropdown mBrand = (CDropdown) dialog.findViewById(R.id.add_customer_brand);
        CDropdown mVehicle = (CDropdown) dialog.findViewById(R.id.add_customer_vehicle);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        btnCancel.setText("hủy");
        btnSubmit.setText("Tạo mới");

        Util.showKeyboardEditTextDelay(iPlate);

        mBrand.setFocusable(false);
        mVehicle.setFocusable(false);

        Util.textPhoneEvent(iPhone.getEdInput(), null);

        List<BaseModel> brands = CustomSQL.getListObject(Constants.BRAND_LIST);
        final List<BaseModel>[] currentVehicleList = new List[]{Vehicle.getBrandList(brands.get(0))};
        final BaseModel[] currentVehicle = {new BaseModel()};

        mBrand.setListDropdown(brands, "name", new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                currentVehicleList[0] = Vehicle.getBrandList(object);
                mVehicle.setListDropdown(currentVehicleList[0], "name", new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object1){
                        currentVehicle[0] = object1;

                    }
                });
            }


        });


        Util.plateNumberEvent(iPlate, new CallbackString() {
            @Override
            public void Result(String s) {
                iPlateShow.setText(s);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iPlate.getText().toString().length()< 8 ){
                    Util.showToast("Sai định dạng biển số");

                }else {
                    String param = String.format(ApiUtil.CUSTOMER_CREATE_PARAM,
                            "",
                            Util.encodeString(iUsername.getText()),
                            "",
                            Util.getPhoneValue(iPhone),
                            "",
                            Shop.getDistrictId(),
                            Shop.getProvinceId(),
                            0,
                            Shop.getId(),
                            Util.encodeString(iPlate.getText().toString().replace(" ","")),
                            currentVehicle[0].getInt("id"));
                    new GetPostMethod(createPostParam(ApiUtil.CUSTOMER_NEW(),
                            param,
                            false,
                            false),
                            new NewCallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result, List<BaseModel> list) {
                            listener.onRespone(true);
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(String error) {
                            listener.onRespone(false);
                            dialog.dismiss();
                        }
                    },2).execute();

                }
            }
        });

        dialog.show();

    }

    public static void createNewVehicle(View view, int position, List<BaseModel> brands, CallbackObject mListener){
        final DialogPlus dialog = DialogPlus.newDialog(Util.getInstance().getCurrentActivity())
                .setContentHolder(new ViewHolder(R.layout.dialog_add_vehiclename))
                .setGravity(Gravity.BOTTOM)
                .setBackgroundColorResId(R.drawable.bg_corner5_white)
                .setMargin(20, 20, 20, 20)
                //.setContentHeight(heigh)
                .setInAnimation(R.anim.slide_up)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                        Util.hideKeyboard(view);
//                        if (dismiss != null) {
//                            dismiss.onRespone(true);
//
//                        }
                    }
                })
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onBackPressed(DialogPlus dialogPlus) {
                        dialogPlus.dismiss();
                    }
                }).create();

        FrameLayout layoutParent = (FrameLayout) dialog.findViewById(R.id.add_vehiclename_parent);
        EditText edName = (EditText) dialog.findViewById(R.id.new_vehicename);
        Spinner spBrand = (Spinner) dialog.findViewById(R.id.new_vehicename_brand);
        RadioGroup rdKind = (RadioGroup) dialog.findViewById(R.id.new_vehicle_kinds);
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);

        btnCancel.setText("hủy");
        btnSubmit.setText("Tạo mới");

        Util.showKeyboardEditTextDelay(edName);

        final int[] currentPosition = {position};

        //List<BaseModel> mBrands = DataUtil.array2ListObject(CustomSQL.getString(Constants.VEHICLEBRAND_LIST));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), R.layout.view_spinner_text, DataUtil.getListStringFromListObject(brands, "name"));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBrand.setAdapter(dataAdapter);
        spBrand.setSelection(currentPosition[0]);

        spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentPosition[0] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        rdKind.check(R.id.new_vehicle_moto);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.hideKeyboard(view);
                dialog.dismiss();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.hideKeyboard(view);
                if (Util.isEmpty(edName)){
                    Util.showToast("Thiếu thông tin tên xe");

                }else {
                    BaseModel param = createPostParam(ApiUtil.VEHICLE_NEW(),
                            String.format(ApiUtil.VEHICLE_CREATE_PARAM,
                                    "" ,
                                    Util.encodeString(edName.getText().toString().trim()),
                                    brands.get(currentPosition[0]).getInt("id"),
                                    rdKind.getCheckedRadioButtonId() == R.id.new_vehicle_moto ? 0 : 1,
                                    ""),
                            false, false);
                    new GetPostMethod(param, new NewCallbackCustom() {
                        @Override
                        public void onResponse(BaseModel result, List<BaseModel> list) {
                            dialog.dismiss();
                            mListener.onResponse(result);

                        }

                        @Override
                        public void onError(String error) {
                            dialog.dismiss();
                        }
                    },1).execute();

                }
            }
        });

        dialog.show();

    }


}
