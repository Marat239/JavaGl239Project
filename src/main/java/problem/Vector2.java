package problem;

class Vector2
{
    double x;
    double y;
    public Vector2()
    {
        x = 1;
        y = 0;
    }

    @Override
    public String toString()
    {
        String s = String.format("(%.2f,%.2f)", x, y);
        return s;
    }

    public Vector2(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 v)
    {
        this.x = v.x;
        this.y = v.y;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getX()
    {
        return x;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getY()
    {
        return y;
    }

    public double len()
    {
        double len = Math.sqrt(x * x + y * y);
        return len;
    }

    public void x(double r)
    {
        x *= r;
        y *= r;
    }

    public void plus(Vector2 v2)
    {
        x += v2.x;
        y += v2.y;
    }

    public void minus(Vector2 v2)
    {
        x -= v2.x;
        y -= v2.y;
    }

    public Vector2 sum(Vector2 v2)
    {
        Vector2 sum = new Vector2(0, 0);

        sum.x = x + v2.x;
        sum.y = y + v2.y;

        return sum;
    }

    public static Vector2 sum(Vector2 v2, Vector2 v3)
    {
        Vector2 sum = new Vector2(0, 0);

        sum.x = v3.x + v2.x;
        sum.y = v3.y + v2.y;

        return sum;
    }

    public Vector2 mult(double r)
    {
        Vector2 mult = new Vector2(x * r, y * r);
        return mult;
    }

    public double mult(Vector2 v2)
    {
        return x * v2.x + y * v2.y;
    }

    public static double mult(Vector2 v2,  Vector2 v3)
    {
        return v3.x * v2.x + v3.y * v2.y;
    }

    public static Vector2 mult(Vector2 v2,  double r)
    {
        Vector2 mult = new Vector2(v2.x * r, v2.y * r);
        return mult;
    }

    public void normalize()
    {
        if(x == 0 && y == 0)
        {
            x = 0;
            y = 0;
            return;
        }

        double length = len();
        x /= length;
        y /= length;
    }

    public Vector2 norm()
    {
        if(x == 0 && y == 0)
        {
            return new Vector2(0, 0);
        }

        double length = len();

        Vector2 norm = new Vector2(x / length, y / length);
        return norm;
    }

    public void rotate(double a)
    {
        double r = x;
        x = x * Math.cos(a) - y * Math.sin(a);
        y = y * Math.cos(a) + r * Math.sin(a);
    }

    public Vector2 rotated(double a)
    {
        Vector2 rot = new Vector2(x * Math.cos(a) - y * Math.sin(a), y * Math.cos(a) + x * Math.sin(a));
        return rot;
    }
}
