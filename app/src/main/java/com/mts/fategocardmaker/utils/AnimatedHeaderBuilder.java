package com.mts.fategocardmaker.utils;

import android.support.annotation.NonNull;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mts.fategocardmaker.R;

public class AnimatedHeaderBuilder extends AccountHeaderBuilder {

    @Override
    public AccountHeaderBuilder withAccountHeader(@NonNull View accountHeader) {
        this.mAccountHeaderContainer = accountHeader;
        return this;
    }

    @Override
    public AccountHeaderBuilder withAccountHeader(int resLayout) {
        this.mAccountHeaderContainer = mActivity.getLayoutInflater().inflate(R.layout.animated_header, null, false);
        return this;
    }
}
