package com.example.hotel_booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PaymentMethodFragment extends Fragment {

    private RadioGroup rgPaymentMethod;
    private RadioButton rbCreditCard, rbDebitCard, rbCash, rbPaypal;
    private Button btnContinue;
    private String selectedPaymentMethod = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_method, container, false);
        initViews(view);
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        rgPaymentMethod = view.findViewById(R.id.rgPaymentMethod);
        rbCreditCard = view.findViewById(R.id.rbCreditCard);
        rbDebitCard = view.findViewById(R.id.rbDebitCard);
        rbCash = view.findViewById(R.id.rbCash);
        rbPaypal = view.findViewById(R.id.rbPaypal);
        btnContinue = view.findViewById(R.id.btnContinue);
    }

    private void setupListeners() {
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCreditCard) {
                selectedPaymentMethod = "Credit Card";
            } else if (checkedId == R.id.rbDebitCard) {
                selectedPaymentMethod = "Debit Card";
            } else if (checkedId == R.id.rbCash) {
                selectedPaymentMethod = "Cash";
            } else if (checkedId == R.id.rbPaypal) {
                selectedPaymentMethod = "PayPal";
            }
        });

        btnContinue.setOnClickListener(v -> {
            if (selectedPaymentMethod.isEmpty()) {
                Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
            } else {
                if (selectedPaymentMethod.equals("Cash")) {
                    Toast.makeText(getContext(), "Cash payment selected", Toast.LENGTH_SHORT).show();
                } else {
                    navigateToPaymentInfo();
                }
            }
        });
    }

    private void navigateToPaymentInfo() {
        Bundle bundle = new Bundle();
        bundle.putString("payment_method", selectedPaymentMethod);

        PaymentInfoFragment paymentInfoFragment = new PaymentInfoFragment();
        paymentInfoFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, paymentInfoFragment)
                .addToBackStack(null)
                .commit();
    }
}

