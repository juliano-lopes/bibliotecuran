package com.julopes.bibliotecuran.repository;

import com.julopes.bibliotecuran.db.*;
import com.julopes.bibliotecuran.Book;
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
public Book getBookById(Integer bookId){
String query ="SELECT * FROM BOOK WHERE ID="+bookId;
        DbHelper dbHelper = new DbHelper(context);
SQLiteDatabase db = dbHelper.getReadableDatabase();
Cursor c = db.rawQuery(query,null);
Book book=null;
if(c.getCount()>0){
    c.moveToFirst();
book = new Book(bookId,
c.getString(c.getColumnIndex(BookTable.BOOK_NAME)),
c.getString(c.getColumnIndex(BookTable.BOOK_CONTENT)),
c.getInt(c.getColumnIndex(BookTable.BOOK_MARK)));
}
c.close();
db.close();
return book;
}

public Book getBookByName(String bookName){
            String query = "SELECT * FROM BOOK WHERE NAME LIKE '"+bookName+"'";
            DbHelper dbHelper = new DbHelper(context);
SQLiteDatabase db = dbHelper.getReadableDatabase();
Cursor c = db.rawQuery(query,null);
Book book=null;
if(c.getCount()>0){
c.moveToFirst();
book = new Book(c.getLong(c.getColumnIndex(BookTable.BOOK_ID)),
bookName,
c.getString(c.getColumnIndex(BookTable.BOOK_CONTENT)),
c.getInt(c.getColumnIndex(BookTable.BOOK_MARK)));
}
c.close();
db.close();
return book;
}
    
    public String insert(Book book){
        DbHelper dbHelper = new DbHelper(context);
SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = toValues(book);
       long result = db.insert(BookTable.TABLE_NAME, null, values);
db.close();
return String.valueOf(result);
    }
    public int saveMark(Book book){
        DbHelper dbHelper = new DbHelper(context);
SQLiteDatabase db = dbHelper.getWritableDatabase();
ContentValues values = toValues(book);
int result =  db.update(BookTable.TABLE_NAME, values, "ID=?", toArgs(book));
db.close();
return result;
}
        private ContentValues toValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookTable.BOOK_NAME, book.getName());
        values.put(BookTable.BOOK_CONTENT, book.getContent());
        values.put(BookTable.BOOK_MARK, book.getMark());
        return values;
    }
    private String[] toArgs(Book book) {
        String[] args = {String.valueOf(book.getId())};
        return args;
    }
}