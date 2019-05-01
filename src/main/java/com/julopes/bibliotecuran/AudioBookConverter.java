package com.julopes.bibliotecuran;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import java.util.*; 
import java.util.Locale; 
import android.content.Intent;
import android.content.Context;
 import java.util.Arrays;
 import java.util.List;
 import java.util.ArrayList;
  import android.os.PowerManager;
 import android.os.PowerManager.WakeLock;
 import android.view.MotionEvent;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;

import android.text.TextUtils;

import android.view.accessibility.AccessibilityManager;

import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.media.AudioManager;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.os.Environment;
import android.net.Uri;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer;
import android.annotation.TargetApi;
import java.util.HashMap;
import android.widget.Toast;
import android.view.View.OnClickListener;
  import java.io.File;
import android.support.v4.content.FileProvider;
import android.os.AsyncTask;
import com.julopes.bibliotecuran.db.*;

import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteDatabase.CursorFactory;  
import android.database.sqlite.SQLiteOpenHelper;  
import android.content.ContentValues;
import android.database.Cursor;  

public class AudioBookConverter extends AsyncTask<String, String, ArrayList<String>>{
private int atualLine;
private long id;
private String bookName;
private String bookContent;
private List<String> bookLines;
private List<String> audioBookLines;

private static final int LINE_LENGTH=500;
private TextView tv;
private Button btn;
public AudioBookConverter(long id, String bookName, String bookContent){
this.id = id;
this.bookName=bookName;
this.bookContent=bookContent;
audioBookLines = new ArrayList<>();
bookLines=new ArrayList<>();
}
public long getId(){
	return id;
}
public String getName(){
	return bookName;
}

public List<String> getAudioBookLines(){
return audioBookLines;
}
public void setTextView(TextView tv){
	this.tv=tv;
}
public void setButton(Button btn){
	this.btn=btn;
}
private List<String> getWords(String bookContent){
return Arrays.asList(bookContent.split(" "));
}
public ArrayList<String> getBookContentWithFormatedLines(String bookContent){
String line="";
ArrayList<String> formedLines = new ArrayList<>();
for(String word : getWords(bookContent)){
if(line.length()<LINE_LENGTH){
line+=word+" ";
}
else{
	line+=word+" ";
	char lastCharacter = word.charAt(word.length()-1);
	if((isPontuation(lastCharacter)) && (word.length()>3)){
line = getLineWithoutSomeCharacters(line);
formedLines.add(line);
line="";
}
}
}

return formedLines;
}
private boolean isPontuation(char character){
	return ((character=='.') || (character=='!') || (character=='?'));
	}

private String getLineWithoutSomeCharacters(String line){
		line = line.replace('_', ' ');
	line = line.replace('-', ' ');
	line = line.replace('=', ' ');
return line;
}
@Override
protected ArrayList<String> doInBackground(String... data) {
ArrayList<String> formedLines = new ArrayList<>();
if(!bookContent.equals("")){
formedLines=getBookContentWithFormatedLines(bookContent);
}
else{
	publishProgress("Nao foi possivel carregar o livro "+bookName);
}
return formedLines;
}
@Override
protected void onProgressUpdate(String... values) {
	tv.setText(values[0]);
}
protected void onPostExecute(ArrayList<String> result) {
SpeakOutActivity.setFormatedBookLines(result);
btn.setEnabled(true);
/*
Intent intent = new Intent(context, SpeakOutActivity.class);
intent.putExtra("bookName", bookName);
intent.putStringArrayListExtra("book", result);
context.startActivity(intent);
*/
}
}