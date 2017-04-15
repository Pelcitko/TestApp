package cz.tul.lp.testapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LP
 */

public class Notes {

    protected static final String DATABASE_NAME = "notepad";
    protected static final int DATABASE_VERSION = 2;

    protected static final String TB_NAME = "notes";

    // Speciální hodnota "_id", pro jednodušší použití SimpleCursorAdapteru
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_NOTE = "note";

    public static final String[] columns = { COLUMN_ID, COLUMN_NOTE,
            COLUMN_TITLE };

    protected static final String ORDER_BY = COLUMN_ID + " DESC";

    private SQLiteOpenHelper openHelper;

    static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TB_NAME + " (" + COLUMN_ID
                    + " INTEGER PRIMARY KEY," + COLUMN_TITLE
                    + " TEXT NOT NULL," + COLUMN_NOTE + " TEXT NOT NULL" + ");");
        }

        /*
         * Ve skutečnosti je potřeba, abychom uživatelům nemazali data, vytvořit
         * pro každou změnu struktury databáze nějaký upgradovací nedestruktivní
         * SQL příkaz.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    public Notes(Context ctx) {
        openHelper = new DatabaseHelper(ctx);
    }

    public Cursor getNotes() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db.query(TB_NAME, columns, null, null, null, null, ORDER_BY);
    }

    public Cursor getNote(long id) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        String[] selectionArgs = { String.valueOf(id) };
        return db.query(TB_NAME, columns, COLUMN_ID + "= ?", selectionArgs,
                null, null, ORDER_BY);
    }

    public boolean deleteNote(long id) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        String[] selectionArgs = { String.valueOf(id) };

        int deletedCount = db.delete(TB_NAME, COLUMN_ID + "= ?", selectionArgs);
        db.close();
        return deletedCount > 0;
    }

    public long insertNote(String title, String text) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_NOTE, text);

        long id = db.insert(TB_NAME, null, values);
        db.close();
        return id;
    }

    public void close() {
        openHelper.close();
    }
}
