package com.example.demo.entities;

public class Vector2d {
    public double x, y;
    
    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void add(Vector2d v) { 
        x += v.x; 
        y += v.y;
    }
    
    public void sub(Vector2d v) { 
        x -= v.x; 
        y -= v.y; 
    }
    
    public void mult(double scalar) { 
        x *= scalar; 
        y *= scalar; 
    }
    
    public void div(double scalar) { 
        if (scalar != 0) { //scalar = some value. what we want to divide by.
            x /= scalar; 
            y /= scalar; 
        }
    }
    
    public double length() { //magnitude
        return Math.sqrt(x*x + y*y); 
    }
    
    public void normalize() { 
        double l = length();
        if (l > 0) div(l); //a and b divided by length. example: 5^2 = 4^2 + ^3  <=>  1^2 = 0.8^2 + 0.6^2
    }
    
    public void setMagnitude(double newMag) {
        normalize();
        mult(newMag);
    }
    
    public void limit(double max) {
        if (length() > max) {
            normalize();
            mult(max);
        }
    }
    
    public double distance(Vector2d other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    public Vector2d copy() { 
        return new Vector2d(x, y); 
    }
}
