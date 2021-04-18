package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Surface
{
    private final ArrayList<Triangle> _triangles = new ArrayList<>();
    private final ArrayList<WideRay> _wideRays = new ArrayList<>();
    private final ArrayList<Overlap> _overlaps = new ArrayList<>();

    public void add(Triangle t)
    {
        _triangles.add(t);
    }

    public void add(WideRay w)
    {
        _wideRays.add(w);
    }

    public List<Triangle> getTriangles()
    {
        return _triangles;
    }

    public List<WideRay> getWideRays()
    {
        return _wideRays;
    }

    public List<Overlap> getOverlaps()
    {
        return _overlaps;
    }

    public List<Triangle> generateRandomTriangles(int n)
    {
        for (int i = 0; i < n; i++)
        {
            add(Triangle.createRandom());
        }

        return getTriangles();
    }

    public List<WideRay> generateRandomWideRays(int n)
    {
        for (int i = 0; i < n; i++)
        {
            add(WideRay.createRandom());
        }

        return getWideRays();
    }

    public void computeIntersections(int n)
    {
        _overlaps.clear();
        for (Triangle t : getTriangles())
        {
            for (WideRay w : getWideRays())
            {
                Polygon p = t.getIntersection(w);
                if (p.getVectors().size() > 0)
                {
                    _overlaps.add(new Overlap(t, w, p, p.square()));
                }
            }
        }
    }

    public static class Overlap
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
}
