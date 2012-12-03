package edu.luc.etl.cs313.android.primechecker.android;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PrimeCheckerTask extends AsyncTask<Long, Integer, Boolean> {

	private ProgressBar progressBar;

	private TextView input;

	public PrimeCheckerTask(final ProgressBar progressBar, final TextView input) {
		this.progressBar = progressBar;
		this.input = input;
	}

	@Override
	protected void onPreExecute() {
		progressBar.setMax(100);
		input.setBackgroundColor(Color.YELLOW);
	}

	@Override
	protected Boolean doInBackground(Long... params) {
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

	@Override
	protected void onProgressUpdate(Integer... values) {
		progressBar.setProgress(values[0]);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		input.setBackgroundColor(result ? Color.GREEN : Color.RED);
	}
}
