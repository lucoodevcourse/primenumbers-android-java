package edu.luc.etl.cs313.android.primechecker.android;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Local async (background) task for prime number checking.
 */
public class PrimeCheckerTask extends AsyncTask<Long, Integer, Boolean> {

	private final ProgressBar progressBar;

	private final TextView input;

	public PrimeCheckerTask(final ProgressBar progressBar, final TextView input) {
		this.progressBar = progressBar;
		this.input = input;
	}

    // begin-method-doInBackground
	@Override
	protected Boolean doInBackground(final Long... params) {
		if (params.length != 1)
			throw new IllegalArgumentException("exactly one argument expected");
		long i = params[0];
		if (i < 2)
			return false;
		long half = i / 2;
		double dHalf = half;
		for (long k = 2; k <= half; k += 1) {
			if (isCancelled()) break;
			publishProgress((int) ((k / dHalf) * 100));
			if (i % k == 0) {
				return false;
			}
		}
		return true;
	}
    // end-method-doInBackground

    // begin-methods-asyncTask
    @Override
    protected void onPreExecute() {
        progressBar.setMax(100);
        input.setBackgroundColor(Color.YELLOW);
    }

	@Override
	protected void onProgressUpdate(final Integer... values) {
		progressBar.setProgress(values[0]);
	}

	@Override
	protected void onPostExecute(final Boolean result) {
		input.setBackgroundColor(result ? Color.GREEN : Color.RED);
	}

	@Override
	protected void onCancelled(final Boolean result) {
		input.setBackgroundColor(Color.WHITE);
	}
    // end-methods-asyncTask
}
