package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import static model.Shared.Epsilon;
import static model.Shared.InfiniteLen;

public class Polygon
{
    private final ArrayList<Vector> _vectors = new ArrayList<>();

    public void addVector(Vector v)
    {
        _vectors.add(v);
    }

    public List<Vector> getVectors()
    {
        return _vectors;
    }

    public boolean contains(Point p)
    {
        for (Vector v : getVectors())
        {
            if (v.getAngle(new Vector(v.From, p)) < 0)
            {
                return false;
            }
        }

        return true;
    }

    public Polygon getIntersection(Polygon polygon)
    {
        ArrayList<Vector> vectors = new ArrayList<>();
        vectors.addAll(getVectors());
        vectors.addAll(polygon.getVectors());

        Vector current = getVectors().get(0);
        boolean inside = polygon.contains(current.From);
        Polygon intersection = new Polygon();
        HashSet<Point> visited = new HashSet<>();

        if (inside)
        {
            visited.add(current.From);
        }

        while (true)
        {
            Crossing c = getNearestCrossing(current, vectors);

            if(!polygon.contains(c.Centre))
            {
                inside = false;
            }

            Vector next = c.getTheRightMost(current);

            if(inside)
            {
                Vector side = new Vector(current.From, c.Centre);
                intersection.getVectors().add(side);
            }

            if(!inside && polygon.contains(c.Centre))
            {
                inside = true;
            }

            if(!visited.add(c.Centre))
            {
                break;
            }

            current = next;
        }

        if(intersection.getVectors().size() == 1)
        {
            intersection.getVectors().clear();
        }

        return intersection;
    }

    public double square()
    {
        double s = 0;
        int n = getVectors().size();

        if(n == 0)
        {
            return 0;
        }

        for (int i = 1; i < n + 1; i++)
        {
            Vector vPrev = getVectors().get(i - 1);
            Vector v = getVectors().get(i % n);
            Vector vNext = getVectors().get((i + 1) % n);
            s += v.From.X * (vNext.From.Y - vPrev.From.Y);
        }

        return 0.5 * Math.abs(s);
    }

    private Crossing getNearestCrossing(Vector current, List<Vector> vectors)
    {
        Hashtable<Point, Crossing> crossings = new Hashtable<>();
        for (Vector v : vectors)
        {
            Point p = current.getSegmentIntersection(v);
            if (p != Point.NullPoint && p != Point.AllPoints && current.From.distanceTo(p) > Epsilon)
            {
                Crossing cross;
                if (!crossings.containsKey(p))
                {
                    cross = new Crossing(p);
                    crossings.put(p, cross);
                    cross.addVector(current);
                }
                else
                {
                    cross = crossings.get(p);
                }
                cross.addVector(v);
            }
        }

        double min = InfiniteLen;
        Crossing nearest = null;
        for (Crossing cross : crossings.values())
        {
            double d = current.From.distanceTo(cross.Centre);
            if (d < min)
            {
                min = d;
                nearest = cross;
            }
        }

        return nearest;
    }

    public Bounds getBounds()
    {
        Bounds b = new Bounds(InfiniteLen, InfiniteLen, 0, 0);
        if(getVectors().size() == 0)
        {
            return b;
        }

        // assuming polygon is closed
        for (Vector v: getVectors())
        {
            b.MinX = Math.min(b.MinX, v.From.X);
            b.MinY = Math.min(b.MinY, v.From.Y);
            b.MaxX = Math.max(b.MaxX, v.From.X);
            b.MaxY = Math.max(b.MaxY, v.From.Y);
        }
        return b;
    }

    private static class Crossing
    {
        ArrayList<Vector> Incoming = new ArrayList<>();
        ArrayList<Vector> Outcoming = new ArrayList<>();
        public Point Centre;

        public Crossing(Point p)
        {
            Centre = p;
        }

        public void addVector(Vector v)
        {
            Vector vIn = new Vector(v.From, Centre);
            Vector vOut = new Vector(Centre, v.To);
            addIfNotEmpty(Incoming, vIn);
            addIfNotEmpty(Outcoming, vOut);
        }

        public void addIfNotEmpty(ArrayList<Vector> vectors, Vector v)
        {
            if (v.len() > Epsilon)
            {
                vectors.add(v);
            }
        }

        public Vector getTheRightMost(Vector current)
        {
            Vector theRightMost = null;
            double maxAngle = -InfiniteLen;

            for (Vector v: Outcoming)
            {
                if(current.getAngle(v) > maxAngle)
                {
                    maxAngle = current.getAngle(v);
                    theRightMost = v;
                }
            }

            return theRightMost;
        }
    }
}
