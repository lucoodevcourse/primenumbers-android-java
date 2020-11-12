package edu.luc.etl.cs313.android.primechecker.android;

import android.graphics.Color;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.concurrent.Cancellable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Local async (background) task for prime number checking.
 */
// begin-fragment-PrimeCheckerTaskSETUP
public class PrimeCheckerTask implements Runnable, Cancellable {

    // TODO pass handler for main UI thread

    private final long number;

    private final ProgressBar progressBar;

    private int progressValue = 0;

    private final TextView input;

    private final Handler mainThreadHandler;

    private final AtomicBoolean cancelationRequested = new AtomicBoolean();

    public PrimeCheckerTask(final long number, final ProgressBar progressBar, final TextView input, Handler mainThreadHandler) {
        this.number = number;
        this.progressBar = progressBar;
        this.input = input;
        this.mainThreadHandler = mainThreadHandler;
    }
// end-fragment-PrimeCheckerTaskSETUP

    // begin-method-isPrime
    protected boolean isPrime(final long i) { // optimized non-Async/local isPrime method
        if (i < 2) return false;
        if (i == 2) return true;
        final long sqrt = Math.round(Math.sqrt(i));
        for (long k = 3; k <= sqrt; k += 2) {
            if (isCancelled() || i % k == 0) return false;
            publishProgress((int) (k * 100 / sqrt));
        }
        return true;
    }
    // end-method-isPrime

    // begin-method-isPrimeLong
    protected boolean isPrimeLong(final long i) { // original isPrime, now used for Async execution
        if (i < 2) return false;
        final long half = i / 2;
        for (long k = 2; k <= half; k += 1) {
            if (isCancelled() || i % k == 0) return false;
            publishProgress((int) (k * 100 / half));
        }
        return true;
    }
    // end-method-isPrimeLong


    @Override
    public boolean cancel() {
        cancelationRequested.set(true);
        mainThreadHandler.post(() -> {
            input.setBackgroundColor(Color.WHITE);
            progressBar.setProgress(0);
        });
        progressValue = 0;
        return true;
    }

    protected boolean isCancelled() {
        return cancelationRequested.get();
    }

    public void onPreExecute() {
        progressBar.setMax(100);
        input.setBackgroundColor(Color.YELLOW);
    }

    public void onPostExecute(final boolean result) {
        input.setBackgroundColor(result ? Color.GREEN : Color.RED);
    }

    @Override
    public void run() {
        mainThreadHandler.post(this::onPreExecute);
        final boolean result = isPrimeLong(number); // Async execution
        mainThreadHandler.post(() -> onPostExecute(result));
    }

    protected void publishProgress(final int value) {
        if (value == progressValue) return;
        progressValue = value;
        mainThreadHandler.post(() -> progressBar.setProgress(value));
    }
    // end-methods-asyncTask
}
