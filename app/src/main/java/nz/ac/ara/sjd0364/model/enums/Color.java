package nz.ac.ara.sjd0364.model.enums;

/**
 * Enum that holds the potential colors of the squares.
 */
public enum Color {
    BLUE, RED, YELLOW, GREEN, BLANK, PURPLE;

    String getHashCode() {
        return switch (this) {
            case BLUE -> "#00ffff";
            case RED -> "#ff0000";
            case YELLOW -> "#ffff00";
            case GREEN -> "#00ff00";
            case BLANK -> "#ffffff";
            case PURPLE -> "#9400d3";
        };
    }
}

