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
import com.example.android.radiusassignment.data.local.Facility;
import com.example.android.radiusassignment.interfaces.RecyclerClickListenerInterface;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterRecyclerViewAdapter extends RecyclerView.Adapter<FilterRecyclerViewAdapter.ViewHolder> {
    private Context mContext;
    private List<Facility> mFacilityList;
    private RecyclerClickListenerInterface mClickListenerInterface;

    public FilterRecyclerViewAdapter(@NonNull Context context, @NonNull List<Facility> facilityList,
                                     @NonNull RecyclerClickListenerInterface clickListenerInterface) {
        this.mContext = context;
        this.mFacilityList = facilityList;
        this.mClickListenerInterface = clickListenerInterface;
    }

    @NonNull
    @Override
    public FilterRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_filter, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterRecyclerViewAdapter.ViewHolder viewHolder, int itemPosition) {
        Facility currentFacility = mFacilityList.get(itemPosition);
        if (currentFacility != null && currentFacility.getName() != null && !TextUtils.isEmpty(currentFacility.getName()) &&
                currentFacility.getOptions() != null && !currentFacility.getOptions().isEmpty()) {
            // set the name of the facility
            viewHolder.mFacilityName.setText(currentFacility.getName());
            // setup recycler view for chips
            setupOptionsRecyclerView(viewHolder.mOptionRecyclerView, currentFacility);
        } else {
            // reset the item
            viewHolder.mFacilityName.setText("");
            viewHolder.itemView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (mFacilityList != null) ? mFacilityList.size() : 0;
    }

    /**
     * Method to update the list
     *
     * @param updatedList List that needs to be added.
     */
    public void updateList(List<Facility> updatedList) {
        if (mFacilityList == null) {
            mFacilityList = new ArrayList<>();
        } else {
            mFacilityList.clear();
        }
        mFacilityList.addAll(updatedList);
        notifyDataSetChanged();
    }

    /**
     * Method to setup Recycler View
     *
     * @param optionRecyclerView RecyclerView for options
     * @param currentFacility    Current facility {@link Facility} object
     */
    private void setupOptionsRecyclerView(@NonNull RecyclerView optionRecyclerView, @NonNull Facility currentFacility) {
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(mContext,
                FlexDirection.ROW, FlexWrap.WRAP);
        optionRecyclerView.setLayoutManager(flexboxLayoutManager);
        optionRecyclerView.setAdapter(new OptionRecyclerViewAdapter(mContext,
                currentFacility.getOptions(), currentFacility.getFacilityId(), mClickListenerInterface));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.facility_name)
        TextView mFacilityName;
        @BindView(R.id.option_recycler_view)
        RecyclerView mOptionRecyclerView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
