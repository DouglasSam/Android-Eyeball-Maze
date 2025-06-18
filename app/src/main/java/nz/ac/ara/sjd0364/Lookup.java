package nz.ac.ara.sjd0364;

import static nz.ac.ara.sjd0364.model.enums.Color.BLANK;
import static nz.ac.ara.sjd0364.model.enums.Color.BLUE;
import static nz.ac.ara.sjd0364.model.enums.Color.GREEN;
import static nz.ac.ara.sjd0364.model.enums.Color.PURPLE;
import static nz.ac.ara.sjd0364.model.enums.Color.RED;
import static nz.ac.ara.sjd0364.model.enums.Color.YELLOW;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import nz.ac.ara.sjd0364.model.enums.Direction;
import nz.ac.ara.sjd0364.model.enums.Message;
import nz.ac.ara.sjd0364.model.enums.Shape;

public class Lookup {

    protected static final int GRID_MARGIN = 7;

    protected static Map<Message, MessageString> messageMap = new HashMap<>();

    protected static Shape getShape(String shape) {
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

    protected static nz.ac.ara.sjd0364.model.enums.Color getColorFromChar(char color) {
        return switch (color) {
            case 'a' -> BLUE;
            case 'b' -> RED;
            case 'c' -> YELLOW;
            case 'd' -> GREEN;
            case 'e' -> PURPLE;
            case '-' -> BLANK;
            default -> throw new IllegalArgumentException("Invalid color: " + color);
        };
    }

    protected static Direction getDirection(String direction) {
        return switch (direction) {
            case "u" -> Direction.UP;
            case "d" -> Direction.DOWN;
            case "l" -> Direction.LEFT;
            case "r" -> Direction.RIGHT;
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        };
    }

    protected static int getDrawableIDFromShape(Shape shape) {
        return switch (shape) {
            case DIAMOND -> R.drawable.diamond;
            case CROSS -> R.drawable.cross;
            case STAR -> R.drawable.star;
            case FLOWER -> R.drawable.flower;
            case LIGHTNING -> R.drawable.lightning;
            default -> throw new IllegalStateException("Unexpected value: " + shape);
        };
    }

    protected static String getHashCodeFromColor(nz.ac.ara.sjd0364.model.enums.Color color) {
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

    protected static int getRotationFromDirection(Direction direction) {
        return switch (direction) {
            case UP -> 0;
            case DOWN -> 180;
            case LEFT -> 270;
            case RIGHT -> 90;
        };
    }

    /**
     *
     * courtesy of Luofeng
     * @param oneImage
     * @param theOtherImage
     * @return
     */
    protected static Bitmap combineTwoImagesAsOne(Bitmap oneImage, Bitmap theOtherImage) {
        Bitmap resultImage = Bitmap.createBitmap(theOtherImage.getWidth(),
                theOtherImage.getHeight(),
                Objects.requireNonNull(theOtherImage.getConfig()));
        Canvas canvas = new Canvas(resultImage);
        canvas.drawBitmap(oneImage, 0f, 0f, null);
        canvas.drawBitmap(theOtherImage, 10, 10, null);
        return resultImage;
    }

    protected static String getAssetAsString(Context context, String assetFilePath) throws IOException {
//        TODO replace with GSON
        InputStream fileStream = context.getAssets().open(assetFilePath);
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


}
