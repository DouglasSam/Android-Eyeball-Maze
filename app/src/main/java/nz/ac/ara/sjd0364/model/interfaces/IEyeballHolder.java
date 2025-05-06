package nz.ac.ara.sjd0364.model.interfaces;

import nz.ac.ara.sjd0364.model.enums.Direction;

public interface IEyeballHolder {
    public void addEyeball(int row, int column, Direction direction);
    public int getEyeballRow();
    public int getEyeballColumn();
    public Direction getEyeballDirection();
}