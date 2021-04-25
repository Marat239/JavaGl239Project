package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static model.Shared.InfiniteLen;

public class Surface
{
    private final ArrayList<Triangle> _triangles = new ArrayList<>();
    private final ArrayList<WideRay> _wideRays = new ArrayList<>();
    private final ArrayList<Overlap> _overlaps = new ArrayList<>();
    private Overlap _largest = null;

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

    public Overlap getLargestOverlap()
    {
        return _largest;
    }

    public List<Triangle> generateRandomTriangles(int n, int min, int max)
    {
        for (int i = 0; i < n; i++)
        {
            add(Triangle.createRandom(min, max));
        }

        return getTriangles();
    }

    public List<WideRay> generateRandomWideRays(int n, int min, int max)
    {
        for (int i = 0; i < n; i++)
        {
            add(WideRay.createRandom(min, max));
        }

        return getWideRays();
    }

    public void computeIntersections()
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

        _largest = null;
        for (Overlap o : _overlaps)
        {
            if(_largest == null || _largest.Square < o.Square)
            {
                _largest = o;
            }
        }
    }

    public Bounds getBounds()
    {
        Bounds bb = new Bounds(InfiniteLen, InfiniteLen, 0, 0);
        for (Triangle t : getTriangles())
        {
            Bounds b = t.getBounds();
            bb.MinX = Math.min(b.MinX, bb.MinX);
            bb.MinY = Math.min(b.MinY, bb.MinY);
            bb.MaxX = Math.max(b.MaxX, bb.MaxX);
            bb.MaxY = Math.max(b.MaxY, bb.MaxY);
        }

        for (WideRay w : getWideRays())
        {
            Bounds b = w.getBounds();
            if (b.MinX != -InfiniteLen)
            {
                bb.MinX = Math.min(b.MinX, bb.MinX);
            }
            if (b.MinY != -InfiniteLen)
            {
                bb.MinY = Math.min(b.MinY, bb.MinY);
            }
            if (b.MaxX != InfiniteLen)
            {
                bb.MaxX = Math.max(b.MaxX, bb.MaxX);
            }
            if (b.MaxY != InfiniteLen)
            {
                bb.MaxY = Math.max(b.MaxY, bb.MaxY);
            }
        }

        return bb;
    }

    public void clear()
    {
        _triangles.clear();
        _wideRays.clear();
        _overlaps.clear();
        _largest = null;
    }

    public void saveToFile(File file)
    {
        //JsonbBuilder
        //доделать
    }

    public void loadFromFile(File file)
    {
        //доделать
        clear();
        add(Triangle.create(
                new Point(0,0),
                new Point(100,200),
                new Point(200,100)));
    }
}

