package com.julopes.bibliotecuran.db;
    import android.content.Context;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.widget.Toast;
    public class DbHelper extends SQLiteOpenHelper {
            static final String DB_NAME = "curan.db";
             static final int VERSION = 1;
            public DbHelper(Context context){
                    super(context, DB_NAME,null,VERSION);
                                                    }
            @Override
            public void onCreate(SQLiteDatabase db) {
db.execSQL("CREATE TABLE BOOK(ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, CONTENT TEXT);");
            }
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
    }