package com.mts.fategocardmaker.main;

import com.mts.fategocardmaker.R;
import com.mts.fategocardmaker.model.PermissionRequestType;
import com.mts.fategocardmaker.model.ServantSpinnerItem;
import com.mts.fategocardmaker.model.ServantType;

import java.io.File;
import java.util.ArrayList;

import static com.mts.fategocardmaker.app.MyConstants.TYPE_STRINGS;

class MainPresenterImpl implements MainPresenter {

    private MainView mainView;
    private int stars = 5;
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
    public void onSetRarity(int stars) {
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
        arrayList.add(new ServantSpinnerItem("Default", R.drawable.class_00,ServantType.DEFAULT));
        arrayList.add(new ServantSpinnerItem("Shielder", R.drawable.class_01, ServantType.SHIELDER));
        arrayList.add(new ServantSpinnerItem("Saber", R.drawable.class_02,ServantType.SABER));
        arrayList.add(new ServantSpinnerItem("Lancer", R.drawable.class_03,ServantType.LANCER));
        arrayList.add(new ServantSpinnerItem("Archer", R.drawable.class_04,ServantType.ARCHER));
        arrayList.add(new ServantSpinnerItem("Rider", R.drawable.class_05,ServantType.RIDER));
        arrayList.add(new ServantSpinnerItem("Caster", R.drawable.class_06,ServantType.CASTER));
        arrayList.add(new ServantSpinnerItem("Assassin", R.drawable.class_07,ServantType.ASSASSIN));
        arrayList.add(new ServantSpinnerItem("Berserker", R.drawable.class_08,ServantType.BERSERKER));
        arrayList.add(new ServantSpinnerItem("Ruler", R.drawable.class_09,ServantType.RULER));
        arrayList.add(new ServantSpinnerItem("Avenger", R.drawable.class_10,ServantType.AVENGER));
        arrayList.add(new ServantSpinnerItem("Alter Ego", R.drawable.class_11,ServantType.ALTEREGO));
        arrayList.add(new ServantSpinnerItem("Moon Cancer", R.drawable.class_12,ServantType.MOONCANCER));
        return arrayList;
    }

    private void calculateCardBorder(){
        mainView.setCardBorder(GetResourceNameForBorder(type,stars));
    }

    private String GetResourceNameForBorder(int type, int stars){
        return "servant_card_0" + (stars) + "_" + TYPE_STRINGS[type];
    }

    @Override
    public void saveCard() {
        if (mainView != null){
            mainView.requestPermissions(PermissionRequestType.SAVE);
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
