package com.julopes.bibliotecuran.repository;

import com.julopes.bibliotecuran.db.*;
import com.julopes.bibliotecuran.AudioBookConverter;
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteDatabase.CursorFactory;  
import android.database.sqlite.SQLiteOpenHelper;  
import android.content.ContentValues;
import android.database.Cursor;  
import android.content.Context;
public class BookRepository {
    //private DbController dbController;
    private Context context;
    public BookRepository(Context context){
        //this.dbController=dbController;
        this.context=context;
    }
public boolean isBookSavedByName(String bookName){
            String query = "SELECT * FROM BOOK WHERE NAME LIKE '"+bookName+"'";
            DbHelper dbHelper = new DbHelper(context);
SQLiteDatabase db = dbHelper.getReadableDatabase();
Cursor c = db.rawQuery(query,null);
int result = c.getCount();
c.close();
db.close();
return (result>0);
}
public AudioBookConverter getAudioBookConverterById(Integer bookId){
String query ="SELECT * FROM BOOK WHERE ID="+bookId;
        DbHelper dbHelper = new DbHelper(context);
SQLiteDatabase db = dbHelper.getReadableDatabase();
Cursor c = db.rawQuery(query,null);
AudioBookConverter book=null;
if(c.getCount()>0){
    c.moveToFirst();
book = new AudioBookConverter(bookId, c.getString(c.getColumnIndex(BookTable.BOOK_NAME)),
c.getString(c.getColumnIndex(BookTable.BOOK_CONTENT)));
}
c.close();
db.close();
return book;
}

public AudioBookConverter getAudioBookConverterByName(String bookName){
            String query = "SELECT * FROM BOOK WHERE NAME LIKE '"+bookName+"'";
            DbHelper dbHelper = new DbHelper(context);
SQLiteDatabase db = dbHelper.getReadableDatabase();
Cursor c = db.rawQuery(query,null);
AudioBookConverter book=null;
if(c.getCount()>0){
c.moveToFirst();
book = new AudioBookConverter(c.getLong(c.getColumnIndex(BookTable.BOOK_ID)), bookName, c.getString(c.getColumnIndex(BookTable.BOOK_CONTENT)));
}
c.close();
db.close();
return book;
}
    
    public String insert(String bookName, String bookContent){
        DbHelper dbHelper = new DbHelper(context);
SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values;
        long result;
       values = new ContentValues();
       values.put(BookTable.BOOK_NAME, bookName);
       values.put(BookTable.BOOK_CONTENT, bookContent);
       result = db.insert(BookTable.TABLE_NAME, null, values);
db.close();
return String.valueOf(result);
    }

}