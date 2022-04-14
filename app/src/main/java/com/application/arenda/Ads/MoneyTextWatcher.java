package com.application.arenda.Ads;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MoneyTextWatcher implements TextWatcher {

    private TextInputEditText editText;

    private String lastAmount = "";

    private int lastCursorPosition = -1;

    public MoneyTextWatcher(TextInputEditText editText) {
        super();
        this.editText = editText;
    }

    @Override
    public void onTextChanged(CharSequence amount, int start, int before, int count) {

        if (!amount.toString().equals(lastAmount)) {

            String cleanString = clearCurrencyToNumber(amount.toString());

            try {
                String formattedAmount = transformToCurrency(cleanString);
                editText.removeTextChangedListener(this);
                editText.setText(formattedAmount);
                editText.setSelection(formattedAmount.length()-1);
                editText.addTextChangedListener(this);

                if (lastCursorPosition != lastAmount.length() && lastCursorPosition != -1) {
                    int lengthDelta = formattedAmount.length() - lastAmount.length();
                    int newCursorOffset = max(0, min(formattedAmount.length(), lastCursorPosition + lengthDelta));
                    editText.setSelection(newCursorOffset);
                }
            } catch (Exception e) {
                //log something
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        String value = s.toString();
        if(!value.equals("")){
            String cleanString = clearCurrencyToNumber(value);
            String formattedAmount = transformToCurrency(cleanString);
            lastAmount = formattedAmount;
            lastCursorPosition = editText.getSelectionStart();
        }
    }

    public static String clearCurrencyToNumber(String currencyValue) {
        String result = null;

        if (currencyValue == null) {
            result = "";
        } else {
            result = currencyValue.replaceAll("[(a-z)|(A-Z)|(₽)]", "");
        }
        return result;
    }
    public static String transformToCurrency(String value) {
        double parsed = Double.parseDouble(value);
        String formatted = NumberFormat.getCurrencyInstance(new Locale("ru",
                "RU")).format((parsed / 100));
        formatted = formatted.replaceAll("[^(0-9)(₽)]", "");
        return formatted;
    }
}