package com.tubeeapp.gallerypro;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tubeeapp.gallerypro.data.Bucket;
import com.tubeeapp.gallerypro.data.provider.CPHelper;
import com.tubeeapp.gallerypro.utils.preferences.Prefs;

import java.util.ArrayList;
import java.util.Arrays;

public class SplashScreen extends AppCompatActivity {

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private SharedPreferences permissionStatus;
    private ProgressBar progressBar;

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0]) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1]) ) {
                ActivityCompat.requestPermissions(this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
            } else if (permissionStatus.getBoolean(permissionsRequired[0],false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This app needs storage permissions.");
                builder.setPositiveButton("Grant", (dialog, which) -> {
                    dialog.cancel();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    Toast.makeText(getBaseContext(), "Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> { dialog.cancel(); finish();});
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0],true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {
        if (Prefs.getRunSplashScreen() == 0) {
            Prefs.setRunSplashScreen(1);
            new GetAlbums().execute();
        } else {
            Intent i = new Intent(SplashScreen.this, MainActivity.class); //start activity
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        permissionStatus = getSharedPreferences("permissionStatus",MODE_PRIVATE);
        progressBar = findViewById(R.id.progressBar);
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CALLBACK_CONSTANT) {
            boolean allgranted = false;
            for(int i=0;i<grantResults.length;i++){
                if(grantResults[i]==PackageManager.PERMISSION_GRANTED){
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if(allgranted){
                proceedAfterPermission();
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,permissionsRequired[1])){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This app needs storage permissions.");
                builder.setPositiveButton("Grant", (dialog, which) -> {
                    dialog.cancel();
                    ActivityCompat.requestPermissions(SplashScreen.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> { dialog.cancel(); finish();});
                builder.show();
            } else {
                Toast.makeText(getBaseContext(),"Unable to get Permission",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }
    private class GetAlbums extends AsyncTask<Void, Integer, Integer> {

        private ArrayList<Bucket> dataReturns;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            dataReturns = CPHelper.getAlbums(SplashScreen.this, new ArrayList<>(Arrays.asList(0, 1)));
            return dataReturns.size();
        }

        @Override
        protected void onPostExecute(Integer size) {
            Intent i = new Intent(SplashScreen.this, MainActivity.class); //start activity
            i.putExtra("album_data", dataReturns);
            startActivity(i);
            finish();
            super.onPostExecute(size);
        }
    }

}
