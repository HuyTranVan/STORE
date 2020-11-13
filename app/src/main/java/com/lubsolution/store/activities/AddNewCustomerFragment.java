package com.lubsolution.store.activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.lubsolution.store.R;
import com.lubsolution.store.callback.CallbackBoolean;
import com.lubsolution.store.customviews.CInputForm;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.Constants;
import com.lubsolution.store.utils.CustomSQL;
import com.lubsolution.store.utils.DataUtil;
import com.lubsolution.store.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class AddNewCustomerFragment extends Fragment implements View.OnClickListener {
    private View view, line;
    private FrameLayout layoutParent;
    private CInputForm iPlate, iPhone, iUsername;
    private Button btnCancel, btnSubmit;
    private Spinner spBrand, spVehicleName;

    private List<BaseModel> mGroups = new ArrayList<>();
    private List<BaseModel> mCurrentVehicles = new ArrayList<>();
    private BaseModel mCurrentBrand ;


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        onDataPass = (CallbackObject) context;
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_customer, container, false);
        initializeView();

        intitialData();

        addEvent();
        return view;
    }

    private void intitialData() {
        mGroups = DataUtil.array2ListObject(CustomSQL.getString(Constants.VEHICLEBRAND_LIST));
        btnCancel.setText("hủy");
        btnSubmit.setText("Tạo mới");
        line.setVisibility(View.GONE);

        setupBrandSpinner();
//        setupVehicleNameSpinner();
        fixShowKeyboard();
        //spBrand.setSelection(mActivity.currentCustomer.getInt("shopType"));




    }

    private void addEvent() {
        layoutParent.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

    }

    private void initializeView() {
        layoutParent = view.findViewById(R.id.add_customer_parent);
        iPlate = view.findViewById(R.id.add_customer_plate);
        iPhone = view.findViewById(R.id.add_customer_phone);
        iUsername = view.findViewById(R.id.add_customer_name);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSubmit = view.findViewById(R.id.btn_submit);
        line = view.findViewById(R.id.line);
        spBrand = view.findViewById(R.id.add_customer_brand);
        spVehicleName = view.findViewById(R.id.add_customer_vehiclename);


    }

    @Override
    public void onClick(View v) {
        Util.hideKeyboard(v);
        switch (v.getId()) {
            case R.id.add_customer_parent:
                getActivity().getSupportFragmentManager().popBackStack();

                break;

            case R.id.icon_delete:

                break;

            case R.id.btn_cancel:
                getActivity().getSupportFragmentManager().popBackStack();
                break;

            case R.id.btn_submit:

                break;


        }
    }

    private void setupBrandSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), R.layout.view_spinner_text, DataUtil.getListStringFromListObject(mGroups, "name"));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBrand.setAdapter(dataAdapter);

        spBrand.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Util.hideKeyboard(view);
                return false;
            }
        });

        spBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
                mCurrentBrand = mGroups.get(i);
                mCurrentVehicles = mCurrentBrand.getList("vehicles");

                setupVehicleNameSpinner();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }

    private void setupVehicleNameSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Util.getInstance().getCurrentActivity(), R.layout.view_spinner_text, DataUtil.getListStringFromListObject(mCurrentVehicles, "name"));
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVehicleName.setAdapter(dataAdapter);

        spVehicleName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //Util.hideKeyboard(view);
                return false;
            }
        });

        spVehicleName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                mActivity.saveCustomerToLocal("shopType", i);
//                updateTitle();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

    }

    private void fixShowKeyboard(){
        Util.showKeyboardEditTextDelay(iPlate.getEdInput(), new CallbackBoolean() {
            @Override
            public void onRespone(Boolean result) {
                layoutParent.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                Rect r = new Rect();
                                layoutParent.getWindowVisibleDisplayFrame(r);
                                int screenHeight = layoutParent.getRootView().getHeight();
                                int heightDifference = screenHeight - (r.bottom);

                                layoutParent.setPadding(Util.convertSdpToInt(R.dimen._5sdp),
                                        0,
                                        Util.convertSdpToInt(R.dimen._5sdp),
                                        heightDifference + Util.convertSdpToInt(R.dimen._5sdp));

                            }
                        });

            }
        });


    }



}
