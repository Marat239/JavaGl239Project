package problem;

import javax.media.opengl.GL2;

public class WideRay
{
    public static void renderWideRay(GL2 gl, Vector2 v1, Vector2 v2)
    {
        double len1 = Math.sqrt(v1.x * v1.x + v1.y * v1.y);
        double len2 = Math.sqrt(v2.x * v2.x + v2.y * v2.y);
        double n = 100 / Math.min(len1, len2);
        Vector2 ort1 = new Vector2(v1.x + (v1.y - v2.y) * n, v1.y + (v2.x - v1.x) * n);
        Vector2 ort2 = new Vector2(v2.x + (v1.y - v2.y) * n, v2.y + (v2.x - v1.x) * n);
        Figures.renderPoint(gl, v1, 1);
        Figures.renderPoint(gl, v2, 1);
        Figures.renderPoint(gl, ort1, 5);
        Figures.renderPoint(gl, ort2, 5);
        Figures.renderLine(gl, v1, v2, 1);
        Figures.renderLine(gl, v1, ort1, 1);
        Figures.renderLine(gl, v2, ort2, 1);
    }
}
