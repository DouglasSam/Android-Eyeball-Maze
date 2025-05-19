package nz.ac.ara.sjd0364;

import static nz.ac.ara.sjd0364.Lookup.getDirection;
import static nz.ac.ara.sjd0364.Lookup.getShape;
import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import nz.ac.ara.sjd0364.model.BlankSquare;
import nz.ac.ara.sjd0364.model.Game;
import nz.ac.ara.sjd0364.model.PlayableSquare;
import nz.ac.ara.sjd0364.model.enums.Direction;
import nz.ac.ara.sjd0364.model.enums.Shape;
import nz.ac.ara.sjd0364.model.interfaces.Square;

public class MazeReader {

    private static final String TAG = "MazeReader";

    private final MainActivity context;

    private final Game game;

    public MazeReader(MainActivity context, Game game) {
        this.context = context;
        this.game = game;
    }

    public void loadSpecificMazeFromFile(int mazeNumber) {
        try {
            game.setLevel(mazeNumber);

            findMazeNumber(mazeNumber);

            JSONObject mazeJson = findMazeNumber(mazeNumber);
            if (mazeJson == null) {
                Log.e(TAG, "Maze not found");
                return;
            }
            JSONArray startPos = mazeJson.getJSONArray("startPlayer");
            Direction direction = getDirection(mazeJson.getString("startOrientation"));

            game.addEyeball(startPos.getInt(0), startPos.getInt(1), direction);

            JSONArray goals = mazeJson.getJSONArray("goals");
            for (int i = 0; i < goals.length() - 1; i += 2) {
                int goalRow = goals.getInt(i);
                int goalCol = goals.getInt(i + 1);
                game.addGoal(goalRow, goalCol);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading maze", e);

        }
    }

    private JSONObject findMazeNumber(int mazeNumber) {
        try {
            String[] mazes = context.getAssets().list("mazes");
            if (mazes != null) {
                for (String asset : mazes) {
                    Log.d(TAG, "Loading maze: " + asset);
                    JSONObject mazeJson = new JSONObject(Lookup.getAssetAsString(context, "mazes/" + asset));
                    int mazeNum = mazeJson.getInt("number");
                    if (mazeNum == mazeNumber + 1) {
                        return mazeJson;
                    }

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error finding maze number", e);
        }
        return null;
    }

    protected void loadAllMazesFromFiles() {
        // Load mazes from resources or any other source
        // This is just a placeholder for the actual implementation
        Log.d(TAG, "Loading mazes from files");

        try {
            String[] mazes = context.getAssets().list("mazes");
            if (mazes != null) {
                for (String asset : mazes) {
                    Log.d(TAG, "Loading maze: " + asset);
                    JSONObject mazeJson = new JSONObject(Lookup.getAssetAsString(context, "mazes/" + asset));
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
                    for (int i = 0; i < goals.length() - 1; i += 2) {
                        int goalRow = goals.getInt(i);
                        int goalCol = goals.getInt(i + 1);
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
