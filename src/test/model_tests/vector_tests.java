package test.model_tests;

import model.Point;
import model.Vector;
import org.junit.Assert;
import org.junit.Test;

import static model.Shared.Epsilon;

public class vector_tests
{
    @Test
    public void should_make_ort_vector()
    {
        Point p1 = new Point(1, 1);
        Point p2 = new Point(3, 5);
        Vector v1 = new Vector(p1, p2);
        Vector v2 = v1.getOrt(Math.sqrt(5));

        Assert.assertEquals(5, v2.To.X, Epsilon);
        Assert.assertEquals(4, v2.To.Y, Epsilon);
        Assert.assertEquals(3, v2.From.X, Epsilon);
        Assert.assertEquals(5, v2.From.Y, Epsilon);
        Assert.assertEquals(Math.sqrt(5), v2.len(), Epsilon);
    }

    @Test
    public void should_make_ort_vector_opposite_direction()
    {
        Point p1 = new Point(3, 5);
        Point p2 = new Point(1, 1);
        Vector v1 = new Vector(p1, p2);
        Vector v2 = v1.getOrt(Math.sqrt(5));

        Assert.assertEquals(-1, v2.To.X, Epsilon);
        Assert.assertEquals(2, v2.To.Y, Epsilon);
        Assert.assertEquals(1, v2.From.X, Epsilon);
        Assert.assertEquals(1, v2.From.Y, Epsilon);
        Assert.assertEquals(Math.sqrt(5), v2.len(), Epsilon);
    }

    @Test
    public void should_make_ort_vector_vertical()
    {
        Point p1 = new Point(1, 5);
        Point p2 = new Point(1, 1);
        Vector v1 = new Vector(p1, p2);
        Vector v2 = v1.getOrt(2);

        Assert.assertEquals(-1, v2.To.X, Epsilon);
        Assert.assertEquals(1, v2.To.Y, Epsilon);
        Assert.assertEquals(1, v2.From.X, Epsilon);
        Assert.assertEquals(1, v2.From.Y, Epsilon);
        Assert.assertEquals(2, v2.len(), Epsilon);
    }

    @Test
    public void should_make_vector_intersection()
    {
        Point p1 = new Point(1, 1);
        Point p2 = new Point(3, 7);
        Vector v1 = new Vector(p1, p2);
        Point p3 = new Point(5, 3);
        Point p4 = new Point(-1, 5);
        Vector v2 = new Vector(p3, p4);

        Point p = v1.getLineIntersection(v2);

        Assert.assertEquals(2, p.X, Epsilon);
        Assert.assertEquals(4, p.Y, Epsilon);
    }

    @Test
    public void should_make_vector_intersection_1()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Vector v1 = new Vector(p1, p2);

        Point p3 = new Point(8, 3);
        Point p4 = new Point(4, 1);
        Vector v2 = new Vector(p3, p4);

        Point p = v1.getSegmentIntersection(v2);

        Assert.assertEquals(4, p.X, Epsilon);
        Assert.assertEquals(1, p.Y, Epsilon);
    }
}
