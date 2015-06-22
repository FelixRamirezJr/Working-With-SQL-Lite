package edu.deanza.cis53.hw4;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by felix on 6/21/15.
 */
public class ConstantsProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final int NAME = 100;
    private static final int VALUE = 101;
    private DatabaseHelper dbHelper; // Was Replaced By mOpenHelper
    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
    static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder(); // This Should Build It Hopefully????
        sWeatherByLocationSettingQueryBuilder.setTables(
                ConstantsContract.NameEntry.TABLE_NAME + " INNER JOIN " +
                        ConstantsContract.ValueEntry.TABLE_NAME);
    }

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ConstantsContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.

        matcher.addURI(authority,ConstantsContract.PATH_NAME,NAME);

        matcher.addURI(authority, ConstantsContract.PATH_VALUE, VALUE);

        return matcher;
    }

    private Cursor getQuery(
            Uri uri, String[] projection, String sortOrder)
    {
       // String name = ConstantsContract.NameEntry.getNameFromUri(uri);
       // string value = cons
       // String locationSetting = WeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
      //  String date = WeatherContract.WeatherEntry.getDateFromUri(uri);
        String name = ConstantsContract.NameEntry.getNameFromUri(uri);
        String value = ConstantsContract.NameEntry.getValue(uri);
        String query=
                String.format("SELECT rowid AS _id, %s, %s FROM %s ORDER BY %s",
                        DatabaseHelper.TITLE, DatabaseHelper.VALUE,
                        DatabaseHelper.TABLE, DatabaseHelper.TITLE);
        //return(db.getReadableDatabase().rawQuery(query, null));

        return sWeatherByLocationSettingQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                query,
                new String[]{
                        name, value},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate()
    {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        retCursor = getQuery(uri,projection,sortOrder);
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case NAME:
                return ConstantsContract.NameEntry.CONTENT_ITEM_TYPE;
            case VALUE:
                return ConstantsContract.ValueEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case NAME:
            {
                long _id = db.insert(ConstantsContract.NameEntry.TABLE_NAME,null,values);
                if ( _id > 0 )
                    returnUri = ConstantsContract.NameEntry.buildNamesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VALUE:
            {
                long _id = db.insert(ConstantsContract.ValueEntry.TABLE_NAME,null,values);
                if ( _id > 0 )
                    returnUri = ConstantsContract.ValueEntry.buildValuesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case NAME:
                rowsDeleted = db.delete(
                        ConstantsContract.NameEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VALUE:
                rowsDeleted = db.delete(
                        ConstantsContract.ValueEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case NAME:
                rowsUpdated = db.update(ConstantsContract.NameEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VALUE:
                rowsUpdated = db.update(ConstantsContract.ValueEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case NAME:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values)
                    {
                        long _id = db.insert(ConstantsContract.NameEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                        {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
