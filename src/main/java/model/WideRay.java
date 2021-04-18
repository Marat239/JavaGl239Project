package model;

public class WideRay extends Polygon
{
    public WideRay(Point p1, Point p2)
    {
        this(new Vector(p1, p2));
    }

    public WideRay(Vector v)
    {
        addVector(v);
        Vector ray1 = v.getOrt(Shared.InfiniteLen);
        addVector(ray1);
        Vector backSide = ray1.getOrt(v.len());
        addVector(backSide);
        Vector ray2 = new Vector(backSide.To, v.From);
        addVector(ray2);
    }

    public static WideRay createRandom()
    {
        Point p1 = Point.createRandom();
        Point p2;
        do
        {
            p2 = Point.createRandom();
        } while (p1.equals(p2));

        return new WideRay(p1, p2);
    }
}
