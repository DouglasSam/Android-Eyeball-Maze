package nz.ac.ara.sjd0364;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        FrameLayout frame = findViewById(R.id.boardFrame);
//
//        LinearLayout verticalLayout = new LinearLayout(this);
//        verticalLayout.setOrientation(LinearLayout.VERTICAL);
////        verticalLayout.
//        frame.addView(verticalLayout);
        GridLayout gridLayout = findViewById(R.id.boardFrame);

        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.flower);
        list.add(R.drawable.diamond);
        list.add(R.drawable.plus);
        list.add(R.drawable.star);
        list.add(R.drawable.lightning);


        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
//                ImageButton button = new ImageButton(this);
//                button.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.flower));
//                horizontalLayout.addView(button);
                ImageView image = new ImageView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(i, 1f);
                params.columnSpec = GridLayout.spec(j, 1f);
                image.setLayoutParams(params);
                image.setBackgroundColor(Color.parseColor("#ffffff"));
//                image.setPadding(1,1,1,1);
//                GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.flower);
//                drawable.setColor(Color.RED); // Set new color
                Drawable drawable = AppCompatResources.getDrawable(this, list.get(j % list.size()));
//                drawable.
                if (drawable != null) {
                    drawable.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                }
                else {
                    throw new NullPointerException("Drawable is null");
                }
                image.setImageDrawable(drawable);
                gridLayout.addView(image);
            }
        }
        System.out.println(gridLayout.getHeight());
    }
}


