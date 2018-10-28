package com.apps.lefo.threadproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ProgressBar progressBar;

    // https://upload.wikimedia.org/wikipedia/en/8/8d/Pablo_Picasso%2C_1905%2C_Au_Lapin_Agile_%28At_the_Lapin_Agile%29%2C_oil_on_canvas%2C_99.1_x_100.3_cm%2C_Metropolitan_Museum_of_Art.jpg
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.prog_hor);
        progressBar.setVisibility(View.INVISIBLE);

        Button startbtn = findViewById(R.id.start_button);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startThread(v);
                startAsync();
            }
        });
    }

    /**
     * Using a second thread for a task*/
    public void startThread(View v) {
        ExampleThread thread = new ExampleThread(10);
        thread.start();
    }

    private class ExampleThread extends Thread {
        int seconds;

        public ExampleThread(int seconds) {
            this.seconds = seconds;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                Log.d(TAG, "run: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Using a second with the asyncTask class to execute a task*/
    private void startAsync() {
        ExampleAsyncTask task = new ExampleAsyncTask(this);
        task.execute(10);
    }

    private static class ExampleAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<MainActivity> activityWeakReference;

        ExampleAsyncTask(MainActivity activity) {
            activityWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            for (int i = 0; i < integers[0]; i++) {
                publishProgress((i * 100) / integers[0]);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "finished";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String string) {
            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.progressBar.setProgress(0);
            activity.progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(activity, string, Toast.LENGTH_SHORT).show();
        }
    }
}
