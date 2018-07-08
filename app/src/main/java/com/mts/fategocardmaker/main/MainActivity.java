package com.mts.fategocardmaker.main;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mts.fategocardmaker.R;
import com.mts.fategocardmaker.adapters.ClassIconAdapter;
import com.mts.fategocardmaker.adapters.RarityAdapter;
import com.mts.fategocardmaker.model.PermissionRequestType;
import com.mts.fategocardmaker.utils.AnimatedHeaderBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

import static com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP;
import static com.mts.fategocardmaker.app.MyConstants.AD_UNIT_ID;
import static com.mts.fategocardmaker.app.MyConstants.APP_STORE_URL;
import static com.mts.fategocardmaker.app.MyConstants.FILE_FOLDER;
import static com.mts.fategocardmaker.app.MyConstants.FILE_PREFIX;
import static com.mts.fategocardmaker.app.MyConstants.FONT_PATH;
import static com.mts.fategocardmaker.utils.FateUtils.calculateNoOfColumns;

public class MainActivity extends AppCompatActivity implements MainView {

    private MainPresenter mainPresenter;
    private InterstitialAd mInterstitialAd;
    private boolean adShown = false;
    private Drawer result = null;

    private SecondaryDrawerItem rarityItem;
    private SecondaryDrawerItem classIconItem;

    private SecondaryDrawerItem classItem;
    private SecondaryDrawerItem nameItem;
    private SecondaryDrawerItem attackItem;
    private SecondaryDrawerItem healthItem;

    private Typeface font;

