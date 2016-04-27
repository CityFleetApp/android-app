package com.cityfleet.util;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/**
 * Created by vika on 28.03.16.
 */
public class DecimalDigitsInputFilter implements InputFilter {
    private int maxSymbols = Constants.MAX_PRICE;

    private String pattern = "(0|[1-9]{1}[0-9]{0," + (maxSymbols - 1) + "})?(\\.[0-9]{0,2})?";

    public void setMaxSymbols(int maxSymbols) {
        this.maxSymbols = maxSymbols;
    }


    public DecimalDigitsInputFilter(int maxSymbols) {
        this.maxSymbols = maxSymbols;
    }

    public DecimalDigitsInputFilter() {
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {

        String checkedText = dest.subSequence(0, dstart).toString() +
                source.subSequence(start, end) +
                dest.subSequence(dend, dest.length()).toString();

        return (!Pattern.matches(pattern, checkedText)) ? "" : null;
    }

}
