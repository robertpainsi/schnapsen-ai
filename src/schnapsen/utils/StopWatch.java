package schnapsen.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StopWatch {

    private final long startTime;

    public StopWatch() {
        startTime = System.currentTimeMillis();
    }

    public double getElapsedSeconds() {
        return (double) getElapsedMillis() / 1000;
    }

    public long getElapsedMillis() {
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    public String getElapsedFormatted() {
        long elapsed = getElapsedMillis();
        int seconds = (int) ((elapsed / 1000) % 60);
        int minutes = (int) ((elapsed / 1000) / 60);

        return String.format("%3d:%02d", minutes, seconds);
    }
}