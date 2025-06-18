package nz.ac.ara.sjd0364;

import android.util.Log;
import android.widget.GridLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import nz.ac.ara.sjd0364.model.Game;
import nz.ac.ara.sjd0364.model.enums.Message;

public class GameController {

    private static final String TAG = "GameController";
    private final Game game;
    private final MainActivity context;
    private int currentLevel = 0;
    private boolean isPlaying = false;
    private Map<Integer, Timer> levelTimeMap;

    private final Map<Integer, List<Move>> levelMoves;

    private final Map<Integer, Boolean> levelCompletedMap;

    private final Map<Integer, GridLayout> levelGridMap;
    private final MazeReader mazeReader;

    public GameController(MainActivity context) {
        this.context = context;
        this.game = new Game();

        levelTimeMap = new HashMap<>();
        levelMoves = new HashMap<>();
        levelCompletedMap = new HashMap<>();
        levelGridMap = new HashMap<>();

        mazeReader = new MazeReader(context, game);

        loadMessageStringsFromFiles();
    }

    public void undoMove() {
        if (levelMoves.containsKey(currentLevel) && !levelMoves.get(currentLevel).isEmpty()) {
            Move lastMove = levelMoves.compute(currentLevel, (level, moves) -> {
                if (moves.size() == 1) {
                    context.enableUndoButton(false);
                }
                return moves;
            }).remove(levelMoves.get(currentLevel).size() - 1);

            PlayableSquareView currentSquareView = context.getSquareView(game.getEyeballRow(), game.getEyeballColumn());

            if (currentSquareView.isWasGoal()) {
                game.addGoal(
                        currentSquareView.getCoordinate().row(),
                        currentSquareView.getCoordinate().column());
                game.addSquare(
                        currentSquareView.getOriginalPlayableSquare(),
                        currentSquareView.getCoordinate().row(),
                        currentSquareView.getCoordinate().column());
                currentSquareView.setGoal();

            }
            currentSquareView.toggleEyeballRendering();

            Coordinate coordinate = lastMove.squareView().getCoordinate();

            game.addEyeball(
                    coordinate.row(),
                    coordinate.column(),
                    lastMove.direction());

            lastMove.squareView().toggleEyeballRendering();

        } else {
            Log.d(TAG, "No moves to undo");
        }
    }

    public void init() {
        isPlaying = false;

        mazeReader.loadAllMazesFromFiles();

//        Reset all details about the current level
        levelTimeMap.remove(currentLevel);
        levelMoves.remove(currentLevel);
        levelCompletedMap.remove(currentLevel);
        levelGridMap.remove(currentLevel);

        game.setLevel(currentLevel);
    }

    public void addCurrentLevelGrid(GridLayout gridLayout) {
        levelGridMap.put(currentLevel, gridLayout);
    }

    public void resetCurrentLevel() {
        levelMoves.computeIfPresent(currentLevel, (level, moves) -> {
            moves.clear();
            return moves;
        });
        levelCompletedMap.put(currentLevel, false);
        levelTimeMap.put(currentLevel, new Timer(context));
        mazeReader.loadSpecificMazeFromFile(currentLevel);
        levelGridMap.remove(currentLevel);
    }

    public GridLayout getCurrentLevelGrid() {
        return levelGridMap.get(currentLevel);
    }

    public void render() {
        levelMoves.computeIfAbsent(currentLevel, k -> new java.util.ArrayList<>());


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

    public void addMove(Move move) {
        if (!levelMoves.containsKey(currentLevel)) {
            levelMoves.put(currentLevel, new java.util.ArrayList<>());
        }
        levelMoves.get(currentLevel).add(move);
    }

    public void completeLevel() {
        levelCompletedMap.put(currentLevel, true);
    }

    public boolean isLevelCompleted() {
        return Boolean.TRUE.equals(levelCompletedMap.getOrDefault(currentLevel, false));
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

    public String getGoalCountText() {
        String r;
        if (game.getGoalCount() == 0) {
            r = context.getResources().getString(R.string.goal_count, game.getCompletedGoalCount(), game.getCompletedGoalCount());
        } else {
            r = context.getResources().getString(R.string.goal_count, game.getCompletedGoalCount(), game.getGoalCount() + game.getCompletedGoalCount());
        }
        return r;
    }

    public String getMoveCountText() {
        return context.getResources().getString(R.string.move_count, getMoveCount());
    }

    public int getMoveCount() {
        return levelMoves.getOrDefault(currentLevel, new ArrayList<>()).size();
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
        if (levelCompletedMap.containsKey(currentLevel)) {
            prefix = "View";
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
//        levelMoves.compute(currentLevel, (level, moves) -> new ArrayList<>());
        isPlaying = playing;
    }

    public Map<Integer, Timer> getLevelTimeMap() {
        return levelTimeMap;
    }

    public void setLevelTimeMap(Map<Integer, Timer> levelTimeMap) {
        this.levelTimeMap = levelTimeMap;
    }

    public Game getGame() {
        return game;
    }

    public String getEndText() {
        return "You have completed the level!\nTime: " +
                Objects.requireNonNull(levelTimeMap.get(currentLevel)).getFormattedElapsedTimeToMillis()
                + "\nMoves: " +
                Objects.requireNonNull(levelMoves.get(currentLevel)).size() +
                "\nLevel: " + (currentLevel + 1) + "/" + game.getLevelCount();
    }

    private void loadMessageStringsFromFiles() {
        try {
            String messageJsonString = Lookup.getAssetAsString(context, "messages.json");
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


}
