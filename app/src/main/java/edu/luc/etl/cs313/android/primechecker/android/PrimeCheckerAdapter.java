package edu.luc.etl.cs313.android.primechecker.android;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.os.HandlerCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple adapter for prime checker app.
 */
public class PrimeCheckerAdapter extends Activity {

    private TextView input;

    private final int NUM = 3;

    private final ToggleButton[] workerToggles = new ToggleButton[NUM]; // added to show status

    private final ToggleButton[] remoteToggles = new ToggleButton[NUM]; // added to show status

    private final ProgressBar[] progressBars = new ProgressBar[NUM];

    private final TextView[] urls = new TextView[NUM];

    private final List<PrimeCheckerTask> localTasks = new ArrayList<>(NUM);

    private final List<PrimeCheckerRemoteTask> remoteTasks = new ArrayList<PrimeCheckerRemoteTask>(NUM);

    private final ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());


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
        input = findViewById(R.id.inputCandidate);
        workerToggles[0] = findViewById(R.id.toggleWorker1); // all toggles added to
        workerToggles[1] = findViewById(R.id.toggleWorker2); // show On/Off status
        workerToggles[2] = findViewById(R.id.toggleWorker3);
        remoteToggles[0] = findViewById(R.id.toggleRemote1);
        remoteToggles[1] = findViewById(R.id.toggleRemote2);
        remoteToggles[2] = findViewById(R.id.toggleRemote3);
        progressBars[0] = findViewById(R.id.progressBar1);
        progressBars[1] = findViewById(R.id.progressBar2);
        progressBars[2] = findViewById(R.id.progressBar3);
        urls[0] = findViewById(R.id.inputUrl1);
        urls[1] = findViewById(R.id.inputUrl2);
        urls[2] = findViewById(R.id.inputUrl3);
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
                if (workerToggles[i].isChecked() || remoteToggles[i].isChecked()) { // added || remotes[i] to correct the if condition
                    asyncOrRemote = true;
                    if (remoteToggles[i].isChecked()) {
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
                                new PrimeCheckerTask(number, progressBars[i], input, mainThreadHandler);
                        localTasks.add(t);
                        executorService.submit(t);
                        // end-fragment-executeBackground
                    }
                }
            }
            if (!asyncOrRemote) {
                // execute this task directly in the foreground
                // begin-fragment-executeForeground
                final PrimeCheckerTask t = new PrimeCheckerTask(number, progressBars[0], input, mainThreadHandler);
                t.onPreExecute();
                final boolean result = t.isPrimeLong(number); // this method is now optimized
                t.onPostExecute(result);
                // end-fragment-executeForeground
            }
        } catch (final NumberFormatException ex) {
            // ignore incorrectly formatted numbers
        }
    }

    private void onCancelHelper() { // added to make this logic reusable and to simplify onCancel
        for (final PrimeCheckerTask t : localTasks) {
            t.cancel();
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
        for (int i = 0; i < remoteToggles.length; i++) { // and all toggles should be reset to Off
            workerToggles[i].setChecked(false);
            remoteToggles[i].setChecked(false);
        }
    }

    public void onWorker(final View view) {
        final int number = Arrays.asList(workerToggles).indexOf(view);
        remoteToggles[number].setChecked(false);
    }

    public void onRemote(final View view) {
        final int number = Arrays.asList(remoteToggles).indexOf(view);
        workerToggles[number].setChecked(false);
    }
}
