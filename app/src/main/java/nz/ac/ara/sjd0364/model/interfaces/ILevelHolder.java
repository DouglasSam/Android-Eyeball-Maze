package nz.ac.ara.sjd0364.model.interfaces;

public interface ILevelHolder {
    public void addLevel(int height, int width);
    public int getLevelWidth();
    public int getLevelHeight();
    public void setLevel(int levelNumber);
    public int getLevelCount();
}