    @BindView(R.id.relativeHolder) RelativeLayout relativeHolder;
    @BindView(R.id.ivCard) SubsamplingScaleImageView ivCard;
    @BindView(R.id.ivBorder) ImageView ivBorder;
    @BindView(R.id.tvAttack) TextView tvAttack;
    @BindView(R.id.tvHealth) TextView tvHealth;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvClass) TextView tvClass;
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mainPresenter = new MainPresenterImpl(this);
        firstTimeSettings();
        setToolbarDrawer(savedInstanceState);
        setFullScreenad();
    }

    @Override
    protected void onDestroy() {
        mainPresenter.onDestroy();
        super.onDestroy();
    }

    //region Toolbar Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                mainPresenter.saveCard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.social_share_app) + " " + APP_STORE_URL);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void donate() {
        //TODO: Implement donation button.
    }

    //endregion

    //region Set Dynamic Heights

    private void firstTimeSettings() {
        //Set custom font
        font = Typeface.createFromAsset(getAssets(), FONT_PATH);
        tvAttack.setTypeface(font);
        tvHealth.setTypeface(font);
        tvName.setTypeface(font);
        tvClass.setTypeface(font);

        ivCard.setImage(ImageSource.resource(R.drawable.saber));
        ivCard.setMinimumScaleType(SCALE_TYPE_CENTER_CROP);
        setLayoutSettings();
    }

    private void setLayoutSettings() {

        ivBorder.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivBorder.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setStatLabelProperties(ivBorder.getHeight(), ivBorder.getWidth());
            }
        });
    }

    private void setStatLabelProperties(int height, int width) {

        //Set text size
        int fontSizeStats = (height * 30) / 1800;
        int fontSizeName = (height * 20) / 1800;
        int fontSizeClass = (height * 35) / 1800;

        tvAttack.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSizeStats);
        tvHealth.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSizeStats);
        tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSizeName);
        tvClass.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSizeClass);

        //Set text margin bottom
        int bottomMarginStats = (height * 10) / 875;
        int bottomMarginName = (height * 90) / 875;
        int bottomMarginClass = (height * 120) / 875;
        int leftMarginStats = (width * 20) / 100;

        RelativeLayout.LayoutParams paramsAttack = (RelativeLayout.LayoutParams) tvAttack.getLayoutParams();
        paramsAttack.setMargins(leftMarginStats, 0, 0, bottomMarginStats);
        tvAttack.setLayoutParams(paramsAttack);

        RelativeLayout.LayoutParams paramsHealth = (RelativeLayout.LayoutParams) tvHealth.getLayoutParams();
        paramsHealth.setMargins(0, 0, leftMarginStats, bottomMarginStats);
        tvHealth.setLayoutParams(paramsHealth);

        RelativeLayout.LayoutParams paramsName = (RelativeLayout.LayoutParams) tvName.getLayoutParams();
        paramsName.setMargins(0, 0, 0, bottomMarginName);
        tvName.setLayoutParams(paramsName);

        RelativeLayout.LayoutParams paramsClass = (RelativeLayout.LayoutParams) tvClass.getLayoutParams();
        paramsClass.setMargins(0, 0, 0, bottomMarginClass);
        tvClass.setLayoutParams(paramsClass);
    }

    //endregion

    //region Ads and back button

    private void setFullScreenad() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(AD_UNIT_ID);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void showFullScreenAd() {
        if (mInterstitialAd.isLoaded() && !adShown) {
            mInterstitialAd.show();
            adShown = true;
        }

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onBackPressed() {
        showFullScreenAd();
        showQuitAppDialog();
    }

    private void showQuitAppDialog() {

        MaterialDialog quitDialog = new MaterialDialog.Builder(this).title(getResources().getString(R.string.header_quit_app)).content(getResources().getString(R.string.dialog_quit_app)).positiveText(getResources().getString(R.string.dialog_button_quit)).negativeText(getResources().getString(R.string.dialog_button_cancel)).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                finish();
            }
        }).build();
        quitDialog.show();

    }

    //endregion

    //region Toolbar drawer

    private void setToolbarDrawer(Bundle savedInstanceState) {
        // Handle Toolbar
        AccountHeader header = new AnimatedHeaderBuilder().withHeaderBackground(R.drawable.ubw).withActivity(this).build();

        rarityItem = new SecondaryDrawerItem().withName(R.string.menu_rarity).withIcon(FontAwesome.Icon.faw_star).withDescription(R.string.menu_desc_rarity);
        classIconItem = new SecondaryDrawerItem().withName(R.string.menu_icon).withIcon(FontAwesome.Icon.faw_circle).withDescription(R.string.menu_desc_icon);

        classItem = new SecondaryDrawerItem().withName(R.string.menu_class).withDescription(R.string.menu_desc_class).withIcon(FontAwesome.Icon.faw_diamond).withBadge(R.string.beta).withBadgeStyle(new BadgeStyle().withColorRes(R.color.beta_color).withTextColorRes(R.color.beta_text_color));
        nameItem = new SecondaryDrawerItem().withName(R.string.menu_name).withDescription(R.string.menu_desc_name).withIcon(FontAwesome.Icon.faw_sun_o).withBadge(R.string.beta).withBadgeStyle(new BadgeStyle().withColorRes(R.color.beta_color).withTextColorRes(R.color.beta_text_color));
        attackItem = new SecondaryDrawerItem().withName(R.string.menu_attack).withDescription(R.string.menu_desc_attack).withIcon(FontAwesome.Icon.faw_hand_rock_o).withBadge(R.string.beta).withBadgeStyle(new BadgeStyle().withColorRes(R.color.beta_color).withTextColorRes(R.color.beta_text_color));
        healthItem = new SecondaryDrawerItem().withName(R.string.menu_health).withDescription(R.string.menu_desc_health).withIcon(FontAwesome.Icon.faw_shield).withBadge(R.string.beta).withBadgeStyle(new BadgeStyle().withColorRes(R.color.beta_color).withTextColorRes(R.color.beta_text_color));

        setSupportActionBar(toolbar);
        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .withToolbar(toolbar)
                .withSavedInstance(savedInstanceState)
                .withDisplayBelowStatusBar(false)
                .withTranslucentStatusBar(false)
                .withDrawerLayout(R.layout.material_drawer_fits_not)
                .addDrawerItems(
                        rarityItem,
                        classIconItem,
                        new SecondaryDrawerItem().withName(R.string.menu_image).withIcon(FontAwesome.Icon.faw_picture_o).withDescription(R.string.menu_desc_image),
                        new DividerDrawerItem(),
                        classItem,
                        nameItem,
                        attackItem,
                        healthItem,
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.menu_rate).withIcon(FontAwesome.Icon.faw_star).withDescription(R.string.menu_desc_rate),
                        new SecondaryDrawerItem().withName(R.string.menu_share).withIcon(FontAwesome.Icon.faw_share_alt).withDescription(R.string.menu_desc_share),
                        new SecondaryDrawerItem().withName(R.string.menu_donate).withIcon(FontAwesome.Icon.faw_money).withDescription(R.string.menu_desc_donate)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {
                            case 1:
                                setRarity();
                                break;
                            case 2:
                                setClassIcon();
                                break;
                            case 3:
                                requestPermissions(PermissionRequestType.PICK);
                                break;
                            case 5:
                                setClass();
                                break;
                            case 6:
                                setName();
                                break;
                            case 7:
                                setAttack();
                                break;
                            case 8:
                                setHealth();
                                break;
                            case 10:
                                rateApp();
                                break;
                            case 11:
                                shareApp();
                                break;
                            case 12:
                                donate();
                                break;
                        }

                        result.deselect();
                        return false;
                    }
                }).build();

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        result.deselect();
        //result.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    //endregion

    //region Design Dialogs

    private void setRarity() {

        RarityAdapter rarityAdapter = new RarityAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        final MaterialDialog rarityDialog = new MaterialDialog.Builder(this).title(R.string.header_rarity).adapter(rarityAdapter, linearLayoutManager).negativeText(R.string.dialog_button_cancel).build();

        RarityAdapter.RarityItemCallback callback = new RarityAdapter.RarityItemCallback() {
            @Override
            public void onItemClicked(int itemIndex) {
                mainPresenter.onSetRarity(itemIndex);
                rarityItem.withDescription(getResources().getQuantityString(R.plurals.dialog_star_count, itemIndex, itemIndex));
                result.updateItem(rarityItem);
                rarityDialog.dismiss();
            }
        };
        rarityAdapter.setItemCallback(callback);
        rarityDialog.show();
    }

    private void setClassIcon() {

        ClassIconAdapter classIconAdapter = new ClassIconAdapter(mainPresenter.generateListItems());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, calculateNoOfColumns(this, 100));
        final MaterialDialog classIconDialog = new MaterialDialog.Builder(this).title(R.string.header_icon).adapter(classIconAdapter, gridLayoutManager).negativeText(R.string.dialog_button_cancel).build();

        ClassIconAdapter.ClassIconItemCallback callback = new ClassIconAdapter.ClassIconItemCallback() {
            @Override
            public void onItemClicked(int itemIndex, String itemName) {
                mainPresenter.onSetClass(itemIndex);
                classIconItem.withDescription(itemName);
                result.updateItem(classIconItem);
                classIconDialog.dismiss();
            }
        };
        classIconAdapter.setItemCallback(callback);
        classIconDialog.show();
    }

    private void setClass() {

        MaterialDialog classDialog = new MaterialDialog.Builder(this).title(R.string.header_class).inputRangeRes(0, 20, R.color.error_color).inputType(InputType.TYPE_CLASS_TEXT).input(getResources().getString(R.string.menu_desc_class), tvClass.getText().toString(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                String className = input.toString();
                tvClass.setText(className);
                classItem.withDescription(className);
                result.updateItem(classItem);
                setLayoutSettings();
                dialog.dismiss();
            }
        }).positiveText(R.string.dialog_button_done).negativeText(R.string.dialog_button_cancel).build();

        classDialog.show();
    }

    private void setName() {

        MaterialDialog nameDialog = new MaterialDialog.Builder(this).title(R.string.header_name).inputRangeRes(0, 30, R.color.error_color).inputType(InputType.TYPE_CLASS_TEXT).input(getResources().getString(R.string.menu_desc_name), tvName.getText().toString(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                String name = input.toString();
                tvName.setText(name);
                nameItem.withDescription(name);
                result.updateItem(nameItem);
                setLayoutSettings();
                dialog.dismiss();
            }
        }).positiveText(R.string.dialog_button_done).negativeText(R.string.dialog_button_cancel).build();

        nameDialog.show();

    }

    private void setAttack() {

        MaterialDialog attackDialog = new MaterialDialog.Builder(this).title(R.string.header_attack).inputRangeRes(0, 5, R.color.error_color).inputType(InputType.TYPE_CLASS_NUMBER).input(getResources().getString(R.string.menu_desc_attack), tvAttack.getText().toString(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                String attack = input.toString();
                tvAttack.setText(attack);
                attackItem.withDescription(attack);
                result.updateItem(attackItem);
                setLayoutSettings();
                dialog.dismiss();
            }
        }).positiveText(R.string.dialog_button_done).negativeText(R.string.dialog_button_cancel).build();

        attackDialog.show();
    }

    private void setHealth() {

        MaterialDialog healthDialog = new MaterialDialog.Builder(this).title(R.string.header_health).inputRangeRes(0, 5, R.color.error_color).inputType(InputType.TYPE_CLASS_NUMBER).input(getResources().getString(R.string.menu_desc_health), tvHealth.getText().toString(), new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                String health = input.toString();
                tvHealth.setText(health);
                healthItem.withDescription(health);
                result.updateItem(healthItem);
                setLayoutSettings();
                dialog.dismiss();
            }
        }).positiveText(R.string.dialog_button_done).negativeText(R.string.dialog_button_cancel).build();

        healthDialog.show();

    }

    //endregion

    @Override
    public void showToast(int message) {
        Toast.makeText(this, getResources().getString(message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void requestPermissions(final PermissionRequestType type) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    mainPresenter.onAllPermissionsGranted(type);
                } else {
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
        EasyImage.openChooserWithGallery(this, getResources().getString(R.string.header_pick_image), 0);
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
    public void setCardImage(File file) {
        Log.e("Yigit",file.getAbsolutePath());
        ivCard.setImage(ImageSource.uri(Uri.fromFile(file)));
    }

    @Override
    public void setCardBorder(String resourceName) {
        ivBorder.setImageDrawable(ContextCompat.getDrawable(this, getResources().getIdentifier(resourceName, "drawable", this.getPackageName())));
    }

    @Override
    public void saveCardToGallery() {
        relativeHolder.setDrawingCacheEnabled(true);
        Bitmap bitmap = relativeHolder.getDrawingCache();


        File file = saveToGallery(bitmap);
        validateFileSave(file);
    }

    private File saveToGallery(Bitmap emptyBitmap) {

        FileOutputStream output = null;
        File file = null;
        try {
            File path = new File(Environment.getExternalStorageDirectory(), "/" + FILE_FOLDER + "/");
            path.mkdirs();
            String fileName = FILE_PREFIX + "_" + System.currentTimeMillis() + ".jpg";
            file = new File(path, fileName);
            output = new FileOutputStream(file);
            emptyBitmap.compress(Bitmap.CompressFormat.JPEG, 80, output);
        } catch (FileNotFoundException e) {
            file = null;
            e.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    private void validateFileSave(File file) {
        if (file != null) {
            Toast.makeText(getApplicationContext(), "Saved to gallery.", Toast.LENGTH_LONG).show();
            Intent requestScan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            requestScan.setData(Uri.fromFile(file));
            sendBroadcast(requestScan);
            relativeHolder.setDrawingCacheEnabled(false);
        }
    }
}


