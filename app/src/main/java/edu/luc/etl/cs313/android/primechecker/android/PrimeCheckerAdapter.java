package edu.luc.etl.cs313.android.primechecker.android;

import android.app.Activity;
import android.graphics.Color; // added for Color.WHITE to reset candidate background color
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple adapter for prime checker app.
 */
public class PrimeCheckerAdapter extends Activity {

    private TextView input;

    private final int NUM = 3;

    private final boolean[] workers = new boolean[3];

    private final ToggleButton[] workerToggles = new ToggleButton[3]; // added to show status

    private final boolean[] remotes = new boolean[3];

    private final ToggleButton[] remoteToggles = new ToggleButton[3]; // added to show status

    private final ProgressBar[] progressBars = new ProgressBar[NUM];

    private final TextView[] urls = new TextView[NUM];

    private final List<AsyncTask<Long, Integer, Boolean>> localTasks = new ArrayList<AsyncTask<Long, Integer, Boolean>>(NUM);

    private final List<PrimeCheckerRemoteTask> remoteTasks = new ArrayList<PrimeCheckerRemoteTask>(NUM);

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime_checker_adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.activity_prime_checker_adapter, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        input = (TextView) findViewById(R.id.inputCandidate);
        workerToggles[0] = (ToggleButton) findViewById(R.id.toggleWorker1); // all toggles added to
        workerToggles[1] = (ToggleButton) findViewById(R.id.toggleWorker2); // show On/Off status
        workerToggles[2] = (ToggleButton) findViewById(R.id.toggleWorker3);
        remoteToggles[0] = (ToggleButton) findViewById(R.id.toggleRemote1);
        remoteToggles[1] = (ToggleButton) findViewById(R.id.toggleRemote2);
        remoteToggles[2] = (ToggleButton) findViewById(R.id.toggleRemote3);
        progressBars[0] = (ProgressBar) findViewById(R.id.progressBar1);
        progressBars[1] = (ProgressBar) findViewById(R.id.progressBar2);
        progressBars[2] = (ProgressBar) findViewById(R.id.progressBar3);
        urls[0] = (TextView) findViewById(R.id.inputUrl1);
        urls[1] = (TextView) findViewById(R.id.inputUrl2);
        urls[2] = (TextView) findViewById(R.id.inputUrl3);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onCancel(input);
    }

    public void onCheck(final View view) {
        try {
            onCancelHelper();
            final long number = Long.parseLong(input.getText().toString());
            boolean asyncOrRemote = false;
            for (int i = 0; i < NUM; i += 1) {
                progressBars[i].setProgress(0);
                if (workers[i] || remotes[i]) { // added || remotes[i] to correct the if condition
                    asyncOrRemote = true;
                    if (remotes[i]) {
                        // offload this task to a cloud-based service
                        // begin-fragment-executeRemote
                        final PrimeCheckerRemoteTask t =
                                new PrimeCheckerRemoteTask(progressBars[i], input);
                        remoteTasks.add(t);
                        t.start(urls[i].getText().toString() + input.getText().toString());
                        // end-fragment-executeRemote
                    } else {
                        // execute this task in the background on a thread pool
                        // begin-fragment-executeBackground
                        final PrimeCheckerTask t =
                                new PrimeCheckerTask(progressBars[i], input);
                        localTasks.add(t);
                        t.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, number);
                        // end-fragment-executeBackground
                    }
                }
            }
            if (!asyncOrRemote) {
                // execute this task directly in the foreground
                // begin-fragment-executeForeground
                final PrimeCheckerTask t = new PrimeCheckerTask(progressBars[0], input);
                localTasks.add(t);
                t.onPreExecute();
                final boolean result = t.isPrime(number); // this method is now optimized
                t.onPostExecute(result);
                localTasks.clear();
                // end-fragment-executeForeground
            }
        } catch (final NumberFormatException ex) {
            // ignore incorrectly formatted numbers
        }
    }

    private void onCancelHelper() { // added to make this logic reusable and to simplify onCancel
        for (final AsyncTask<?, ?, ?> t : localTasks) {
            t.cancel(true);
        }
        for (final PrimeCheckerRemoteTask t : remoteTasks) {
            t.cancel();
        }
        localTasks.clear();
        remoteTasks.clear();
        for (ProgressBar pb : progressBars) { // added to reset progress bars
            pb.setMax(100);
            pb.setProgress(0);
        }
    }

    public void onCancel(final View view) {
        onCancelHelper(); // added
        input.setBackgroundColor(Color.WHITE); // if cancelled, background should be white
        for (int i = 0; i < remotes.length; i++) { // and all toggles should be reset to Off
            if (remotes[i]) {
                remoteToggles[i].toggle();
                remotes[i] = false;
            }
            if (workers[i]) {
                workerToggles[i].toggle();
                workers[i] = false;
            }
        }
    }

    public void onWorker(final int number, final boolean enabled) {
        workers[number] = enabled;
        if (remotes[number]) { // added to turn off corresponding remote
            remoteToggles[number].toggle();
            remotes[number] = false;
        }
    }

    public void onRemote(final int number, final boolean enabled) {
        remotes[number] = enabled;
        if (workers[number]) { // added to turn off corresponding worker
            workerToggles[number].toggle();
            workers[number] = false;
        }
    }

    public void onWorker1(final View view) {
        onWorker(0, ((ToggleButton) view).isChecked());
        }

    public void onWorker2(final View view) {
        onWorker(1, ((ToggleButton) view).isChecked());
    }

    public void onWorker3(final View view) {
        onWorker(2, ((ToggleButton) view).isChecked());
    }

    public void onRemote1(final View view) {
        onRemote(0, ((ToggleButton) view).isChecked());
    }

    public void onRemote2(final View view) {
        onRemote(1, ((ToggleButton) view).isChecked());
    }

    public void onRemote3(final View view) {
        onRemote(2, ((ToggleButton) view).isChecked());
    }
}
