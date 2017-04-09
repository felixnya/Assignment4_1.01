package com.example.robertjackson.assignment4;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.robertjackson.assignment4.data.SpritesContract;

/**
 * Robert Jackson
 * 4/8/2017
 */

public class SpritesDetailActivity extends BaseActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String KEY_URI = "SpriteDetailActivity.SPRITE_URI";

    private static final int LOADER_ID = 58;

    private static final String[] PROJ = new String[]{
            // values taken from SpritesContract.java
            SpritesContract.Columns.ID,
            SpritesContract.Columns.COLOR,
            SpritesContract.Columns.DX,
            SpritesContract.Columns.DY,
            SpritesContract.Columns.PANEL_HEIGHT,
            SpritesContract.Columns.PANEL_WIDTH,
            SpritesContract.Columns.X,
            SpritesContract.Columns.Y,
            SpritesContract.Columns.STATUS
    };
    private View statusView;
    private TextView idView;
    private String id = "";
    private TextView colorView;
    private String color = "";
    private TextView dxView;
    private String dx = "";
    private TextView dyView;
    private String dy = "";
    private TextView panelheightView;
    private String panelheight = "";
    private TextView panelwidthView;
    private String panelwidth = "";
    private TextView xView;
    private String x = "";
    private TextView yView;
    private String y = "";
    private Uri contactUri;

    /**
     * @param id
     * @param args
     * @return
     * On create loader, this is an sqlite helper. when the application is first launched.
     * it will create the loader with these values.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, contactUri, PROJ, null, null, null);
    }

    /**
     * @param loader
     * @param cursor
     * On load finished, after the current load has been finished, it will populate the view
     * that is list view, with the values at the cursor location.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        populateView(cursor);
    }

    /**
     * @param loader
     * On loader reset, nothing at this point and time.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * @param state
     * On create, the entry point of sprites detail activty. inquires about state, uri, and contacturi.
     * if any are null/not null, it will set these values.
     *
     * It sets the current view, with activity sprite details. then instantiates several variables.
     * creates two click listeners, these being the buttons. for launching update and delete.
     */
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        if (null == state) {
            state = getIntent().getExtras();
        }
        String uri = null;
        if (null != state) {
            uri = state.getString(KEY_URI);
        }
        if (null != uri) {
            contactUri = Uri.parse(uri);
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }

        setContentView(R.layout.activity_sprite_details);

        statusView = findViewById(R.id.activity_detail_status);
        idView = (TextView) findViewById(R.id.activity_detail_id);
        colorView = (TextView) findViewById(R.id.activity_detail_color);
        dxView = (TextView) findViewById(R.id.activity_detail_dx);
        dyView = (TextView) findViewById(R.id.activity_detail_dy);
        panelheightView = (TextView) findViewById(R.id.activity_detail_panelheight);
        panelwidthView = (TextView) findViewById(R.id.activity_detail_panelwidth);
        xView = (TextView) findViewById(R.id.activity_detail_x);
        yView = (TextView) findViewById(R.id.activity_detail_y);

        findViewById(R.id.activity_detail_update).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        update();
                    }
                });

        findViewById(R.id.activity_detail_delete).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete();
                    }
                });
    }

    /**
     * @param state
     * on save instance state, if contact uri is not null, it puts a string in state bundle.
     */
    @Override
    protected void onSaveInstanceState(Bundle state) {
        if (null != contactUri) {
            state.putString(KEY_URI, contactUri.toString());
        }
    }

    /**
     *delete, if contacturi is not null, it instantiates a new instance of delete sprite.
     * using get content resolver, executing with contact uri, then launches the gotosprites method.
     */
    void delete() {
        if (null != contactUri) {
            new DeleteSprite(getContentResolver()).execute(contactUri);
        }
        goToSprites();
    }

    /**
     *  update, creates a new variable with contentvalues. adds this all to vals, using the add string
     *  method, then creates a new update sprite using the values added. then go to sprites.
     */
    void update() {

        ContentValues vals = new ContentValues();
        addString(idView, id, vals, SpritesContract.Columns.ID);
        addString(colorView, color, vals, SpritesContract.Columns.COLOR);
        addString(dxView, dx, vals, SpritesContract.Columns.DX);
        addString(dyView, dy, vals, SpritesContract.Columns.DY);
        addString(panelheightView, panelheight, vals, SpritesContract.Columns.PANEL_HEIGHT);
        addString(panelwidthView, panelwidth, vals, SpritesContract.Columns.PANEL_WIDTH);
        addString(xView, x, vals, SpritesContract.Columns.X);
        addString(yView, y, vals, SpritesContract.Columns.Y);
        new UpdateSprite(getContentResolver(), vals).execute(contactUri);
        goToSprites();
    }

    /**
     * @param c
     * populate view.
     * sets background, then populates the view by extracting the fields current at this point
     * by the cursor variable.
     */
    private void populateView(Cursor c) {
        if (!c.moveToNext()) {
            return;
        }

        setStatusBackground(
                c.getInt(c.getColumnIndex(SpritesContract.Columns.STATUS)),
                statusView);

        String s;

        s = getString(c, SpritesContract.Columns.ID);
        id = (TextUtils.isEmpty(s)) ? "" : s;
        idView.setText(id);
        s = getString(c, SpritesContract.Columns.COLOR);
        color = (TextUtils.isEmpty(s)) ? "" : s;
        colorView.setText(color);
        s = getString(c, SpritesContract.Columns.DX);
        dx = (TextUtils.isEmpty(s)) ? "" : s;
        dxView.setText(dx);
        s = getString(c, SpritesContract.Columns.DY);
        dy = (TextUtils.isEmpty(s)) ? "" : s;
        dyView.setText(dy);
        s = getString(c, SpritesContract.Columns.PANEL_HEIGHT);
        panelheight = (TextUtils.isEmpty(s)) ? "" : s;
        panelheightView.setText(panelheight);
        s = getString(c, SpritesContract.Columns.PANEL_HEIGHT);
        panelwidth = (TextUtils.isEmpty(s)) ? "" : s;
        panelwidthView.setText(panelwidth);
        s = getString(c, SpritesContract.Columns.X);
        x = (TextUtils.isEmpty(s)) ? "" : s;
        xView.setText(x);
        s = getString(c, SpritesContract.Columns.Y);
        y = (TextUtils.isEmpty(s)) ? "" : s;
        yView.setText(y);
    }

    /**
     * creates a new intent, souly for starting the activity sprites activity.
     */
    private void goToSprites() {
        Intent intent = new Intent(this, SpritesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    /**
     * @param c
     * @param col
     * @return
     * get string, returns the string at cursor provided.
     */
    private String getString(Cursor c, String col) {
        return c.getString(c.getColumnIndex(col));
    }

    /**
     * @param view
     * @param oldVal
     * @param vals
     * @param col
     * add string, used to add a string to a certain value that being to content values.
     */
    private void addString(
            TextView view,
            String oldVal,
            ContentValues vals,
            String col) {
        String s = view.getText().toString();
        if (!oldVal.equals(s)) {
            vals.put(col, s);
        }
    }

    /**
     * update sprite, is a static inner class used for async tasks.
     */
    static class UpdateSprite extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;
        private final ContentValues vals;

        public UpdateSprite(ContentResolver resolver, ContentValues vals) {
            this.resolver = resolver;
            this.vals = vals;
        }

        /**
         * @param args
         * @return
         * do in backgroun.
         * creates an array of uri, if it isnt null, it will use resolver and update.
         * otherwise if it is null it will insert a sprite contact. then retun null.
         */
        @Override
        protected Void doInBackground(Uri... args) {
            Uri uri = args[0];
            if (null != uri) {
                resolver.update(uri, vals, null, null);
            } else {
                resolver.insert(SpritesContract.URI, vals);
            }
            return null;
        }
    }

    /**
     *
     * another async inner class, delete sprite
     */
    static class DeleteSprite extends AsyncTask<Uri, Void, Void> {
        private final ContentResolver resolver;

        public DeleteSprite(ContentResolver resolver) {
            this.resolver = resolver;
        }

        /**
         * @param args
         * @return Used to delete an entry in resolver.
         */
        @Override
        protected Void doInBackground(Uri... args) {
            resolver.delete(args[0], null, null);
            return null;
        }
    }
}
