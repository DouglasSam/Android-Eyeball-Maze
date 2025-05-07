package nz.ac.ara.sjd0364;

import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;
import static nz.ac.ara.sjd0364.model.enums.Color.BLUE;
import static nz.ac.ara.sjd0364.model.enums.Color.GREEN;
import static nz.ac.ara.sjd0364.model.enums.Color.PURPLE;
import static nz.ac.ara.sjd0364.model.enums.Color.RED;
import static nz.ac.ara.sjd0364.model.enums.Color.YELLOW;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nz.ac.ara.sjd0364.model.BlankSquare;
import nz.ac.ara.sjd0364.model.Game;
import nz.ac.ara.sjd0364.model.Level;
import nz.ac.ara.sjd0364.model.PlayableSquare;
import nz.ac.ara.sjd0364.model.enums.Direction;
import nz.ac.ara.sjd0364.model.enums.Shape;
import nz.ac.ara.sjd0364.model.interfaces.Square;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

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

        renderCurrentLevel();

    }

    public void changeLevelOnClick(int change, Button nextButton, Button previousButton) {
        currentLevel += change;
        game.setLevel(currentLevel);

        previousButton.setEnabled(currentLevel != 0);
        nextButton.setEnabled(currentLevel != game.getLevelCount() - 1);

        renderCurrentLevel();
    }


    private void renderCurrentLevel() {
        GridLayout gridLayout = findViewById(R.id.boardFrame);

//        clear the grid layout
        gridLayout.removeAllViews();

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
                image.setLayoutParams(params);
                image.setBackgroundColor(Color.parseColor("#ffffff"));
                Log.d(TAG, "onCreate: "+ shape + " " + color);
                if (shape != Shape.BLANK && color != BLANK) {

                    Drawable drawable = AppCompatResources.getDrawable(this, getDrawableIDFromShape(shape));
                    if (drawable != null) {
                        drawable.setColorFilter(Color.parseColor(getHashCodeFromColor(color)), PorterDuff.Mode.MULTIPLY);
                    } else {
                        throw new NullPointerException("Drawable is null");
                    }
                    image.setImageDrawable(drawable);
                }
                gridLayout.addView(image);
            }
        }

        TextView levelTitle = findViewById(R.id.levelTitle);
        String title = "Level " + (currentLevel + 1) + " of " + game.getLevelCount();
        levelTitle.setText(title);
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
}


