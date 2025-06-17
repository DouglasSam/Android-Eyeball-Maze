package nz.ac.ara.sjd0364;

import static nz.ac.ara.sjd0364.Lookup.GRID_MARGIN;
import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nz.ac.ara.sjd0364.model.Game;
import nz.ac.ara.sjd0364.model.enums.Shape;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private GameController gameController;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private ImageButton undoButton;
    private ImageButton restartButton;
    private ImageButton playPauseButton;
    private Button startButton;

    private GridLayout gridLayout;
    private TextView goalCount;
    private TextView levelTitle;

    private Timer levelTimer;
    private ConstraintLayout levelInfoLayout;

    private TextView moveCount;


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

        gameController = new GameController(this);

        goalCount = findViewById(R.id.goalCount);

//        Set up the level change buttons
        nextButton = findViewById(R.id.nextLevel);
        previousButton = findViewById(R.id.backLevel);
        undoButton = findViewById(R.id.undoMove);
        restartButton = findViewById(R.id.restart);
        playPauseButton = findViewById(R.id.playPause);
        levelTitle = findViewById(R.id.levelTitle);
        moveCount = findViewById(R.id.moveCount);


        nextButton.setOnClickListener(v -> changeLevelOnClick(1));
        previousButton.setOnClickListener(v -> changeLevelOnClick(-1));

        playPauseButton.setOnClickListener(v -> togglePlayPause());

        restartButton.setOnClickListener(v -> {
            Log.d(TAG, "Restarting game");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to restart the level?");
            builder.setPositiveButton("Confirm", (dialog, which) -> init());
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        });

        undoButton.setOnClickListener(v -> {
            gameController.undoMove();
            moveCount.setText(gameController.getMoveCountText());
        });



        startButton = findViewById(R.id.startLevel);

        init();

