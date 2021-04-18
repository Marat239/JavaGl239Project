package model;

import static model.Shared.Epsilon;

public class Triangle extends Polygon
{
    private Triangle(Point p1, Point p2, Point p3)
    {
        addVector(new Vector(p1, p2));
        addVector(new Vector(p2, p3));
        addVector(new Vector(p3, p1));
    }

    public static Triangle create(Point p1, Point p2, Point p3)
    {
        Vector side1 = new Vector(p1, p2);
        Vector side2 = new Vector(p1, p3);
        double a = side1.getAngle(side2);

        if (Math.abs(a) < Epsilon || Math.abs(a - 180) < Epsilon)
        {
            return null;
        }

        return a > 0 ? new Triangle(p1, p2, p3) : new Triangle(p1, p3, p2);
    }

    public static Triangle createRandom()
    {
        Triangle t;
        do
        {
            Point p1 = Point.createRandom();
            Point p2 = Point.createRandom();
            Point p3 = Point.createRandom();
            t = create(p1, p2, p3);
        } while (t == null);

        return t;
    }
}

