package com.example.robertjackson.assignment4;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.robertjackson.assignment4.data.SpritesContract;

/**
 * Robert Jackson
 * 4/8/2017
 */
public class SpritesActivity extends BaseActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "SPRITES";

    private static final int LOADER_ID = 42;

    private static final String[] PROJ = new String[]{
            SpritesContract.Columns.ID,
            SpritesContract.Columns.DX,
            SpritesContract.Columns.DY,
            SpritesContract.Columns.PANEL_HEIGHT,
            SpritesContract.Columns.PANEL_WIDTH,
            SpritesContract.Columns.X,
            SpritesContract.Columns.Y,
            SpritesContract.Columns.STATUS
    };

    private static final String[] FROM = new String[PROJ.length - 1];
    private static final int[] TO = new int[]{
            R.id.row_sprites_dx,
            R.id.row_sprites_status
    };

    static {
        System.arraycopy(PROJ, 1, FROM, 0, FROM.length);
    }

    private SimpleCursorAdapter listAdapter;

    /**
     *
     * On create loader, this is used for when a sprite is created, more aless is used for creating
     * a table for the sqlite folder for when the program is first created.
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                SpritesContract.URI,
                PROJ,
                null,
                null,
                SpritesContract.Columns.DX + " ASC");
    }

    /**
     * On load finished, using swapCursor, is for implementing the listAdapter for this
     * activity. we could have used change cursor, as it closes after use, but we wanted it to
     * remain open for the activity lifetime.
     * @param loader
     * @param cursor
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        listAdapter.swapCursor(cursor);
    }

    /**
     * On loader reset, in the event that the activity is reset, it will set the listadapter to
     * a swapcursor null value.
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        listAdapter.swapCursor(null);
    }

    /**
     *
     * on create, this is the starting point for sprites activty.
     * it starts off by creating a click listener for sprites_add, using the show details method
     * feeding in a null value.
     *
     * after wards it creates a new listAdapter and fills the view. first
     * it creates it with the on creation sqlite portion, binds the view.
     * then afterwards it sets the view with a new adapter using the prior listAdapter, creates a
     * new click listener then initiates the loader.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprites);

        findViewById(R.id.activity_sprites_add).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDetails(null);
                    }
                });

        listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.sprite_row,
                null,
                FROM,
                TO,
                0);
        listAdapter.setViewBinder(new StatusBinder());

        ListView listView
                = ((ListView) findViewById(R.id.activity_sprites_list));
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> l, View v, int p, long id) {
                showDetails(p);
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /**
     * Show details, this creates a cursor using listAdapter's get item at int position.
     * using show details, it creates a uri path, then builds off of it.
     * @param pos
     */
    void showDetails(int pos) {
        Cursor c = (Cursor) listAdapter.getItem(pos);
        showDetails(SpritesContract.URI.buildUpon()
                .appendPath(
                        c.getString(c.getColumnIndex(SpritesContract.Columns.ID)))
                .build());
    }

    /**
     * show details, using a uri, it creates a new intent, setting the class to sprite detail activity
     * afterwards, it puts all needed data, and starts the activity. Note this is only starting
     * not ending current activty, nor asking for a response.
     * @param uri
     */
    void showDetails(Uri uri) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "adding contact");
        }
        Intent intent = new Intent();
        intent.setClass(this, SpritesDetailActivity.class);
        if (null != uri) {
            intent.putExtra(SpritesDetailActivity.KEY_URI, uri.toString());
        }
        startActivity(intent);
    }

    /**
     * Static inner class, status binder.
     */
    private static class StatusBinder
            implements SimpleCursorAdapter.ViewBinder {
        public StatusBinder() {
        }

        /**
         * Set View Value, this uses the view cursor and int for index. see's if the view
         * is not row_sprites_status, if it is, it will return a false. if it isnt.
         * it will set the status backround, using the cursor, with current index, and view.
         * then returns true.
         * @param view
         * @param cursor
         * @param idx
         * @return
         */
        @Override
        public boolean setViewValue(View view, Cursor cursor, int idx) {
            if (view.getId() != R.id.row_sprites_status) {
                return false;
            }
            setStatusBackground(cursor.getInt(idx), view);
            return true;
        }
    }
}
