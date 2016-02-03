package edu.asu.mc.assignment1.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import edu.asu.mc.assignment1.R;
import edu.asu.mc.assignment1.data.Point;

/**
 * Created by Vikranth and Sachin on 1/31/2016.
 *
 * Improved graph view
 */
public class RGraphView extends View {

    private List<Point> points;

    /**
     * Min y value
     */
    private float yMin;

    /**
     * Max y value
     */
    private float yMax;

    /**
     * Title of the graph
     */
    private String title;

    /**
     * Num of points on x axis
     */
    private int numXPoints;

    /**
     * Num of points on y axis
     */
    private int numYPoints;

    /**
     * num of points a graph view can hold
     */
    private int numOfPoints;


    private float titleSize;

    private float labelSize;

    /**
     * border to be sckipped
     */
    private float border;

    private float xStartValue;
    private float xEndValue;

    private float startX;
    private float startY;

    private int width;
    private int height;

    private float graphWidth;
    private float graphHeight;

    private Paint paint;

    private float labelWidthDiff;
    private float labelHeightDiff;

    private static String TAG = "RGraphView";
    private static float LINE_STROKE_WIDTH = 1.0f;
    private static float GRAPH_STROKE_WIDTH = 2.0f;

    /**
     * Default constructor
     * @param context
     * @param points
     * @param title
     * @param yMax
     * @param yMin
     */
    public RGraphView(Context context, List<Point> points,
                      String title, float yMin, float yMax,
                      int numXPoints, int numYPoints,
                      float xStartValue, float xEndValue,
                      int numOfPoints) {
        super(context);

        points = points == null ? new ArrayList<Point>() : points;
        title = title == null ? "" : title;
        numXPoints = numXPoints < 2 ? 2 : numXPoints;
        numYPoints = numYPoints < 2 ? 2 : numYPoints;
        numOfPoints = numOfPoints < 2 ? 2 : numOfPoints;

        // set the values
        this.points = points;
        this.yMax = yMax;
        this.yMin = yMin;
        this.title = title;
        this.numXPoints = numXPoints;
        this.numYPoints = numYPoints;
        this.xStartValue = xStartValue;
        this.xEndValue = xEndValue;
        this.numOfPoints = numOfPoints;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        // set initial values
        initValues();
        // rectangle background
        drawRectangleBG(canvas);
        // set the vertical labels
        setVerticalLabels(canvas);
        // set horizontal labels
        setHorizontalLabels(canvas);
        // set Title
        setTitle(canvas);
        // plot the graph
        plotGraph(canvas);
    }

    /**
     * Rect background
     * @param canvas
     */
    private void drawRectangleBG(Canvas canvas) {
        paint.setColor(Color.BLACK);
        canvas.drawRect(startX, startY, startX + graphWidth, startY + graphHeight, paint);
    }

    /**
     * Plot the graph with the points
     * @param canvas
     */
    private void plotGraph(Canvas canvas) {
        float x1, y1, x2, y2;
        x1 = startX;
        y1 = startY;

        boolean isStart = true;

        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(GRAPH_STROKE_WIDTH);

        for (Point point: points) {
            // copy previous values to x2 and y2
            x2 = x1;
            y2 = y1;

            // compute new values
            x1 = getXPosition(point.getX());
            y1 = getYPosition(point.getY());

            if (isStart) {
                isStart = false;
                continue;
            }

            // draw a new line
            canvas.drawLine(x1, y1, x2, y2, paint);
        }

    }

    /**
     * Set the horizontal labels
     * @param canvas
     */
    private void setVerticalLabels(Canvas canvas) {
        // draw horizontal lines
        float diff = (yMax - yMin) / (float) (numYPoints - 1);
        labelHeightDiff = graphHeight / (float) (numYPoints - 1);

        float yMax = this.yMax;
        float y;
        float halfBorder = border / 2.0f;
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(labelSize);
        paint.setStrokeWidth(LINE_STROKE_WIDTH);

        for (int i = 0; i < numYPoints; i++) {
            y = (labelHeightDiff * i) + startY;
            canvas.drawLine(startX, y, startX + graphWidth, y, paint);
            // draw the labels
            canvas.drawText(getRoundNum(yMax), 0, y + halfBorder, paint);
            yMax -= diff;
        }
    }

    /**
     * Set horizontal labels
     * @param canvas
     */
    private void setHorizontalLabels(Canvas canvas) {
        // need horizontal line length
        float diff = (xEndValue - xStartValue) / (float) (numXPoints - 1);
        labelWidthDiff = graphWidth / (float)(numXPoints - 1);

        float xStartValue = this.xStartValue;
        float x;

        paint.setColor(Color.DKGRAY);
        paint.setStrokeWidth(LINE_STROKE_WIDTH);
        paint.setTextSize(labelSize);

        for (int i = 0; i < numXPoints; i++) {
            x = (labelWidthDiff * i) + startX;
            canvas.drawLine(x, startY, x, startY + graphHeight, paint);
            // draw the labels
            if (i == 0) {
                // left alignment for first value
                paint.setTextAlign(Paint.Align.LEFT);
            } else if (i == numXPoints - 1) {
                // right alignment for last value
                paint.setTextAlign(Paint.Align.RIGHT);
            } else {
                // center alignment for remaining
                paint.setTextAlign(Paint.Align.CENTER);
            }
            canvas.drawText(getRoundNum(xStartValue), x, height, paint);
            xStartValue += diff;
        }
    }

    /**
     * Sets the title
     * @param canvas
     */
    private void setTitle(Canvas canvas) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(titleSize);
        canvas.drawText(title, (graphWidth / 2.0f) + startX, startY - 4, paint);
    }

    /**
     * Set initial values
     */
    private void initValues() {
        titleSize = getResources().getDimension(R.dimen.title_size);
        labelSize = getResources().getDimension(R.dimen.label_size);
        border = titleSize;

        // set height and width
        width = getWidth() - 1; // no idea why -1
        height = getHeight();

        // graph width and height
        graphWidth = width - (2 * border);
        graphHeight = height - (2 * border);

        // new paint
        paint = new Paint();
        paint.setTextSize(titleSize);

        // start values of x and y
        startX = 2 * border;
        // graph has a padding of width = border
        // on the top of graph and at the bottom
        startY = border;
    }

    /**
     * Round float value and return string
     * @param f
     * @return string
     */
    private static String getRoundNum(float f) {
        int i = (int) Math.abs(f * 10);
        if (i % 10 == 0) {
            return Integer.toString(i / 10);
        }
        return Float.toString(i / 10.0f);
    }

    /**
     * Get x android position from point x
     * @param x
     * @return
     */
    private float getXPosition(float x) {
        float xpos = ((x - xStartValue) / (xEndValue - xStartValue)) * graphWidth;
        return xpos + startX;
    }

    /**
     * Get y android position from point y
     * @param y
     * @return
     */
    private float getYPosition(float y) {
        float ypos = ((y - yMin) / (yMax - yMin))  * graphHeight;
        return (graphHeight - ypos) + startY;
    }

    /**
     *
     * @param points
     */
    public void setPoints(List<Point> points) {
        this.points = points;
    }

    /**
     * Set the x start value and x end value
     * @param xStartValue
     * @param xEndValue
     */
    public void setStartEndXValues(float xStartValue, float xEndValue) {
        this.xStartValue = xStartValue;
        this.xEndValue = xEndValue;
    }
}
