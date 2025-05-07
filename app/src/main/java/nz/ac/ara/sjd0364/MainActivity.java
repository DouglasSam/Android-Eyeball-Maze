package nz.ac.ara.sjd0364;

import static java.security.AccessController.getContext;
import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;
import static nz.ac.ara.sjd0364.model.enums.Color.BLUE;
import static nz.ac.ara.sjd0364.model.enums.Color.GREEN;
import static nz.ac.ara.sjd0364.model.enums.Color.PURPLE;
import static nz.ac.ara.sjd0364.model.enums.Color.RED;
import static nz.ac.ara.sjd0364.model.enums.Color.YELLOW;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

import nz.ac.ara.sjd0364.model.BlankSquare;
import nz.ac.ara.sjd0364.model.Game;
import nz.ac.ara.sjd0364.model.PlayableSquare;
import nz.ac.ara.sjd0364.model.enums.Direction;
import nz.ac.ara.sjd0364.model.enums.Shape;
import nz.ac.ara.sjd0364.model.interfaces.Square;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int GRID_MARGIN = 7;

    private Game game;
    private int currentLevel = 0;

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

        game = new Game();

        loadMazesFromFiles();

        game.setLevel(currentLevel);

//        Set up the level change buttons
        Button nextButton = findViewById(R.id.nextLevel);
        Button previousButton = findViewById(R.id.backLevel);
        nextButton.setOnClickListener(v -> changeLevelOnClick(1,nextButton, previousButton));
        previousButton.setOnClickListener(v -> changeLevelOnClick(-1, nextButton, previousButton));
        previousButton.setEnabled(false);

        Button startButton = findViewById(R.id.startLevel);
        startButton.setOnClickListener(this::startGame);

//        TODO remove when not testing
        startButton.callOnClick();
