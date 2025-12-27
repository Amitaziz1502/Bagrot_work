package com.example.bagrot_work.models;

class Particle {
    float x, y, vx, vy;
    int alpha = 255;

    Particle(float x, float y) {
        this.x = x;
        this.y = y;
        // Random speed to all directions
        this.vx = (float) (Math.random() * 20 - 10);
        this.vy = (float) (Math.random() * 20 - 10);
    }

    void update() {
        x += vx;
        y += vy;
        alpha -= 5; // The particle is slowly disapering
    }
}
