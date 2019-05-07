package com.julopes.bibliotecuran;
public class Book {
private long id;
private String name;
private String content;
private int mark;
public Book(long id, String name, String content, int mark){
    this.id=id;
    this.name=name;
    this.content = content;
    this.mark = mark;
}
public Book(String name, String content){
    this.name=name;
    this.content = content;
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
}