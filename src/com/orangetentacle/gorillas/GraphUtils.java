package com.orangetentacle.gorillas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

/**
 * Graphic Utilities
 */
public class GraphUtils {

    /* Horizontally flips a bitmap */
    public static Bitmap flipBitmap(Bitmap bitmap)
    {
        Bitmap target = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);

        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(bitmap.getWidth(), 0);
        canvas.drawBitmap(bitmap, matrix, new Paint(Paint.ANTI_ALIAS_FLAG));

        return target;
    }

    public static Bitmap[] rotateMany(Bitmap bitmap, int count)
    {
        Log.d("Gorilla", "Rotate");

        Bitmap[] values = new Bitmap[count];
        Matrix matrix = new Matrix();
        for(int i = 0; i < count; i++)
        {
            Log.d("Gorilla", Integer.toString(i));
            int angle = (360/count) * i;
            Log.d("Gorilla", "Angle:" + Integer.toString(angle));
            matrix.postRotate((360/count) * i);
            values[i] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return values;
    }
}
