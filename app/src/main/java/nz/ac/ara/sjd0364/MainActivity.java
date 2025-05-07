package nz.ac.ara.sjd0364;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
import nz.ac.ara.sjd0364.model.PlayableSquare;
import nz.ac.ara.sjd0364.model.enums.Direction;
import nz.ac.ara.sjd0364.model.enums.Shape;
import nz.ac.ara.sjd0364.model.interfaces.Square;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    Game game;

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

//        FrameLayout frame = findViewById(R.id.boardFrame);
//
//        LinearLayout verticalLayout = new LinearLayout(this);
//        verticalLayout.setOrientation(LinearLayout.VERTICAL);
////        verticalLayout.
//        frame.addView(verticalLayout);
        GridLayout gridLayout = findViewById(R.id.boardFrame);


//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < 5; j++) {
////                ImageButton button = new ImageButton(this);
////                button.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.flower));
////                horizontalLayout.addView(button);
//                ImageView image = new ImageView(this);
//                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//                params.width = 0;
//                params.height = 0;
//                params.rowSpec = GridLayout.spec(i, 1f);
//                params.columnSpec = GridLayout.spec(j, 1f);
//                image.setLayoutParams(params);
//                image.setBackgroundColor(Color.parseColor("#ffffff"));
////                image.setPadding(1,1,1,1);
////                GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.flower);
////                drawable.setColor(Color.RED); // Set new color
//                Drawable drawable = AppCompatResources.getDrawable(this, list.get(j % list.size()));
////                drawable.
//                if (drawable != null) {
//                    drawable.setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
//                }
//                else {
//                    throw new NullPointerException("Drawable is null");
//                }
//                image.setImageDrawable(drawable);
//                gridLayout.addView(image);
//            }
//        }
        System.out.println(gridLayout.getHeight());
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
                            if (shape == Shape.BLANK && color == nz.ac.ara.sjd0364.model.enums.Color.BLANK) {
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
            case "a" -> nz.ac.ara.sjd0364.model.enums.Color.BLUE;
            case "b" -> nz.ac.ara.sjd0364.model.enums.Color.RED;
            case "c" -> nz.ac.ara.sjd0364.model.enums.Color.YELLOW;
            case "d" -> nz.ac.ara.sjd0364.model.enums.Color.GREEN;
            case "e" -> nz.ac.ara.sjd0364.model.enums.Color.PURPLE;
            case "-" -> nz.ac.ara.sjd0364.model.enums.Color.BLANK;
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
}


