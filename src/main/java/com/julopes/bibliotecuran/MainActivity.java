package com.julopes.bibliotecuran;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.AdapterView.OnItemClickListener;

import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.os.Build;
import java.util.Locale;
import android.util.Log;
import android.net.ConnectivityManager;
import android.net.NetworkInfo; 
import android.content.Context;
public class MainActivity extends Activity {
    @Override
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
if(isConnected(this)){
setContentView(R.layout.main_listview_layout);
ListView listView = (ListView) findViewById(R.id.list_view);
new DownloadDados(this, listView).execute();
}
else{
	setContentView(R.layout.activity_main_without_internet);
}	
}
@Override
public void onStart() {
super.onStart();
}
private   boolean isConnected(Context cont){
 ConnectivityManager conmag = (ConnectivityManager) cont.getSystemService(cont.CONNECTIVITY_SERVICE);
 if ( conmag != null ) {
  conmag.getActiveNetworkInfo();
  if (conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
   return true;
  }
  if (conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
   return true;
  }
 }
 return false;
}
}
