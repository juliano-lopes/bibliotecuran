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
import android.media.MediaPlayer.OnPreparedListener;
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
public class SpeakOutActivity extends Activity {
    private MediaPlayer mMediaPlayer;
private TextView textView;
	private Button btnLerOnline;
	private Button btnSpeak;
	    private Button btnGoAhead;
			    private Button btnGoBack;
	private String audioBookName;
private String audioBookUrl;
     private long downloadID;  
   private String path;
   private static final int OFFLINE=0;
      private static final int ONLINE=1;
	        private static final int ONLINE_ASYNC=2;
	private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {  
         @Override  
         public void onReceive(Context context, Intent intent) {  
   
             //Fetching the download id received with the broadcast  
             long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);  
   
             //Checking if the received broadcast is for our enqueued download by matching download id  
             if (downloadID == id) {  
                 Toast.makeText(SpeakOutActivity.this, "Livro baixado com sucesso", Toast.LENGTH_SHORT).show();  
             			initializeMediaPlayer(getAudioBookPath(audioBookName), OFFLINE);
			 btnSpeak.setEnabled(true);
			 btnSpeak.setText("Ler Offline");
			 textView.setText("");
			 }  
   
         }  
     };     
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speak_out_activity);
		textView  = (TextView) findViewById(R.id.text_view);
		btnLerOnline = (Button) findViewById(R.id.btn_ler_online);
		btnSpeak = (Button) findViewById(R.id.btn_speak);
        btnGoAhead = (Button) findViewById(R.id.btn_avancar);
		btnGoBack = (Button) findViewById(R.id.btn_retroceder);
Intent intent = getIntent();
Bundle extras = intent.getExtras();
audioBookName = extras.getString("bookName");
audioBookUrl = extras.getString("book");
mMediaPlayer = new MediaPlayer();
btnSpeak.setText("Baixar e Ler Offline");
btnGoAhead.setEnabled(false);
	btnGoBack.setEnabled(false);
       registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	   if(!audioBookExists(getAudioBookPath(audioBookName))){
	   initializeMediaPlayer(audioBookUrl, ONLINE_ASYNC);
	   }
		 	if(audioBookExists(getAudioBookPath(audioBookName))){
			initializeMediaPlayer(getAudioBookPath(audioBookName), OFFLINE);
btnSpeak.setText("Ler Offline");
btnLerOnline.setEnabled(false);
			}
	}
@Override
public void onStart() {
super.onStart();
OnClickListener btnLerOnlineClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
				btnGoAhead.setEnabled(true);
			 btnGoBack.setEnabled(true);
			 if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        playMediaPlayer(1);
                        btnLerOnline.setText("Ler Online");
                    }
					else{
		 playMediaPlayer(0);
			btnLerOnline.setText("Parar Leitura Online");
	
		 					               }
}
};
        btnLerOnline.setOnClickListener(btnLerOnlineClickListener);
		
		OnClickListener btnSpeakClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
if(!audioBookExists(getAudioBookPath(audioBookName))){
			beginDownload(audioBookName, audioBookUrl);
			return;
}
			 				btnGoAhead.setEnabled(true);
			 btnGoBack.setEnabled(true);
			 
			 if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        playMediaPlayer(1);
                        btnSpeak.setText("Ler Offline");
                    }
					else{
												playMediaPlayer(0);
												btnSpeak.setText("Parar Leitura");
					               }
}
};
        btnSpeak.setOnClickListener(btnSpeakClickListener);
		OnClickListener btnGoAheadClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
		if((mMediaPlayer.getCurrentPosition()-5000)<mMediaPlayer.getDuration())
			mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition()+5000);
		else
			mMediaPlayer.seekTo(mMediaPlayer.getDuration());
}
};
        btnGoAhead.setOnClickListener(btnGoAheadClickListener);
		OnClickListener btnGoBackClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
if((mMediaPlayer.getCurrentPosition()-5000)>0)
			mMediaPlayer.seekTo(mMediaPlayer.getCurrentPosition()-5000);
		else
			mMediaPlayer.seekTo(0);
}
};
        btnGoBack.setOnClickListener(btnGoBackClickListener);

        OnCompletionListener mediaPlayerCompletionListener = new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                            }
        };
mMediaPlayer.setOnCompletionListener(mediaPlayerCompletionListener);	
}

	@Override
    public void onDestroy() {
                 super.onDestroy();
				 unregisterReceiver(onDownloadComplete);  
		mMediaPlayer.stop();
        mMediaPlayer.release();
	}

    private void initializeMediaPlayer(String audioBookPath, int REPRODUTION_MODE){
 	switch(REPRODUTION_MODE){
	case OFFLINE:
	Uri uri = Uri.parse("file://"+audioBookPath);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
            mMediaPlayer.prepare();
        } catch (Exception e) {
		textView.setText("Erro ao tentar preparar o livro. Tente carregar novamente..."+e.getMessage());
                        btnSpeak.setText("Ler");
//            e.printStackTrace();
}
	break;
	case ONLINE_ASYNC:
		btnLerOnline.setEnabled(false);
		
						mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(audioBookPath);
			    mMediaPlayer.setOnPreparedListener(new OnPreparedListener(){
@Override
    public void onPrepared(MediaPlayer mp) {
		btnLerOnline.setEnabled(true);
	}
});
		mMediaPlayer.prepareAsync();
	} catch (Exception e) {
		textView.setText("Erro ao tentar preparar a leitura online. Tente carregar novamente..."+e.getMessage());
                        btnSpeak.setText("Ler");
//            e.printStackTrace();
}
        break;
	}
    }
    
	
	private void playMediaPlayer(int status){
		if(status==0){
            mMediaPlayer.start();
        }
        if(status==1){
            mMediaPlayer.pause();
        }
    }
private void beginDownload(String audioBookName, String audioBookUrl){  
		 if(!audioBookExists(getAudioBookPath(audioBookName))){
         btnSpeak.setEnabled(false);
		 btnSpeak.setText("Baixando...");
		 textView.setText("Baixando livro, por favor aguarde...");
		 /*  
         Create a DownloadManager.Request with all the information necessary to start the download  
          */  
   
         DownloadManager.Request request=new DownloadManager.Request(Uri.parse(audioBookUrl))  
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

		 }
	}  
private String getAudioBookPath(String audioBookName){
	            File file=new File(getExternalFilesDir(null),"AudioBooks");
		path = file.getAbsolutePath()+"/"+audioBookName;
return path;
	}
		 private boolean audioBookExists(String audioBookPath){
			 return new File(audioBookPath).exists();
		 }
	}