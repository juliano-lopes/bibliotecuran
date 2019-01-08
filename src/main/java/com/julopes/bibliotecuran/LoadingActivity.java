package com.julopes.bibliotecuran;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Intent;
import android.widget.ImageView;
import android.os.Handler;

import com.bumptech.glide.*;
public class LoadingActivity extends AppCompatActivity {
private ImageView gif;
@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_loading);
gif = (ImageView) findViewById(R.id.imgInitial);
Glide.with(this).load(R.drawable.img_initial).asGif().into(gif);
               new Handler().postDelayed(new Runnable() {
                      @Override
public void run() {
Intent i = new Intent(LoadingActivity.this, SpeakOutActivity.class);
startActivity(i);
finish();
}
}, 5000);
}
/*
public void iniciar(View v){
Intent intent = new Intent(this, SpeakOutActivity.class);
startActivity(intent);
}
*/

}
