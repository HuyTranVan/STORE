package com.lubsolution.store.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lubsolution.store.R;
import com.lubsolution.store.callback.CallbackListObject;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.callback.CallbackString;
import com.lubsolution.store.customviews.CDropdown;
import com.lubsolution.store.customviews.CInputForm;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.models.District;
import com.lubsolution.store.models.Province;
import com.lubsolution.store.models.Vehicle;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomBottomDialog;
import com.lubsolution.store.utils.CustomInputDialog;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by macos on 9/16/17.
 */

public class CustomerInfoFragment extends Fragment implements View.OnClickListener {
    private View view;
    private CInputForm edProvince, edDistrict, edPhone, edName, edNote;
    private CDropdown mBrand, mVehicle;
    private EditText edPlate;
    protected TextView tvPlateShow;

    private CustomerActivity mActivity;
    private List<BaseModel> brands = new ArrayList<>();
    private BaseModel currentBrand;
    private List<BaseModel> vehicles = new ArrayList<>();
    private BaseModel currentVehicle;
    private List<BaseModel> mProvince = new ArrayList<>() ;
    private List<BaseModel> mDistrict = new ArrayList<>() ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_info, container, false);

        initializeView();

//        addEvent();

        return view;
    }

    private void initializeView() {
        mActivity = (CustomerActivity) getActivity();
        mActivity.infoFragment = this;

        edDistrict = (CInputForm) view.findViewById(R.id.customer_info_district);
        edProvince = (CInputForm) view.findViewById(R.id.customer_info_province);
        edPhone = (CInputForm) view.findViewById(R.id.customer_info_phone);
        edName = (CInputForm) view.findViewById(R.id.customer_info_name);
        edNote = view.findViewById(R.id.customer_info_note);
        edPlate = view.findViewById(R.id.customer_info_plate);
        tvPlateShow = view.findViewById(R.id.customer_info_plate_show);
        mBrand = view.findViewById(R.id.customer_brand);
        mVehicle = view.findViewById(R.id.customer_vehicle);

    }

    public void reloadInfo() {
        edPlate.setText(Util.FormatPlate(mActivity.currentCustomer.getString("plate_number")));
        tvPlateShow.setText(Util.FormatPlate(mActivity.currentCustomer.getString("plate_number")).replace(" ", "\n"));

        edName.setText(mActivity.currentCustomer.getString("name"));

        edPhone.setText(Util.FormatPhone(mActivity.currentCustomer.getString("phone")));
        edPhone.setIconMoreText(Util.isEmpty(edPhone.getEdInput()) ? null : Util.getIcon(R.string.icon_copy));

        edNote.setText(getNoteText(mActivity.currentCustomer.getString("note")));
        edNote.setIconMoreText(Util.getIcon(R.string.icon_edit_pen));
        edNote.setFocusable(false);

        edDistrict.setText( mActivity.currentCustomer.getBaseModel("district").getString("text"));
        edProvince.setText( mActivity.currentCustomer.getBaseModel("province").getString("text"));

        initialBrand();

        addEvent();

        //mCall.setVisibility(mActivity.currentCustomer.getString("phone").equals("") ? View.GONE : View.VISIBLE);,

    }

    private void addEvent() {
        phoneEvent();
        nameEvent();
        plateNumberEvent();

        edNote.setOnMoreClickView(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogNote();

            }
        });
        edPhone.setOnMoreClickView(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager manager = (ClipboardManager) mActivity.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", Util.getPhoneValue(edPhone));
                manager.setPrimaryClip(clipData);

                Util.showToast("Đã copy số điện thoại");
            }
        });

        edProvince.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                if (mProvince.size() < 1){
                    Province.getProvincetList(new CallbackListObject() {
                        @Override
                        public void onResponse(List<BaseModel> list) {
                            mProvince = list;
                            selectProvince();
                        }
                    });

                }else {
                    selectProvince();

                }


            }
        });

        edDistrict.setDropdown(true, new CInputForm.ClickListener() {
            @Override
            public void onClick(View view) {
                if (mDistrict.size() < 1){
                    District.getDistrictList(mActivity.currentCustomer.getInt("province_id"),
                            new CallbackListObject() {
                        @Override
                        public void onResponse(List<BaseModel> list) {
                            mDistrict = list;
                            selectDistrict();
                        }
                    });

                }else {
                    selectDistrict();

                }


            }
        });


    }

    private void selectProvince() {
        CustomBottomDialog.choiceListObject("CHỌN TỈNH / THÀNH PHỐ",
                mProvince, "text", new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                if (object.getInt("provinceid") != mActivity.currentCustomer.getInt("province_id")){
                    edProvince.setText( object.getString("text"));
                    mActivity.currentCustomer.put("province_id", object.getInt("provinceid"));
                    mActivity.currentCustomer.put("province", object);

                    District.getDistrictList(mActivity.currentCustomer.getInt("province_id"),
                            new CallbackListObject() {
                                @Override
                                public void onResponse(List<BaseModel> list) {
                                    mDistrict = list;
                                    edDistrict.setText(mDistrict.get(0).getString("text"));
                                    mActivity.currentCustomer.put("district_id", mDistrict.get(0).getInt("districtid"));
                                    mActivity.currentCustomer.put("district", mDistrict.get(0));

                                    mActivity.saveCustomerToLocal("district_id", mDistrict.get(0).getInt("districtid"));
                                    mActivity.saveCustomerToLocal("district", mDistrict.get(0));

                                }
                            });
                }

            }
        }, null);



    }

    private void selectDistrict() {
        CustomBottomDialog.choiceListObject("CHỌN QUẬN / HUYỆN",
                mDistrict, "text", new CallbackObject() {
                    @Override
                    public void onResponse(BaseModel object) {
                        edDistrict.setText( object.getString("text"));
                        mActivity.saveCustomerToLocal("district_id", object.getInt("districtid"));
                        mActivity.saveCustomerToLocal("district", object);

                    }
                }, null);
    }



    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {

//            case R.id.customer_info_call:
//                Transaction.openCallScreen(mActivity.currentCustomer.getString("phone"));
//
//                break;




        }
    }


    private void plateNumberEvent() {
        Util.plateNumberEvent(edPlate, new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.saveCustomerToLocal("plate_number", s);

                mActivity.updateTitle(mActivity.currentCustomer);
                tvPlateShow.setText(Util.FormatPlate(mActivity.currentCustomer.getString("plate_number")).replace(" ", "\n"));


            }
        });
    }

    private void phoneEvent() {
        edPhone.addTextChangePhone(new CallbackString() {
            @Override
            public void Result(String s) {
                if (Util.isEmpty(edPhone.getEdInput())){
                    edPhone.setIconMoreText(null);

                }else {
                    edPhone.setIconMoreText(Util.getIcon(R.string.icon_copy));
                }
                mActivity.saveCustomerToLocal("phone", s);
                //mCall.setVisibility(mActivity.currentCustomer.getString("phone").equals("") ? View.GONE : View.VISIBLE);

            }
        });
    }



    private void initialBrand(){
        mBrand.setFocusable(false);
        mBrand.setClickable(true);

        mVehicle.setFocusable(false);
        mVehicle.setClickable(true);

        brands = CustomSQL.getListObject(Constants.BRAND_LIST);
        currentBrand = mActivity.currentCustomer.getBaseModel("vehicle").getBaseModel("brand");
        currentVehicle = mActivity.currentCustomer.getBaseModel("vehicle") ;

        mBrand.setListDropdown(
                currentBrand.getString("name"),
                brands,
                "name",
                0,
                new CallbackObject() {
            @Override
            public void onResponse(BaseModel object) {
                if (object.getInt("id") != currentBrand.getInt("id")){
                    currentBrand = object;
                    //vehicles = Vehicle.getVehicleListWithNew(currentBrand);
                    //currentVehicle =  vehicles.get(1);

                }else {
                    //vehicles = Vehicle.getVehicleListWithNew(currentBrand);
                    //currentVehicle =  vehicles.get(1);

                }
                vehicles = Vehicle.getVehicleListWithNew(currentBrand);
                currentVehicle =  vehicles.get(1);
                initialVehicle(vehicles, currentVehicle );

            }


        });
    }

    private void initialVehicle(List<BaseModel> listVehicle, BaseModel currentvehicle){
        mVehicle.setFocusable(false);
        mVehicle.setClickable(true);
        mVehicle.setListDropdown(currentvehicle == null? null: currentvehicle.getString("name"),
                listVehicle,
                "name",
                1,
                new CallbackObject() {
                            @Override
                            public void onResponse(BaseModel object1) {
                                if (object1.getInt("id") > 0){
                                    if (object1.getInt("id") != mActivity.currentCustomer.getInt("vehicle_id")){
                                        mActivity.saveCustomerToLocal("vehicle_id", object1.getInt("id"));
                                        mActivity.currentCustomer.putBaseModel("vehicle", object1);
                                        mActivity.updateTitle(mActivity.currentCustomer);
                                    }

                                }else {
                                    CustomInputDialog.createNewVehicle(mVehicle,
                                            null,
                                            currentBrand,
                                            new CallbackObject() {
                                                @Override
                                                public void onResponse(BaseModel object) {

                                                }
                                            });

                                }

                            }
                        });
    }



    private void nameEvent() {
        edName.addTextChangeName(new CallbackString() {
            @Override
            public void Result(String s) {
                mActivity.saveCustomerToLocal("name", s);

            }
        });
    }


    private String getNoteText(String note) {
        if (!note.isEmpty()) {
            try {
                JSONObject object = new JSONObject(note.trim());
                return object.getString("note");


            } catch (JSONException e) {
                return note;
            }
        } else {
            return "";
        }

    }








    private void showDialogNote(){
        String currentNote = getNoteText(mActivity.currentCustomer.getString("note"));
        CustomInputDialog.showNoteInput(edNote,
                "Nhập nội dung ghi chú ",
                Util.isEmpty(currentNote) ? "" : currentNote + "\n",
                new CallbackString() {
                    @Override
                    public void Result(String s) {
                        edNote.setText(s);
                        mActivity.saveCustomerToLocal("note", s);

                    }
                });
    }


}

