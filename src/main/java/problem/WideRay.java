package problem;
import javax.media.opengl.GL2;

public class WideRay
{
    public static void renderWideRay(GL2 gl, Vector2 v1, Vector2 v2)
    {
        Vector2 v = new Vector2(Math.abs(v1.x - v2.x), Math.abs(v1.y - v2.y));
        Vector2 rot = new Vector2(v.y, v.x);
        Figures.renderPoint(gl, v1, 1);
        Figures.renderPoint(gl, v2, 1);
        Figures.renderLine(gl, v1, v2, 1);
        Figures.renderLine(gl, v1, rot, 1);
    }
}
