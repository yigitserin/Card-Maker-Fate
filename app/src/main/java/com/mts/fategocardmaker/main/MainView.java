package com.mts.fategocardmaker.main;

import com.mts.fategocardmaker.model.PermissionRequestType;

import java.io.File;

interface MainView {
    void showToast(int message);
    void startImagePicker();
    void setCardImage(File file);
    void setCardBorder(String resourceName);
    void saveCardToGallery();
    void requestPermissions(PermissionRequestType type);
}
