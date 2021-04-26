package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
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

    @JsonIgnore
    public List<Overlap> getOverlaps()
    {
        return _overlaps;
    }

    @JsonIgnore
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
        clearIntersections();

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

        for (Overlap o : _overlaps)
        {
            if(_largest == null || _largest.Square < o.Square)
            {
                _largest = o;
            }
        }

        _overlaps.sort(Comparator.comparingDouble(x -> x.Square));
    }

    public void clearIntersections()
    {
        _overlaps.clear();
        _largest = null;

    }
    @JsonIgnore
    public boolean isEmpty()
    {
        return getTriangles().isEmpty() && getWideRays().isEmpty();
    }

    @JsonIgnore
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

    public boolean saveToFile(File file)
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try
        {
            mapper.writeValue(file, this);
        }
        catch(Exception e)
        {
            return false;
        }

        return true;
    }

    public boolean loadFromFile(File file)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            Surface surface = mapper.readValue(file, Surface.class);

            clear();

            _triangles.addAll(surface._triangles);
            _wideRays.addAll(surface._wideRays);

            clearIntersections();
        }
        catch(IOException e)
        {
            return false;
        }

        return true;
    }
}

