package nz.ac.ara.sjd0364;

import static nz.ac.ara.sjd0364.Lookup.GRID_MARGIN;
import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    //    private GridLayout gridLayout;
    private TextView goalCount;
    private TextView levelTitle;

    private Timer levelTimer;
    private ConstraintLayout levelInfoLayout;

    private TextView moveCount;

    private GridLayout gridLayoutMaster;

    private ConstraintLayout main;


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
        gridLayoutMaster = findViewById(R.id.boardFrame);
        main = findViewById(R.id.main);


        nextButton.setOnClickListener(v -> changeLevelOnClick(1));
        previousButton.setOnClickListener(v -> changeLevelOnClick(-1));

        playPauseButton.setOnClickListener(v -> togglePlayPause());

        restartButton.setOnClickListener(v -> {
            resetLeve();

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

    private void resetLeve() {
        levelTimer.stop();
        levelInfoLayout.setVisibility(View.GONE);
        gameController.getCurrentLevelGrid().setVisibility(View.GONE);


        Log.d(TAG, "Restarting game");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to restart the level?");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            gameController.resetCurrentLevel();
            togglePlayPause(false);
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            resumeFromDialog();
        });
        builder.setOnCancelListener(dialog -> {
            resumeFromDialog();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resumeFromDialog() {
        levelTimer.start();
        levelInfoLayout.setVisibility(View.VISIBLE);
        gameController.getCurrentLevelGrid().setVisibility(View.VISIBLE);
    }

    private void init() {
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
        alertBuilder.setPositiveButton("Next Level", (dialog, which) -> {
            changeLevelOnClick(1);
        });
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
            gameController.getCurrentLevelGrid().setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want to change levels? The game will be paused and the level saved.");
            builder.setPositiveButton("Confirm", (dialog, which) -> {
                changeLevel(change);
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                resumeFromDialog();
            });
            builder.setOnCancelListener(dialog -> {
                resumeFromDialog();
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

        if (gameController.getCurrentLevelGrid() != null) {
            main.removeView(gameController.getCurrentLevelGrid());
//            levelInfoLayout.removeAllViews();
        }
        startButton.setText(gameController.getStartButtonText());
    }

    public void togglePlayPause() {
        togglePlayPause(gameController.isPlaying());
    }

    public void togglePlayPause(boolean pause) {
        if (pause) {
            if (levelTimer != null) {
                levelTimer.stop();
            }
            levelInfoLayout.setVisibility(View.INVISIBLE);
            gameController.setPlaying(false);
            startButton.setVisibility(View.VISIBLE);
            undoButton.setEnabled(false);
            restartButton.setEnabled(false);
            playPauseButton.setImageResource(R.drawable.play);
            if (gameController.getCurrentLevelGrid() != null) {
                gameController.getCurrentLevelGrid().setVisibility(View.GONE);
            }
            startButton.setText(gameController.getStartButtonText());
        } else {
            gameController.setPlaying(true);
            startButton.setVisibility(View.GONE);
            if (gameController.getMoveCount() != 0) {
                undoButton.setEnabled(true);
            }
            playPauseButton.setImageResource(R.drawable.pause);
            restartButton.setEnabled(true);
            renderCurrentLevel();
        }
    }

    public void enableUndoButton(boolean enabled) {
        undoButton.setEnabled(enabled);
    }

    private void renderCurrentLevel() {
        GridLayout gridLayout;
        if (gameController.getCurrentLevelGrid() != null) {
            gridLayout = gameController.getCurrentLevelGrid();
            gridLayout.setVisibility(View.VISIBLE);
        } else {
            gridLayout = getGameBoardView();
            gameController.addCurrentLevelGrid(gridLayout);
        }

        if (main.indexOfChild(gridLayout) == -1) {
            main.addView(gridLayout);
        }

        if (levelTimer != null) {
            levelInfoLayout.removeView(levelTimer);
        }

        levelTitle.setText(gameController.getLevelTitle());

        goalCount.setVisibility(View.VISIBLE);
        goalCount.setText(gameController.getGoalCountText());

        gameController.render();

        moveCount.setText(gameController.getMoveCountText());
        moveCount.setVisibility(View.VISIBLE);

        levelTimer = gameController.getCurrentLevelTimer();

        levelInfoLayout = findViewById(R.id.levelInfo);
        if (levelInfoLayout.indexOfChild(levelTimer) == -1) {
            levelInfoLayout.addView(levelTimer);
        }
//        levelInfoLayout.addView(moveCount);

        if (!gameController.isLevelCompleted()) {
            levelTimer.start();
        }
        levelInfoLayout.setVisibility(View.VISIBLE);
    }

    @NonNull
    private GridLayout getGameBoardView() {
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setLayoutParams(gridLayoutMaster.getLayoutParams());

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
        return gridLayout;
    }

    public PlayableSquareView getSquareView(int row, int column) {
        if (gameController.getCurrentLevelGrid() != null) {
            View view = gameController.getCurrentLevelGrid().getChildAt(row * gameController.getGame().getLevelWidth() + column);
            if (view instanceof PlayableSquareView) {
                return (PlayableSquareView) view;
            }
        }
        return null;
    }

    public PlayableSquareView getSquareView(Coordinate coordinate) {
        return getSquareView(coordinate.row(), coordinate.column());
    }

}


