package edu.luc.etl.cs313.android.primechecker.android;

import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PrimeCheckerRemoteTask extends AsyncTask<URL, Void, Boolean> {

	private ProgressBar progressBar;

	private TextView input;

	private HttpGet request;

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
	protected Boolean doInBackground(final URL... params) {
		if (params.length != 1)
			throw new IllegalArgumentException("exactly one argument expected");
		System.out.println("hello1");
		URL url = params[0];
		progressBar.setIndeterminate(true);
		System.out.println("hello2 " + url.toString());
		try {
			final HttpClient client = new DefaultHttpClient();
		    request = new HttpGet(url.toURI());
		    final HttpResponse response = client.execute(request);
			System.out.println("hello3");
		    int status = response.getStatusLine().getStatusCode();
			System.out.println("hello4");
		    if (status == 200)
		    	return true;
		    else if (status == 404)
		    	return false;
		    else {
				System.out.println("hello5 " + response);
		    	throw new RuntimeException("unexpected server response");
		    }
		} catch (final Throwable ex) {
			System.out.println("hello6");
			throw new RuntimeException(ex);
		}
	}

	@Override
	protected void onPostExecute(final Boolean result) {
		progressBar.setIndeterminate(false);
		progressBar.setProgress(100);
		input.setBackgroundColor(result ? Color.GREEN : Color.RED);
	}


	@Override
	protected void onCancelled(final Boolean result) {
		progressBar.setIndeterminate(false);
		input.setBackgroundColor(Color.WHITE);
		request.abort();
	}
}
