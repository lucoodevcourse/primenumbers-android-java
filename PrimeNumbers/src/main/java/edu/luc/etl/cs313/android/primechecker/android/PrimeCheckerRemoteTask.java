package edu.luc.etl.cs313.android.primechecker.android;

import android.graphics.Color;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;

import org.apache.http.Header;

/**
 * Task for remote checking whether a number is prime via the given URL.
 * Assumes that response status OK means prime, NotFound means composite.
 */
public class PrimeCheckerRemoteTask {

    private static String TAG = "edu.luc.etl.cs313.android.primechecker.android.PrimeCheckerRemoteTask";

    private final ProgressBar progressBar;

    private final TextView input;

    private RequestHandle request;

    public PrimeCheckerRemoteTask(final ProgressBar progressBar, final TextView input) {
        this.progressBar = progressBar;
        this.input = input;
    }

    // begin-method-remoteStart
    public void start(final String url) {
        try {
            Log.d(TAG, "starting request for URL = " + url);
            progressBar.setMax(100);
            progressBar.setIndeterminate(true);
            input.setBackgroundColor(Color.YELLOW);
            Log.d(TAG, "creating client");
            final AsyncHttpClient client = new AsyncHttpClient();
            Log.d(TAG, "submitting request to " + client);
            request = client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    Log.d(TAG, "request started");
                }
                @Override
                public void onSuccess(final int statusCode, final Header[] headers,
                                      final byte[] responseBody) {
                    Log.d(TAG, "request handled successfully with status code " + statusCode);
                    input.setBackgroundColor(statusCode == 200 ? Color.GREEN : Color.MAGENTA);
                }
                @Override
                public void onFailure(final int statusCode, final Header[] headers,
                                      final byte[] responseBody, final Throwable error) {
                    Log.d(TAG, "request failed with status code " + statusCode);
                    input.setBackgroundColor(statusCode == 404 ? Color.RED : Color.MAGENTA);
                    if (error != null) {
                        Log.d(TAG, "request failed with error " + error);
                    }
                }
                @Override
                public void onFinish() {
                    progressBar.setIndeterminate(false);
                    progressBar.setProgress(100);
                }
            });
            Log.d(TAG, "submitted request");
        } catch (final Throwable ex) {
            Log.d(TAG, "exception...rethrowing");
            throw new RuntimeException(ex);
        }
    }
    // end-method-remoteStart

    // begin-method-remoteCancel
    protected void cancel() {
        Log.d(TAG, "canceling request");
        progressBar.setIndeterminate(false);
        input.setBackgroundColor(Color.WHITE);
        request.cancel(true);
        Log.d(TAG, "canceled request");
    }
    // end-method-remoteCancel
}
