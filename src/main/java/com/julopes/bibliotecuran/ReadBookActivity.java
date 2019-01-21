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
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
public class ReadBookActivity extends Activity {
    private MediaPlayer mMediaPlayer;
private TextView textView;
	private Button btnRead;
	    private Button btnGoAhead;
			    private Button btnGoBack;
	private String audioBookName;
private String audioBookUrl;
private static final String AUDIO_BOOK_DIRECTORY = "AudioBooks";
   private static final int OFFLINE=0;
      private static final int ONLINE=1;
	        private static final int ONLINE_ASYNC=2;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_book_activity);
		textView  = (TextView) findViewById(R.id.text_view);
		btnRead = (Button) findViewById(R.id.btn_ler_online);
		btnGoAhead = (Button) findViewById(R.id.btn_avancar);
		btnGoBack = (Button) findViewById(R.id.btn_retroceder);
Intent intent = getIntent();
Bundle extras = intent.getExtras();
audioBookName = extras.getString("audio_book_name");
audioBookUrl = extras.getString("audio_book_url");
mMediaPlayer = new MediaPlayer();
btnGoAhead.setEnabled(false);
	btnGoBack.setEnabled(false);
       if(!audioBookExists(getAudioBookPath(audioBookName))){
	   initializeMediaPlayer(audioBookUrl, ONLINE_ASYNC);
	   }
	}
@Override
public void onStart() {
super.onStart();
OnClickListener btnReadClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
				btnGoAhead.setEnabled(true);
			 btnGoBack.setEnabled(true);
			 if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
                        playMediaPlayer(1);
                        btnRead.setText("Ler");
                    }
					else{
		 playMediaPlayer(0);
			btnRead.setText("Parar Leitura");
	
		 					               }
}
};
        btnRead.setOnClickListener(btnReadClickListener);
		
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
}
	break;
	case ONLINE_ASYNC:
		btnRead.setEnabled(false);
		btnRead.setText("Carregando livro...");
						mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(audioBookPath);
			    mMediaPlayer.setOnPreparedListener(new OnPreparedListener(){
@Override
    public void onPrepared(MediaPlayer mp) {
		btnRead.setEnabled(true);
		btnRead.setText("Ler");
	}
});
		mMediaPlayer.prepareAsync();
	} catch (Exception e) {
		textView.setText("Erro ao tentar preparar a leitura online. Tente carregar novamente..."+e.getMessage());
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
private String getAudioBookPath(String audioBookName){
	            File file=new File(getExternalFilesDir(null),AUDIO_BOOK_DIRECTORY);
		String path = file.getAbsolutePath()+"/"+audioBookName;
return path;
	}
		 private boolean audioBookExists(String audioBookPath){
			 return new File(audioBookPath).exists();
		 }
	}