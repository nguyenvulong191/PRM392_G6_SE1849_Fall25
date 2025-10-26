package com.example.hotel_booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SelectGuestsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_guests, container, false);

        NumberPicker guestPicker = view.findViewById(R.id.guestPicker);
        guestPicker.setMinValue(1);
        guestPicker.setMaxValue(10);
        guestPicker.setValue(2); // giá trị mặc định

        return view;
    }
}
