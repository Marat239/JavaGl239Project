package model;

import static model.Shared.InfiniteLen;

public class WideRay extends Polygon
{
    public WideRay(Point p1, Point p2)
    {
        this(new Vector(p1, p2));
    }

    public WideRay(Vector v)
    {
        addVector(v);
        Vector ray1 = v.getOrt(InfiniteLen);
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

    public static WideRay createRandom(int min, int max)
    {
        Point p1 = Point.createRandom();
        Point p2;

        while (true)
        {
            p2 = Point.createRandom();
            double d = p1.distanceTo(p2);
            if (min > d || d > max)
            {
                continue;
            }

            if(!p1.equals(p2))
            {
                break;
            }
        }

        return new WideRay(p1, p2);
    }

    public Bounds getBounds()
    {
        Vector axisY = new Vector(new Point(0, 1));
        Vector side = getVectors().get(0);
        double angle = side.getAngle(axisY);

        if (angle > 0)
        {
            if (angle < 90)
            {
                return new Bounds(side.To.X, side.From.Y, InfiniteLen, InfiniteLen);
            }
            else
            {
                return new Bounds(side.From.X, -InfiniteLen, InfiniteLen, side.To.Y);
            }
        }
        else
        {
            if (angle > -90)
            {
                return new Bounds(-InfiniteLen, side.To.Y, side.From.X, InfiniteLen);
            }
            else
            {
                return new Bounds(-InfiniteLen, -InfiniteLen, side.To.X, side.From.Y);
            }
        }
    }

}
