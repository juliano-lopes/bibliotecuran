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
    private Context context;
    public BookRepository(Context context){
        this.context=context;
    }
public AudioBookConverter getAudioBookConverterById(long bookId){
DbHelper dh = new DbHelper(context);
SQLiteDatabase db = dh.getReadableDatabase();
String query ="select * from "+BookTable.TABLE_NAME+" where "+BookTable.COLUMN_NAME_ID+"="+bookId;
Cursor c = db.rawQuery(query,null);
AudioBookConverter book=null;
if(c.moveToNext()){
book = new AudioBookConverter(bookId, c.getString(c.getColumnIndex(BookTable.COLUMN_NAME_NAME)),
c.getString(c.getColumnIndex(BookTable.COLUMN_NAME_CONTENT)));
}
c.close();
return book;
}
public AudioBookConverter getAudioBookConverterByName(String bookName){
DbHelper dh = new DbHelper(context);
SQLiteDatabase db = dh.getReadableDatabase();
String query ="select * from "+BookTable.TABLE_NAME+" where "+BookTable.COLUMN_NAME_ID+"="+bookName;
Cursor c = db.rawQuery(query,null);
AudioBookConverter book=null;
if(c.moveToNext()){
book = new AudioBookConverter(c.getLong(c.getColumnIndex(BookTable.COLUMN_NAME_ID)), bookName, c.getString(c.getColumnIndex(BookTable.COLUMN_NAME_CONTENT)));
}
c.close();
return book;
}

}