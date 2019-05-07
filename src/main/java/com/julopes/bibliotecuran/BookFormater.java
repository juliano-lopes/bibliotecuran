package com.julopes.bibliotecuran;
import java.util.Arrays;
 import java.util.List;
 import java.util.ArrayList;
import android.os.AsyncTask;  
import android.widget.Button;
import android.widget.TextView;

public class BookFormater extends AsyncTask<String, String, ArrayList<String>>{
    private static final int LINE_LENGTH=30;
private List<String> getWords(String bookContent){
return Arrays.asList(bookContent.split(" "));
}
private boolean isPontuation(char character){
	return ((character=='.') || (character=='!') || (character=='?'));
	}

private String getLineWithoutSomeCharacters(String line){
		line = line.replace('_', ' ');
	line = line.replace('\n', ' ');
	line = line.replace('=', ' ');
return line;
}

@Override
protected ArrayList<String> doInBackground(String... data) {
String line="";
int i=0;
ArrayList<String> formedLines = new ArrayList<>();
String bookContent=data[0];
for(String word : getWords(bookContent)){
if(line.length()<LINE_LENGTH){
line+=word+" ";
}
else{
	line+=word+" ";
	char lastCharacter = word.charAt(word.length()-1);
	if((isPontuation(lastCharacter)) && (word.length()>3)){
line = getLineWithoutSomeCharacters(line);
formedLines.add(line);
line="";

}
}
i++;
//int progress =(int) ((i*100)/getWords(bookContent).size());
//publishProgress(progress+"%");
}

return formedLines;

}
@Override
protected void onProgressUpdate(String... values) {
}
protected void onPostExecute(ArrayList<String> result) {
SpeakOutActivity.setFormatedBookLines(result);
SpeakOutActivity.changeBtnSpeakStatus();
/*
Intent intent = new Intent(context, SpeakOutActivity.class);
intent.putExtra("bookName", bookName);
intent.putStringArrayListExtra("book", result);
context.startActivity(intent);
*/
}


    }
    