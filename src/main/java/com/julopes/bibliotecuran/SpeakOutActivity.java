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
	private Uri caminhoArmazenar;
	private static final String TALKBACK_SETTING_ACTIVITY_NAME = "com.android.talkback.TalkBackPreferencesActivity";
        private static TextToSpeech mTts;
        
  private int mStatus = 0;
    private MediaPlayer mMediaPlayer;
AssetManager assets;
AssetFileDescriptor fd;
    private boolean mProcessed = false;
    private final String FILENAME = "wpta_tts.wav";
    
	private TextView textView;
	private Button btnSpeak;
	    private Button btnAvancar;
			    private Button btnRetroceder;
		
private static List<String> bookLines;
private List<String> audioBookLines;
private int atualLine;
private static int indexCreator;
private static final int QUANTITY_TO_BE_CREATED=5;
private boolean read;
private String url;
private HashMap<String, String> myHashRender;
private boolean bookCreated;
private AudioBookConverter book;
private BookRepository bookRepo;
private AudioManager am;
 private final AudioManager.OnAudioFocusChangeListener afl = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            // TODO React to audio-focus changes here!
        }
    };
   
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
//bookLines = extras.getParcelableArrayList("book");

audioBookLines = new ArrayList<>();
bookLines = new ArrayList<>();
//bookLines = intent.getStringArrayListExtra("bookLines");
atualLine=0;
indexCreator=0;
bookCreated=false;        
mMediaPlayer = new MediaPlayer();
assets = this.getAssets();
btnSpeak.setText("Falar");
btnSpeak.setEnabled(false);
btnAvancar.setEnabled(false);
	btnRetroceder.setEnabled(false);
    isToSpeak=false;
am = (AudioManager) getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
      
mTts = new TextToSpeech(this,this);

BookRepository bookRepo = new BookRepository(this);
book=bookRepo.getAudioBookConverterById(Integer.parseInt(bookId));
if(book!=null){
    book.setTextView(textView);
    book.setButton(btnSpeak);
    book.execute();
}
else{
    Toast.makeText(getApplicationContext(), "Desculpe, ocorreu um erro ao buscar este livro...", Toast.LENGTH_SHORT).show();
}

}
private String obterDiretorio()
{
    File root = android.os.Environment.getExternalStorageDirectory();
    return root.toString();
}
public ArrayList<String> listar()
{  
   ArrayList<String> minhaLista = new ArrayList<String>();
   File diretorio = new File(obterDiretorio()+"/WhatsApp/Media/WhatsApp Documents"); 
   File[] arquivos = diretorio.listFiles();    
if(arquivos != null)
   { 
      int length = arquivos.length; 
      for(int i = 0; i < length; ++i)
      { 
          File f = arquivos[i]; 
          if(f.isFile())
          {
              minhaLista.add(f.getName());
          } 
      }
       }   
return minhaLista;
}
public static void setFormatedBookLines(ArrayList<String> formatedLines){
    bookLines=formatedLines;
}
@Override
public void onStart() {
		super.onStart();
        
        
        // Defining click event listener for the button btn_speak
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
                    toSpeak(atualLine,TextToSpeech.QUEUE_FLUSH);
                    /*
                    if(!bookCreated){
                        convert(indexCreator);
                        return;
                    }
                    
                    if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        pauseMediaPlayer();
                        btnSpeak.setText("Ler");
                    }
					else{
						btnSpeak.setText("Parar Leitura");
if(mMediaPlayer.getCurrentPosition()>0){
playMediaPlayer();    
}
else{
    initializeMediaPlayer(atualLine);
    playMediaPlayer();    
}
}
*/

			}
};
        btnSpeak.setOnClickListener(btnClickListener);
        OnCompletionListener mediaPlayerCompletionListener = new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                                                                                            mMediaPlayer.reset();
                                initializeMediaPlayer(avancar());
				playMediaPlayer();
}
        };
mMediaPlayer.setOnCompletionListener(mediaPlayerCompletionListener);	
		
		        btnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
if(atualLine<(bookLines.size()-2))
atualLine++;
toNotSpeak();
toSpeak(atualLine,TextToSpeech.QUEUE_FLUSH);
								/*
                                pauseMediaPlayer();
								mMediaPlayer.reset();
                                initializeMediaPlayer(avancar());
				playMediaPlayer();
		*/
        }
        });
		        btnRetroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
												if(atualLine>1)
                                                atualLine-=2;
                                                toNotSpeak();
                                                toSpeak(atualLine,TextToSpeech.QUEUE_ADD);
                                                /*
                                                pauseMediaPlayer();
                                                mMediaPlayer.reset();
																initializeMediaPlayer(retroceder());
				playMediaPlayer();
                */
			}
        });
}

	@Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the TextToSpeech Engine
        mTts.stop();
         //Shutdown the TextToSpeech Engine
        mTts.shutdown();
        // Stop the MediaPlayer
        mMediaPlayer.stop();
        // Release the MediaPlayer
        mMediaPlayer.release();
  }
        
