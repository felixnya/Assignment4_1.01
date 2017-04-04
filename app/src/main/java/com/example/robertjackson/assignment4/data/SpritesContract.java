package com.example.robertjackson.assignment4.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


public final class SpritesContract {
    public static final String TABLE = "sprite";
    public static final String AUTHORITY = "com.enterpriseandroid.restfulcontacts.SPRITES";
    public static final Uri URI
        = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY)
            .appendPath(TABLE)
            .build();
    /** Contacts DIR type */
    public static final String CONTENT_TYPE_DIR
            = ContentResolver.CURSOR_DIR_BASE_TYPE // needs to be changed
            + " + /vnd.com.enterpriseandroid.restfulsprites.sprite";
    /** Contacts ITEM type */
    public static final String CONTENT_TYPE_ITEM
        = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + " + /vnd.com.enterpriseandroid.restfulsprites.sprite";
    public static final int STATUS_OK = 0;
    public static final int STATUS_SYNC = 1;
    public static final int STATUS_DIRTY = 2;

    private SpritesContract() {
    }

    public static final class Columns implements BaseColumns {
        public static final String COLOR = "color"; // string
        public static final String DX = "dx"; // string
        public static final String DY = "dy"; // string
        public static final String PANEL_HEIGHT = "panelheight"; // string
        public static final String PANEL_WIDTH = "panelwidth"; // string
        public static final String X = "x"; // string
        public static final String Y = "y"; // string
        public static final String ID = "id"; // string
        public static final String STATUS = "status";

        // !!!
        // These columns really should not be exposed.
        public static final String SYNC = "sync";         // string!
        public static final String DIRTY = "dirty";       // int!
        public static final String REMOTE_ID = "rem";     // int!

        private Columns() {
        }
    }
}
