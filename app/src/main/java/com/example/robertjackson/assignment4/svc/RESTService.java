package com.example.robertjackson.assignment4.svc;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.robertjackson.assignment4.BuildConfig;
import com.example.robertjackson.assignment4.R;
import com.example.robertjackson.assignment4.SpritesApplication;
import com.example.robertjackson.assignment4.data.SpritesContract;
import com.example.robertjackson.assignment4.data.SpritesHelper;
import com.example.robertjackson.assignment4.data.SpritesProvider;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Robert Jackson
 * 4/8/2017
 */
public class RESTService extends IntentService {
    public static final String HEADER_ENCODING = "Accept-Encoding";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String MIME_ANY = "*/*";
    public static final String MIME_JSON = "application/json;charset=UTF-8";
    public static final String ENCODING_NONE = "identity";
    public static final int HTTP_READ_TIMEOUT = 30 * 1000; // ms
    public static final int HTTP_CONN_TIMEOUT = 30 * 1000; // ms
    public static final String XACT = "RESTService.XACT";
    public static final String ID = "RESTService.ID";
    public static final String COLOR = "RESTService.COLOR";
    public static final String DX = "RESTService.DX";
    public static final String DY = "RESTService.DY";
    public static final String PANEL_HEIGHT = "RESTService.PANEL_HEIGHT";
    public static final String PANEL_WIDTH = "RESTService.PANEL_WIDTH";
    public static final String X = "RESTService.X";
    public static final String Y = "RESTService.Y";
    private static final String TAG = "http://";
    private static final String OP = "RESTService.OP";
    private String USER_AGENT;

    public RESTService() {
        super(TAG);
    }

    /**
     * @param ctxt
     * @param vals
     * @return insert
     * used to insert a sprite into the database.
     */
    public static String insert(Context ctxt, ContentValues vals) {
        Intent intent = getIntent(ctxt, RESTService.Op.CREATE);

        marshalRequest(vals, intent);

        ctxt.startService(intent);

        return intent.getStringExtra(RESTService.XACT);
    }

    /**
     * @param ctxt
     * @param id
     * @return delete
     * used to delete a sprite from the database.
     */
    public static String delete(Context ctxt, String id) {
        if (null == id) {
            return null;
        }

        Intent intent = RESTService.getIntent(ctxt, RESTService.Op.DELETE);

        intent.putExtra(RESTService.ID, id);

        ctxt.startService(intent);

        return intent.getStringExtra(RESTService.XACT);
    }

    /**
     * @param ctxt
     * @param id
     * @param vals
     * @return update
     * used to access the update method via marshal request.
     * used as a narrow transaction for selection.
     */
    public static String update(Context ctxt, String id, ContentValues vals) {
        if (null == id) {
            return null;
        }

        Intent intent = RESTService.getIntent(ctxt, RESTService.Op.UPDATE);

        intent.putExtra(RESTService.ID, id);
        marshalRequest(vals, intent);

        ctxt.startService(intent);

        return intent.getStringExtra(RESTService.XACT);
    }

    /**
     * @param ctxt
     * @param op
     * @return get intent
     * standard accessor method for intent.
     */
    private static Intent getIntent(Context ctxt, Op op) {
        Intent intent = new Intent(ctxt, RESTService.class);

        intent.putExtra(RESTService.OP, op.toInt());

        String xact = UUID.randomUUID().toString();
        intent.putExtra(RESTService.XACT, xact);

        return intent;
    }

    /**
     * @param vals
     * @param intent marshal request
     *               used to take current content values and an intent to put together a sprite
     *               inside of an intent, this is normally used later to place this inside a database.
     */
    // the server always wants all values
    private static void marshalRequest(ContentValues vals, Intent intent) {
        intent.putExtra(
                RESTService.ID,
                (!vals.containsKey(SpritesHelper.COL_ID))
                        ? "" : vals.getAsString(SpritesHelper.COL_ID));
        intent.putExtra(
                RESTService.COLOR,
                (!vals.containsKey(SpritesHelper.COL_COLOR))
                        ? "" : vals.getAsString(SpritesHelper.COL_COLOR));
        intent.putExtra(
                RESTService.DX,
                (!vals.containsKey(SpritesHelper.COL_DX))
                        ? "" : vals.getAsString(SpritesHelper.COL_DX));
        intent.putExtra(
                RESTService.DY,
                (!vals.containsKey(SpritesHelper.COL_DY))
                        ? "" : vals.getAsString(SpritesHelper.COL_DY));
        intent.putExtra(
                RESTService.PANEL_HEIGHT,
                (!vals.containsKey(SpritesHelper.COL_PANEL_HEIGHT))
                        ? "" : vals.getAsString(SpritesHelper.COL_PANEL_HEIGHT));
        intent.putExtra(
                RESTService.PANEL_WIDTH,
                (!vals.containsKey(SpritesHelper.COL_PANEL_WIDTH))
                        ? "" : vals.getAsString(SpritesHelper.COL_PANEL_WIDTH));
        intent.putExtra(
                RESTService.X,
                (!vals.containsKey(SpritesHelper.COL_X))
                        ? "" : vals.getAsString(SpritesHelper.COL_X));
        intent.putExtra(
                RESTService.Y,
                (!vals.containsKey(SpritesHelper.COL_Y))
                        ? "" : vals.getAsString(SpritesHelper.COL_Y));
    }

