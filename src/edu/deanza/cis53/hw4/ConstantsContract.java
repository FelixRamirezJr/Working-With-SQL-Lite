package edu.deanza.cis53.hw4;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by felix on 6/21/15.
 */
public class ConstantsContract {
    public static final String CONTENT_AUTHORITY = "edu.deanza.cis53.hw4.03495";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // This is the ContentAuthority...


    public static final String PATH_NAME = "names";
    public static final String PATH_VALUE = "values";



    // Inner Class That Defines The Columns Inside.
    public static final class ValueEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VALUE).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_VALUE;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_VALUE;

        // Table name
        public static final String TABLE_NAME = "values"; // This  is the Name of the table
        public static final String COLUMN_VALUE = "value"; // This is the Name of the Column

        public static Uri buildValuesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // Inner Class That Defines The Columns Inside.
    public static final class NameEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_NAME).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_NAME;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_NAME;

        // Table name
        public static final String TABLE_NAME = "names"; // This  is the Name of the table
        public static final String COLUMN_NAME = "name"; // This is the Name of the Column
        public static final String COLUMN_VALUE = "value";



        public static Uri buildNamesUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildValues(String values) {
            return CONTENT_URI.buildUpon().appendPath(values).build();
        }

        // Not Sure what this is doing... But I might as well as give it a shot.
        public static String getNameFromUri(Uri uri)
        { return uri.getPathSegments().get(1); }

        public static String getValue(Uri uri) {
            return uri.getPathSegments().get(2);
        }


    }

}


