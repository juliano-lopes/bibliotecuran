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
public class MainActivity extends Activity {


//private WebView webView;
private ListView listView;
private TextView titulo;
private TextView descricao;
    @Override
    
public void onCreate(Bundle savedInstanceState) {
        
super.onCreate(savedInstanceState);
        
setContentView(R.layout.main_listview_layout);
titulo = (TextView) findViewById(R.id.titulo);
descricao = (TextView) findViewById(R.id.descricao);
listView = (ListView) findViewById(R.id.list_view);
//WebSettings webSettings = webView.getSettings();
//webSettings.setJavaScriptEnabled(true);
DownloadDados dd = new DownloadDados(this, listView);
dd.execute();


}


    
@Override
    
public void onStart() {
        
super.onStart();
        


}


}
