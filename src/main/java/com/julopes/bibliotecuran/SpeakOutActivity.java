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
import com.julopes.bibliotecuran.Book;
import com.julopes.bibliotecuran.BookFormater;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import android.text.Html;
public class SpeakOutActivity extends Activity implements TextToSpeech.OnInitListener {
private boolean isToSpeak;
	private static final String TALKBACK_SETTING_ACTIVITY_NAME = "com.android.talkback.TalkBackPreferencesActivity";
        private static TextToSpeech mTts;
  private int mStatus = 0;
    private TextView textViewContent;
    private TextView textViewTitle;
	private static Button btnSpeak;
	    private Button btnAvancar;
			    private Button btnRetroceder;
                private static Button btnVoz;
                private static Button btnPage;
                private EditText editPage;
private static Book book;
private BookRepository bookRepo;
   private int voz;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speak_out_activity);
textViewContent = (TextView) findViewById(R.id.content);
textViewTitle = (TextView) findViewById(R.id.title);
		btnSpeak = (Button) findViewById(R.id.btn_speak);
        btnAvancar = (Button) findViewById(R.id.btn_avancar);
		btnRetroceder = (Button) findViewById(R.id.btn_retroceder);
btnVoz = (Button) findViewById(R.id.btn_voz);
btnPage = (Button) findViewById(R.id.btn_page);
editPage = (EditText) findViewById(R.id.edit_page);
Intent intent = getIntent();
Bundle extras = intent.getExtras();
String bookId = extras.getString("insertion");
btnSpeak.setText("Carregar livro");
btnSpeak.setEnabled(false);
btnAvancar.setEnabled(false);
	btnRetroceder.setEnabled(false);
    btnVoz.setEnabled(false);
    btnPage.setEnabled(false);
    isToSpeak=false;
mTts = new TextToSpeech(this,this);
bookRepo = new BookRepository(this);
book=bookRepo.getBookById(Integer.parseInt(bookId));
if(book!=null){
    String title = book.getName().replace('-',' ');
    textViewTitle.setText(Html.fromHtml("<h1>"+title+"</h1>"));
    book.setCurrentLine(book.getMark());
        btnSpeak.setEnabled(true);
}
else{
    Toast.makeText(getApplicationContext(), "Desculpe, ocorreu um erro ao buscar este livro...", Toast.LENGTH_SHORT).show();
}

}
private Voice getTtsVoice(int voz){
     int i=0;
     for(Iterator<Voice> iter = mTts.getVoices().iterator(); 
iter.hasNext();) {
Voice voice = iter.next();
//msg+="Voice "+i+" : "+voice.toString()+"\n";
if(i==voz){
return voice;

}
i++;

}
return null;
}
@Override
public void onStart() {
		super.onStart();
                      OnClickListener btnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                            if(book.getLineQuantity()==0){
                            btnSpeak.setEnabled(false);
    BookFormater bFormater = new BookFormater();
    bFormater.execute(book.getContent());
    return;
                            }
                    if(isToSpeak){
                    isToSpeak=false;
                    btnSpeak.setText("Continuar leitura");
                    }
                                                          else{
                                                              if(book.isEndOfBook())
                    book.setCurrentLine(0);
                                        isToSpeak=true;
                    btnSpeak.setText("Pausar leitura");
                                                          }
                    btnAvancar.setEnabled(true);
	btnRetroceder.setEnabled(true);
                    toSpeak(book.getCurrentLine(),TextToSpeech.QUEUE_ADD);
			}
};
        btnSpeak.setOnClickListener(btnClickListener);
		btnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
book.nextLine();
toNotSpeak();
toSpeak(book.getCurrentLine(),TextToSpeech.QUEUE_ADD);
		}
        });
		        btnRetroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
												book.priorLine();
                                                toNotSpeak();
                                                toSpeak(book.getCurrentLine(),TextToSpeech.QUEUE_ADD);
            }
        });
        		        btnVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
Voice voice = getTtsVoice(voz);
if(voice!=null)
mTts.setVoice(voice);
			btnVoz.setText("Voz "+(voz+1)+": "+mTts.getVoice().getName());
            if((voz>=0)&&(voz<mTts.getVoices().size()-1))
            voz++;
            else
            voz=0;
                        
            }
        });
btnPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
if(!editPage.getText().toString().equals("")){
    int page = Integer.parseInt(editPage.getText().toString());
            if(page>0 && page<=book.getPageQuantity()){
book.setCurrentLine(book.goToPage(page));
toNotSpeak();
toSpeak(book.getCurrentLine(),TextToSpeech.QUEUE_ADD);
            }
            }
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
      book.setMark(book.getCurrentLine());
  bookRepo.saveMark(book);
  }
  
}
public static void setFormatedBookLines(ArrayList<String> formatedLines){
    book.setBookLines(formatedLines);
}
 public static void changeBtnSpeakStatus(){
     btnSpeak.setText("Iniciar leitura");
btnSpeak.setEnabled(true);
btnPage.setEnabled(true);
btnVoz.setEnabled(true);
 }
private void setReadingProgress(){
    String content = "<h2>Linha "+book.getCurrentLine()+" - pagina "+book.getCurrentPage()+" de "+book.getPageQuantity()+"</h2><p align='center'>"+book.getLine(book.getCurrentLine())+"</p>";
    textViewContent.setText(Html.fromHtml(content));
}
private  void endOfBook(){
    if(book.isEndOfBook()){
mTts.speak("Fim do livro",TextToSpeech.QUEUE_ADD,null,"end_of_book#-1");
btnSpeak.setText("Iniciar leitura");
    }
}
private void toSpeak(int i, int flag){
    String code="book_line#"+i;
    if(isToSpeak){
           mTts.speak(book.getLine(i),flag,null,code);
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
    private void setTts(TextToSpeech tts) {
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
toSpeak(book.getCurrentLine(), TextToSpeech.QUEUE_ADD);
break;
default:
//atualLine=++index;
 toSpeak(book.nextLine(),TextToSpeech.QUEUE_ADD);
  break;
}
setReadingProgress();
endOfBook();
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