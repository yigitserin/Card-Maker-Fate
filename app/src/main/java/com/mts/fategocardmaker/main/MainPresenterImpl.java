package com.mts.fategocardmaker.main;

import com.mts.fategocardmaker.R;
import com.mts.fategocardmaker.model.PermissionRequestType;
import com.mts.fategocardmaker.model.ServantSpinnerItem;
import com.mts.fategocardmaker.model.ServantType;

import java.io.File;
import java.util.ArrayList;

class MainPresenterImpl implements MainPresenter {

    private static String TYPE_STRINGS[] = {"00","01","02","03","04","05","06","07","08","09","10"};

    private MainView mainView;
    private int stars = 4;
    private int type = 0;

    MainPresenterImpl(MainView mainView){
        this.mainView = mainView;
        calculateCardBorder();
    }

    @Override
    public void onDestroy() {
        mainView = null;
    }

    @Override
    public void designCard() {
        if (mainView != null){
            mainView.showDesignDialog();
        }
    }

    @Override
    public void onAllPermissionsGranted(PermissionRequestType type) {
        if (mainView != null){

            switch (type){
                case SAVE:
                    mainView.saveCardToGallery();
                    break;
                case PICK:
                    mainView.startImagePicker();
                    break;
            }
        }
    }

    @Override
    public void onAnyPermissionDenied() {
        if (mainView != null){
            mainView.showToast(R.string.error_permission);
        }
    }

    @Override
    public void onPickedImage(File file) {
        if (mainView != null) {
            mainView.setCardImage(file);
        }
    }

    @Override
    public void onSetStars(int stars) {
        this.stars = stars;
        calculateCardBorder();
    }

    @Override
    public void onSetClass(int type) {
        this.type = type;
        calculateCardBorder();
    }

    @Override
    public int getStars() {
        return stars;
    }

    @Override
    public int getClassId() {
        return type;
    }

    @Override
    public ArrayList<ServantSpinnerItem> generateListItems() {
        ArrayList<ServantSpinnerItem> arrayList = new ArrayList<>();
        arrayList.add(new ServantSpinnerItem("Shielder", R.drawable.class_0003, ServantType.SHIELDER));
        arrayList.add(new ServantSpinnerItem("Saber", R.drawable.class_0103,ServantType.SABER));
        arrayList.add(new ServantSpinnerItem("Lancer", R.drawable.class_0203,ServantType.LANCER));
        arrayList.add(new ServantSpinnerItem("Archer", R.drawable.class_0303,ServantType.ARCHER));
        arrayList.add(new ServantSpinnerItem("Rider", R.drawable.class_0403,ServantType.RIDER));
        arrayList.add(new ServantSpinnerItem("Caster", R.drawable.class_0503,ServantType.CASTER));
        arrayList.add(new ServantSpinnerItem("Assassin", R.drawable.class_0603,ServantType.ASSASSIN));
        arrayList.add(new ServantSpinnerItem("Berserker", R.drawable.class_0703,ServantType.BERSERKER));
        arrayList.add(new ServantSpinnerItem("Ruler", R.drawable.class_0803,ServantType.RULER));
        arrayList.add(new ServantSpinnerItem("Default", R.drawable.class_0903,ServantType.DEFAULT));
        arrayList.add(new ServantSpinnerItem("Avenger", R.drawable.class_1003,ServantType.AVENGER));
        return arrayList;
    }

    private void calculateCardBorder(){
        mainView.setCardBorder(GetResourceNameForBorder(type,stars));
    }

    private String GetResourceNameForBorder(int type, int stars){
        return "servant_card_0" + (stars+1) + "_" + TYPE_STRINGS[type];
    }

    @Override
    public void saveCard() {
        if (mainView != null){
            mainView.requestPermissions(PermissionRequestType.SAVE);
        }
    }

    @Override
    public void pickCardImage() {
        if (mainView != null){
            mainView.requestPermissions(PermissionRequestType.PICK);
        }
    }

    @Override
    public void savedCardResult(Boolean isSuccess) {
        if (mainView != null){
            if (isSuccess){
                mainView.showToast(R.string.success_save);
            }else{
                mainView.showToast(R.string.error_save);
            }
        }
    }
}