//        renderCurrentLevel();

    }

    public void changeLevelOnClick(int change, Button nextButton, Button previousButton) {
        currentLevel += change;
        game.setLevel(currentLevel);

        previousButton.setEnabled(currentLevel != 0);
        nextButton.setEnabled(currentLevel != game.getLevelCount() - 1);

        renderCurrentLevel();
    }

    public void startGame(View view) {
        view.setVisibility(View.GONE);
        renderCurrentLevel();
    }


    private void renderCurrentLevel() {
        GridLayout gridLayout = findViewById(R.id.boardFrame);

//        clear the grid layout
        gridLayout.removeAllViews();
        gridLayout.setBackgroundColor(Color.BLACK);

        Log.d(TAG, "rendering: " + currentLevel + " " + game.getLevelHeight() + " " + game.getLevelWidth());


        for (int i = 0; i < game.getLevelHeight(); i++) {
            for (int j = 0; j < game.getLevelWidth(); j++) {;

                Shape shape = game.getShapeAt(i, j);
                nz.ac.ara.sjd0364.model.enums.Color color = game.getColorAt(i, j);

                ImageView image = new ImageView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(i, 1f);
                params.columnSpec = GridLayout.spec(j, 1f);

//                image.setBackgroundColor(getColor(R.color.background));
                image.setBackgroundColor(getColor(R.color.background));
                Log.d(TAG, "onCreate: "+ shape + " " + color);
                if (shape != Shape.BLANK && color != BLANK) {

                    image.setBackgroundColor(Color.WHITE);

                    Bitmap squareBitmap = getSquareBitmap(getDrawableIDFromShape(shape), color, 0.8f);
                    if (i == game.getEyeballRow() && j == game.getEyeballColumn()) {
                        Bitmap eyeballBitmap = getEyeBallBitmap(R.drawable.eyeball, getRotationFromDirection(game.getEyeballDirection()));
                        squareBitmap = combineTwoImagesAsOne(squareBitmap, eyeballBitmap);
//                        TODO find better colour
                        image.setBackgroundColor(Color.YELLOW);
                    }
//                    TODO make better
                    if (game.hasGoalAt(i, j)) {
                        image.setBackgroundColor(Color.RED);
                    }
                    int topMargin = GRID_MARGIN;
                    int bottomMargin = GRID_MARGIN;
                    int leftMargin = GRID_MARGIN;
                    int rightMargin = GRID_MARGIN;
                    if (i == 0) {
                        topMargin *= 2;
                    }
                    if (i == game.getLevelHeight() - 1) {
                        bottomMargin *= 2;
                    }
                    if (j == 0) {
                        leftMargin *= 2;
                    }
                    if (j == game.getLevelWidth() - 1) {
                        rightMargin *= 2;
                    }
                    params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                    image.setImageBitmap(squareBitmap);

                }
//              Set margins for blank spaces
                else {
                    int topMargin = 0;
                    int bottomMargin = 0;
                    int leftMargin = 0;
                    int rightMargin = 0;
                    if (i != 0) {
                        if (game.getShapeAt(i - 1, j) != Shape.BLANK) {
                            topMargin = GRID_MARGIN;
                        }
                    }
                    if (i != game.getLevelHeight() - 1) {
                        if (game.getShapeAt(i + 1, j) != Shape.BLANK) {
                            bottomMargin = GRID_MARGIN;
                        }
                    }
                    if (j != 0) {
                        if (game.getShapeAt(i, j - 1) != Shape.BLANK) {
                            leftMargin = GRID_MARGIN;
                        }
                    }
                    if (j != game.getLevelWidth() - 1) {
                        if (game.getShapeAt(i, j + 1) != Shape.BLANK) {
                            rightMargin = GRID_MARGIN;
                        }
                    }
                    params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                }
                image.setLayoutParams(params);
                gridLayout.addView(image);
            }
        }

        TextView levelTitle = findViewById(R.id.levelTitle);
        String title = "Level " + (currentLevel + 1) + " of " + game.getLevelCount();
        levelTitle.setText(title);
    }


    public Bitmap getSquareBitmap(int drawableId, nz.ac.ara.sjd0364.model.enums.Color color, float scale) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);

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

    public Bitmap getEyeBallBitmap(int drawableId, float rotate) {
        Drawable drawable = ContextCompat.getDrawable(this, drawableId);
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


    private Bitmap combineTwoImagesAsOne(Bitmap oneImage, Bitmap theOtherImage) {
        Bitmap resultImage = Bitmap.createBitmap(theOtherImage.getWidth(),
                theOtherImage.getHeight(),
                Objects.requireNonNull(theOtherImage.getConfig()));
        Canvas canvas = new Canvas(resultImage);
        canvas.drawBitmap(oneImage, 0f, 0f, null);
        canvas.drawBitmap(theOtherImage, 10, 10, null);
        return resultImage;
    }

    private void loadMazesFromFiles() {
        // Load mazes from resources or any other source
        // This is just a placeholder for the actual implementation
        Log.d(TAG, "Loading mazes from files");

        try {
            String[] mazes = getAssets().list("mazes");
            if (mazes != null) {
                for (String asset : mazes) {
                    Log.d(TAG, "Loading maze: " + asset);
                    JSONObject mazeJson = new JSONObject(getAssetAsString("mazes/" + asset));
                    JSONArray mazeSize = mazeJson.getJSONArray("size");
                    int rows = mazeSize.getInt(0);
                    int cols = mazeSize.getInt(1);
                    game.addLevel(rows, cols);
                    JSONArray board = mazeJson.getJSONArray("board");
                    for (int i = 0; i < rows; i++) {
                        JSONArray row = board.getJSONArray(i);
                        for (int j = 0; j < cols; j++) {
                            String cellDate = row.getString(j);
                            Shape shape = getShape(cellDate.substring(0, 1));
                            nz.ac.ara.sjd0364.model.enums.Color color = getColor(cellDate.substring(1));
                            Square square = new PlayableSquare(color, shape);
                            if (shape == Shape.BLANK && color == BLANK) {
                                square = new BlankSquare();
                            }
                            game.addSquare(square, i, j);
                        }
                    }
                    JSONArray startPos = mazeJson.getJSONArray("startPlayer");
                    Direction direction = getDirection(mazeJson.getString("startOrientation"));
                    game.addEyeball(startPos.getInt(0), startPos.getInt(1), direction);

                    JSONArray goals = mazeJson.getJSONArray("goals");
                    for (int i = 0; i < goals.length()-1; i+=2) {
                        int goalRow = goals.getInt(i);
                        int goalCol = goals.getInt(i+1);
                        game.addGoal(goalRow, goalCol);
                    }
                }
            } else {
                Log.w(TAG, "No mazes found in assets");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading mazes", e);
        }
    }

    private String getAssetAsString(String assetFilePath) throws IOException {
        InputStream fileStream = getAssets().open(assetFilePath);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = fileReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        fileReader.close();
        fileStream.close();
        return stringBuilder.toString();
    }

    private Shape getShape(String shape) {
        return switch (shape) {
            case "1" -> Shape.DIAMOND;
            case "2" -> Shape.CROSS;
            case "3" -> Shape.STAR;
            case "4" -> Shape.FLOWER;
            case "5" -> Shape.LIGHTNING;
            case "-" -> Shape.BLANK;
            default -> throw new IllegalArgumentException("Invalid shape: " + shape);
        };
    }

    private nz.ac.ara.sjd0364.model.enums.Color getColor(String color) {
        return switch (color) {
            case "a" -> BLUE;
            case "b" -> RED;
            case "c" -> YELLOW;
            case "d" -> GREEN;
            case "e" -> PURPLE;
            case "-" -> BLANK;
            default -> throw new IllegalArgumentException("Invalid color: " + color);
        };
    }

    private Direction getDirection(String direction) {
        return switch (direction) {
            case "u" -> Direction.UP;
            case "d" -> Direction.DOWN;
            case "l" -> Direction.LEFT;
            case "r" -> Direction.RIGHT;
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        };
    }

    private int getDrawableIDFromShape(Shape shape) {
        return switch (shape) {
            case DIAMOND -> R.drawable.diamond;
            case CROSS -> R.drawable.cross;
            case STAR -> R.drawable.star;
            case FLOWER -> R.drawable.flower;
            case LIGHTNING -> R.drawable.lightning;
            default -> throw new IllegalStateException("Unexpected value: " + shape);
        };
    }

    String getHashCodeFromColor(nz.ac.ara.sjd0364.model.enums.Color color) {
        return switch (color) {
            case BLUE -> "#00ffff";
            case RED -> "#ff0000";
            case YELLOW -> "#ffff00";
            case GREEN -> "#00ff00";
            case BLANK -> "#ffffff";
            case PURPLE -> "#9400d3";
            default -> throw new IllegalStateException("Unexpected value: " + color);
        };
    }

    int getRotationFromDirection(Direction direction) {
        return switch (direction) {
            case UP -> 0;
            case DOWN -> 180;
            case LEFT -> 270;
            case RIGHT -> 90;
        };
    }

}


