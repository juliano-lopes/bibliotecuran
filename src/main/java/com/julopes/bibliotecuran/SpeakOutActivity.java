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
public class SpeakOutActivity extends Activity implements TextToSpeech.OnInitListener {

	private Uri caminhoArmazenar;
	private static final String TALKBACK_SETTING_ACTIVITY_NAME = "com.android.talkback.TalkBackPreferencesActivity";
        private TextToSpeech mTts;
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
		
    private String bookName;
private String book;
private List<String> bookLines;
private List<String> audioBookLines;
private int atualLine;
private int indexCreator;
private static final int QUANTITY_TO_BE_CREATED=5;
private boolean read;
private String url;
private HashMap<String, String> myHashRender;
private boolean bookCreated;
private AudioBookConverter converter;
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
bookName = extras.getString("bookName");
//book = extras.getString("book");
//bookLines = extras.getParcelableArrayList("book");
bookLines = intent.getStringArrayListExtra("book");
audioBookLines = new ArrayList<>();
//converter = new AudioBookConverter(this, bookName, book);
//bookLines = converter.getBookContentWithFormatedLines();
atualLine=0;
indexCreator=0;
bookCreated=false;        
mMediaPlayer = new MediaPlayer();
assets = this.getAssets();
btnSpeak.setText("Ler");
btnSpeak.setEnabled(false);
//btnAvancar.setEnabled(false);
	//btnRetroceder.setEnabled(false);
am = (AudioManager) getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
        
mTts = new TextToSpeech(this, this);
if(bookLines.size()>0){
    //textView.setText(bookLines.get(atualLine));
    //progress.start();
}
else{
	//textView.setText("o texto que veio foi: vazio");

    
}
	
    }
@Override
public void onStart() {
		super.onStart();
        
        
        // Defining click event listener for the button btn_speak
        OnClickListener btnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
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
								pauseMediaPlayer();
								mMediaPlayer.reset();
                                initializeMediaPlayer(avancar());
				playMediaPlayer();
		}
        });
		        btnRetroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
												pauseMediaPlayer();
                                                mMediaPlayer.reset();
																initializeMediaPlayer(retroceder());
				playMediaPlayer();
			}
        });
}

	@Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the TextToSpeech Engine
//        mTts.stop();
        // Shutdown the TextToSpeech Engine
//        mTts.shutdown();
        // Stop the MediaPlayer
        mMediaPlayer.stop();
        // Release the MediaPlayer
        mMediaPlayer.release();
//        progress.interrupt();
		
    }
    Thread progress = new Thread(new Runnable(){
public void run(){
    textView.post(new Runnable(){
    public void run(){
        textView.setText("Criado "+indexCreator+" de "+bookLines.size());
    }
});
}
    });
        
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
					 String tempDestFile = appTmpPath.getAbsolutePath();
String utteranceCode = tempDestFile+"#"+i;
HashMap<String, String> myHashRender = new HashMap();
myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceCode);

int status = mTts.synthesizeToFile(bookLines.get(i), myHashRender, tempDestFile);
if(status==TextToSpeech.SUCCESS){
	//audioBookLines.add(tempDestFile);
}
													

/*
	btnSpeak.post(new Runnable(){
	public void run(){
		btnSpeak.setText("Ler");
	}
});
*/
	}
}
    public void setTts(TextToSpeech tts) {
        this.mTts = tts;
        if( Build.VERSION.SDK_INT  >= 15 ){
            this.mTts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId){
 String[] arUtteranceId = utteranceId.split("#");
String audioPath= arUtteranceId[0];
int index = Integer.parseInt(arUtteranceId[1]);
//int index = Character.getNumericValue(utteranceId.charAt(utteranceId.length()-1));
//String audioPath=utteranceId.substring(0, utteranceId.length()-1);
//if(audioBookLines.contains(audioPath)){
//audioBookLines.set(index, audioPath);
//}else{
    audioBookLines.add(audioPath);
//}
//        btnSpeak.setEnabled(true);                         
//convert(indexCreator++);

if(indexCreator<100){
convert(indexCreator++);
if(indexCreator<50){
    	btnSpeak.setText("Carregando pagina...");

}else{
            am.abandonAudioFocus(afl);
btnSpeak.setText("Ler");
        btnSpeak.setEnabled(true);
        }
}
else{

//textView.setText(audioBookLines.size()+"Linhas de audio\n"+textView.getText().toString());
}
//textView.setText(textView.getText().toString()+"\nCreator "+indexCreator+"\n");
    /*
    textView.post(new Runnable(){
    public void run(){
        textView.setText("Criado "+indexCreator+" de "+bookLines.size());
    }
});
*/
}
                @Override
                public void onError(String utteranceId){
	//textView.setText("o texto do error, id "+utteranceId);
				}
@Override
public void onStop(String utteranceId, boolean interrupted){
if(interrupted){
 String[] arUtteranceId = utteranceId.split("#");
//String audioPath= utteranceId[0];
int index = Integer.parseInt(arUtteranceId[1]);

//int index = Character.getNumericValue(utteranceId.charAt(utteranceId.length()-1));
convert(index);
	//textView.setText(textView.getText().toString()+"\nA linha "+utteranceId+" foi interrompida...\n");
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
            mTts.setPitch(5); // set pitch level
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
convert(indexCreator);
		}
    }

	private boolean success(){
		return mStatus==TextToSpeech.SUCCESS;
	}

}