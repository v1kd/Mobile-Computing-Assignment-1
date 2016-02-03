package edu.asu.mc.assignment1;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
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

    /**
     * Status view
     */
    private TextView textStatusView;

    private static String TAG = "MainActivity";

    private int numXPoints = 6;

    private int numYPoints = 10;

    private int numPointsPerScreen = 60;

    private int totalPoints = numPointsPerScreen * 6;

    private int numSkipPoints = 1;

    private int yMin = 0;

    private int yMax = 9;

    private int delayTime = 50;

    /**
     * Thread to update points
     *
     * http://stackoverflow.com/questions/14056240/delaye-canvas-update-using-timer-class
     */
    private Runnable updaterUI = new Runnable() {

        /**
         * skips
         */
        private int skips = 0;

        @Override
        public void run() {
            // run the task only started
            int start = 0;
            List<Point> nextPoints = new ArrayList<Point>();
            switch (getCurrentState()) {
                case PAUSED:
                    break;
                case STARTED:
                    // check if points are empty
                    if (points == null) {
                        points = Point.getValues(yMin, yMax, totalPoints);
                    }
                    nextPoints = getNextPoints();
                    if (nextPoints.size() == 0) {
                        skips = 0;
                        stopGraph();
                    } else {
                        start = skips * numSkipPoints;
                        skips++;
                    }
                    graphView.setPoints(nextPoints);
                    graphView.setStartEndXValues(start, start + numPointsPerScreen);
                    graphView.invalidate();
                    break;
                case STOPPED:
                    skips = 0;
                    if (points != null) {
                        points = null;
                    }
                    // set new points
                    graphView.setPoints(nextPoints);
                    graphView.setStartEndXValues(start, start + numPointsPerScreen);
                    graphView.invalidate();
                    break;
            }
            handler.postDelayed(updaterUI, delayTime);
        }

        /**
         * Get next set of points
         * @return points
         */
        private List<Point> getNextPoints() {
            if (skips * numSkipPoints >= points.size()) {
                return new ArrayList<Point>();
            }

            int start = skips * numSkipPoints;
            int end = start + numPointsPerScreen;
            end = end > points.size() ? points.size() : end;

            return points.subList(start, end);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // init text status
        this.textStatusView = (TextView) findViewById(R.id.text_status);

        setSupportActionBar(toolbar);
        // get random data
        // initData();

        // add event listeners for start and stop buttons
        addEventListeners();

        // initialize graph
        initGraph();

        // create a handler
        handler = new Handler();
        handler.post(updaterUI);
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
                switch (getCurrentState()) {
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

            private boolean init = true;

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

        // set clickable false
        stopButton.setClickable(false);
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
     * @return currentState
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Init points
     */
    private void initData() {
        points = Point.getValues(yMin, yMax, totalPoints);
    }

    /**
     * Init graph
     */
    private void initGraph() {
        // Initialize graph view
        ViewGroup viewGroup;
        graphView = new RGraphView(getApplicationContext(),
                null, // Points - initially set it to null
                "Graph", // title of the graph
                yMin, // min value of y coordinate
                yMax, // max value of y coordinate
                numXPoints, // num of x points
                numYPoints, // num of y points
                0, // x start value
                numPointsPerScreen, // x end value
                numPointsPerScreen); // num of points on the screen

        viewGroup = (ViewGroup) findViewById(R.id.graph_content);
        viewGroup.addView(graphView, 0, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * Start the graph when in stopped state
     */
    private void startGraph() {
        setCurrentState(State.STARTED);
        textStatusView.setText(R.string.graph_status_running);
        startButton.setText(getResources().getText(R.string.button_pause));
        // enable stop button
        stopButton.setClickable(true);
    }

    /**
     * Stop the graph when paused or started
     */
    private void stopGraph() {
        textStatusView.setText(R.string.graph_status_init);
        setCurrentState(State.STOPPED);
        stopButton.setClickable(false);
        startButton.setText(getResources().getText(R.string.button_start));
    }

    /**
     * Pause from started state
     */
    private void pauseGraph() {
        textStatusView.setText(R.string.graph_status_paused);
        setCurrentState(State.PAUSED);
        startButton.setText(getResources().getText(R.string.button_start));
    }

    /**
     * Resume from pause state
     */
    private void resumeGraph() {
        textStatusView.setText(R.string.graph_status_resumed);
        setCurrentState(State.STARTED);
        startButton.setText(getResources().getText(R.string.button_pause));
    }
}
