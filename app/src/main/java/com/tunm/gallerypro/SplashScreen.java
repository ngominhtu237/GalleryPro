package com.tunm.gallerypro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tunm.gallerypro.utils.preferences.Prefs;

public class SplashScreen extends AppCompatActivity {

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private SharedPreferences permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(getColor(R.color.accent_white), android.graphics.PorterDuff.Mode.MULTIPLY);
        checkPermissions();
    }

    @SuppressLint("ApplySharedPref")
    private void checkPermissions() {
        if (!checkPermissionGranted()) {
            if (checkShowUIRequestPermission()) {
                ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                showDialogRequestSettingPermission();
            } else {
                ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            proceedAfterPermission();
        }
    }

    // nếu cả 2 thằng đc grant => true
    private boolean checkPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED;
    }

    // show UI with rationale of requesting this permission
    // nếu 1 trong 2 thằng là false (nghĩa là 1 thằng ấn vào Deny & don't ask again) => don't show UI
    private boolean checkShowUIRequestPermission() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1]);
    }

    private void proceedAfterPermission() {
        if (Prefs.getRunSplashScreen() == 0) {
            Prefs.setRunSplashScreen(1);
        }
        Intent i = new Intent(SplashScreen.this, MainActivity.class); //start activity
        startActivity(i);
        finish();
    }

    // hàm này đc gọi khi nào user ấn vào bất kì option nào trên UIRequestPermission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            boolean allgranted = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (checkShowUIRequestPermission()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This app needs storage permissions.");
                builder.setPositiveButton("Grant", (dialog, which) -> {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(SplashScreen.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                    finish();
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission();
            } else {
                showDialogRequestSettingPermission(); // not get permission from Settings
            }
        }
    }

    private void showDialogRequestSettingPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This app needs storage permissions.");
        builder.setPositiveButton("Setting", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
            Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            finish();
        });
        builder.show();
    }
}
