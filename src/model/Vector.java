package model;

import java.text.MessageFormat;

import static model.Shared.Epsilon;

public class Vector
{
    public Point From;
    public Point To;

    public Vector(Point from, Point to)
    {
        From = from;
        To = to;
    }

    public Vector(Point to)
    {
        From = new Point(0,0);
        To = to;
    }

    public double dx()
    {
        return To.X - From.X;
    }

    public double dy()
    {
        return To.Y - From.Y;
    }

    public double len()
    {
        return From.distanceTo(To);
    }

    public double getAngle(Vector v)
    {
        if (len() == 0 || v.len() == 0)
        {
            return 0;
        }

        double dx1 = dx();
        double dx2 = v.dx();
        double dy1 = dy();
        double dy2 = v.dy();

        double cos = (dx1 * dx2 + dy1 * dy2) / len() / v.len();
        double k = -(dx1 * dy2 - dx2 * dy1);

        return Math.acos(cos) / Math.PI * 180 * (k >= 0 ? 1 : -1);
    }

    public Vector getOrt(double k)
    {
        double dx2, dy2;
        double dx1 = dx();
        double dy1 = dy();
        if (Math.abs(dy1) > Math.abs(dx1))
        {
            dx2 = dy1 / len() * k;
            dy2 = -dx1 * dx2 / dy1;
        } else
        {
            dy2 = -dx1 / len() * k;
            dx2 = -dy1 * dy2 / dx1;
        }
        return new Vector(To, new Point(dx2 + To.X, dy2 + To.Y));
    }

    public Point getLineIntersection(Vector v)
    {
        return getLineIntersection(this, v);
    }

    public Point getSegmentIntersection(Vector v)
    {
        Point p = getLineIntersection(this, v);
        return hasPoint(p) && v.hasPoint(p) ? p : Point.NullPoint;
    }

    private boolean hasPoint(Point p)
    {
        double b1X = Math.min(To.X, From.X) - Epsilon;
        double b2X = Math.max(To.X, From.X) + Epsilon;
        double b1Y = Math.min(To.Y, From.Y) - Epsilon;
        double b2Y = Math.max(To.Y, From.Y) + Epsilon;

        return b1X < p.X && p.X < b2X && b1Y < p.Y && p.Y < b2Y;
    }

    public Point getLineIntersection(Vector v1, Vector v2)
    {
        double dx1 = v1.dx();
        double dy1 = v1.dy();
        double dx2 = v2.dx();
        double dy2 = v2.dy();

        if(Math.abs(dx1) < Epsilon)
        {
            if(Math.abs(dx2) < Epsilon)
            {
                return Math.abs(v1.From.X - v2.From.X) < Epsilon ? Point.AllPoints : Point.NullPoint;
            }

            return getLineIntersection(v2, v1);
        }

        if(Math.abs(dx2) < Epsilon)
        {
            double k = dy1 / dx1;
            double y = k * v2.From.X + v1.From.Y - k * v1.From.X;

            return new Point(v2.From.X, y);
        }

        double k1 = dy1 / dx1;
        double k2 = dy2 / dx2;
        double b1 = v1.From.Y - k1 * v1.From.X;
        double b2 = v2.From.Y - k2 * v2.From.X;


        if(Math.abs(k1 - k2) < Epsilon)
        {
            return Math.abs(b1 - b2) < Epsilon ? Point.AllPoints : Point.NullPoint;
        }

        double x = (b2 - b1) / (k1 - k2);
        double y = k1 * x + b1;
        return new Point(x, y);
    }

    @Override
    public int hashCode()
    {
        return From.hashCode() * 2017 ^ To.hashCode();
    }

    @Override
    public String toString()
    {
        return MessageFormat.format("{0} -> {1}", From, To);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof Vector))
        {
            return false;
        }

        Vector v = (Vector) obj;

        return Math.abs(From.X - v.From.X) < Epsilon && Math.abs(From.Y - v.From.Y) < Epsilon &&
                Math.abs(To.X - v.To.X) < Epsilon && Math.abs(To.Y - v.To.Y) < Epsilon;
    }
}

