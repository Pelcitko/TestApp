package cz.tul.lp.testapp;

import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by LP
 */

public class Text {
    private String text           = "";
    private Typeface fontFamily   = Typeface.DEFAULT;
    private Paint.Align textAlign = Paint.Align.RIGHT;
    private float fontSize        = 32F;
    private float textX           = 0F;
    private float textY           = 0F;

    public Text(String text, float textX, float textY, Typeface fontFamily, float fontSize, Paint.Align textAlign) {
        this.text = text;
        this.textX = textX;
        this.textY = textY;
        this.fontFamily = fontFamily;
        this.fontSize = fontSize;
        this.textAlign = textAlign;
    }

    public Text(String text) {
        this.text = text;
    }

    public Text() {}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Typeface getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(Typeface fontFamily) {
        this.fontFamily = fontFamily;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public Paint.Align getAlign() {
        return textAlign;
    }

    public void setAlign(Paint.Align textAlign) {
        this.textAlign = textAlign;
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
}
