package com.mts.fategocardmaker.main;

import com.mts.fategocardmaker.model.PermissionRequestType;
import com.mts.fategocardmaker.model.ServantSpinnerItem;

import java.io.File;
import java.util.ArrayList;

interface MainPresenter {
    void onDestroy();
    void saveCard();
    void onAllPermissionsGranted(PermissionRequestType type);
    void onAnyPermissionDenied();
    void onPickedImage(File file);
    void onSetRarity(int stars);
    void onSetClass(int classId);
    int getStars();
    int getClassId();
    ArrayList<ServantSpinnerItem> generateListItems();
    void savedCardResult(Boolean isSuccess);
}
