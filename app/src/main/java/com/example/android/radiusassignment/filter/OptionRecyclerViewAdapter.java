package com.example.android.radiusassignment.filter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
            // TODO: bind the item boiiiiiiiiii
            viewHolder.mChip.setText(currentOption.getName());
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
            viewHolder.mChip.setText("");
            viewHolder.mChip.setAlpha(1.0f);
            viewHolder.mChip.setEnabled(true);
            viewHolder.itemView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (mOptionList != null) ? mOptionList.size() : 0;
    }

    /**
     * Method to update the list
     *
     * @param updatedList List that needs to be added.
     */
    public void updateList(List<Option> updatedList) {
        if (mOptionList == null) {
            mOptionList = new ArrayList<>();
        } else {
            mOptionList.clear();
        }
        mOptionList.addAll(updatedList);
        notifyDataSetChanged();
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
