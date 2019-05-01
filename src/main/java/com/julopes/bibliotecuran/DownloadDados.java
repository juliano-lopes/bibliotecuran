package com.julopes.bibliotecuran;



import android.os.Bundle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.widget.TextView;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;

import android.text.Html;
import android.webkit.WebView;

import android.widget.Toast;
import android.content.Intent;
import com.julopes.bibliotecuran.AudioBookConverter;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import java.util.List;
 import java.util.ArrayList;
import com.julopes.bibliotecuran.db.*;
import com.julopes.bibliotecuran.repository.*;
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteDatabase.CursorFactory;  
import android.database.sqlite.SQLiteOpenHelper;  
import android.content.ContentValues;
import android.database.Cursor;  
  
public class DownloadDados extends AsyncTask<Void, Void, String> {
private Context context;
private ListView listView;
private String url;
private String book;
public DownloadDados(Context context, ListView listView){
this.context = context;
this.listView = listView;
this.url = "http://julianolopes.com.br/api_android/android_request.php?id=com.julopes.bibliotecuran&list_book=txt";
this.book = "";
}

public DownloadDados(Context context, String book, String url){
this.context = context;
this.url = url;
this.book = book;
this.listView = null;
}

	@Override
	protected String doInBackground(Void... params) {
		
		if(!book.equals("")){
			BookRepository bookRepo = new BookRepository(context);
			AudioBookConverter audioBook = bookRepo.getAudioBookConverterByName(book);
			if(audioBook!=null){
				return String.valueOf(audioBook.getId());
			}
		}
		
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		try {
			URL url = new URL(this.url);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();

			InputStream inputStream = urlConnection.getInputStream();

			reader = new BufferedReader(new InputStreamReader(inputStream));

			String linha;
			StringBuffer buffer = new StringBuffer();
			while ((linha = reader.readLine()) != null) {
				buffer.append(linha);
				buffer.append("\n");
			}
if(!book.equals("")){


DbHelper dh = new DbHelper(context);
SQLiteDatabase db = dh.getWritableDatabase();
ContentValues values = new ContentValues();
values.put(BookTable.COLUMN_NAME_NAME,book);
values.put(BookTable.COLUMN_NAME_CONTENT,buffer.toString());
long numRows = db.insert(BookTable.TABLE_NAME,null,values);
db.close();

			//return buffer.toString();
			return String.valueOf(numRows);
}
return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			if (urlConnection != null) {
				urlConnection.disconnect();
			}

			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(String data) {

if(book!=""){
loadBook(data);
}
else{
loadList(data);
}

	}
public void loadBook(String data){

//new AudioBookConverter(context, book, data).execute(data);
Intent intent = new Intent(context, SpeakOutActivity.class);
intent.putExtra("bookName", book);
intent.putExtra("insertion", data);
context.startActivity(intent);

}
public void loadList(String data){
String[] listBooks = data.split(";");
ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listBooks);
listView.setAdapter(adapter);
listView.setOnItemClickListener(new OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> parent, View view,
int position, long id) {
String bookName, msg;
bookName = parent.getItemAtPosition(position).toString();
msg="Buscando livro...";
Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
/*
Intent intent = new Intent(context, SpeakOutActivity.class);
intent.putExtra("bookName", bookName);
intent.putExtra("book", book);
context.startActivity(intent);
*/
String newUrl = "http://julianolopes.com.br/api_android/android_request.php?id=com.julopes.bibliotecuran&book="+bookName;
new DownloadDados(context, bookName, newUrl).execute();


}
});


}
}
