package com.julopes.bibliotecuran.db;
import android.content.Context;  
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteDatabase.CursorFactory;  
import android.database.sqlite.SQLiteOpenHelper;  
import android.util.Log;  
import android.content.ContentValues;
import android.database.Cursor;  
public class DbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String LONG_TYPE = " LONG";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_MARKS =
        "CREATE TABLE " + Mark.TABLE_NAME + " (" +
Mark.COLUMN_ID + " INTEGER PRIMARY KEY," +
        Mark.COLUMN_AUDIO_BOOK_NAME + TEXT_TYPE + COMMA_SEP +
Mark.COLUMN_AUDIO_BOOK_MARK + " INTEGER )";

    private static final String SQL_DELETE_MARKS =
        "DROP TABLE IF EXISTS " + Mark.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "bibliotecuran.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MARKS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}