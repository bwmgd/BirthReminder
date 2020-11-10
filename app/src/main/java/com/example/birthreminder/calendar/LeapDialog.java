package com.example.birthreminder.calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.birthreminder.R;

public class LeapDialog extends Dialog implements View.OnClickListener {
    public boolean canceled = false;
    private SpecialBirthdayListener listener;

    public LeapDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leap_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    public void onClick(View v) {
        if (listener == null) return;
        listener.onSpecialChoose(v);
        dismiss();
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        super.setOnCancelListener(listener);
        canceled = true;
    }

    public void setListener(SpecialBirthdayListener listener) {
        this.listener = listener;
    }

    public interface SpecialBirthdayListener {
        void onSpecialChoose(View v);
    }
}
