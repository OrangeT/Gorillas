package com.orangetentacle.gorillas;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

public class Gorilla
{
    String Name;
    int Tower;
    boolean Throwing;
    boolean Exploded;
    boolean Dancing;
    boolean Danced;
    long danceTicks;
    boolean Left;

    int Score;

    Rect Shape;
    Point Hand;

    /* Sets the shape of the gorilla based on the tower */
    public void assignShape(Tower tower, Bitmap gorilla)
    {
        Shape = new Rect();
        Shape.left = tower.Shape.left + tower.Shape.width() /2 - gorilla.getWidth() / 2;
        Shape.right = tower.Shape.left + gorilla.getWidth();
        Shape.top = tower.Shape.top - gorilla.getHeight();
        Shape.bottom = tower.Shape.top;

        assignHand();
    }

    /* Sets the point of the hand of the gorilla */
    public void assignHand()
    {
        Hand = new Point();
        Hand.x = Left ? Shape.left - 5 : Shape.right + 5;
        Hand.y = Shape.top -5;
    }

    public void dance()
    {
         Dancing = true;
         danceTicks = System.currentTimeMillis();
    }

    public void explode()
    {
        Exploded = true;
        danceTicks = System.currentTimeMillis();
    }

    public long explodedSize()
    {
        return ((System.currentTimeMillis() - danceTicks) / 5);
    }

    /* Determine how the gorilla should be drawn. */
    public GorillaDrawState getDrawState() {
        if (Dancing)
        {
            long currentTicks = (System.currentTimeMillis() - danceTicks);
            if (currentTicks > 2000)
            {
                Dancing = false;
                Danced = true;
                return GorillaDrawState.ArmsDown;
            }

            return (currentTicks / 250) % 2 == 0 ? GorillaDrawState.LeftArmUp : GorillaDrawState.RightArmUp;
        }

        if (Throwing && Left)
        {
            return GorillaDrawState.LeftArmUp;
        }

        if (Throwing)
        {
            return GorillaDrawState.RightArmUp;
        }

        if (Exploded)
        {
            long currentTicks = (System.currentTimeMillis() - danceTicks);
            if (currentTicks > 2000)
            {
                Exploded = false;
                return GorillaDrawState.ArmsDown;
            }

            return GorillaDrawState.Exploded;
        }

        return GorillaDrawState.ArmsDown;
    }

    public enum GorillaDrawState
    {
        ArmsDown,
        LeftArmUp,
        RightArmUp,
        Exploded
    }

}
