package com.example.cobot;

import android.view.View;
import android.view.ViewGroup;

// General Utilities class for methods that do not belong to an activity or other class.
public class MyUtilities {
    // Sets the margins for a view
    public static void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}
