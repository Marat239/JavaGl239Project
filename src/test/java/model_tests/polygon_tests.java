/*package model_tests;

import model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static model.Shared.Epsilon;

public class polygon_tests
{
    @Test
    public void should_compute_intersection_for_simple_case()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(1, 2);
        Point p5 = new Point(1, 6);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(4,2));
        points.add(new Point(4,6));
        points.add(new Point(5,6));
        points.add(new Point(8,3));
        points.add(new Point(6,2));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_for_rotated_wr()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(2, 5);
        Point p5 = new Point(4, 9);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(4,4));
        points.add(new Point(4,7));
        points.add(new Point(8,3));
        points.add(new Point(7,2.5));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_through_vertex()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(2, 6);
        Point p5 = new Point(4, 10);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(4,5));
        points.add(new Point(4,7));
        points.add(new Point(8,3));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_full_inside_1()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(3, 0);
        Point p5 = new Point(3, 8);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(4,1));
        points.add(new Point(4,7));
        points.add(new Point(8,3));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_full_outside()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(3, 8);
        Point p5 = new Point(3, 0);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        Assert.assertEquals(0, intersection.getVectors().size());
    }

    @Test
    public void should_compute_intersection_common_vertex_full_inside()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(4, 7);
        Point p5 = new Point(8, 7);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(4,1));
        points.add(new Point(4,7));
        points.add(new Point(8,3));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_common_vertex_half_inside()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 5);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(4, 7);
        Point p5 = new Point(8, 9);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(4,7));
        points.add(new Point(8,5));
        points.add(new Point(6,3));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_common_vertex_full_outside()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 5);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(4, 7);
        Point p5 = new Point(4, 11);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();

        Assert.assertEquals(0, intersection.getVectors().size());
    }

    @Test
    public void should_compute_intersection_common_side_full_outside()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 5);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(0, 7);
        Point p5 = new Point(4, 7);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();

        Assert.assertEquals(0, intersection.getVectors().size());
    }

    @Test
    public void should_compute_square_triangle_1()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 5);

        Triangle t = Triangle.create(p1, p2, p3);

        double s = t.square();

        Assert.assertEquals(12, s, Epsilon);
    }

    @Test
    public void should_compute_square_triangle_2()
    {
        Point p1 = new Point(3, 3);
        Point p2 = new Point(6, 3);
        Point p3 = new Point(1, 7);

        Triangle t = Triangle.create(p1, p2, p3);

        double s = t.square();

        Assert.assertEquals(6, s, Epsilon);
    }

    @Test
    public void should_compute_intersection_full_inside_2()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(3, 1);
        Point p5 = new Point(3, 7);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(4,1));
        points.add(new Point(4,7));
        points.add(new Point(8,3));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_full_inside_3()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(9, 7);
        Point p5 = new Point(9, 1);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(4,1));
        points.add(new Point(4,7));
        points.add(new Point(8,3));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_full_inside_4()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(8, 0);
        Point p5 = new Point(4, 0);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(4,1));
        points.add(new Point(4,7));
        points.add(new Point(8,3));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_full_inside_5()
    {
        Point p1 = new Point(5, 3);
        Point p2 = new Point(3, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(8, 0);
        Point p5 = new Point(3, 0);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(5,3));
        points.add(new Point(3,7));
        points.add(new Point(8,3));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_full_inside_6()
    {
        Point p1 = new Point(3, 3);
        Point p2 = new Point(6, 3);
        Point p3 = new Point(1, 7);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(6, 1);
        Point p5 = new Point(1, 1);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        double s = intersection.square();
        Assert.assertEquals(6, s, Epsilon);

        ArrayList<Point> points = new ArrayList<>();


        points.add(new Point(3,3));
        points.add(new Point(1,7));
        points.add(new Point(6,3));

        assertPolygon(points, intersection);
    }

    @Test
    public void should_compute_intersection_square_quadrangle()
    {
        Point p1 = new Point(4, 1);
        Point p2 = new Point(4, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(2, 4);
        Point p5 = new Point(4, 8);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        double s = intersection.square();
        Assert.assertEquals(10, s, Epsilon);
    }

    @Test
    public void should_compute_intersection_partially_inside()
    {
        Point p1 = new Point(5, 3);
        Point p2 = new Point(3, 7);
        Point p3 = new Point(8, 3);

        Triangle t = Triangle.create(p1, p2, p3);

        Point p4 = new Point(8, 0);
        Point p5 = new Point(3, 0);

        WideRay w = new WideRay(p4, p5);

        Polygon intersection = t.getIntersection(w);

        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(5,3));
        points.add(new Point(3,7));
        points.add(new Point(8,3));

        assertPolygon(points, intersection);
    }

    private void assertPolygon(List<Point> points, Polygon polygon)
    {
        Assert.assertEquals(points.size(), polygon.getVectors().size());
        for (int i = 0; i < points.size(); i++)
        {
            Assert.assertEquals(points.get(i).X, polygon.getVectors().get(i).From.X, Epsilon);
            Assert.assertEquals(points.get(i).Y, polygon.getVectors().get(i).From.Y, Epsilon);
        }
    }
}*/
