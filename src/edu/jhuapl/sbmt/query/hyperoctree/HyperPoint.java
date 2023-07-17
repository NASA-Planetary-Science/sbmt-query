package edu.jhuapl.sbmt.query.hyperoctree;

public interface HyperPoint extends Dimensioned
{
    public double getCoordinate(int i);
    public double[] get();
}
