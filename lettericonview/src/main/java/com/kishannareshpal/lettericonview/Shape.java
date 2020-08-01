package com.kishannareshpal.lettericonview;

public enum Shape {

    SQUARE(0),
    ROUNDED_SQUARE(1),
    CIRCLE(2),
    CUSTOM_RADIUS(3);

    private int id;
    Shape(int id) {
        this.id = id;
    }

    static Shape fromId(int id) {
        for (Shape shape : values()) {
            if (shape.id == id) return shape;
        }
        throw new IllegalArgumentException("There is no Shape matching the id: " + id + ". To check all possible Shape ids use #values().");
    }

    public int getId() {
        return this.id;
    }
}