//        TODO remove when not testing
//        startButton.callOnClick();


    }

    private void init() {
        if (gridLayout != null) {
            gridLayout.setVisibility(View.GONE);
            gridLayout = null;
            levelInfoLayout.removeView(levelTimer);
            moveCount.setVisibility(View.INVISIBLE);
        }

        gameController.init();

        goalCount.setVisibility(View.INVISIBLE);

        undoButton.setEnabled(false);
        restartButton.setEnabled(false);

        previousButton.setEnabled(gameController.getCurrentLevel() != 0);
        nextButton.setEnabled(gameController.getCurrentLevel() != gameController.getGame().getLevelCount() - 1);

        playPauseButton.setImageResource(R.drawable.play);

        startButton.setText(gameController.getStartButtonText());
        startButton.setVisibility(View.VISIBLE);
        startButton.setOnClickListener(v -> this.togglePlayPause());


    }

    public void updateMoves(Move move, boolean onGoal) {

        gameController.addMove(move);
        enableUndoButton(true);
        moveCount.setText(gameController.getMoveCountText());
        if (onGoal) {
            goalCount.setText(gameController.getGoalCountText());
            if (gameController.getGame().getGoalCount() == 0) {
                playWinSound();
                Log.d(TAG, "Level complete");

                levelTimer.stop();

                gameController.completeLevel();

                Dialog dialog = getEndLevelDialog();
                dialog.show();
            }
        }
    }

    private Dialog getEndLevelDialog() {
        String message = gameController.getEndText();

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Level Complete");
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton("Next Level", (dialog, which) -> changeLevelOnClick(1));
        alertBuilder.setNegativeButton("View Level", (dialog, which) -> {

            playPauseButton.setEnabled(false);
            undoButton.setEnabled(false);
        });

        return alertBuilder.create();
    }


    public void changeLevelOnClick(int change) {

        if (gameController.isPlaying() && !gameController.isLevelCompleted()) {
            levelTimer.stop();
            levelInfoLayout.setVisibility(View.GONE);
            gridLayout.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to change levels? The game will be paused and the level saved.");
            builder.setPositiveButton("Confirm", (dialog, which) -> changeLevel(change));
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                levelTimer.start();
                gridLayout.setVisibility(View.VISIBLE);
                levelInfoLayout.setVisibility(View.VISIBLE);
            });
            builder.setOnCancelListener(dialog -> {
                levelTimer.start();
                gridLayout.setVisibility(View.VISIBLE);
                levelInfoLayout.setVisibility(View.VISIBLE);
            });
            builder.create().show();

        } else {
            changeLevel(change);
        }


    }

    private void changeLevel(int change) {
        togglePlayPause(true);
        gameController.changeCurrentLevel(change);
        gameController.setCurrentLevel();

        goalCount.setVisibility(View.INVISIBLE);

        playPauseButton.setEnabled(true);
        previousButton.setEnabled(!gameController.isFirstLevel());
        nextButton.setEnabled(!gameController.isLastLevel());

        levelTitle.setText(R.string.app_name);


        if (gridLayout != null) {
            gridLayout = null;
            levelInfoLayout.removeView(levelTimer);
            moveCount.setVisibility(View.INVISIBLE);
//            levelInfoLayout.removeAllViews();
        }
        startButton.setText(gameController.getStartButtonText());
    }

    public void togglePlayPause() {
        playClickClackSound();
        togglePlayPause(gameController.isPlaying());
    }

    public void togglePlayPause(boolean pause) {
        if (pause) {
            if (levelTimer != null) {
                levelTimer.stop();
                levelInfoLayout.setVisibility(View.INVISIBLE);
            }
            gameController.setPlaying(false);
            startButton.setVisibility(View.VISIBLE);
            undoButton.setEnabled(false);
            restartButton.setEnabled(false);
            playPauseButton.setImageResource(R.drawable.play);
            if (gridLayout != null) {
                gridLayout.setVisibility(View.GONE);
                startButton.setText(gameController.getStartButtonText());
            }
        } else {
            gameController.setPlaying(true);
            startButton.setVisibility(View.GONE);
            playPauseButton.setImageResource(R.drawable.pause);
            restartButton.setEnabled(true);
            if (gridLayout == null) {
                renderCurrentLevel();
            } else {
                gridLayout.setVisibility(View.VISIBLE);
                levelTimer.start();
                levelInfoLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void enableUndoButton(boolean enabled) {
        undoButton.setEnabled(enabled);
    }

    private void renderCurrentLevel() {
        gridLayout = findViewById(R.id.boardFrame);
        gridLayout.setVisibility(View.VISIBLE);

//        clear the grid layout
        gridLayout.removeAllViews();
        gridLayout.setBackgroundColor(Color.BLACK);

        Log.d(TAG, "rendering: " + gameController.getCurrentLevel());

        Game game = gameController.getGame();


        for (int i = 0; i < game.getLevelHeight(); i++) {
            for (int j = 0; j < game.getLevelWidth(); j++) {

                Shape shape = game.getShapeAt(i, j);
                nz.ac.ara.sjd0364.model.enums.Color color = game.getColorAt(i, j);

                ImageView image;
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(i, 1f);
                params.columnSpec = GridLayout.spec(j, 1f);

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

        levelTitle.setText(gameController.getLevelTitle());

        goalCount.setVisibility(View.VISIBLE);
        goalCount.setText(gameController.getGoalCountText());

        gameController.render();


        moveCount.setText(gameController.getMoveCountText());
        moveCount.setVisibility(View.VISIBLE);

        levelTimer = gameController.getCurrentLevelTimer();

        levelInfoLayout = findViewById(R.id.levelInfo);
        levelInfoLayout.addView(levelTimer);
//        levelInfoLayout.addView(moveCount);

        if (!gameController.isLevelCompleted()) {
            levelTimer.start();
        }
        levelInfoLayout.setVisibility(View.VISIBLE);
    }

    public PlayableSquareView getSquareView(int row, int column) {
        if (gridLayout != null) {
            View view = gridLayout.getChildAt(row * gameController.getGame().getLevelWidth() + column);
            if (view instanceof PlayableSquareView) {
                return (PlayableSquareView) view;
            }
        }
        return null;
    }

    public PlayableSquareView getSquareView(Coordinate coordinate) {
        return getSquareView(coordinate.row(), coordinate.column());
    }



    protected String getAssetAsString(String assetFilePath) throws IOException {
//        TODO replace with GSON
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

    public void playDingSound() {
        playSound(R.raw.ding);

    }

    public void playDudSound() {
        playSound(R.raw.dud);
    }

    /**
     * Win sound courtesy of <a href="https://uppbeat.io/sfx/puff-of-magic-treasure-chest-heavy/644/1179">https://uppbeat.io/sfx/puff-of-magic-treasure-chest-heavy/644/1179</a>
     */
    public void playWinSound() {
        playSound(R.raw.win);
    }

    public void playClickClackSound() {
        playSound(R.raw.clickclack);
    }

    private void playSound(int soundResource) {
        MuteButton muteButton = findViewById(R.id.muteButton);
        if (muteButton != null && !muteButton.isMuted()) {
            MediaPlayer mediaPlayer = MediaPlayer.create(this, soundResource);
            mediaPlayer.setOnCompletionListener(MediaPlayer::release); // Release resources when done
            mediaPlayer.start(); // no need to call prepare(); create() does that for you
        }
    }
}


