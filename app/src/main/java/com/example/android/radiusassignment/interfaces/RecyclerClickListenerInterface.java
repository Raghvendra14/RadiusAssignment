package com.example.android.radiusassignment.interfaces;

import android.support.annotation.NonNull;

/**
 *  Here, one can define mulitple click listeners to perform task in its respective activities/fragment.
 */
public interface RecyclerClickListenerInterface {
    void onItemClickListener(boolean isSelected, @NonNull String facilityId, @NonNull String optionId);
}
