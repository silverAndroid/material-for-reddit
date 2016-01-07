package com.reddit.material.custom;

import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by Rushil Perera on 1/4/2016.
 */
public class ClickSpan extends ClickableSpan {

    private final OnClickListener listener;

    public ClickSpan(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View widget) {
        if (listener != null)
            listener.onClick();
    }

    interface OnClickListener {
        void onClick();
    }
}
