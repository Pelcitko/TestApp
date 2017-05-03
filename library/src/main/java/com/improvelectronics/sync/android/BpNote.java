package com.improvelectronics.sync.android;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LP
 */

public class BpNote implements Parcelable{

    private int baseColor = Color.WHITE;

    private List<SyncPath> pathLists = new ArrayList<>();
    private List<BpText> textLists   = new ArrayList<>();
    private List<Paint> paintLists   = new ArrayList<>();

    // for Undo, Redo
    private int historyPointer = 0;

    //  CONSTRUCTORS:
    public BpNote() {
    }

    public BpNote(Paint paint, int baseColor) {
        this.baseColor = baseColor;
        this.pathLists.add(new SyncPath());
        this.textLists.add(new BpText());
        this.paintLists.add(paint);
        this.historyPointer++;
    }

    protected BpNote(Parcel in) {
        baseColor = in.readInt();
        pathLists = in.createTypedArrayList(SyncPath.CREATOR);
        textLists = in.createTypedArrayList(BpText.CREATOR);
        historyPointer = in.readInt();
    }



    /**
     * This method updates the lists for the instance of Text, Path and Paint.
     * "Undo" and "Redo" are enabled by this method.
     *
     * @param path the instance of SyncPath
     */
    public void updateHistory(SyncPath path, Paint paint) {
        if (this.historyPointer == this.pathLists.size()) {
            this.pathLists.add(path);
            this.textLists.add(null);
            this.paintLists.add(paint);
            this.historyPointer++;
        } else {
            // On the way of Undo or Redo
            this.pathLists.set(this.historyPointer, path);
            this.textLists.set(this.historyPointer, null);
            this.paintLists.set(this.historyPointer, paint);
            this.historyPointer++;

            for (int i = this.historyPointer, size = this.paintLists.size(); i < size; i++) {
                this.pathLists.remove(this.historyPointer);
                this.textLists.remove(this.historyPointer);
                this.paintLists.remove(this.historyPointer);
            }
        }
    }


    /**
     * This method updates the lists for the instance of Text, Path and Paint.
     * "Undo" and "Redo" are enabled by this method.
     *
     * @param text the instance of BpText
     */
    public void updateHistory(BpText text, Paint paint) {
        if (this.historyPointer == this.pathLists.size()) {
            this.pathLists.add(null);
            this.textLists.add(text);
            this.paintLists.add(paint);
            this.historyPointer++;
        } else {
            // On the way of Undo or Redo
            this.pathLists.set(this.historyPointer, null);
            this.textLists.set(this.historyPointer, text);
            this.paintLists.set(this.historyPointer, paint);
            this.historyPointer++;

            for (int i = this.historyPointer, size = this.paintLists.size(); i < size; i++) {
                this.pathLists.remove(this.historyPointer);
                this.textLists.remove(this.historyPointer);
                this.paintLists.remove(this.historyPointer);
            }
        }
    }

    public int getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(int baseColor) {
        this.baseColor = baseColor;
    }

    public List<SyncPath> getPathLists() {
        return pathLists;
    }

    public void setPathLists(List<SyncPath> pathLists) {
        this.pathLists = pathLists;
    }

    public List<BpText> getTextLists() {
        return textLists;
    }

    public void setTextLists(List<BpText> textLists) {
        this.textLists = textLists;
    }

    public void addTextLists(List<BpText> textLists) {
        this.textLists.addAll(textLists);
    }

    public void addText(BpText text) {
        this.textLists.add(text);
    }

    public void addText(int i, BpText text) {
        this.textLists.add(i, text);
    }

    public void setText(int i, BpText text) {
        this.textLists.set(i, text);
    }

    public List<Paint> getPaintLists() {
        return paintLists;
    }

    public void setPaintLists(List<Paint> paintLists) {
        this.paintLists = paintLists;
    }

    public int getHistoryPointer() {
        return historyPointer;
    }

    public void setHistoryPointer(int historyPointer) {
        this.historyPointer = historyPointer;
    }

    public void incHistoryPointer() {
        this.historyPointer++;
    }

    public void decHistoryPointer() {
        this.historyPointer--;
    }

    public static final Creator<BpNote> CREATOR = new Creator<BpNote>() {
        @Override
        public BpNote createFromParcel(Parcel in) {
            return new BpNote(in);
        }

        @Override
        public BpNote[] newArray(int size) {
            return new BpNote[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(baseColor);
        parcel.writeTypedList(pathLists);
        parcel.writeTypedList(textLists);
        parcel.writeInt(historyPointer);
    }

    /**
     * This method gets the instance of Path that pointer indicates.
     *
     * @return the instance of Path
     */
    public Path getCurrentPath() {
        return this.pathLists.get(this.historyPointer - 1);
    }

    /**
     * This method gets the instance of BpText that pointer indicates.
     *
     * @return the instance of BpText
     */
    public BpText getCurrentText() {
        return this.textLists.get(this.historyPointer - 1);
    }

    public Path getPath(int i) {
        return this.pathLists.get(i);
    }

    public BpText getText(int i) {
        return this.textLists.get(i);
    }

    public Paint getPaint(int i) {
        return this.paintLists.get(i);
    }

    /**
     * This method updates history for Undo.
     *
     * @return If Undo is enabled, this is returned as true. Otherwise, this is returned as false.
     */
    public boolean undo() {
        if (this.historyPointer > 1) {
            this.historyPointer--;
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method updates history for Redo.
     *
     * @return If Redo is enabled, this is returned as true. Otherwise, this is returned as false.
     */
    public boolean redo() {
        if (this.historyPointer < this.pathLists.size()) {
            this.historyPointer++;
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return If Undo is enabled, this is returned as true. Otherwise, this is returned as false.
     */
    public boolean undoable() {
        if (this.historyPointer > 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return If Redo is enabled, this is returned as true. Otherwise, this is returned as false.
     */
    public boolean redoable() {
        if (this.historyPointer < this.pathLists.size()) {
            return true;
        } else {
            return false;
        }
    }

    public void set(SyncPath path, BpText Text, Paint paint) {
        if (this.historyPointer == this.pathLists.size()) {
            this.pathLists.add(path);
            this.textLists.add(null);
            this.paintLists.add(paint);
            this.historyPointer++;
        } else {
            // On the way of Undo or Redo
            this.pathLists.set(this.historyPointer, path);
            this.textLists.set(this.historyPointer, null);
            this.paintLists.set(this.historyPointer, paint);
            this.historyPointer++;

            for (int i = this.historyPointer, size = this.paintLists.size(); i < size; i++) {
                this.pathLists.remove(this.historyPointer);
                this.textLists.remove(this.historyPointer);
                this.paintLists.remove(this.historyPointer);
            }
        }
    }
}
