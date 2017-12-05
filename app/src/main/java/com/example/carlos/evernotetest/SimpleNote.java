package com.example.carlos.evernotetest;

import android.graphics.Bitmap;

/**
 * Created by carlos on 5/12/17.
 */

public class SimpleNote {
    String title;
    String content;
    Bitmap image;
    String imageOcr;
    private byte[] imageData;

    public SimpleNote(String title, String content, Bitmap image, String imageOcr) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.imageOcr = imageOcr;
    }

    public SimpleNote() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageOcr() {
        return imageOcr;
    }

    public void setImageOcr(String imageOcr) {
        this.imageOcr = imageOcr;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
    public byte[] getImageData() {
        return imageData;
    }
}
