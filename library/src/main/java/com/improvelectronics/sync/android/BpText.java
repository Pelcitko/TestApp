package com.improvelectronics.sync.android;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LP
 */

public class BpText implements Parcelable {
    private String text           = "";
    private float textX           = 0F;
    private float textY           = 0F;
    private float size           = 0F;

    public BpText(String text, float textX, float textY) {
        this.text = text;
        this.textX = textX;
        this.textY = textY;
    }

    public BpText(String text) {
        this.text = text;
    }

    public BpText() {}

    protected BpText(Parcel in) {
        text = in.readString();
        textX = in.readFloat();
        textY = in.readFloat();
        size = in.readFloat();
    }

    public static final Creator<BpText> CREATOR = new Creator<BpText>() {
        @Override
        public BpText createFromParcel(Parcel in) {
            return new BpText(in);
        }

        @Override
        public BpText[] newArray(int size) {
            return new BpText[size];
        }
    };

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getX() {
        return textX;
    }

    public void setX(float textX) {
        this.textX = textX;
    }

    public float getY() {
        return textY;
    }

    public void setY(float textY) {
        this.textY = textY;
    }

    public void moveTo(float x, float y) {
        this.textX = x;
        this.textY = y;
    }

    public void setFontSize(float size) {
        this.size = size;
    }

    public float getFontSize() {
        return size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(text);
        parcel.writeFloat(textX);
        parcel.writeFloat(textY);
        parcel.writeFloat(size);
    }
}
