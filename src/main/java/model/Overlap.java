package model;

public class Overlap
{
    public Triangle Triangle;
    public WideRay WideRay;
    public double Square;
    public Polygon Intersection;

    public Overlap(Triangle t, WideRay w, Polygon p, double s)
    {
        Triangle = t;
        WideRay = w;
        Intersection = p;
        Square = s;
    }
}
