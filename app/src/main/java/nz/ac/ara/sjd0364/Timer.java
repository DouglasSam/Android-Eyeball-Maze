package nz.ac.ara.sjd0364;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Chronometer;

import java.util.Locale;

public class Timer extends Chronometer {
    private long startTime;

    private long elapsedTime = 0;

    private boolean isRunning = false;

    public Timer(Context context) {
        this(context, null);
    }

    public Timer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Timer(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Timer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFormat("Elapsed Time: 00:00");
        this.setOnChronometerTickListener(this::tickListener);
    }

    private void tickListener(Chronometer chronometer) {
        if (isRunning) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - startTime + elapsedTime;
            String formattedTime = "Elapsed Time: " + formatTimeSeconds(elapsed);
            setText(formattedTime);
        }
    }

    private String formatTimeSeconds(long elapsed) {
        int minutes = (int) (elapsed / 60000);
        int seconds = (int) ((elapsed % 60000) / 1000);

        return String.format(Locale.UK,"%02d:%02d", minutes, seconds);
    }

    private String formatTimeMillis(long elapsed) {
        int minutes = (int) (elapsed / 60000);
        int seconds = (int) ((elapsed % 60000) / 1000);
        int milliseconds = (int) (elapsed % 1000);

        return String.format(Locale.UK,"%02d:%02d:%03d", minutes, seconds, milliseconds);
    }

    public String getFormattedElapsedTimeToMillis() {
        return formatTimeMillis(elapsedTime);
    }

    public String getFormattedElapsedTimeToSeconds() {
        return formatTimeSeconds(elapsedTime);
    }

    public void stop() {
        super.stop();
        this.isRunning = false;
        long endTime = System.currentTimeMillis();
        this.elapsedTime += endTime - startTime;
    }

    public void start() {
        super.start();
        this.isRunning = true;
        this.startTime = System.currentTimeMillis();
    }

    public void reset() {
        super.stop();
        this.elapsedTime = 0;
        setText("Elapsed Time: 00:00");
    }


    public long getElapsedTime() {
        return elapsedTime;
    }
}