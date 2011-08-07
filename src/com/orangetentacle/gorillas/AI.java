package com.orangetentacle.gorillas;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * AI class responsible for calculating the next shot of the AI.
 * This is not designed to be a particularly *complex* AI.
 */
public class AI {

    public int Power;
    public int Angle;

    public Shot CurrentShot;
    public List<Shot> PreviousShots;

    public AI() {
        Power = 0;
        Angle = 0;
        CurrentShot = new Shot();
        PreviousShots = new ArrayList<Shot>();
    }

    public Shot MakeShot() {
        CurrentShot = new Shot();
        CalculateShot();
        return CurrentShot;
    }

    public void CompleteShot(World.Feedback feedback) {

        Log.d("Gorilla", "AI Feedback: Collision Type:" + feedback.CollisionState.toString()
                + " MissType:" + feedback.MissType.toString());

        CurrentShot.Missed = feedback.MissType;
        CurrentShot.CollisionType = feedback.CollisionState;
        PreviousShots.add(CurrentShot);
        CurrentShot = null;
    }

    /* Calculates the next shot the AI should make.
    *  Feel free to improve on this based on the feedback you're given :-) */
    public void CalculateShot() {

        if (PreviousShots.size() == 0) {
            Log.d("Gorilla", "New AI");
            CurrentShot.Power = 60;
            CurrentShot.Angle = ((int)(Math.random() * 20) + 110);
            return;
        }

        Shot lastShot = PreviousShots.get(PreviousShots.size() - 1);
        if (lastShot.Missed == World.MissType.ShortUnder)
        {
            CurrentShot.Angle = lastShot.Angle;
            CurrentShot.Power = lastShot.Power + (int)(Math.random() * 2) + 1;
        }

        if (lastShot.Missed == World.MissType.ShortOver)
        {
            CurrentShot.Angle = lastShot.Angle;
            CurrentShot.Power = lastShot.Power - (int)(Math.random() * 2) - 1;
        }

        if (lastShot.Missed == World.MissType.LongUnder)
        {
            CurrentShot.Angle = lastShot.Angle + (int)(Math.random() * 3) + 1;
            CurrentShot.Power = lastShot.Power + (int)(Math.random() * 5) + 5;
        }

        if (lastShot.Missed == World.MissType.LongOver)
        {
            CurrentShot.Angle = lastShot.Angle - (int)(Math.random() * 3) - 1;
            CurrentShot.Power = lastShot.Power - (int)(Math.random() * 5) - 5;
        }

        Log.d("Gorilla", "AI Feedback: Angle:" + Integer.toString(CurrentShot.Angle)
                + " Power:" + Integer.toString(CurrentShot.Power));
    }

    public class Shot
    {
        public int Power;
        public int Angle;
        public World.MissType Missed;
        public World.CollisionState CollisionType;
    }
}
