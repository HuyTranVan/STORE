package com.lubsolution.store.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lubsolution.store.R;
import com.lubsolution.store.callback.CallbackObject;
import com.lubsolution.store.models.BaseModel;
import com.lubsolution.store.utils.DataUtil;

import java.util.List;


/**
 * Created by macos on 9/17/17.
 */

public class CDropdown extends RelativeLayout {
    private AutoCompleteTextView mText;
    private TextView tvIcon;
    private Context mContext;
    private View mLayout;

    public CDropdown(Context context) {
        this(context, null);
    }

    public CDropdown(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CDropdown(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        mContext = context;

        initiateView();

        initStyle(attrs, defStyleAttr);
    }

    public void initiateView() {
        LayoutInflater.from(mContext).inflate(R.layout.viewcustom_dropdown, this, true);
        mLayout = findViewById(R.id.dropdown_parent);

        mText = (AutoCompleteTextView) mLayout.findViewById(R.id.dropdown_text);
        tvIcon = (TextView) mLayout.findViewById(R.id.dropdown_icon);

    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.CDropdown, defStyleAttr, 0);

        if (a != null) {

            if (a.hasValue(R.styleable.CDropdown_iconDropdownColor)) {
                setIconColor(a.getColor(R.styleable.CDropdown_iconDropdownColor, 0));
            }

            if (a.hasValue(R.styleable.CDropdown_iconDropdownSize)) {
                setIconSize(a.getDimension(R.styleable.CDropdown_iconDropdownSize, 0));
            }

            if (a.hasValue(R.styleable.CDropdown_iconDropdownText)) {
                setIconText(a.getString(R.styleable.CDropdown_iconDropdownText));
            }

            if (a.hasValue(R.styleable.CDropdown_textDropdownColor)) {
                setTextColor(a.getColor(R.styleable.CDropdown_textDropdownColor, 0));
            }

            if (a.hasValue(R.styleable.CDropdown_textDropdown)) {
                setText(a.getString(R.styleable.CDropdown_textDropdown));
            }

            if (a.hasValue(R.styleable.CDropdown_textDropdownSize)) {
                setTextSize(a.getDimension(R.styleable.CDropdown_textDropdownSize, 0));
            }

            if (a.hasValue(R.styleable.CDropdown_android_inputType)) {
                setInputType(a.getInt(R.styleable.CDropdown_android_inputType, EditorInfo.TYPE_NULL));
            }

            if (a.hasValue(R.styleable.CDropdown_backgroundDropdown)) {
                setBackgroundColor(a.getDrawable(R.styleable.CDropdown_backgroundDropdown));
            }

//            if (a.hasValue(R.styleable.CInputForm_backgroundLayout)) {
//                setBackground(a.getDrawable(R.styleable.CInputForm_backgroundLayout));
//            }


            a.recycle();
        }
    }

    public void setInputType(int type) {
        mText.setInputType(type);

    }

//    public void setBoldStyle(boolean bold) {
//        mText.(type);
//
//    }

    public void setFocusable(boolean value) {
        if (value) {
            mText.setFocusable(true);
            mText.setFocusableInTouchMode(true);

        } else {
            mText.setFocusable(false);
        }
    }


    public void setTextSize(float dimension) {
        mText.setTextSize(dimension);

    }

    public void setText(String string) {
        mText.setText(string);
    }

    public String getText() {
        return mText.getText().toString();
    }

    public void setTextColor(int color) {
        mText.setTextColor(color);
    }

    public void setBackgroundColor(Drawable color) {
        //edInput.setBackgroundColor(color);
        mLayout.setBackground(color);

    }


    public void setIconText(String string) {
        tvIcon.setText(string);
    }

    public void setIconSize(float dimension) {
        tvIcon.setTextSize(dimension);
    }

    public void setIconColor(int color) {
        tvIcon.setTextColor(color);
    }

    public interface OnQueryTextListener {
        boolean textChanged(String query);

    }

    public void setListDropdown(List<BaseModel> list, String key , CallbackObject itemSelected){
        List<String> listToString = DataUtil.getListStringFromListObject(list , key);
        ArrayAdapter adapter = new ArrayAdapter<String>(
                mContext,
                android.R.layout.simple_spinner_dropdown_item, listToString);

        mText.setAdapter(adapter);
        mText.setText(listToString.get(0), false);
        itemSelected.onResponse(list.get(0));

        mText.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                mText.showDropDown();
                return false;
            }
        });

        tvIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mText.showDropDown();

            }


        });

        mText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemSelected.onResponse(list.get(i));
            }
        });

    }



    public interface ClickListener {
        void onClick(View view);

    }


}
