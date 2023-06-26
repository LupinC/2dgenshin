package physics2d.primitives;

import org.joml.Vector2f;
import physics2d.rigidbody.Rigidbody2D;

public class Circle extends Collider2D{
    private float radius = 1.0f;
    private Rigidbody2D rigidbody = null;

    public Circle(){}

    public float getRadius(){
        return this.radius;
    }

    public Vector2f getCenter(){
        return rigidbody.getPosition();
    }

    public void setRadius(float r){
        this.radius = r;
    }

    public void setRigidbody(Rigidbody2D rb){
        this.rigidbody = rb;
    }
}
