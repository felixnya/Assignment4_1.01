package com.example.robertjackson.assignment4;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.robertjackson.assignment4.data.SpritesContract;

/**
 * Robert Jackson
 * 4/8/2017
 */
@SuppressLint("Registered")
public class BaseActivity extends Activity {
    private static final String TAG = "BASE";

    private static final SparseIntArray STATUS_COLOR_MAP;

    static {
        SparseIntArray a = new SparseIntArray();
        a.put(SpritesContract.STATUS_OK, Color.GREEN);
        a.put(SpritesContract.STATUS_SYNC, Color.YELLOW);
        a.put(SpritesContract.STATUS_DIRTY, Color.RED);
        STATUS_COLOR_MAP = a;
    }

    /**
     * @param status
     * @param view
     * Used to set current activities background. it tests to see if color does not equal zero
     * if it does, it sets the color to color. else it turns it into black.
     */
    protected static void setStatusBackground(int status, View view) {
        int color = STATUS_COLOR_MAP.get(status);
        view.setBackgroundColor((0 != color) ? color : Color.BLACK);
    }

    /**
     * @param menu
     * @return
     * On create options menu, is a method used to include the menu for the current activity.
     * All this does is inflate the toolbar at the top of the screen with the menu layout
     * then returns that it has accomplished such.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * @param item
     * @return
     * On options item selected, this is the logic behind the menu selection options.
     * Such as, if i press on item preferences icon, it will start a new activity.
     * otherwise it will say it is unrecognized, which should in all fairness, never activate.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_prefs:
                startActivity(new Intent(this, PrefsActivity.class));
                break;

            default:
                Log.i(TAG, "Unrecognized menu item: " + item);
                return false;
        }

        return true;
    }
}