private int avancar(){
		if(atualLine<audioBookLines.size()){
atualLine++;
		}
		return atualLine;
		}
	private int retroceder(){
		if(atualLine>0){
			atualLine--;
		}
		return atualLine;
	}

    private void initializeMediaPlayer(int audioBookLine){
 	Uri uri;
	if(audioBookLines.size()>0){
if(audioBookLine<audioBookLines.size()){
		uri = Uri.parse("file://"+audioBookLines.get(audioBookLine));
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.prepare();
        } catch (Exception e) {
		textView.setText("Erro ao tentar preparar o livro. Tente carregar novamente... "+e.getMessage()+" Linha "+audioBookLine+"\n"+audioBookLines.get(audioBookLine)+"\n"+bookLines.get(audioBookLine));
                        btnSpeak.setText("Ler");

//            e.printStackTrace();
        }
}
	}else{
//mTts.speak("Erro ao carregar  audio livro...",TextToSpeech.QUEUE_FLUSH,null);
		//Toast.makeText(getApplicationContext(),"Erro ao carregar o livro. Por favor, tente novamente...", Toast.LENGTH_SHORT).show();
		textView.setText("Erro ao tentar reproduzir o livro. Nenhuma linha em audio. Tente carregar novamente...");
	}
		
    }
    private void playMediaPlayer(){
        mMediaPlayer.start();
    }
private void pauseMediaPlayer(){
    mMediaPlayer.pause();
}
public void convert(int i){
if(i<bookLines.size()){
			 String musicName = "book_line_"+i+".wav";
			 File appTmpPath = new File(getApplicationContext().getFilesDir(), musicName);
//File appTmpPath = new File(filesDir, musicName);
					 String tempDestFile = appTmpPath.getAbsolutePath();
String utteranceCode = tempDestFile+"#"+i;
HashMap<String, String> myHashRender = new HashMap();
myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceCode);
mTts.synthesizeToFile(bookLines.get(i), myHashRender, tempDestFile);
	}
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
 /*
 String[] arUtteranceId = utteranceId.split("#");
String audioPath= arUtteranceId[0];
int index = Integer.parseInt(arUtteranceId[1]);
    audioBookLines.add(audioPath);

if(indexCreator<100){
convert(indexCreator++);
if(indexCreator<25){
    	btnSpeak.setEnabled(false);
        btnSpeak.setText("Ler");
}else {
                              btnSpeak.setEnabled(true);
                  btnRetroceder.setEnabled(true);
btnAvancar.setEnabled(true);
            bookCreated=true;
            
            am.abandonAudioFocus(afl);

        }
}
else{

//textView.setText(audioBookLines.size()+"Linhas de audio\n"+textView.getText().toString());
}
*/
}
                @Override
                public void onError(String utteranceId){
	toSpeak(atualLine,TextToSpeech.QUEUE_ADD);
    //textView.setText("o texto do error, id "+utteranceId);
				}
@Override
public void onStop(String utteranceId, boolean interrupted){
//String[] arUtteranceId = utteranceId.split("#");
//int index = Integer.parseInt(arUtteranceId[1]);
doSilence();
//toSpeak(index,TextToSpeech.QUEUE_ADD);
if(interrupted){
 


//toNotSpeak();

 /*
 String[] arUtteranceId = utteranceId.split("#");
//String audioPath= utteranceId[0];
int index = Integer.parseInt(arUtteranceId[1]);

//int index = Character.getNumericValue(utteranceId.charAt(utteranceId.length()-1));
convert(index);
	//textView.setText(textView.getText().toString()+"\nA linha "+utteranceId+" foi interrompida...\n");
*/
}
}
                @Override
                public void onStart(String utteranceId){
                }
            });
        }else{
            this.mTts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
                @Override
                public void onUtteranceCompleted(String utteranceId) {
                    // Speech file is created
	textView.setText("o texto do completed, id "+utteranceId);
					
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
//                btnSpeak.setEnabled(true);
            }
        
		}
        setTts(mTts);
		int focus_res = am.requestAudioFocus(afl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
        if (focus_res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
//convert(indexCreator);
		}
    }

	private boolean success(){
		return mStatus==TextToSpeech.SUCCESS;
	}

}