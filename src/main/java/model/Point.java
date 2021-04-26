package model;

import java.text.MessageFormat;

import static model.Shared.*;

public class Point
{
    public double X;
    public double Y;

    // for deserialization
    public Point()
    {
    }

    public Point(double x, double y)
    {
        X = x;
        Y = y;
    }

    public double distanceTo(Point p)
    {
        double dx = X - p.X;
        double dy = Y - p.Y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static Point createRandom()
    {
        double pX = Random.nextDouble() * MaxX;
        double pY = Random.nextDouble() * MaxY;

        return new Point(pX, pY);
    }

    public static final Point NullPoint = new Point(-InfiniteLen, -InfiniteLen);
    public static final Point AllPoints = new Point(InfiniteLen, InfiniteLen);

    @Override
    public int hashCode()
    {
        return Double.hashCode(Math.round(X/Epsilon)) * 239 ^ Double.hashCode(Math.round(Y/Epsilon));
    }

    @Override
    public String toString()
    {
        return MessageFormat.format("({0}, {1})", X, Y);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof Point))
        {
            return false;
        }

        Point p = (Point) obj;

        return Math.abs(X - p.X) < Epsilon && Math.abs(Y - p.Y) < Epsilon;
    }
}