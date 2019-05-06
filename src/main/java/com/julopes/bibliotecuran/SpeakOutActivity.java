package com.julopes.bibliotecuran;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
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
import android.media.*;
import android.content.res.*;
import com.julopes.bibliotecuran.AudioBookConverter;
import com.julopes.bibliotecuran.repository.*;
import com.julopes.bibliotecuran.db.*;
import android.database.sqlite.SQLiteDatabase;  
import android.database.sqlite.SQLiteDatabase.CursorFactory;  
import android.database.sqlite.SQLiteOpenHelper;  
import android.content.ContentValues;
import android.database.Cursor;  
import android.speech.tts.*;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
public class SpeakOutActivity extends Activity implements TextToSpeech.OnInitListener {
private boolean isToSpeak;
	private static final String TALKBACK_SETTING_ACTIVITY_NAME = "com.android.talkback.TalkBackPreferencesActivity";
        private static TextToSpeech mTts;
  private int mStatus = 0;
    private TextView textView;
	private Button btnSpeak;
	    private Button btnAvancar;
			    private Button btnRetroceder;
private static List<String> bookLines;
private List<String> audioBookLines;
private int atualLine;
private AudioBookConverter book;
private BookRepository bookRepo;
   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speak_out_activity);
textView  = (TextView) findViewById(R.id.text_view);
		btnSpeak = (Button) findViewById(R.id.btn_speak);
        btnAvancar = (Button) findViewById(R.id.btn_avancar);
		btnRetroceder = (Button) findViewById(R.id.btn_retroceder);

Intent intent = getIntent();
Bundle extras = intent.getExtras();
String bookName = extras.getString("bookName");
String bookId = extras.getString("insertion");
audioBookLines = new ArrayList<>();
bookLines = new ArrayList<>();
btnSpeak.setText("Iniciar leitura");
btnSpeak.setEnabled(false);
btnAvancar.setEnabled(false);
	btnRetroceder.setEnabled(false);
    isToSpeak=false;
mTts = new TextToSpeech(this,this);
bookRepo = new BookRepository(this);
book=bookRepo.getAudioBookConverterById(Integer.parseInt(bookId));
if(book!=null){
    atualLine=book.getMark();
        book.setTextView(textView);
    book.setButton(btnSpeak);
    book.execute();
}
else{
    Toast.makeText(getApplicationContext(), "Desculpe, ocorreu um erro ao buscar este livro...", Toast.LENGTH_SHORT).show();
}

}
@Override
public void onStart() {
		super.onStart();
                      OnClickListener btnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(isToSpeak){
                    isToSpeak=false;
                    btnSpeak.setText("Continuar leitura");
                    }
                                                          else{
                    isToSpeak=true;
                    btnSpeak.setText("Pausar leitura");
                                                          }
                    btnAvancar.setEnabled(true);
	btnRetroceder.setEnabled(true);
                    toSpeak(atualLine,TextToSpeech.QUEUE_ADD);
			}
};
        btnSpeak.setOnClickListener(btnClickListener);
		btnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
if(atualLine<(bookLines.size()-2))
atualLine++;
toNotSpeak();
toSpeak(atualLine,TextToSpeech.QUEUE_ADD);
		}
        });
		        btnRetroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
												if(atualLine>1)
                                                atualLine-=2;
                                                toNotSpeak();
                                                toSpeak(atualLine,TextToSpeech.QUEUE_ADD);
            }
        });
}
@Override
protected void onStop(){
    super.onStop();
    closeResourceAndSaveData();
}
	@Override
    public void onDestroy() {
        super.onDestroy();
closeResourceAndSaveData();
  }
private void closeResourceAndSaveData(){
          mTts.stop();
        mTts.shutdown();
  if(book!=null){
      book.setMark(atualLine);
  bookRepo.saveMark(book);
  }
  
}
public static void setFormatedBookLines(ArrayList<String> formatedLines){
    bookLines=formatedLines;
}
        
public void toSpeak(int i, int flag){
    String code="book_line#"+i;
    if(isToSpeak){
       if((i>=0)&&(i<bookLines.size()))
    mTts.speak(bookLines.get(i),flag,null,code);
    }
    else{
        toNotSpeak();
    }
}    
    private void toNotSpeak(){
        mTts.stop();
    }
    private void doSilence(){
mTts.playSilentUtterance(0500,TextToSpeech.QUEUE_FLUSH,"code_to_not_speak#-2");
    }
    public void setTts(TextToSpeech tts) {
        this.mTts = tts;
        if( Build.VERSION.SDK_INT  >= 15 ){
            this.mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId){
  String[] arUtteranceId = utteranceId.split("#");
int index = Integer.parseInt(arUtteranceId[1]);
switch(index){
case -1:
break;
case -2:
toSpeak(atualLine, TextToSpeech.QUEUE_ADD);
break;
default:
atualLine=++index;
 toSpeak(atualLine,TextToSpeech.QUEUE_ADD);
 break;
}

}
                @Override
                public void onError(String utteranceId){
doSilence();
    				}
@Override
public void onStop(String utteranceId, boolean interrupted){
doSilence();
}
                @Override
                public void onStart(String utteranceId){
                }
            });
        }else{
            this.mTts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
                @Override
                public void onUtteranceCompleted(String utteranceId) {
                  	}
            });
        }
    }

	    @Override
    public void onInit(int status) {
        mStatus = status;
		if(success()){
			    int result = mTts.setLanguage(Locale.getDefault());
            mTts.setPitch(0); // set pitch level
             mTts.setSpeechRate(0); // set speech speed rate
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {

            }
        
		}
        setTts(mTts);
	}

	private boolean success(){
		return mStatus==TextToSpeech.SUCCESS;
	}

}