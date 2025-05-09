package nz.ac.ara.sjd0364;

import static nz.ac.ara.sjd0364.Lookup.GRID_MARGIN;
import static nz.ac.ara.sjd0364.Lookup.combineTwoImagesAsOne;
import static nz.ac.ara.sjd0364.Lookup.getDrawableIDFromShape;
import static nz.ac.ara.sjd0364.Lookup.getHashCodeFromColor;
import static nz.ac.ara.sjd0364.Lookup.getRotationFromDirection;
import static nz.ac.ara.sjd0364.Lookup.messageMap;
import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.GridLayout;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import nz.ac.ara.sjd0364.model.Game;
import nz.ac.ara.sjd0364.model.enums.Shape;

@SuppressLint("ViewConstructor")
public class PlayableSquareView extends AppCompatImageView {

    private final int row;
    private final int column;
    private final Game game;

    private final Context context;

    private Bitmap shapeBitmap;

    private Drawable eyeballDrawable;

    private final GridLayout.LayoutParams params;

    private boolean renderingEyeball;



    public PlayableSquareView(Context context, int row, int column, Game game, GridLayout.LayoutParams params) {
        this(context, null, row, column, game, params);
    }

    public PlayableSquareView(Context context, AttributeSet attrs, int row, int column, Game game, GridLayout.LayoutParams params) {
        this(context, null, 0, row, column, game, params);
    }

    public PlayableSquareView(Context context, AttributeSet attrs, int defStyleAttr, int row, int column, Game game, GridLayout.LayoutParams params) {
        super(context, attrs, defStyleAttr);

        this.context = context;
        this.row = row;
        this.column = column;
        this.game = game;
        this.params = params;

        init();
    }

    private void init() {
        this.setBackgroundColor(Color.WHITE);

        Shape shape = game.getShapeAt(row, column);
        nz.ac.ara.sjd0364.model.enums.Color color = game.getColorAt(row, column);
//
        shapeBitmap = getSquareBitmap(getDrawableIDFromShape(shape), color);
        eyeballDrawable = ContextCompat.getDrawable(context, R.drawable.eyeball);

////                    TODO make better
        if (game.hasGoalAt(row, column)) {
          this.setBackgroundColor(Color.RED);
        }
        int topMargin = GRID_MARGIN;
        int bottomMargin = GRID_MARGIN;
        int leftMargin = GRID_MARGIN;
        int rightMargin = GRID_MARGIN;
        if (row == 0) {
            topMargin *= 2;
        }
        if (row == game.getLevelHeight() - 1) {
            bottomMargin *= 2;
        }
        if (column == 0) {
            leftMargin *= 2;
        }
        if (column == game.getLevelWidth() - 1) {
            rightMargin *= 2;
        }
        params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        this.setLayoutParams(params);

        renderingEyeball = !(row == game.getEyeballRow() && column == game.getEyeballColumn());
        toggleEyeballRendering();

        this.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        if (game.canMoveTo(row, column)) {
            ViewParent parent = getParent();
            if (parent instanceof GridLayout gridLayout) {
                int row = game.getEyeballRow();  // Change based on the row you want
                int column = game.getEyeballColumn();  // Change based on the column you want
                int totalColumns = gridLayout.getColumnCount(); // Number of columns in your GridLayout

                int index = row * totalColumns + column;

                PlayableSquareView oldEyeballSquare = (PlayableSquareView) gridLayout.getChildAt(index);
                if (oldEyeballSquare != null) {
                    oldEyeballSquare.toggleEyeballRendering();
                }

            }
            game.moveTo(row, column);
            toggleEyeballRendering();
        }
        else {
            MessageString messageString = messageMap.get(game.messageIfMovingTo(row, column));
            String message;
            if (messageString == null) {
                message = "Unknown error";
            }
            else {
                message = messageString.message();
            }

//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
            snackbar.setAnchorView((View) this.getParent());
            snackbar.show();
        }
    }

    private void toggleEyeballRendering() {
        if (renderingEyeball) {
            this.setImageBitmap(shapeBitmap);
            renderingEyeball = false;
//            this.setBackgroundColor(Color.WHITE);
        } else {
            Bitmap eyeballBitmap = getEyeBallBitmap(getRotationFromDirection(game.getEyeballDirection()));
            Bitmap squareBitmap = combineTwoImagesAsOne(shapeBitmap, eyeballBitmap);
            this.setImageBitmap(squareBitmap);
            renderingEyeball = true;
//            this.setBackgroundColor(Color.YELLOW);
        }

    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);

    }

    private Bitmap getSquareBitmap(int drawableId, nz.ac.ara.sjd0364.model.enums.Color color) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable == null) {
            throw new IllegalArgumentException("Drawable not found for ID: " + drawableId);
        }

        if (color != BLANK) {
            drawable.setColorFilter(Color.parseColor(getHashCodeFromColor(color)), PorterDuff.Mode.MULTIPLY);
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.scale((float) 0.8, (float) 0.8, canvas.getWidth() / 2f, canvas.getHeight() / 2f);
        drawable.draw(canvas);
        return bitmap;

    }

    private Bitmap getEyeBallBitmap(float rotate) {

//        TODO make an option
        eyeballDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        if (eyeballDrawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(eyeballDrawable.getIntrinsicWidth(), eyeballDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.rotate(rotate, canvas.getWidth() / 2f, canvas.getHeight() / 2f);
            eyeballDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            eyeballDrawable.draw(canvas);
            return bitmap;
        } else {
            return ((BitmapDrawable) eyeballDrawable).getBitmap();
        }
    }




}
