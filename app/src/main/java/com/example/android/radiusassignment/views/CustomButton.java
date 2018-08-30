package com.example.android.radiusassignment.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.example.android.radiusassignment.R;

import static com.google.common.base.Preconditions.checkNotNull;

public class CustomButton extends AppCompatTextView {
    private int mOnSelectedBackground = R.drawable.custom_btn_selected_background;
    private int mOnUnselectedBackground = R.drawable.custom_btn_unselected_background;
    private int mSelectedTextColor = android.R.color.white;
    private int mUnSelectedTextColor = android.R.color.black;
    private boolean isSelected = false;

    private boolean isToggleEnabled = true;

    private OnClickListener mClickListener;

    @NonNull
    private Context mContext;

    public CustomButton(@NonNull Context context) {
        this(context, null);
        mContext = context;
    }

    public CustomButton(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public CustomButton(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(context, attrs, defStyleAttr);
    }

    /**
     * A method to perform all the initialisations.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        initAttributes(context, attrs, defStyleAttr);
        changeBackgroundAndTextColor();
        super.setOnClickListener(v -> {
            this.setSelected(!isSelected);

            if (mClickListener != null) {
                mClickListener.onClick(CustomButton.this);
            }
        });
    }

    /**
     * This method get attributes values from the XML, if not found it takes the default values.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomButton, defStyleAttr, 0);
            mOnSelectedBackground = array.getResourceId(R.styleable.CustomButton_selectedBackground, mOnSelectedBackground);
            mOnUnselectedBackground = array.getResourceId(R.styleable.CustomButton_unselectedBackground, mOnUnselectedBackground);
            mSelectedTextColor = array.getResourceId(R.styleable.CustomButton_selectedTextColor, mSelectedTextColor);
            mUnSelectedTextColor = array.getResourceId(R.styleable.CustomButton_unselectedTextColor, mUnSelectedTextColor);
            isSelected = array.getBoolean(R.styleable.CustomButton_selected, isSelected);
            isToggleEnabled = array.getBoolean(R.styleable.CustomButton_isToggleable, isToggleEnabled);
            String fontName = array.getString(R.styleable.CustomButton_cbfont);
            // set font name
            setFontName(context, fontName);
            array.recycle();
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mClickListener = l;
    }

    private void setFontName(@NonNull Context context, String fontName) {
        checkNotNull(context);
        try {
            if (fontName != null) {
                Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
                setTypeface(myTypeface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetButton() {
        checkNotNull(mContext);
        this.setToggleEnabled(true);
        this.setSelected(false);
        super.setBackground(ContextCompat.getDrawable(mContext, mOnUnselectedBackground));
        super.setTextColor(ContextCompat.getColor(mContext, mUnSelectedTextColor));
    }

    private void changeBackgroundAndTextColor() {
        checkNotNull(mContext);
        if (isSelected) {
            super.setBackground(ContextCompat.getDrawable(mContext, mOnSelectedBackground));
            super.setTextColor(ContextCompat.getColor(mContext, mSelectedTextColor));
        } else {
            super.setBackground(ContextCompat.getDrawable(mContext, mOnUnselectedBackground));
            super.setTextColor(ContextCompat.getColor(mContext, mUnSelectedTextColor));
        }
    }

    /*
     * Getters and Setters
     */
    public
    @DrawableRes
    int getSelectedBackground() {
        return mOnSelectedBackground;
    }

    public void setSelectedBackground(@DrawableRes int selectedBackgroundDrawable) {
        this.mOnSelectedBackground = selectedBackgroundDrawable;
    }

    public
    @DrawableRes
    int getUnselectedBackground() {
        return mOnUnselectedBackground;
    }

    public void setUnselectedBackground(@DrawableRes int unselectedBackgroundDrawable) {
        this.mOnUnselectedBackground = unselectedBackgroundDrawable;
    }

    public @ColorRes
    int getSelectedTextColor() {
        return mSelectedTextColor;
    }

    public void setSelectedTextColor(@ColorRes int selectedTextColor) {
        this.mSelectedTextColor = selectedTextColor;
    }

    public @ColorRes
    int getUnselectedTextColor() {
        return mUnSelectedTextColor;
    }

    public void setUnselectedTextColor(@ColorRes int unselectedTextColor) {
        this.mUnSelectedTextColor = unselectedTextColor;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        if (isToggleEnabled) {
            this.isSelected = isSelected;
            this.changeBackgroundAndTextColor();
        }
    }

    public boolean isToggleEnabled() {
        return isToggleEnabled;
    }

    public void setToggleEnabled(boolean isToggleEnabled) {
        this.isToggleEnabled = isToggleEnabled;
    }
}