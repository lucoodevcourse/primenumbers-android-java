package edu.luc.etl.cs313.android.primechecker.android;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PrimeCheckerRemoteTask extends AsyncTask<URL, Void, Boolean> {

	private ProgressBar progressBar;

	private TextView input;

	public PrimeCheckerRemoteTask(final ProgressBar progressBar, final TextView input) {
		this.progressBar = progressBar;
		this.input = input;
	}

	@Override
	protected void onPreExecute() {
		progressBar.setMax(100);
		input.setBackgroundColor(Color.YELLOW);
	}

	@Override
	protected Boolean doInBackground(URL... params) {
		if (params.length != 1)
			throw new IllegalArgumentException("exactly one argument expected");
		System.out.println("hello1");
		URL url = params[0];
		progressBar.setIndeterminate(true);
		System.out.println("hello2 " + url.toString());
		try {
		    final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			System.out.println("hello3");
		    int response = urlConnection.getResponseCode();
			System.out.println("hello4");
		    if (response == 200)
		    	return true;
		    else if (response == 404)
		    	return false;
		    else {
				System.out.println("hello5 " + response);
		    	throw new RuntimeException("unexpected server response");
		    }
		} catch (final IOException ex) {
			System.out.println("hello6");
			throw new RuntimeException(ex);
		}
	}

	@Override
	protected void onPostExecute(Boolean result) {
		progressBar.setIndeterminate(false);
		progressBar.setProgress(100);
		input.setBackgroundColor(result ? Color.GREEN : Color.RED);
	}


	@Override
	protected void onCancelled(Boolean result) {
		progressBar.setIndeterminate(false);
		input.setBackgroundColor(Color.WHITE);
	}
}
