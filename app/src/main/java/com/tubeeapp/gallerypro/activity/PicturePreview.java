package com.tubeeapp.gallerypro.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tubeeapp.gallerypro.R;
import com.tubeeapp.gallerypro.data.MediaItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.tubeeapp.gallerypro.data.utils.DataUtils.readableFileSize;

public class PicturePreview extends AppCompatActivity {

    public static ArrayList<MediaItem> mImageList;
    private int selectedImagePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_picture_preview);

        Intent receivedIntent = getIntent();
        selectedImagePosition = receivedIntent.getIntExtra("current_image_position", 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picture_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_details:
                ViewGroup viewGroup = findViewById(android.R.id.content);
                //then we will inflate the custom alert dialog xml that we created
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_picture_details, viewGroup, false);
                TextView dateTakenTitle = dialogView.findViewById(R.id.dateTakenTitle);
                TextView tvItemDate = dialogView.findViewById(R.id.tvItemDate);
                TextView tvItemSize = dialogView.findViewById(R.id.tvItemSize);
                TextView tvItemResolution = dialogView.findViewById(R.id.tvItemResolution);
                TextView tvItemPath = dialogView.findViewById(R.id.tvItemPath);
                TextView tvItemTitle = dialogView.findViewById(R.id.tvItemTitle);
                Button btOK = dialogView.findViewById(R.id.buttonOk);
                MediaItem mediaItem = mImageList.get(selectedImagePosition);

                String format = "MM-dd-yyyy HH:mm:ss";
                SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);

                String dateTime;
                if(mediaItem.getDateTaken() != null) {
                    dateTime = formatter.format(new Date(Long.parseLong(mediaItem.getDateTaken())));
                } else {
                    dateTakenTitle.setText("Date modified");
                    dateTime = formatter.format(new Date(Long.parseLong(mediaItem.getDateModified())  * 1000L));
                }
                tvItemDate.setText(dateTime);
                tvItemSize.setText(readableFileSize(Long.valueOf(mediaItem.getSize())));
                tvItemResolution.setText(mediaItem.getWidth() + "x" + mediaItem.getHeight());
                tvItemPath.setText(mediaItem.getPathMediaItem());
                tvItemTitle.setText(mediaItem.getName() + mediaItem.getPathMediaItem().substring(mediaItem.getPathMediaItem().lastIndexOf(".")));

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                btOK.setOnClickListener((view) -> alertDialog.dismiss());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
