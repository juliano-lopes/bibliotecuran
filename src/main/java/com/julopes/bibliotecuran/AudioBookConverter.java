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
        private TextToSpeech mTts;
    private int mStatus = 0;
private List<String> bookLines;
private List<String> audioBookLines;
private int atualLine;
private String bookName;
private String bookContent;
private Context context;
private static final int LINE_LENGTH=500;
private TextView tv;
public AudioBookConverter(Context context, String bookName, String bookContent){
this.context = context;
//this.mTts=mTts;
this.bookName=bookName;
this.bookContent=bookContent;
audioBookLines = new ArrayList<>();
bookLines = Arrays.asList(bookContent.split("\n"));
}
public AudioBookConverter(Context context, TextView tv){
this.context=context;
this.tv = tv;

}
public List<String> getAudioBookLines(){
return audioBookLines;
}

public void convert(int i){

													String utteranceCode = "tts_sound"+i;
HashMap<String, String> myHashRender = new HashMap();
myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceCode);
					 String musicName = "book_line_"+i+".wav";
					 File appTmpPath = new File(context.getFilesDir(), musicName);
					 String tempDestFile = appTmpPath.getAbsolutePath();
int status=0;
//status = mTts.synthesizeToFile(bookLines.get(i), myHashRender, tempDestFile);
if(status==TextToSpeech.SUCCESS){
	audioBookLines.add(tempDestFile);
}
													

/*
	btnSpeak.post(new Runnable(){
	public void run(){
		btnSpeak.setText("Ler");
	}
});
*/
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
	line = line.replace('_', ' ');
	line = line.replace('-', ' ');
	line = line.replace('=', ' ');
formedLines.add(line);
line="";

	}
}
}
return formedLines;
}
private boolean isPontuation(char character){
	return ((character=='.') || (character=='!') || (character=='?'));}
public String getAudioBookLinesAsText(){
String asText="";
if(audioBookLines.size()>0){
for(String line : audioBookLines){
asText+=line+";";
}
} else{
//mTts.speak("nenhuma linha de audio",TextToSpeech.QUEUE_FLUSH,null);
return asText;
}
return asText.substring(0, asText.length()-1);
}
protected ArrayList<String> doInBackground(String... data) {
DbHelper dh = new DbHelper(context);
SQLiteDatabase db = dh.getReadableDatabase();
long id = Long.parseLong(data[0]);
String query ="select * from "+BookTable.TABLE_NAME+" where "+BookTable.COLUMN_NAME_ID+"="+id;
Cursor c = db.rawQuery(query,null);
c.moveToNext();
String n = c.getString(c.getColumnIndex(BookTable.COLUMN_NAME_NAME));
String content = c.getString(c.getColumnIndex(BookTable.COLUMN_NAME_CONTENT));
c.close();

String line="";
int i=1;
ArrayList<String> formedLines = new ArrayList<>();

if(content!=""){


for(String word : getWords(content)){
if(line.length()<LINE_LENGTH){
line+=word+" ";
}
else{
	line+=word+" ";
	char lastCharacter = word.charAt(word.length()-1);
	if((isPontuation(lastCharacter)) && (word.length()>3)){
	line = line.replace('_', ' ');
	line = line.replace('-', ' ');
	line = line.replace('=', ' ');
formedLines.add(line);
line="";
publishProgress("Linha "+i+" formed");
/*
try{
Thread.sleep(1000);
}catch(InterruptedException e){
e.printStackTrace();
}
*/
i++;
	}
}
}
}
else{
	publishProgress("Nao foi possivel carregar o livro "+n);
}

return formedLines;
}
@Override
protected void onProgressUpdate(String... values) {
	tv.setText(values[0]);
}
protected void onPostExecute(ArrayList<String> result) {
//tv.setText("Book was formated");
SpeakOutActivity.setFormatedBookLines(result, tv);
//SpeakOutActivity.finalizou(tv);
/*
Intent intent = new Intent(context, SpeakOutActivity.class);
intent.putExtra("bookName", bookName);
intent.putStringArrayListExtra("book", result);
context.startActivity(intent);
*/
}
}