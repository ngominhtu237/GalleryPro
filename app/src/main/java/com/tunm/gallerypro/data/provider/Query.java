package com.tunm.gallerypro.data.provider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.tunm.gallerypro.data.utils.DataUtils;

public class Query {

    private Uri uri;
    private String[] projection;
    private String selection;
    private String[] args;

    Query(Builder builder) {
        uri = builder.uri;
        projection = builder.projection;
        selection = builder.selection;
        args = builder.getStringArgs();
    }

    public Cursor getCursor(ContentResolver cr) {
        return cr.query(uri, projection, selection, args, null);
    }

    public static final class Builder {
        Uri uri = null;
        String[] projection = null;
        String selection = null;
        Object[] args = null;

        Builder() {}

        public Builder uri(Uri val) {
            uri = val;
            return this;
        }

        public Builder projection(String[] val) {
            projection = val;
            return this;
        }

        public Builder selection(String val) {
            selection = val;
            return this;
        }

        public Builder args(Object ... val) {
            args = val;
            return this;
        }

        public Query build() {
            return new Query(this);
        }

        public String[] getStringArgs() {
            return DataUtils.getStringArgs(args);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
