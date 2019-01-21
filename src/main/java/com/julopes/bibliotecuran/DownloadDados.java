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

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import java.io.File;
import java.io.FileOutputStream;

public class DownloadDados extends AsyncTask<Void, Void, String> {
private Context context;
private ListView listView;
private String url;
private static final String AUDIO_BOOK_URL = "http://julianolopes.com.br/api_android/library/audiobook/";
private static final String AUDIO_BOOK_FORMATE = ".mp3";
public DownloadDados(Context context, ListView listView){
this.context = context;
this.listView = listView;
this.url = "http://julianolopes.com.br/api_android/android_request.php?id=com.julopes.bibliotecuran&list_book=true";
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
loadList(data);
	}
public void loadList(String data){
String[] listBooks = data.split(";");
ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listBooks);
listView.setAdapter(adapter);
listView.setOnItemClickListener(new OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
String audioBookName, msg;
audioBookName = parent.getItemAtPosition(position).toString();
msg="Buscando Livro...";
Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
audioBookName+=AUDIO_BOOK_FORMATE;
String audioBookUrl = AUDIO_BOOK_URL+audioBookName;
Intent intent = new Intent(context, ReadBookActivity.class);
intent.putExtra("audio_book_name", audioBookName);
intent.putExtra("audio_book_url", audioBookUrl);
context.startActivity(intent);

}
});


}
}
