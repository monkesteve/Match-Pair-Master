package com.example.assignment;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GameResultProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.assignment.gameresultprovider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/results");

    private static final int RESULTS = 1;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "results", RESULTS);
    }

    private String playerName;
    private int moves;
    private double duration;

    @Override
    public boolean onCreate() {
        return true;
    }

    public void setGameResult(String playerName, int moves, double duration) {
        this.playerName = playerName;
        this.moves = moves;
        this.duration = duration;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (uriMatcher.match(uri) == RESULTS) {
            MatrixCursor cursor = new MatrixCursor(new String[]{"playerName", "moves", "duration"});
            cursor.addRow(new Object[]{playerName, moves, duration});
            return cursor;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (values != null) {
            playerName = values.getAsString("playerName");
            moves = values.getAsInteger("moves");
            duration = values.getAsDouble("duration");
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}