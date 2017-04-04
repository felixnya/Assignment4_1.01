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
     */
    protected static void setStatusBackground(int status, View view) {
        int color = STATUS_COLOR_MAP.get(status);
        view.setBackgroundColor((0 != color) ? color : Color.BLACK);
    }

    /**
     * @param menu
     * @return
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
