package nz.ac.ara.sjd0364;

import static nz.ac.ara.sjd0364.Lookup.GRID_MARGIN;
import static nz.ac.ara.sjd0364.Lookup.combineTwoImagesAsOne;
import static nz.ac.ara.sjd0364.Lookup.getDrawableIDFromShape;
import static nz.ac.ara.sjd0364.Lookup.getHashCodeFromColor;
import static nz.ac.ara.sjd0364.Lookup.getRotationFromDirection;
import static nz.ac.ara.sjd0364.Lookup.messageMap;
import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;

import android.annotation.SuppressLint;
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
import nz.ac.ara.sjd0364.model.PlayableSquare;
import nz.ac.ara.sjd0364.model.enums.Shape;

@SuppressLint("ViewConstructor")
public class PlayableSquareView extends AppCompatImageView {

    private static final String TAG = "PlayableSquareView";

    private final Coordinate coordinate;
    private final Game game;

    private final MainActivity context;

    private Bitmap shapeBitmap;

    private Drawable eyeballDrawable;

    private final GridLayout.LayoutParams params;

    private boolean renderingEyeball;

    private boolean wasGoal = false;

    private final nz.ac.ara.sjd0364.model.enums.Color originalColor;
    private final Shape originalShape;



    public PlayableSquareView(MainActivity context, int row, int column, Game game, GridLayout.LayoutParams params) {
        this(context, null, row, column, game, params);
    }

    public PlayableSquareView(MainActivity context, AttributeSet attrs, int row, int column, Game game, GridLayout.LayoutParams params) {
        this(context, null, 0, row, column, game, params);
    }

    public PlayableSquareView(MainActivity context, AttributeSet attrs, int defStyleAttr, int row, int column, Game game, GridLayout.LayoutParams params) {
        super(context, attrs, defStyleAttr);

        originalShape = game.getShapeAt(row, column);
        originalColor = game.getColorAt(row, column);
        this.context = context;
        coordinate = new Coordinate(row, column);
        this.game = game;
        this.params = params;
        if (this.game.hasGoalAt(coordinate.row(), coordinate.column())) {
            wasGoal = true;
        }

        init();
    }

    public PlayableSquare getOriginalPlayableSquare() {
        return new PlayableSquare(originalColor, originalShape);
    }

    private void init() {
        this.setBackgroundColor(Color.WHITE);
//
        shapeBitmap = getSquareBitmap(getDrawableIDFromShape(originalShape), originalColor);
        eyeballDrawable = ContextCompat.getDrawable(context, R.drawable.eyeball);


        if (game.hasGoalAt(coordinate.row(), coordinate.column())) {
            setGoal();
        }
        int topMargin = GRID_MARGIN;
        int bottomMargin = GRID_MARGIN;
        int leftMargin = GRID_MARGIN;
        int rightMargin = GRID_MARGIN;
        if (coordinate.row() == 0) {
            topMargin *= 2;
        }
        if (coordinate.row() == game.getLevelHeight() - 1) {
            bottomMargin *= 2;
        }
        if (coordinate.column() == 0) {
            leftMargin *= 2;
        }
        if (coordinate.column() == game.getLevelWidth() - 1) {
            rightMargin *= 2;
        }
        params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        this.setLayoutParams(params);

        renderingEyeball = !(coordinate.row() == game.getEyeballRow() && coordinate.column() == game.getEyeballColumn());
        toggleEyeballRendering();

        this.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {
        if (game.canMoveTo(coordinate.row(), coordinate.column()) && !renderingEyeball) {
            move();
//            Play ding sound on success
            context.playDingSound();
        }
//        Cannot move to this square, so play dud sound and show Snack bar with reason
        else {
            context.playDudSound();
            MessageString messageString = messageMap.get(game.messageIfMovingTo(coordinate.row(), coordinate.column()));
            String message;
            if (messageString == null) {
                message = "Unknown error";
            }
            else {
                message = messageString.message();
            }

            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
            snackbar.setAnchorView((View) this.getParent());
            snackbar.show();
        }
    }

    public void setGoal() {
        this.setBackgroundColor(Color.RED);
    }

    private void move() {
        ViewParent parent = getParent();
        if (parent instanceof GridLayout gridLayout) {
//            Get index of 2d object in 1d array
            int row = game.getEyeballRow();
            int column = game.getEyeballColumn();
            int totalColumns = gridLayout.getColumnCount();

            int index = row * totalColumns + column;

            PlayableSquareView oldEyeballSquare = (PlayableSquareView) gridLayout.getChildAt(index);
            if (oldEyeballSquare != null) {
                oldEyeballSquare.toggleEyeballRendering();
                if (oldEyeballSquare.isWasGoal()) {
                    oldEyeballSquare.removeShape();
                }

                Move move = new Move(game.getEyeballDirection(), oldEyeballSquare, wasGoal);

                boolean onGoal = game.hasGoalAt(coordinate.row(), coordinate.column());
                game.moveTo(coordinate.row(), coordinate.column());

                if (onGoal) {
                    this.setBackgroundColor(Color.WHITE);
                }

                context.updateMoves(move, onGoal);

                toggleEyeballRendering();
            }

        }
    }

    public void toggleEyeballRendering() {
//        No longer rendering the eyeball
        if (renderingEyeball) {
            this.setImageBitmap(shapeBitmap);
            renderingEyeball = false;
//        Rendering the eyeball
        } else {
            Bitmap eyeballBitmap = getEyeBallBitmap(getRotationFromDirection(game.getEyeballDirection()));
            Bitmap squareBitmap = combineTwoImagesAsOne(shapeBitmap, eyeballBitmap);
            this.setImageBitmap(squareBitmap);
            renderingEyeball = true;
        }
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);

    }

    public boolean isWasGoal() {
        return wasGoal;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void removeShape() {
        this.setImageBitmap(null);
        this.setImageDrawable(null);
        this.setBackgroundColor(Color.WHITE);

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
