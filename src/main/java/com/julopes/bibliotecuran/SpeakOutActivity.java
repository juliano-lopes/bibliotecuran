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
public class SpeakOutActivity extends Activity implements TextToSpeech.OnInitListener {

	private Uri caminhoArmazenar;
	private static final String TALKBACK_SETTING_ACTIVITY_NAME = "com.android.talkback.TalkBackPreferencesActivity";
        private TextToSpeech mTts;
    private int mStatus = 0;
    private MediaPlayer mMediaPlayer;
    private boolean mProcessed = false;
    private final String FILENAME = "wpta_tts.wav";
private String tempDestFile="";
    
	private TextView textView;
	private Button btnSpeak;
	    private Button btnAvancar;
			    private Button btnRetroceder;
		
    private String bookName;
private String book;
private List<String> bookLines;
private List<String> bookAudioLines;
private int atualLine;
private int indexCreator;
private static final int QUANTITY_TO_BE_CREATED=5;
private boolean read;
private String url;
private HashMap<String, String> myHashRender;
private boolean bookCreated;
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
bookName = extras.getString("bookName");
book = extras.getString("book");
bookLines = Arrays.asList(book.split("\n"));
bookAudioLines = new ArrayList<>();
atualLine=0;
indexCreator=0;
bookCreated=false;        
mTts = new TextToSpeech(this, this);
mMediaPlayer = new MediaPlayer();
btnSpeak.setText("Carregar Livro");
//btnAvancar.setEnabled(false);
	//btnRetroceder.setEnabled(false);
			
	}
@Override
public void onStart() {
		// Defining click event listener for the button btn_speak
        OnClickListener btnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(success()){
                if(!bookCreated){
createAudioBook.start();
				return;
				}				
						
                    if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        playMediaPlayer(1);
                        btnSpeak.setText("Ler");
                        return;
                    }
					else{
						btnSpeak.setText("Parar Leitura");
						initializeMediaPlayer(atualLine);
				playMediaPlayer(0);
					}
                }
			}
};
        btnSpeak.setOnClickListener(btnClickListener);
        OnCompletionListener mediaPlayerCompletionListener = new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                mMediaPlayer.reset();
//								initializeMediaPlayer(avancar());
//				playMediaPlayer(0);
String msg="O tamanho da linha: "+bookLines.get(atualLine).length();
		mTts.speak(msg,TextToSpeech.QUEUE_FLUSH,null);
                            }
        };
mMediaPlayer.setOnCompletionListener(mediaPlayerCompletionListener);	
		
		        btnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
								playMediaPlayer(1);
mMediaPlayer.reset();
								initializeMediaPlayer(avancar());
				playMediaPlayer(0);
		}
        });
		        btnRetroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
												playMediaPlayer(1);
								mMediaPlayer.reset();
								initializeMediaPlayer(retroceder());
				playMediaPlayer(0);
			}
        });
      
super.onStart();
        


}

	@Override
    public void onDestroy() {
        // Stop the TextToSpeech Engine
        mTts.stop();
        // Shutdown the TextToSpeech Engine
        mTts.shutdown();
        // Stop the MediaPlayer
        mMediaPlayer.stop();
        // Release the MediaPlayer
        mMediaPlayer.release();
		if(createAudioBook.isAlive()){
createAudioBook.interrupt();
		}
		super.onDestroy();
    }
    private int avancar(){
		if(atualLine<bookAudioLines.size()){
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
    public void setTts(TextToSpeech tts) {
        this.mTts = tts;
        if( Build.VERSION.SDK_INT  >= 15 ){
            this.mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId){
                }
                @Override
                public void onError(String utteranceId){
					
				}
@Override
public void onStop(String utteranceId, boolean interrupted){
                
	
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
					
                }
            });
        }
    }
	    @Override
    public void onInit(int status) {
        mStatus = status;
		if(success()){
			    int result = mTts.setLanguage(Locale.getDefault());
            mTts.setPitch(5); // set pitch level
             mTts.setSpeechRate(0); // set speech speed rate
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
				btnSpeak.setEnabled(false);
            } else {
                btnSpeak.setEnabled(true);
            }
        
		}
        setTts(mTts);
    }
    private void initializeMediaPlayer(int bookAudioLine){
 	Uri uri;
	if(bookAudioLines.size()>0){
if(atualLine==bookAudioLines.size()){	
btnSpeak.setText("Ler");
}
else{
		uri = Uri.parse("file://"+bookAudioLines.get(bookAudioLine));

		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.prepare();
			//Toast.makeText(getApplicationContext(),"aqui fica a preparacao doplayer", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
		//Toast.makeText(getApplicationContext(),"aqui fica o erro da preparacao do player", Toast.LENGTH_SHORT).show();
		mTts.speak(e.getMessage(),TextToSpeech.QUEUE_FLUSH,null);
		textView.setText(e.getMessage());
            e.printStackTrace();
        }
}
	}else{
		mTts.speak("Erro ao carregar  audio livro...",TextToSpeech.QUEUE_FLUSH,null);
	}
		
    }
    private void playMediaPlayer(int status){
        // Start Playing
        if(status==0){
            if(atualLine<bookAudioLines.size())
			mMediaPlayer.start();
        }
        // Pause Playing
        if(status==1){
            mMediaPlayer.pause();
        }
    }
	private boolean success(){
		return mStatus==TextToSpeech.SUCCESS;
	}

private Thread   createAudioBook = new Thread(){ 
	@Override
	public void run(){
															for(int i=0;i<5;i++){
																if(i==bookLines.size()){
		mTts.speak("Fim do audio livro...",TextToSpeech.QUEUE_FLUSH,null);
																	return;
																}
													String utteranceCode = "tts_sound"+i;
myHashRender = new HashMap();
myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceCode);
					 String musicName = "book_line_"+i+".wav";
					 File appTmpPath = new File(getApplicationContext().getFilesDir(), musicName);
					 String tempDestFile2 = appTmpPath.getAbsolutePath();
				 
int status = mTts.synthesizeToFile(bookLines.get(i), myHashRender, tempDestFile2);

if(status==TextToSpeech.SUCCESS){
	tempDestFile+="linha "+i+" criada. caminho: "+tempDestFile2+"\n";
	//tempDestFile=tempDestFile2;
	bookAudioLines.add(tempDestFile2);
}
else{
	tempDestFile+="linha "+i+" nao criada.\n\n"; 
}
													}


	bookCreated=true;
	if(bookCreated){
/*
	btnSpeak.post(new Runnable(){
	public void run(){
		btnSpeak.setText("Ler");
	}
});
*/
	//btnSpeak.setText("Ler");
	
//textView.setText(tempDestFile);

	}

	}
	
};


	
	
}