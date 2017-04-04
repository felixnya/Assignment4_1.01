package com.example.robertjackson.assignment4.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class SpritesHelper extends SQLiteOpenHelper {
    // sprite column data
    public static final String COL_COLOR = "color"; // string
    public static final String COL_DX = "dx"; // string
    public static final String COL_DY = "dy"; // string
    public static final String COL_PANEL_HEIGHT = "panelheight"; // string
    public static final String COL_PANEL_WIDTH = "panelwidth"; // string
    public static final String COL_X = "x"; // string
    public static final String COL_Y = "y"; // string
    // pk
    public static final String COL_ID = "id";                   // long
    static final int VERSION = 2;
    static final String DB_FILE = "enterprisesprites.db";
    static final String TAB_SPRITES = "sprite";
    // meta-data
    static final String COL_REMOTE_ID = "remoteId";      // string
    static final String COL_DELETED = "deleted";         // boolean (null or MARK)
    static final String COL_DIRTY = "dirty";             // boolean (null or MARK)
    static final String COL_SYNC = "sync";               // string

    /**
     * @param context
     */
    public SpritesHelper(Context context) {
        super(context, DB_FILE, null, VERSION);
    }

    /**
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TAB_SPRITES + "("
                        + COL_ID + " integer PRIMARY KEY AUTOINCREMENT,"
                        + COL_COLOR + " text,"
                        + COL_DX + " integer,"
                        + COL_DY + " integer,"
                        + COL_PANEL_HEIGHT + " integer,"
                        + COL_PANEL_WIDTH + " integer,"
                        + COL_X + " integer,"
                        + COL_Y + " integer,"
                        + COL_REMOTE_ID + " string UNIQUE,"
                        + COL_DELETED + " integer,"
                        + COL_DIRTY + " integer,"
                        + COL_SYNC + " string UNIQUE)");
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("drop table " + TAB_SPRITES);
        } catch (SQLiteException e) {
        }
        onCreate(db);
    }
}
