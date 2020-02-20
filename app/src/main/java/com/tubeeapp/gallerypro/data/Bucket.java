package com.tubeeapp.gallerypro.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class Bucket implements Parcelable {

    private String bucketId;
    private String name;
    private String pathPhotoCover;
    private String pathToAlbum;
    private String dateTaken;
    private String dateModified;
    private int count;

    public static String[] getProjection() {
        return new String[]{
                MediaStore.Images.Media.DATA,              // đường dẫn đến ảnh
                MediaStore.Images.Media.BUCKET_ID,          // bucket_id: id của folder ảnh
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // bucket_display_name: tên folder ảnh
                MediaStore.Images.Media.DATE_TAKEN,         // example: 1388563180000 => need to convert
                MediaStore.Images.Media.DATE_MODIFIED       // date_modified
        };
    }

    public Bucket() {
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getPathToAlbum() {
        return pathToAlbum;
    }

    public void setPathToAlbum(String pathToAlbum) {
        this.pathToAlbum = pathToAlbum;
    }

    public static Creator<Bucket> getCREATOR() {
        return CREATOR;
    }

    private Bucket(Parcel in) {
        this.bucketId = in.readString();
        this.name = in.readString();
        this.pathPhotoCover = in.readString();
        this.pathToAlbum = in.readString();
        this.dateModified = in.readString();
        this.dateTaken = in.readString();
        this.count = in.readInt();
    }

    public String getPathPhotoCover() {
        return pathPhotoCover;
    }

    public void setPathPhotoCover(String pathPhotoCover) {
        this.pathPhotoCover = pathPhotoCover;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(bucketId);
        parcel.writeString(name);
        parcel.writeString(pathPhotoCover);
        parcel.writeString(pathToAlbum);
        parcel.writeString(dateModified);
        parcel.writeString(dateTaken);
        parcel.writeInt(count);
    }

    public static final Creator<Bucket> CREATOR = new Creator<Bucket>() {
        @Override
        public Bucket createFromParcel(Parcel in) {
            return new Bucket(in);
        }

        @Override
        public Bucket[] newArray(int size) {
            return new Bucket[size];
        }
    };

}
