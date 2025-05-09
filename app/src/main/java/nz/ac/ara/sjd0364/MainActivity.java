package nz.ac.ara.sjd0364;

import static nz.ac.ara.sjd0364.Lookup.GRID_MARGIN;
import static nz.ac.ara.sjd0364.Lookup.getDirection;
import static nz.ac.ara.sjd0364.Lookup.getShape;
import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import nz.ac.ara.sjd0364.model.BlankSquare;
import nz.ac.ara.sjd0364.model.Game;
import nz.ac.ara.sjd0364.model.PlayableSquare;
import nz.ac.ara.sjd0364.model.enums.Direction;
import nz.ac.ara.sjd0364.model.enums.Message;
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

        loadMessageStringsFromFiles();

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
//        startButton.callOnClick();
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

                ImageView image;
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(i, 1f);
                params.columnSpec = GridLayout.spec(j, 1f);

                Log.d(TAG, "onCreate: "+ shape + " " + color);
                if (shape != Shape.BLANK && color != BLANK) {
                    image = new PlayableSquareView(this, i, j, game, params);
                }
//              Set margins for blank spaces
                else {
                    image = new ImageView(this);
                    image.setBackgroundColor(getColor(R.color.background));
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
                    image.setLayoutParams(params);
                }

                gridLayout.addView(image);
            }
        }

        TextView levelTitle = findViewById(R.id.levelTitle);
        levelTitle.setText(getResources().getString(R.string.level_count, (currentLevel + 1), game.getLevelCount()));

        TextView goalCount = findViewById(R.id.goalCount);
        goalCount.setVisibility(View.VISIBLE);
        goalCount.setText(getResources().getString(R.string.goal_count, game.getCompletedGoalCount(), game.getGoalCount()));

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
                            nz.ac.ara.sjd0364.model.enums.Color color = Lookup.getColorFromChar(cellDate.charAt(1));
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


    private void loadMessageStringsFromFiles() {
        try {
            String messageJsonString = getAssetAsString("messages.json");
            JSONObject messageJson = new JSONObject(messageJsonString);
            Arrays.stream(Message.values()).forEach(message -> {
                try {
                    JSONObject messageObject = messageJson.getJSONObject(message.toString());
                    String messageString = messageObject.getString("message");
                    String description = messageObject.getString("description");
                    Lookup.messageMap.put(message, new MessageString(messageString, description));
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing message string for " + message, e);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "Error loading message strings", e);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing message strings", e);
        }

    }
}


