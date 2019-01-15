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
import android.content.IntentFilter;
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
import com.julopes.bibliotecuran.HttpDownloadUtility;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
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
     private long downloadID;  
   
     private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {  
         @Override  
         public void onReceive(Context context, Intent intent) {  
   
             //Fetching the download id received with the broadcast  
             long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);  
   
             //Checking if the received broadcast is for our enqueued download by matching download id  
             if (downloadID == id) {  
                 Toast.makeText(SpeakOutActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();  
             btnSpeak.setEnabled(true);
			 }  
   
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
book = extras.getString("book");
audioBookLines = new ArrayList<>();
converter = new AudioBookConverter(this, bookName, book);
bookLines = converter.getBookContentWithFormatedLines();
atualLine=0;
indexCreator=0;
bookCreated=false;        
mMediaPlayer = new MediaPlayer();
assets = this.getAssets();
btnSpeak.setText("Ler");
//btnSpeak.setEnabled(false);
//btnAvancar.setEnabled(false);
	//btnRetroceder.setEnabled(false);
am = (AudioManager) getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE);
        
mTts = new TextToSpeech(this, this);
/*
new Thread(){
	@Override
	public void run(){

	String url = "http://www.julianolopes.com.br/documentos/index.php?file=alice-no-pais-das-maravilhas.mp3";
	String folderName="bibliotecuran";
	DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
Uri uri = Uri.parse(url);
DownloadManager.Request request = new DownloadManager.Request(uri);
request.setTitle("My File");
request.setDescription("Downloading");
request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
request.setDestinationUri(Uri.parse("file://" + folderName + "/alice-no-pais-das-maravilhas.mp3"));
downloadmanager.enqueue(request);

	DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
request.setDescription("Baixando seu livro 'alice-no-pais-das-maravilhas.mp3'");
request.setTitle("Download de livro bibliotecuran");
// in order for this if to run, you must use the android 3.2 to compile your app
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
    request.allowScanningByMediaScanner();
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
}
request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "alice-no-pais-das-maravilhas.mp3");

// get download service and enqueue file
DownloadManager manager = (DownloadManager) getSystemService(getApplicationContext().DOWNLOAD_SERVICE);
manager.enqueue(request);

	}
};
*/
if(bookLines.size()>0){
		textView.setText(book);
}
else{
	textView.setText("o texto que veio foi: vazio");
}
         registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));  
btnSpeak.setEnabled(false);
		 beginDownload();  
	}
@Override
public void onStart() {

		// Defining click event listener for the button btn_speak
        OnClickListener btnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
						                 
					if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        playMediaPlayer(1);
                        btnSpeak.setText("Ler");
                    }
					else{
						btnSpeak.setText("Parar Leitura");
								mMediaPlayer.reset();
initializeMediaPlayer(atualLine);
playMediaPlayer(0);
					               }



			}
};
        btnSpeak.setOnClickListener(btnClickListener);
        OnCompletionListener mediaPlayerCompletionListener = new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.reset();
								initializeMediaPlayer(avancar());
				playMediaPlayer(0);
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
                 unregisterReceiver(onDownloadComplete);  
		// Stop the TextToSpeech Engine
        mTts.stop();
         //Shutdown the TextToSpeech Engine
        mTts.shutdown();
        // Stop the MediaPlayer
        mMediaPlayer.stop();
        // Release the MediaPlayer
        mMediaPlayer.release();
		super.onDestroy();
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
if(atualLine<audioBookLines.size()){
		uri = Uri.parse("file://"+audioBookLines.get(audioBookLine));
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.prepare();
        } catch (Exception e) {
		textView.setText("Erro ao tentar preparar o livro. Tente carregar novamente...");
                        btnSpeak.setText("Ler");
atualLine=0;
//            e.printStackTrace();
        }
}
	}else{
//mTts.speak("Erro ao carregar  audio livro...",TextToSpeech.QUEUE_FLUSH,null);
		Toast.makeText(getApplicationContext(),"Erro ao carregar o livro. Por favor, tente novamente...", Toast.LENGTH_SHORT).show();
		textView.setText("Erro ao tentar reproduzir o livro. Nenhuma linha em audio. Tente carregar novamente...");
	}
		
    }
    private void playMediaPlayer(int status){
		textView.setText("Tamanho: "+String.valueOf(mTts.getMaxSpeechInputLength()));
    	int focus_res = am.requestAudioFocus(afl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
        if (focus_res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
convert(++indexCreator);
		}
        // Start Playing
        if(status==0){
            if(atualLine<audioBookLines.size())
			mMediaPlayer.start();
        }
        // Pause Playing
        if(status==1){
            mMediaPlayer.pause();
        }
    }

public void convert(int i){
if(i<bookLines.size()){
			 String musicName = "book_line_"+i+".wav";
					 File appTmpPath = new File(getApplicationContext().getFilesDir(), musicName);
					 String tempDestFile = appTmpPath.getAbsolutePath();
String utteranceCode = tempDestFile+i;
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
int index = Character.getNumericValue(utteranceId.charAt(utteranceId.length()-1));
String audioPath=utteranceId.substring(0, utteranceId.length()-1);
if(!audioBookLines.contains(audioPath))
audioBookLines.add(index, audioPath);
        btnSpeak.setEnabled(true);
		am.abandonAudioFocus(afl);

textView.setText("Tamanho: "+String.valueOf(mTts.getMaxSpeechInputLength()));

/*+". "motor padrao: "+mTts.getDefaultEngine()+". "Voz: "+mTts.getVoice().toString());
*/


//	textView.setText("o texto do done, id "+utteranceId);
                }
                @Override
                public void onError(String utteranceId){
	textView.setText("o texto do error, id "+utteranceId);
				}
@Override
public void onStop(String utteranceId, boolean interrupted){
if(interrupted){
int index = Character.getNumericValue(utteranceId.charAt(utteranceId.length()-1));
convert(index);
	textView.setText("A linha "+utteranceId+" foi interrompida...");
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
            mTts.setPitch(1); // set pitch level
             mTts.setSpeechRate(1); // set speech speed rate
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {
//                btnSpeak.setEnabled(true);
            }
    	int focus_res = am.requestAudioFocus(afl, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
        if (focus_res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
//convert(indexCreator);
		}
//textView.setText("Tamanho: "+String.valueOf(mTts.getMaxSpeechInputLength()));        
		}
        setTts(mTts);
	
	}

	private boolean success(){
		return mStatus==TextToSpeech.SUCCESS;
	}
     private void beginDownload(){  
   
         File file=new File(getExternalFilesDir(null),"AudioBooks");
		 String path = file.getAbsolutePath()+"/alice-no-pais-das-maravilhas.mp3";
         /*  
         Create a DownloadManager.Request with all the information necessary to start the download  
          */  
   
         DownloadManager.Request request=new DownloadManager.Request(Uri.parse("http://www.julianolopes.com.br/documentos/index.php?file=alice-no-pais-das-maravilhas.mp3"))  
                 .setTitle("")// Title of the Download Notification  
                 .setDescription("Downloading")// Description of the Download Notification  
//.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "alice-no-pais-das-maravilhas.mp3")
				                  .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification  
                 .setDestinationUri(Uri.fromFile(new File(path)))// Uri of the destination file  
                 //.setRequiresCharging(false)// Set if charging is required to begin the download  
                 .setAllowedOverMetered(true)// Set if download is allowed on Mobile network  
                 .setAllowedOverRoaming(true);// Set if download is allowed on roaming network  
   
         DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);  
         downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.  
		 textView.setText(path);
audioBookLines.add(path);		 
     }  
}