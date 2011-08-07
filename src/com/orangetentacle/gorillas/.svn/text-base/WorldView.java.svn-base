package com.orangetentacle.gorillas;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WorldView extends SurfaceView implements SurfaceHolder.Callback {

    public class WorldThread extends Thread {

        private GameState mGameState;
        private World mWorld;
        private AI mAI;
        private Bitmap mBackground;
        private Bitmap mMoon;
        private Bitmap mMoonShock;
        private Bitmap mGorilla;
        private Bitmap mGorillaFling;
        private Bitmap mGorrilaFlingLeft;
        private Bitmap mBanana;
        private Bitmap[] mBananas;
        private GameEndListener gameEndListener;

        int Rounds = 0;

        /* Bit hacky - should be able to put these straight in...*/
        String Gorilla1Name;
        String Gorilla2Name;

        private SurfaceHolder mSurfaceHolder;
        private Handler mHandler;
        private Context mContext;
        private boolean mRun;


        public WorldThread(SurfaceHolder surfaceHolder, Context context,
                Handler handler) {
            // get handles to some important objects
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            mContext = context;

            doStart();
        }

        public void doStart() {
            Resources res = mContext.getResources();
            mBackground = BitmapFactory.decodeResource(res, R.drawable.sky);
            mMoon = BitmapFactory.decodeResource(res, R.drawable.moon);
            mMoonShock = BitmapFactory.decodeResource(res, R.drawable.moon_shock);
            mGorilla = BitmapFactory.decodeResource(res, R.drawable.gorilla);
            mGorillaFling = BitmapFactory.decodeResource(res, R.drawable.gorilla_fling);
            mGorrilaFlingLeft = GraphUtils.flipBitmap(mGorillaFling);
            mBanana = BitmapFactory.decodeResource(res, R.drawable.banana);
            mBananas = GraphUtils.rotateMany(mBanana, 12);
            mGameState = GameState.PlayerOneWaiting;
            mAI = new AI();
        }

        public void setRunning(boolean b) {
            mRun = b;
        }

        /* TODO - Implement restore state */
//        public synchronized void restoreState(Bundle savedState) {
//            synchronized (mSurfaceHolder) {
//            }
//        }                                  *¡¡

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /* TODO: Implement save state */
//        public Bundle saveState(Bundle map) {
//            synchronized (mSurfaceHolder) {
//                if (map != null) {
//                }
//            }
//            return map;
//        }


        public void setRounds(int rounds)
        {
            Rounds = rounds;
        }

        public void setNames(String gorilla1Name, String gorilla2Name)
        {
            Gorilla1Name = gorilla1Name;
            Gorilla2Name = gorilla2Name;
        }

        public void setGameEndListener(GameEndListener listener)
        {
            gameEndListener = listener;
        }

        public void throwBanana(int gorilla, int angle, int power)
        {
            if (mGameState == GameState.PlayerOneWaiting) {
                mWorld.throwBanana(gorilla, angle, power);
                mGameState = GameState.PlayerOneTurn;
            }
        }

        public void doDraw(Canvas canvas)
        {
            if (mWorld == null) {
                mWorld = new World(getWidth(),getHeight());

                /* Ensure gorilla shape is assigned */
                mWorld.Gorillas[0].assignShape(mWorld.Towers[mWorld.Gorillas[0].Tower], mGorilla);
                mWorld.Gorillas[1].assignShape(mWorld.Towers[mWorld.Gorillas[1].Tower], mGorilla);

                mWorld.Gorillas[0].Name = Gorilla1Name;
                mWorld.Gorillas[1].Name = Gorilla2Name;
            }

            World.Feedback feedback = mWorld.checkCollisions();
            if (feedback.CollisionState != World.CollisionState.Throwing)
            {
                if (feedback.CollisionState == World.CollisionState.Gorilla)
                    mGameState = GameState.PlayerWin;
                else if (mGameState == GameState.PlayerOneTurn)
                    mGameState = GameState.PlayerTwoWaiting;
                else if (mGameState == GameState.PlayerTwoTurn) {
                    // If AI - we need some feedback.
                    mAI.CompleteShot(feedback);
                    mGameState = GameState.PlayerOneWaiting;
                }
            }

            // Assume we're using AI for now...
            if (mGameState == GameState.PlayerTwoWaiting)  {
                AI.Shot shot = mAI.MakeShot();
                mWorld.throwBanana(1, shot.Angle, shot.Power);
                mGameState = GameState.PlayerTwoTurn;
            }


            /* If Gorillas have danced, get a new world */
            if (mGameState == GameState.PlayerWin && (mWorld.Gorillas[0].Danced || mWorld.Gorillas[1].Danced))
            {
                if ((mWorld.Gorillas[0].Score == Rounds) || (mWorld.Gorillas[1].Score == Rounds)) {
                    gameEndListener.onEnd();
                }
                else {
                    mWorld.newWorld();
                    mGameState = GameState.PlayerOneWaiting;
                    mWorld.Gorillas[0].assignShape(mWorld.Towers[mWorld.Gorillas[0].Tower], mGorilla);
                    mWorld.Gorillas[1].assignShape(mWorld.Towers[mWorld.Gorillas[1].Tower], mGorilla);
                }
            }

            drawSky(canvas);
            drawTowers(canvas);
            drawCollisions(canvas);
            drawBanana(canvas);
            drawGorillas(canvas);

            drawScore(canvas);
        }

        private void drawSky(Canvas canvas)
        {

            canvas.drawBitmap(mBackground, 0, 0, null);

            if (mWorld.MoonShocked)
                canvas.drawBitmap(mMoonShock, mWorld.Moon.left, mWorld.Moon.top, null);
            else
                canvas.drawBitmap(mMoon, mWorld.Moon.left, mWorld.Moon.top, null);
        }

        private void drawTowers(Canvas canvas)
        {
            for(Tower tower: mWorld.Towers)
            {
                Paint paint = new Paint();
                paint.setColor(tower.getColor()[0]);

                GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, tower.getColor());
                gradient.setBounds(tower.Shape);
                gradient.draw(canvas);

            }
        }

        private void drawGorillas(Canvas canvas)
        {
            for (Gorilla gorilla: mWorld.Gorillas)
            {
                Tower tower = mWorld.Towers[gorilla.Tower];

                switch(gorilla.getDrawState()) {
                    case LeftArmUp:
                        canvas.drawBitmap(mGorrilaFlingLeft, gorilla.Shape.left, gorilla.Shape.top, null);
                        break;
                    case RightArmUp:
                        canvas.drawBitmap(mGorillaFling, gorilla.Shape.left, gorilla.Shape.top, null);
                        break;
                    case ArmsDown:
                        canvas.drawBitmap(mGorilla, gorilla.Shape.left, gorilla.Shape.top, null);
                        break;
                    case Exploded:
                        canvas.drawBitmap(mGorilla, gorilla.Shape.left, gorilla.Shape.top, null);
                        RectF exploded = getExploded(gorilla);
                        Paint p = new Paint();
                        p.setShader(new RadialGradient(exploded.centerX(), exploded.centerY(),
                                (exploded.width() / 2) > 1 ? (exploded.width() / 2) : 1,
                                new int[] { Color.YELLOW, Color.rgb(255, 128, 0),  Color.RED, Color.TRANSPARENT },
                                new float[] { 0f, .3f, .5f, 1f }, Shader.TileMode.MIRROR ));
                        canvas.drawOval(exploded, p);
                        break;
                }


            }
        }

        /* Return an exploded bitmap of the gorilla */
        private RectF getExploded(Gorilla gorilla) {
            Point median = new Point(gorilla.Shape.centerX(), gorilla.Shape.centerY());

            int size = (int)gorilla.explodedSize();

            int offset = gorilla.Left ? 10 : 10;

            RectF rect = new RectF();
            rect.left = median.x - (int)(size / 2) + offset; // No idea why we need the additional 8, but we do...
            rect.right = median.x + (int)(size / 2) + offset;
            rect.top = median.y - (int)(size / 2) + offset;
            rect.bottom = median.y + (int)(size /2) + offset;

            return rect;
        }

        private void drawBanana(Canvas canvas)
        {
            if (mWorld.Banana == null)
                return;

            /* Banana will need rotation before drawing. */
            Bitmap toDraw = mBananas[mWorld.Banana.Index];

            int x = mWorld.Banana.Point.x - (toDraw.getWidth() / 2);
            int y = mWorld.Banana.Point.y - (toDraw.getHeight() / 2);

            canvas.drawBitmap(toDraw, x, y, null);
        }

        private void drawCollisions(Canvas canvas)
        {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);

            Bitmap circle = Bitmap.createBitmap(50,50,Bitmap.Config.ARGB_8888);
            Canvas circleCanvas = new Canvas(circle);
            circleCanvas.drawCircle(25, 25, 25, paint);


            for(Point point: mWorld.Collisions)
            {

                Bitmap sourceBmp = Bitmap.createBitmap(mBackground, point.x -25, point.y -25, 50, 50);
                Bitmap resultBmp = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                Canvas resultCanvas = new Canvas(resultBmp);

                paint.setXfermode(null);
                resultCanvas.drawBitmap(sourceBmp, new Rect(0,0,50,50), new Rect(0,0,50,50), paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                resultCanvas.drawBitmap(circle, new Rect(0,0,50,50), new Rect(0,0,50,50), paint);

                canvas.drawBitmap(resultBmp, point.x - 25, point.y -25, null);

            }
        }

        private void drawScore(Canvas canvas)
        {
            Paint p = new Paint();
            p.setAntiAlias(true);
            p.setColor(Color.BLACK);
            p.setTextSize(30);

            /* Draws the left score in the corners. */
            canvas.drawText(mWorld.Gorillas[0].Name, 5, 30, p);
            canvas.drawText(Integer.toString(mWorld.Gorillas[0].Score), 5, 60, p);

            /* Draws the right score*/
            p.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(mWorld.Gorillas[1].Name, mWorld.WorldBoundaries.width() - 5, 30, p);
            canvas.drawText(Integer.toString(mWorld.Gorillas[1].Score), mWorld.WorldBoundaries.width() - 5, 60, p);
        }


    }

    private WorldThread thread;

    public WorldView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // create thread only; it's started in surfaceCreated()
        thread = new WorldThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
            }
        });

        setFocusable(true); // make sure we get key events
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int height, int width) {
        // thread.setSurfaceSize(width, height);
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public WorldThread getThread() {
        return thread;
    }

    public enum GameState
    {
        PlayerOneWaiting,
        PlayerOneTurn,
        PlayerTwoWaiting,
        PlayerTwoTurn,
        PlayerWin
    }
}
