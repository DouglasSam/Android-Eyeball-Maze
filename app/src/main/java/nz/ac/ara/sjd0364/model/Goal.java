package nz.ac.ara.sjd0364.model;

import androidx.annotation.NonNull;

/**
 * Where the player needs to get to.
 *
 * @author sjd0364 - Samuel Douglas
 */
public record Goal(int row, int column) {

    @NonNull
    @Override
    public String toString() {
        return row+" "+column;
    }
}
