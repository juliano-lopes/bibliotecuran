package com.julopes.bibliotecuran;




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
import com.julopes.bibliotecuran.LoadingActivity;
import java.io.File;
import java.io.FileOutputStream;

public class DownloadDados extends AsyncTask<Void, Void, String> {
private Context context;
//private TextView textView;
private ListView listView;
private String url;
private String book;
private TextToSpeech mTts;
    private static final int BUFFER_SIZE = 4096;
public DownloadDados(Context context, ListView listView){
this.context = context;
this.listView = listView;
this.url = "http://julianolopes.com.br/api_android/android_request.php?id=com.julopes.bibliotecuran&list_book=true";
this.book = "";
//this.mTts = mTts;
}

public DownloadDados(Context context, String book, String url){
this.context = context;
this.url = "http://www.julianolopes.com.br/documentos/index.php?file=Que%20Amor%20Bonito%20-%20Thiago%20Grulha.mp3";
this.book = book;
this.listView = null;
//this.mTts= mTts;
}

	@Override
	protected String doInBackground(Void... params) {
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
Intent intent = new Intent(context, SpeakOutActivity.class);
intent.putExtra("bookName", book);
intent.putExtra("book", data);
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
msg="Carregando Livro...";
Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
bookName+=".mp3";
String audioBookUrl = "http://julianolopes.com.br/api_android/library/audiobook/"+bookName;
Intent intent = new Intent(context, SpeakOutActivity.class);
intent.putExtra("bookName", bookName);
intent.putExtra("book", audioBookUrl);
context.startActivity(intent);

}
});


}
}
