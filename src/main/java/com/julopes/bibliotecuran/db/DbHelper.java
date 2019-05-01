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
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_BOOKS =
        "CREATE TABLE " + BookTable.TABLE_NAME + " (" +
        BookTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
        BookTable.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
        BookTable.COLUMN_NAME_CONTENT + TEXT_TYPE + " )";

    private static final String SQL_DELETE_BOOKS =
        "DROP TABLE IF EXISTS " + BookTable.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "bibliotecuran.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_BOOKS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}