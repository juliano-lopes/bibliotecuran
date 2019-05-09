package com.julopes.bibliotecuran;
import java.util.Arrays;
 import java.util.List;
 import java.util.ArrayList;
import android.os.AsyncTask;  
import android.widget.Button;
import android.widget.TextView;

public class BookFormater extends AsyncTask<String, String, ArrayList<String>>{
    private static final int LINE_MIN_LENGTH =30;

private String getContentWithoutSomeCharacters(String content){
		content = content.replace('_', ' ');
	content = content.replace('\n', ' ');
	content = content.replace('=', ' ');
return content;
}

private ArrayList<String>   getFormatedContent(String content){
int mark;
String line;
ArrayList<String> formedLines = new ArrayList<>();
content=getContentWithoutSomeCharacters(content);
while(true){
if(content.indexOf(".")>-1){
mark = content.indexOf(".")+1;
line=content.substring(0,mark);
if(line.indexOf("!")>-1){
mark = line.indexOf(".")+1;
line=content.substring(0,mark);
}
if(line.indexOf("?")>-1){
	mark = content.indexOf("?")+1;
line=content.substring(0,mark);
}
content = content.substring(mark);
formedLines.add(line);
}
else if(content.indexOf("!")>-1){
mark=content.indexOf("!")+1;
line=content.substring(0,mark);
if(line.indexOf("?")>-1){
	mark=line.indexOf("?")+1;
	line=line.substring(0,mark);
}
content = content.substring(mark);
formedLines.add(line);
}
else if(content.indexOf("?")>-1){
	mark=content.indexOf("?")+1;
	line=content.substring(0,mark);
content = content.substring(mark);
formedLines.add(line);
}
else{
formedLines.add(content);
break;
}
}
return formedLines;
}

@Override
protected ArrayList<String> doInBackground(String... data) {
		String bookContent=data[0];
return getFormatedContent(bookContent);
}
@Override
protected void onProgressUpdate(String... values) {
}
protected void onPostExecute(ArrayList<String> result) {
SpeakOutActivity.setFormatedBookLines(result);
SpeakOutActivity.changeBtnSpeakStatus();
}


    }
    