    /**
     * on create
     * standard on create method used as a starting point for this activity.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        USER_AGENT = getString(R.string.app_name)
                + "/" + getString(R.string.app_version);
    }

    /**
     * @param intent on handle intent
     *               used to handle intent being thrown and gathers the extra as a bundle.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle args = intent.getExtras();
        sendRequest(args);
    }

    /**
     * @param args send request
     *             this is a switch statement using the bundle arguments to select an operation out of a list.
     */
    private void sendRequest(Bundle args) {
        int op = 0;
        if (args.containsKey(OP)) {
            op = args.getInt(OP);
        }
        switch (Op.toOp(op)) {
            case CREATE:
                createSprite(args);
                break;

            case UPDATE:
                updateSprite(args);
                break;

            case DELETE:
                deleteSprite(args);
                break;

            default:
                cleanup(args, null);
                throw new IllegalArgumentException("Unrecognized op: " + op);
        }
    }

    /**
     * @param args create sprite
     *             creates a new sprite and adds it to the server's database
     */
    private void createSprite(Bundle args) {
        if (args.containsKey(ID)) {
            throw new IllegalArgumentException("create must not specify id");
        }
        Uri uri = ((SpritesApplication) getApplication()).getApiUri();

        final ContentValues vals = new ContentValues();
        try {
            String payload = new MessageHandler().marshal(args);

            sendRequest(
                    HttpMethod.POST,
                    uri,
                    payload,
                    new ResponseHandler() {
                        @Override
                        public void handleResponse(BufferedReader in)
                                throws IOException {
                            new MessageHandler().unmarshal(in, vals);
                        }
                    });

            vals.putNull(SpritesContract.Columns.DIRTY);
        } catch (Exception e) {
            Log.w(TAG, "create failed: " + e, e);
        } finally {
            cleanup(args, vals);
        }
    }

    /**
     * @param args update sprite
     *             Updates current sprite with new values
     */
    private void updateSprite(Bundle args) {
        if (!args.containsKey(ID)) {
            throw new IllegalArgumentException("missing id in update");
        }

        Uri uri = ((SpritesApplication) getApplication()).getApiUri()
                .buildUpon().appendPath(args.getString(ID)).build();

        final ContentValues vals = new ContentValues();
        try {
            String payload = new MessageHandler().marshal(args);

            sendRequest(
                    HttpMethod.PUT,
                    uri,
                    payload,
                    new ResponseHandler() {
                        @Override
                        public void handleResponse(BufferedReader in)
                                throws IOException {
                            new MessageHandler().unmarshal(in, vals);
                        }
                    });

            checkId(args, vals);
            vals.putNull(SpritesContract.Columns.DIRTY);
        } catch (Exception e) {
            Log.w(TAG, "update failed: " + e);
        } finally {
            cleanup(args, vals);
        }
    }

    /**
     * @param args delete sprite
     *             Used to delete a selected sprite from the server/arraylist
     */
    private void deleteSprite(Bundle args) {
        if (!args.containsKey(ID)) {
            throw new IllegalArgumentException("missing id in delete");
        }
        Uri uri = ((SpritesApplication) getApplication()).getApiUri()
                .buildUpon().appendPath(args.getString(ID)).build();

        try {
            sendRequest(HttpMethod.DELETE, uri, null, null);
        } catch (Exception e) {
            cleanup(args, null);
            return;
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "delete @" + args.getString(XACT));
        }

