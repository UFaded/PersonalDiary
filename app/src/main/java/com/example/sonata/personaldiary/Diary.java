package com.example.sonata.personaldiary;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Diary implements Serializable {

    //序列化版本号
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String data;
    private String text;
    private String bitmap;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public void setText(String text) {
        this.text = text;
    }
}
