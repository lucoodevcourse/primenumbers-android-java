package edu.luc.etl.cs313.android.primechecker.android;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Local async (background) task for prime number checking.
 */
// begin-fragment-PrimeCheckerTaskSETUP
public class PrimeCheckerTask extends AsyncTask<Long, Integer, Boolean> {

    private final ProgressBar progressBar;

    private final TextView input;

    public PrimeCheckerTask(final ProgressBar progressBar, final TextView input) {
        this.progressBar = progressBar;
        this.input = input;
    }
// end-fragment-PrimeCheckerTaskSETUP

    // begin-method-isPrime
    protected boolean isPrime(final long i) { // optimized non-Async/local isPrime method
        if (i < 2) return false;
        if (i == 2) return true;
        final long sqrt = (long) Math.sqrt(i);
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

    // begin-methods-asyncTask
    @Override protected void onPreExecute() {
        progressBar.setMax(100);
        input.setBackgroundColor(Color.YELLOW);
    }

    @Override protected Boolean doInBackground(final Long... params) {
        if (params.length != 1)
            throw new IllegalArgumentException("exactly one argument expected");
        return isPrimeLong(params[0]); // Async execution
    }

    @Override protected void onProgressUpdate(final Integer... values) {
        progressBar.setProgress(values[0]);
    }

    @Override protected void onPostExecute(final Boolean result) {
        input.setBackgroundColor(result ? Color.GREEN : Color.RED);
    }

    @Override protected void onCancelled(final Boolean result) {
        input.setBackgroundColor(Color.WHITE);
        progressBar.setProgress(0);
    }
    // end-methods-asyncTask
}