        // !!!
        // Using the transaction id to identify the record needing update
        // causes a data race if there is more than one update in progress
        getContentResolver().delete(
                SpritesContract.URI,
                SpritesProvider.SYNC_CONSTRAINT,
                new String[]{args.getString(XACT)});
    }

    /**
     * @param args
     * @param vals cleanup
     *             it is used to sync constraints with sprites provider.
     */
    private void cleanup(Bundle args, ContentValues vals) {
        if (null == vals) {
            vals = new ContentValues();
        }

        vals.putNull(SpritesContract.Columns.SYNC);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "cleanup @" + args.getString(XACT) + ": " + vals);
        }

        String sel;
        String[] selArgs;
        if (!args.containsKey(ID)) {
            sel = SpritesProvider.SYNC_CONSTRAINT;
            selArgs = new String[]{args.getString(XACT)};
        } else {
            sel = new StringBuilder("(")
                    .append(SpritesProvider.SYNC_CONSTRAINT)
                    .append(") AND (")
                    .append(SpritesProvider.REMOTE_ID_CONSTRAINT)
                    .append(")")
                    .toString();
            selArgs = new String[]{args.getString(XACT), args.getString(XACT)};
        }

        // !!!
        // Using the transaction id to identify the record needing update
        // causes a data race if there is more than one update in progress
        getContentResolver().update(SpritesContract.URI, vals, sel, selArgs);
    }

    /**
     * @param args
     * @param vals Check id
     *             used as an error checking for sprite values, in terms of their id vs the server
     */
    private void checkId(Bundle args, ContentValues vals) {
        String id = args.getString(ID);
        String rid = vals.getAsString(SpritesContract.Columns.REMOTE_ID);
        if (!id.equals(rid)) {
            Log.w(TAG, "request id does not match response id: " + id + ", " + rid);
        }
        vals.remove(SpritesContract.Columns.REMOTE_ID);
    }

    /**
     * @param method
     * @param uri
     * @param payload
     * @param hdlr
     * @return
     * @throws IOException send request.
     *                     with your http/method/uri/payload
     *                     access a request response with the active server
     */
    // the return code is being ignored, at present
    private int sendRequest(
            HttpMethod method,
            Uri uri,
            String payload,
            ResponseHandler hdlr)
            throws IOException {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "sending " + method + " @" + uri + ": " + payload);
        }

        HttpURLConnection conn
                = (HttpURLConnection) new URL(uri.toString()).openConnection();

        int code = HttpURLConnection.HTTP_UNAVAILABLE;
        try {
            conn.setReadTimeout(HTTP_READ_TIMEOUT);
            conn.setConnectTimeout(HTTP_CONN_TIMEOUT);
            conn.setRequestMethod(method.toString());
            conn.setRequestProperty(HEADER_USER_AGENT, USER_AGENT);
            conn.setRequestProperty(HEADER_ENCODING, ENCODING_NONE);

            if (null != hdlr) {
                conn.setRequestProperty(HEADER_ACCEPT, MIME_JSON);
                conn.setDoInput(true);
            }

            if (null != payload) {
                conn.setRequestProperty(HEADER_CONTENT_TYPE, MIME_JSON);
                conn.setFixedLengthStreamingMode(payload.length());
                conn.setDoOutput(true);

                conn.connect();
                // writer normally sync's with the rest of the stream
                Writer out = new OutputStreamWriter(
                        new BufferedOutputStream(conn.getOutputStream()),
                        "UTF-8"); // standard type code language
                out.write(payload);
                out.flush();
            }

            code = conn.getResponseCode();
            //handler
            if (null != hdlr) {
                hdlr.handleResponse(new BufferedReader(
                        new InputStreamReader(conn.getInputStream())));
            }
        } finally {
            // connection
            if (null != conn) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                }
            }
        }

        return code;
    }

    // odd that these aren't defined elsewhere...
    public enum HttpMethod {
        GET, PUT, POST, DELETE
    }

    public enum Op {
        NOOP, CREATE, UPDATE, DELETE;

        /**
         * @param code
         * @return Operators array list.
         */
        static Op toOp(int code) {
            Op[] ops = Op.values();
            code = (code * -1) - 1;
            return ((0 > code) || (ops.length <= code))
                    ? NOOP
                    : ops[code];
        }

        int toInt() {
            return (ordinal() + 1) * -1;
        }
    }

    private interface ResponseHandler {
        void handleResponse(BufferedReader in) throws IOException;
    }
}
