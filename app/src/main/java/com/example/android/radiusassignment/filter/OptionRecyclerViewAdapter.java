package com.example.android.radiusassignment.filter;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.radiusassignment.R;
import com.example.android.radiusassignment.data.local.Option;
import com.example.android.radiusassignment.interfaces.RecyclerClickListenerInterface;
import com.example.android.radiusassignment.views.CustomButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OptionRecyclerViewAdapter extends RecyclerView.Adapter<OptionRecyclerViewAdapter.ViewHolder> {
    private final Context mContext;
    private List<Option> mOptionList;
    private final String mFacilityId;
    private final RecyclerClickListenerInterface mClickListenerInterface;

    public OptionRecyclerViewAdapter(@NonNull Context context, @NonNull List<Option> optionList,
                                     @NonNull String facilityId, @NonNull RecyclerClickListenerInterface recyclerClickListenerInterface) {
        this.mContext = context;
        this.mOptionList = optionList;
        this.mClickListenerInterface = recyclerClickListenerInterface;
        this.mFacilityId = facilityId;
    }

    @NonNull
    @Override
    public OptionRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_option, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionRecyclerViewAdapter.ViewHolder viewHolder, int itemPosition) {
        Option currentOption = mOptionList.get(itemPosition);
        if (currentOption != null && currentOption.getName() != null && !TextUtils.isEmpty(currentOption.getName()) &&
                currentOption.getIcon() != null && !TextUtils.isEmpty(currentOption.getIcon())) {
            // set option name
            viewHolder.mChip.setText(currentOption.getName());
            // set option id
            int iconResource = getDrawableID(currentOption.getIcon());
            // add icon
            if (iconResource != 0) {
                viewHolder.mChip.setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, 0, 0);
                viewHolder.mChip.setCompoundDrawablePadding(8);
                if (currentOption.isSelected()) {
                    // change color of drawable to white
                    setTextViewDrawableColor(viewHolder.mChip, android.R.color.white);
                } else {
                    // reset color of drawable
                    resetTextViewDrawableColor(viewHolder.mChip);
                }
            } else {
                // remove any drawables, if exists, to avoid invalid data
                viewHolder.mChip.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                viewHolder.mChip.setCompoundDrawablePadding(0);
            }
            // set chip selection based on current option's isSelected field
            viewHolder.mChip.setSelected(currentOption.isSelected());
            // check whether chip is enabled or not
            if (currentOption.isEnabled()) {
                viewHolder.mChip.setAlpha(1.0f);
                viewHolder.mChip.setEnabled(true);
            } else {
                viewHolder.mChip.setAlpha(0.3f);
                viewHolder.mChip.setEnabled(false);
            }
            viewHolder.mChip.setOnClickListener(view -> {
                // call the onItemClickListener method to invoke its implementation in its respective activity/fragment
                mClickListenerInterface.onItemClickListener(currentOption.isSelected(), mFacilityId, currentOption.getId());
            });
        } else {
            //reset values
            resetChip(viewHolder);
        }
    }

    @Override
    public int getItemCount() {
        return (mOptionList != null) ? mOptionList.size() : 0;
    }

    /**
     * Resets chips in case current Option is null
     *
     * @param viewHolder ViewHolder object to reset chips and item visibility
     */
    private void resetChip(@NonNull ViewHolder viewHolder) {
        viewHolder.mChip.setText("");
        viewHolder.mChip.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        viewHolder.mChip.setCompoundDrawablePadding(0);
        viewHolder.mChip.setAlpha(1.0f);
        viewHolder.mChip.setEnabled(true);
        viewHolder.mChip.setSelected(false);
        viewHolder.mChip.setOnClickListener(null);
        viewHolder.itemView.setVisibility(View.GONE);
    }

    /**
     * Get drawable id using icon name, if available. Otherwise, returns 0
     *
     * @param iconName name of the icon that is used to get drawable id
     * @return Drawable id of the icon
     */
    private int getDrawableID(@NonNull String iconName) {
        if (mContext != null) {
            iconName = iconName.replaceAll("-", "_");
            return mContext.getResources().getIdentifier(iconName, "drawable",
                    mContext.getApplicationInfo().packageName);
        }
        return 0;
    }

    /**
     * Set custom button drawable colorFilter. It uses {@link PorterDuffColorFilter} and
     * {@link Drawable#setColorFilter(ColorFilter)}
     *
     * @param customButton Custom button which consists drawables
     * @param color        Color id that needs to be added
     */
    private void setTextViewDrawableColor(CustomButton customButton, int color) {
        if (mContext != null) {
            for (Drawable drawable : customButton.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(mContext.getResources()
                            .getColor(color), PorterDuff.Mode.SRC_IN));
                }
            }
        }
    }

    /**
     * Resets custom button drawable colorFilter. It uses {@link Drawable#clearColorFilter()}
     *
     * @param customButton Custom button which consists drawables
     */
    private void resetTextViewDrawableColor(CustomButton customButton) {
        for (Drawable drawable : customButton.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.clearColorFilter();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chip)
        CustomButton mChip;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
