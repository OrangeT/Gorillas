package com.orangetentacle.gorillas;

import android.graphics.Point;

/**
 * Banana is the projectile thrown by a gorilla.
 */
public class Banana {


    double Angle;
    double Power;
    double XVel;
    double YVel;
    Point StartPoint;
    Point Point;
    private World mWorld;

    private long mTick;
    public long Tock;
    public int Index;
    public int IndexCount;

    /* Creates the banana and starts it's path towards the heavens */
    public Banana(Point start, int angle, int power, World world)
    {
        StartPoint = start;
        Angle = (double)angle / 180 * 3.141; /* Convert to radians */
        Power = power;

        mWorld = world;

        XVel = Math.cos(Angle) * Power;
        YVel = Math.sin(Angle) * Power;

        mTick = System.currentTimeMillis();

        Point = new Point();

        IndexCount = 0;
        Index = 0;

        move();
    }

    /* Moves the projective to the current time since tick occured */
    public void move()
    {
        Tock = (System.currentTimeMillis() - mTick);

        Point.x = (int)(StartPoint.x + (XVel * ((double)Tock/500) * 2) + (.5 * (mWorld.Wind / 5) * Math.pow((double)2 * Tock/500,2)));
        Point.y = (int)(StartPoint.y - (YVel * ((double)Tock/500) * 2) + (.5 * mWorld.Gravity * Math.pow((double)2 * Tock/500, 2)));

//        Log.d("Gorilla", String.format("Banana Move: StartPoint.x:%1$d XVel:%2$f Wind:%3$f Time:%4$f",
//                    StartPoint.x, XVel, mWorld.Wind, time));
//        Log.d("Gorilla", String.format("Banana Move: StartPoint.y:%1$d YVel:%2$f Gravity:%3$f Time:%4$f",
//                    StartPoint.y, YVel, mWorld.Gravity, time));

        // There's a bug here somewhere.  But I'm damned if I can work out what it is...
        Index = (int)((Tock/100) % 12);
    }

}

