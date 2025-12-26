package com.example.bagrot_work.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.example.bagrot_work.R;

public class Spike {
    Bitmap spikeImg;
    int x, y;
    int width, height;

    public Spike(Context context, int screenWidth, int screenHeight) {
        spikeImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.triangle);
        width = spikeImg.getWidth();
        height = spikeImg.getHeight();

        x = screenWidth;
        y = screenHeight - height;
    }

    public Rect getCollisionShape() {
        //Creating a shape rectangle like that represents the spike touching area
        return new Rect(x, y, x + width, y + height);
    }
}
