package edu.asu.mc.assignment1.data;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Vikranth on 1/31/2016.
 */
public class Point {

    /**
     * x coordinate
     */
    protected float x;

    /**
     * y coordinate
     */
    protected float y;

    public Point() {

    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     *
     * @param min
     * @param max
     * @param size
     * @return points
     */
    public static List<Point> getValues(float min, float max, int size) {

        List<Point> points = new ArrayList<Point>();
        for (int i = 0; i < size; i++) {
            points.add(new Point(i, min + (float) Math.random() * max));
        }

        return points;
    }
}
