package com.orangetentacle.gorillas;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * World file representing the world state.
 */
public class World {

    public Rect WorldBoundaries;
    public Tower[] Towers;
    public Gorilla[] Gorillas;
    public List<Point> Collisions;

    public Rect Moon;
    public boolean MoonShocked;

    public double Gravity = 9.8;
    public double Wind = 3; /* TODO: Randomised between -5 and 5 (also random for really strong wind */

    public Banana Banana;

    public World(int width, int height)
    {
        WorldBoundaries = new Rect(0,0,width, height);
        newGorillas();
        newWorld();

        Moon = new Rect(WorldBoundaries.width() / 2 - 38, 10, WorldBoundaries.width() / 2 + 38, 76);
    }

    /* Generates a new "Level" */
    public void newWorld()
    {
        getTowers(8, WorldBoundaries.width(), WorldBoundaries.height());
        Collisions = new ArrayList<Point>();

        newGorillaPositions();

        Random random = new Random();
        Wind = random.nextInt(10) - 5;
        MoonShocked = false;
    }

    public void getTowers(int count, int width, int height)
    {
        int maxHeight = (int)(height * 0.6);
        int minHeight = (int)(height * 0.01);
        int heightRange = maxHeight - minHeight;

        int avgWidth = width / count;
        int totalWidth = 0;

        Random random = new Random(); // We need a random number generator to calculate tower heights.
        Towers = new Tower[count];

        for (int i = 0; i < Towers.length; i++) {
            Towers[i] = new Tower();
            Towers[i].ColorIndex = random.nextInt(3);
            Towers[i].Height = random.nextInt(heightRange) + minHeight;

            if (i == Towers.length - 1) {
                Towers[i].Width = width - totalWidth;
                Towers[i].Shape = new Rect(totalWidth, height - Towers[i].Height, totalWidth + Towers[i].Width, height);
            }
            else {
                Towers[i].Width = random.nextInt(20) + avgWidth - 10;
                Towers[i].Shape = new Rect(totalWidth, height - Towers[i].Height, totalWidth + Towers[i].Width - 1, height);
            }

            totalWidth += Towers[i].Width;
        }
    }

    public void newGorillas()
    {
        /* Return a list of Gorillas */
        Gorillas = new Gorilla[2];

        Random random = new Random();
        Gorillas[0] = new Gorilla();
        Gorillas[0].Name = "Player 1";
        Gorillas[0].Left = true;
        Gorillas[0].Throwing = false;

        Gorillas[1] = new Gorilla();
        Gorillas[1].Name = "Player 2";
        Gorillas[1].Left = false;
        Gorillas[1].Throwing = false;
    }

    public void newGorillaPositions()
    {
        Random random = new Random();

        Gorillas[0].Tower = random.nextInt(2) + 1;
        Gorillas[1].Tower = Towers.length - 1 - random.nextInt(2);

        Gorillas[0].Danced = false;
        Gorillas[1].Danced = false;
    }

    public void throwBanana(int gorilla, int angle, int power)
    {
        /* What happens if the banana is negative? */
        /* Answer - deal with that problem when you've got a working banana dofus */

        Gorilla throwingGorilla = Gorillas[gorilla];
        throwingGorilla.Throwing = true;
        Banana = new Banana(throwingGorilla.Hand, angle, power, this);
    }

    /* Checks colissions by the banana. */
    public Feedback checkCollisions()
    {
        if (Banana == null)
            return new Feedback(CollisionState.NoBanana, MissType.Nothing);

        Banana.move();

        // Detect out of bounds.
        // Top is not out of bounds.
        if (Banana.Point.x < 0
                || Banana.Point.x > WorldBoundaries.width()
                || Banana.Point.y > WorldBoundaries.height()) {

            Log.d("Gorilla", "Banana out of bounds");
            MissType missType = GetMissType();
            Gorillas[0].Throwing = false;
            Gorillas[1].Throwing = false;
            Banana = null;
            MoonShocked = false;
            return new Feedback(CollisionState.OutOfBounds, missType);
        }

        // Detect collisions with gorillas.
        for (int i = 0; i < Gorillas.length; i++) {
            if (Gorillas[i].Shape.contains(Banana.Point.x, Banana.Point.y)) {
                Gorillas[0].Throwing = false;
                Gorillas[1].Throwing = false;

                Gorillas[(i + 1) % 2].dance();
                Gorillas[(i + 1) % 2].Score++;

                Gorillas[i].explode();

                Banana = null;
                MoonShocked = false;
                return new Feedback(CollisionState.Gorilla, MissType.Hit);
            }
        }

        // Detect collisions with buildings & existing collisions.
        for (int i = 0; i < Towers.length; i++) {
            if (Towers[i].Shape.contains(Banana.Point.x, Banana.Point.y)) {

                /* Check for collisions against collisions - doesn't count*/
                /* Simple collision detection against bounding box */
                boolean collided = false;
                for(Point collision: Collisions) {
                    collided = collided ||
                    (collision.x - 20 < Banana.Point.x && collision.x + 20 > Banana.Point.x)
                            && (collision.y - 20 < Banana.Point.y && collision.y + 20 > Banana.Point.y);
                }

                if (! collided) {
                    MissType missType = GetMissType();
                    Gorillas[0].Throwing = false;
                    Gorillas[1].Throwing = false;

                    Collisions.add(new Point(Banana.Point.x > 0 ? Banana.Point.x : 0,
                            Banana.Point.y > 0 ? Banana.Point.y : 0));
                    Banana = null;
                    MoonShocked = false;
                    return new Feedback(CollisionState.Landscape, missType);
                }
            }
        }

        // Detect collissions with moon.
        if (Moon.contains(Banana.Point.x, Banana.Point.y)) {
            MoonShocked = true;
        }

        return new Feedback(CollisionState.Throwing, MissType.Nothing);
    }

    public MissType GetMissType() {
        int diff = 0;
        if (Gorillas[0].Throwing) {
            diff = (Banana.Point.x - (Gorillas[1].Shape.left + (int)(Gorillas[1].Shape.width() / 2)));
        } else {
            diff = ((Gorillas[0].Shape.left + (int)(Gorillas[0].Shape.width() / 2)) - Banana.Point.x);
        }

        Log.d("Gorillas", "Missed by: " + Integer.toString(diff));

        if (diff > 30)
            return MissType.LongOver;
        if (diff > 0)
            return MissType.ShortOver;
        if (diff < -30)
            return MissType.LongUnder;
        if (diff < 0)
            return MissType.ShortUnder;

        return MissType.Hit;
    }

    public class Feedback
    {
        public Feedback(CollisionState collisionState, MissType missType)
        {
            CollisionState = collisionState;
            MissType = missType;
        }

        CollisionState CollisionState;
        MissType MissType;
    }

    public enum CollisionState
    {
        NoBanana,
        OutOfBounds,
        Landscape,
        Gorilla,
        Throwing
    }
    
    public enum MissType
    {
        Nothing,
        ShortOver,
        ShortUnder,
        LongOver,
        LongUnder,
        Hit
    }
}
