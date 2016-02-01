package edu.asu.mc.assignment1;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import edu.asu.mc.assignment1.data.Point;
import edu.asu.mc.assignment1.data.State;
import edu.asu.mc.assignment1.views.RGraphView;

public class MainActivity extends AppCompatActivity {

    /**
     * To handle timeout
     */
    private Handler handler;

    /**
     * Graph view
     */
    private RGraphView graphView;

    /**
     * Graph points
     */
    private List<Point> points;

    /**
     * current state of the app
     */
    private State currentState = State.STOPPED;

    /**
     * Start/Pause button
     */
    private Button startButton;

    /**
     * Stop button
     */
    private Button stopButton;

    private static String TAG = "MainActivity";

    /**
     * Thread to update points
     */
    private Runnable updaterUI = new Runnable() {

        private int start = 0;

        @Override
        public void run() {
            if (start >= points.size()) {
                // stop the process
                return;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get random data
        initData();

        // add event listeners for start and stop buttons
        addEventListeners();

        // Initialize graph view
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup viewGroup;
        graphView = new RGraphView(getApplicationContext(),
                points,
                "Title",
                0,
                9,
                4,
                10,
                0,
                123,
                124);

        viewGroup = (ViewGroup) findViewById(R.id.graph_content);
        viewGroup.addView(graphView, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // create a handler
        handler = new Handler();
    }

    /**
     * Add event listeners for buttons
     */
    private void addEventListeners() {

        // initialize buttons
        startButton = (Button) findViewById(R.id.button_start);
        stopButton = (Button) findViewById(R.id.button_stop);

        // start button event
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (currentState) {
                    case STOPPED: // initially stopped
                        startGraph();
                        break;
                    case STARTED: // current button is paused
                        pauseGraph();
                        break;
                    case PAUSED: // current button is started
                        resumeGraph();
                        break;
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (currentState) {
                    case STOPPED: // initially stopped
                        // do nothing
                        break;
                    case STARTED: // current button is paused
                    case PAUSED: // current button is started
                        stopGraph();
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Synchronized setter for state
     * @param currentState
     */
    public synchronized void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    /**
     * Init points
     */
    private void initData() {
        points = Point.getValues(0, 9, 124);
    }

    /**
     * Reset the points
     */
    private void resetData() {

    }

    /**
     * Start the graph when in stopped state
     */
    private void startGraph() {
        setCurrentState(State.STARTED);
        startButton.setText(getResources().getText(R.string.button_pause));
        // enable stop button
        stopButton.setClickable(true);
    }

    /**
     * Stop the graph when paused or started
     */
    private void stopGraph() {
        setCurrentState(State.STOPPED);
        resetData();
        stopButton.setClickable(false);
        startButton.setText(getResources().getText(R.string.button_start));
    }

    /**
     * Pause from started state
     */
    private void pauseGraph() {
        setCurrentState(State.PAUSED);
        startButton.setText(getResources().getText(R.string.button_start));
    }

    /**
     * Resume from pause state
     */
    private void resumeGraph() {
        setCurrentState(State.STARTED);
        startButton.setText(getResources().getText(R.string.button_pause));
    }
}
