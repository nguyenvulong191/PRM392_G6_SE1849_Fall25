package com.example.hotel_booking;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PaymentInfoFragment extends Fragment {

    private TextView tvPaymentMethod;
    private EditText etCardNumber, etCardHolderName, etExpiryDate, etCvv;
    private Button btnConfirmPayment;
    private String paymentMethod;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_info, container, false);

        if (getArguments() != null) {
            paymentMethod = getArguments().getString("payment_method", "Credit Card");
        }

        initViews(view);
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        tvPaymentMethod = view.findViewById(R.id.tvPaymentMethod);
        etCardNumber = view.findViewById(R.id.etCardNumber);
        etCardHolderName = view.findViewById(R.id.etCardHolderName);
        etExpiryDate = view.findViewById(R.id.etExpiryDate);
        etCvv = view.findViewById(R.id.etCvv);
        btnConfirmPayment = view.findViewById(R.id.btnConfirmPayment);

        tvPaymentMethod.setText(paymentMethod);
    }

    private void setupListeners() {
        etCardNumber.addTextChangedListener(new TextWatcher() {
            private static final int TOTAL_SYMBOLS = 19;
            private static final int DIVIDER_POSITION = 4;
            private static final char DIVIDER = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_POSITION, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_SYMBOLS), DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerPosition, char divider) {
                boolean isCorrect = s.length() <= totalSymbols;
                for (int i = 0; i < s.length(); i++) {
                    if (i > 0 && (i + 1) % (dividerPosition + 1) == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }
                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });

        etExpiryDate.addTextChangedListener(new TextWatcher() {
            private int prevLength = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 2 && prevLength < s.length()) {
                    s.append("/");
                }
            }
        });

        btnConfirmPayment.setOnClickListener(v -> validateAndConfirm());
    }

    private void validateAndConfirm() {
        String cardNumber = etCardNumber.getText().toString().trim().replace(" ", "");
        String cardHolder = etCardHolderName.getText().toString().trim();
        String expiry = etExpiryDate.getText().toString().trim();
        String cvv = etCvv.getText().toString().trim();

        if (cardNumber.isEmpty() || cardNumber.length() < 13) {
            etCardNumber.setError("Invalid card number");
            return;
        }

        if (cardHolder.isEmpty()) {
            etCardHolderName.setError("Card holder name required");
            return;
        }

        if (expiry.length() != 5) {
            etExpiryDate.setError("Invalid expiry date (MM/YY)");
            return;
        }

        if (cvv.isEmpty() || cvv.length() < 3) {
            etCvv.setError("Invalid CVV");
            return;
        }

        Toast.makeText(getContext(), "Payment confirmed successfully!", Toast.LENGTH_LONG).show();

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
