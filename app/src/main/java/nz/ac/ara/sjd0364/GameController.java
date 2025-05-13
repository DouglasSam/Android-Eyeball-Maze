package nz.ac.ara.sjd0364;

import static nz.ac.ara.sjd0364.Lookup.getDirection;
import static nz.ac.ara.sjd0364.Lookup.getShape;
import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nz.ac.ara.sjd0364.model.BlankSquare;
import nz.ac.ara.sjd0364.model.Game;
import nz.ac.ara.sjd0364.model.PlayableSquare;
import nz.ac.ara.sjd0364.model.enums.Direction;
import nz.ac.ara.sjd0364.model.enums.Message;
import nz.ac.ara.sjd0364.model.enums.Shape;
import nz.ac.ara.sjd0364.model.interfaces.Square;

public class GameController {

    private static final String TAG = "GameController";
    private Game game;
    private final MainActivity context;
    private int currentLevel = 0;
    private boolean isPlaying = false;
    private Map<Integer, Timer> levelTimeMap;

    private Map<Integer, List<MainActivity.Coordinate>> levelMoves;

    public GameController(MainActivity context) {
        this.context = context;
        this.game = new Game();

        loadMessageStringsFromFiles();
    }

    public void init() {
        isPlaying = false;

        game = new Game();

        loadMazesFromFiles();

        levelTimeMap = new HashMap<>();
        levelMoves = new HashMap<>();

        game.setLevel(currentLevel);
    }

    public void render() {
        if (levelMoves.get(currentLevel) == null) {
            levelMoves.put(currentLevel, new java.util.ArrayList<>());
        }

        Timer levelTimer;

        if (levelTimeMap.get(currentLevel) != null) {
            levelTimer = levelTimeMap.get(currentLevel);
        } else {
            levelTimer = new Timer(context);
        }

        levelTimeMap.put(currentLevel, levelTimer);

    }

    public Timer getCurrentLevelTimer() {
        return levelTimeMap.get(currentLevel);
    }


    public int getCurrentLevel() {
        return currentLevel;
    }

    public void changeCurrentLevel(int change) {
        this.currentLevel += change;
    }

    public void setCurrentLevel() {
        game.setLevel(currentLevel);
    }

    public boolean isFirstLevel() {
        return currentLevel == 0;
    }

    public boolean isLastLevel() {
        return currentLevel == game.getLevelCount() - 1;
    }

    public String getStartButtonText() {
        String prefix = "Start";
        if (levelTimeMap.containsKey(currentLevel)) {
            prefix = "Resume";
        }

        return context.getResources().getString(R.string.start_resume_level,
                prefix, currentLevel + 1);
    }

    public String getLevelTitle() {
        return context.getResources().getString(R.string.level_count, (currentLevel + 1), game.getLevelCount());
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Map<Integer, Timer> getLevelTimeMap() {
        return levelTimeMap;
    }

    public void setLevelTimeMap(Map<Integer, Timer> levelTimeMap) {
        this.levelTimeMap = levelTimeMap;
    }

    public Map<Integer, List<MainActivity.Coordinate>> getLevelMoves() {
        return levelMoves;
    }

    public void setLevelMoves(Map<Integer, List<MainActivity.Coordinate>> levelMoves) {
        this.levelMoves = levelMoves;
    }

    public Game getGame() {
        return game;
    }

    public String getEndText() {
        return "You have completed the level!\n" +
                "Time: " + levelTimeMap.get(currentLevel).getFormattedElapsedTimeToMillis() + "\n" +
                "Moves: " + levelMoves.get(currentLevel).size() + "\n" +
                "Level: " + (currentLevel + 1) + "/" + game.getLevelCount();
    }

    private void loadMessageStringsFromFiles() {
        try {
            String messageJsonString = context.getAssetAsString("messages.json");
            JSONObject messageJson = new JSONObject(messageJsonString);
            Arrays.stream(Message.values()).forEach(message -> {
                try {
                    if (!message.equals(Message.OK)) {
                        JSONObject messageObject = messageJson.getJSONObject(message.toString());
                        String messageString = messageObject.getString("message");
                        String description = messageObject.getString("description");
                        Lookup.messageMap.put(message, new MessageString(messageString, description));
                    }
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

    protected void loadMazesFromFiles() {
        // Load mazes from resources or any other source
        // This is just a placeholder for the actual implementation
        Log.d(TAG, "Loading mazes from files");

        try {
            String[] mazes = context.getAssets().list("mazes");
            if (mazes != null) {
                for (String asset : mazes) {
                    Log.d(TAG, "Loading maze: " + asset);
                    JSONObject mazeJson = new JSONObject(context.getAssetAsString("mazes/" + asset));
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
}
