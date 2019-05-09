package com.julopes.bibliotecuran;
 import java.util.Arrays;
 import java.util.List;
 import java.util.ArrayList;

public class Book {
private static final int MAX_LINE_PER_PAGE=30;
private static final int PAGE_MIN_QUANTITY=1;
private long id;
private String name;
private String content;
private int mark;
private int currentLine;
private List<String> bookLines;

public Book(long id, String name, String content, int mark){
    this.id=id;
    this.name=name;
    this.content = content;
    this.mark = mark;
    bookLines = new ArrayList<String>();
}
public Book(String name, String content){
    this.name=name;
    this.content = content;
bookLines = new ArrayList<String>();
}
public long getId(){
    return id;
}
public String getName(){
    return name;
}
public String getContent(){
    return content;
}
public int getMark(){
    return mark;
}
public void setMark(int mark){
    this.mark = mark;
}
public int getCurrentLine(){
    return currentLine;
}
public void setCurrentLine(int currentLine){
    this.currentLine=currentLine;
}
public int nextLine(){
    if(currentLine<bookLines.size())
    ++currentLine;
    return currentLine;
}
public int priorLine(){
    if(currentLine>0)
--currentLine;
    return currentLine;
    }
public void setBookLines(List<String> bookLines){
    this.bookLines=bookLines;
}
public int getLineQuantity(){
    return bookLines.size();
}
public String getLine(int line){
    if((line>=0)&&(line<getLineQuantity()) && (getLineQuantity()>0))
return bookLines.get(line);
else
return "";
}
public int getPageQuantity(){
    if(bookLines.isEmpty())
    return 0;
    if(bookLines.size()%MAX_LINE_PER_PAGE==0)
    return bookLines.size()/MAX_LINE_PER_PAGE;
    else
    return bookLines.size()/MAX_LINE_PER_PAGE+1;
}
public int getCurrentPage(){
int currentPage;
if(currentLine%MAX_LINE_PER_PAGE==0)
currentPage=currentLine/MAX_LINE_PER_PAGE;
else
currentPage=currentLine/MAX_LINE_PER_PAGE+1;
if(currentPage>PAGE_MIN_QUANTITY)
return currentPage;
else
return PAGE_MIN_QUANTITY;
}
public int goToPage(int pageNumber){
    currentLine = (pageNumber-1)*30+1;
    return currentLine;
}
public boolean isEndOfBook(){
    return currentLine>=getLineQuantity();
}
}

