package dk.sdu.cbse.core;

import java.util.UUID;

/**
 * Base class for all game entities.
 */
public class Entity {
    private final UUID id;
    private float x;
    private float y;
    private float dx;
    private float dy;
    private float radians;
    private float radius;
    private boolean active;
    private String type;

    public Entity() {
        this.id = UUID.randomUUID();
        this.active = true;
    }

    public UUID getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getDx() {
        return dx;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public float getRadians() {
        return radians;
    }

    public void setRadians(float radians) {
        this.radians = radians;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

