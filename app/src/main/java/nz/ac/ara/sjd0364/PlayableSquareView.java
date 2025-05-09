package nz.ac.ara.sjd0364;

import static nz.ac.ara.sjd0364.Lookup.combineTwoImagesAsOne;
import static nz.ac.ara.sjd0364.MainActivity.GRID_MARGIN;
import static nz.ac.ara.sjd0364.Lookup.getDrawableIDFromShape;
import static nz.ac.ara.sjd0364.Lookup.getHashCodeFromColor;
import static nz.ac.ara.sjd0364.Lookup.getRotationFromDirection;
import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.widget.GridLayout;


import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import java.util.Objects;

import nz.ac.ara.sjd0364.model.Game;
import nz.ac.ara.sjd0364.model.enums.Shape;

public class PlayableSquareView extends AppCompatImageView {

    private final int row;
    private final int column;
    private final Game game;

    private final Context context;

    private Bitmap shapeBitmap;

    private Bitmap eyeballBitmap;

    private final GridLayout.LayoutParams params;

    private boolean renderingEyeball;



    public PlayableSquareView(Context context, int row, int column, Game game, GridLayout.LayoutParams params) {
        super(context);
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
        shapeBitmap = getSquareBitmap(getDrawableIDFromShape(shape), color, 0.8f);
        eyeballBitmap = getEyeBallBitmap(R.drawable.eyeball, getRotationFromDirection(game.getEyeballDirection()));
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
    }

    private void toggleEyeballRendering() {
        if (renderingEyeball) {
            this.setImageBitmap(shapeBitmap);
            renderingEyeball = false;
//            this.setBackgroundColor(Color.WHITE);
        } else {
            Bitmap squareBitmap = combineTwoImagesAsOne(shapeBitmap, eyeballBitmap);
            this.setImageBitmap(squareBitmap);
            renderingEyeball = true;
//            this.setBackgroundColor(Color.YELLOW);
        }

    }


    private Bitmap getSquareBitmap(int drawableId, nz.ac.ara.sjd0364.model.enums.Color color, float scale) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (color != BLANK) {
            drawable.setColorFilter(Color.parseColor(getHashCodeFromColor(color)), PorterDuff.Mode.MULTIPLY);
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.scale(scale, scale, canvas.getWidth() / 2f, canvas.getHeight() / 2f);
        drawable.draw(canvas);
        return bitmap;

    }

    private Bitmap getEyeBallBitmap(int drawableId, float rotate) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
//        TODO make an option
        drawable.setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        if (drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.rotate(rotate, canvas.getWidth() / 2f, canvas.getHeight() / 2f);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return ((BitmapDrawable) drawable).getBitmap();
        }
    }




}
