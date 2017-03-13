package com.mts.fategocardmaker.main;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mts.fategocardmaker.R;
import com.mts.fategocardmaker.adapters.ServantSpinnerAdapter;
import com.mts.fategocardmaker.adapters.StarSpinnerAdapter;
import com.mts.fategocardmaker.model.PermissionRequestType;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP;

public class MainActivity extends AppCompatActivity implements MainView {

    private MainPresenter mainPresenter;

    @BindView(R.id.relativeHolder) RelativeLayout relativeHolder;
    @BindView(R.id.ivCard) SubsamplingScaleImageView ivCard;
    @BindView(R.id.ivBorder) ImageView ivBorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainPresenter = new MainPresenterImpl(this);
        ivCard.setImage(ImageSource.resource(R.drawable.saber));
        ivCard.setMinimumScaleType(SCALE_TYPE_CENTER_CROP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_image:
                mainPresenter.designCard();
                return true;
            case R.id.menu_item_save:
                mainPresenter.saveCard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        mainPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showToast(int message) {
        Toast.makeText(this, getResources().getString(message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void requestPermissions(final PermissionRequestType type) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()){
                    mainPresenter.onAllPermissionsGranted(type);
                }else{
                    mainPresenter.onAnyPermissionDenied();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    @Override
    public void startImagePicker() {
        EasyImage.openChooserWithGallery(this,getResources().getString(R.string.header_pick_image),0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
           @Override
           public void onImagePicked(File file, EasyImage.ImageSource imageSource, int i) {
               mainPresenter.onPickedImage(file);
           }

           @SuppressWarnings("ResultOfMethodCallIgnored")
           @Override
           public void onCanceled(EasyImage.ImageSource source, int type) {
               if (source == EasyImage.ImageSource.CAMERA) {
                   File photoFile = EasyImage.lastlyTakenButCanceledPhoto(MainActivity.this);
                   if (photoFile != null) photoFile.delete();
               }
               super.onCanceled(source, type);
           }
       });
    }

    @Override
    public void showDesignDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.header_customize))
                .customView(R.layout.dialog_inner, true)
                .positiveText(getResources().getString(R.string.dialog_button_done))
                .build();

        View innerView = dialog.getCustomView();
        Spinner spinnerClassSelect =  (Spinner)innerView.findViewById(R.id.spinnerClassSelect);
        Spinner spinnerStarSelect = (Spinner)innerView.findViewById(R.id.spinnerStarSelect);
        Button buttonBrowse = (Button)innerView.findViewById(R.id.buttonBrowse);

        ServantSpinnerAdapter servantSpinnerAdapter = new ServantSpinnerAdapter(this, mainPresenter.generateListItems());
        spinnerClassSelect.setAdapter(servantSpinnerAdapter);
        spinnerClassSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mainPresenter.onSetClass(adapterView.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        StarSpinnerAdapter starSpinnerAdapter = new StarSpinnerAdapter(this);
        spinnerStarSelect.setAdapter(starSpinnerAdapter);
        spinnerStarSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mainPresenter.onSetStars(adapterView.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPresenter.pickCardImage();
            }
        });


        dialog.show();
        spinnerClassSelect.setSelection(mainPresenter.getClassId());
        spinnerStarSelect.setSelection(mainPresenter.getStars());
    }

    @Override
    public void setCardImage(File file) {
        ivCard.setImage(ImageSource.uri(Uri.fromFile(file)));
    }

    @Override
    public void setCardBorder(String resourceName) {
        Log.e("Yigit",resourceName);
        ivBorder.setImageDrawable(ContextCompat.getDrawable(this,getResources().getIdentifier(resourceName,"drawable",this.getPackageName())));
    }

    @Override
    public void saveCardToGallery() {
        relativeHolder.setDrawingCacheEnabled(true);
        Bitmap bitmap = relativeHolder.getDrawingCache();

        File file = new File("/sdcard/" + "FateGOCard_" + System.currentTimeMillis() + ".png");

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
            relativeHolder.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
            mainPresenter.savedCardResult(false);
        } finally {
            relativeHolder.setDrawingCacheEnabled(false);
            mainPresenter.savedCardResult(true);
        }


        MediaScannerConnection.scanFile(this, new String[] { file.getPath() }, new String[] { "image/jpeg" }, null);
    }

}
