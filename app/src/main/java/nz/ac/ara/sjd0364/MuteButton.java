package nz.ac.ara.sjd0364;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class MuteButton extends AppCompatImageView {

    private boolean isMuted = false;


    public MuteButton(@NonNull Context context) {
        this(context, null);
    }

    public MuteButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MuteButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setImageResource(R.drawable.sound);

        this.setOnClickListener(this::toggle);
    }

    private void toggle(View view) {
        isMuted = !isMuted;
        if (isMuted) {
            setImageResource(R.drawable.mute);
        } else {
            setImageResource(R.drawable.sound);
        }
    }

    public boolean isMuted() {
        return isMuted;
    }


}
