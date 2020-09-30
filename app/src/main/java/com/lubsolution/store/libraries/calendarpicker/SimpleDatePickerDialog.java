package com.lubsolution.store.libraries.calendarpicker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.lubsolution.store.R;


/**
 * Created by macos on 10/16/17.
 */

public class SimpleDatePickerDialog extends AlertDialog implements DialogInterface
        .OnClickListener, SimpleDatePickerDelegate.OnDateChangedListener, View.OnClickListener {

    private static final String YEAR = "year";
    private static final String MONTH = "month";

    private SimpleDatePickerDelegate mSimpleDatePickerDelegate;
    private OnDateSetListener mDateSetListener;
    private OnDismissListener mDismissListener;

    /**
     * @param context The context the dialog is to run in.
     */
    public SimpleDatePickerDialog(Context context, OnDateSetListener listener, OnDismissListener listener1, int year,
                                  int monthOfYear) {
        this(context, 0, listener, listener1, year, monthOfYear);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param theme   the theme to apply to this dialog
     */
    @SuppressLint("InflateParams")
    public SimpleDatePickerDialog(Context context, int theme, OnDateSetListener listener, OnDismissListener listener1, int year,
                                  int monthOfYear) {
        super(context, theme);

        mDateSetListener = listener;
        mDismissListener = listener1;

        Context themeContext = getContext();
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        View view = inflater.inflate(R.layout.view_dialog_select_monthpicker, null);
        setView(view);

        Button ok = view.findViewById(R.id.btn_submit);
        Button cancel = view.findViewById(R.id.btn_cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

//        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
//        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);

        mSimpleDatePickerDelegate = new SimpleDatePickerDelegate(view);
        mSimpleDatePickerDelegate.init(year, monthOfYear, this);
    }

    @Override
    public void onDateChanged(int year, int month) {
        // Stub - do nothing
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mDateSetListener != null) {
                    mDateSetListener.onDateSet(
                            mSimpleDatePickerDelegate.getYear(),
                            mSimpleDatePickerDelegate.getMonth());
                }

                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mSimpleDatePickerDelegate.getYear());
        state.putInt(MONTH, mSimpleDatePickerDelegate.getMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        mSimpleDatePickerDelegate.init(year, month, this);
    }

    public void setMinDate(long minDate) {
        mSimpleDatePickerDelegate.setMinDate(minDate);
    }

    public void setMaxDate(long maxDate) {
        mSimpleDatePickerDelegate.setMaxDate(maxDate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (mDateSetListener != null) {
                    mDateSetListener.onDateSet(
                            mSimpleDatePickerDelegate.getYear(),
                            mSimpleDatePickerDelegate.getMonth());
                }
                dismiss();
                mDismissListener.onDismiss(true);

                break;

            case R.id.btn_cancel:
                mDismissListener.onDismiss(false);
                cancel();

                break;
        }

    }

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param year        The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility with {@link
         *                    java.util.Calendar}.
         */
        void onDateSet(int year, int monthOfYear);
    }

    public interface OnDismissListener {
        void onDismiss(boolean result);
    }
}
