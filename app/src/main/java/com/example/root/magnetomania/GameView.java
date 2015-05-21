package com.example.root.magnetomania;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;


public class GameView extends SurfaceView {

    /******************************************** CLASS MEMBERS ********************************************/
    private SurfaceHolder mHolder;
    private GameThread mThread = null;
    private Context mContext;

    private int mScreenWidth;
    private int mScreenHeight;

    public boolean is_game_started;
    public boolean is_game_paused;
    public boolean is_game_over;

    private MonsterBall mBall = new MonsterBall();
    private MagnetRocket mRocket = new MagnetRocket();

    private BulletFan mFan1 = new BulletFan();
    private BulletFan mFan2 = new BulletFan();
    private BulletFan mFan3 = new BulletFan();

    private HeatWave mWave1 = new HeatWave();
    private HeatWave mWave2 = new HeatWave();
    private HeatWave mWave3 = new HeatWave();
    private HeatWave mWave4 = new HeatWave();
    private HeatWave mWave5 = new HeatWave();

    private int fingerX;
    private int fingerY;
    private int attackAtX;
    private int attackAtY;
    private int attackFromX;
    private int attackFromY;

    private int moveStyle;
    private int monsterSleepCount;
    private int rocketXhaustCount;
    private int bulletFansTimeGap;
    private int heatWaveTimeGap;

    private RectF heatRect1 = new RectF();
    private RectF heatRect2 = new RectF();
    private RectF heatRect3 = new RectF();
    private RectF heatRect4 = new RectF();
    private RectF heatRect5 = new RectF();

    private boolean monster_trick_time;
    private boolean time_to_shoot_bullets;
    private boolean bullets_on_screen;
    private boolean time_for_some_heat;
    private boolean heat_waves_on_screen;

    private Random random = new Random();

    /**---------------------------------------------------------------------------------------------------**/


    /********************************************* CONSTRUCTOR *********************************************/
    public GameView(Context context){
        super(context);

        this.mContext = getContext();
        this.mScreenWidth = GameActivity.mScreenSize.x;
        this.mScreenHeight= GameActivity.mScreenSize.y;

        this.mThread = new GameThread(this);
        this.mHolder = this.getHolder();
        this.is_game_paused = false;
        this.is_game_over = false;
        this.monster_trick_time = false;

        this.monsterSleepCount = 1;
        this.rocketXhaustCount = 1;

        this.mHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder mHolder)
            {
                mThread.setRunning(true);
                mThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder mHolder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder mHolder)
            {
                mThread.setRunning(false);
                boolean retry = true;

                while(retry)
                {
                    try {
                        mThread.join();
                        retry = false;
                    } catch (InterruptedException e) {}
                }
            }
        });
    }
    /**---------------------------------------------------------------------------------------------------**/


    public void update()
    {

        if(is_game_started) {

            /***/   is_game_over = mBall.didMonsterGetTheFinger(fingerX, fingerY);
            /***/   if(is_game_over)
            /***/   tryGameOver();

            if(monsterSleepCount <= mBall.monsterSleepTime)
            {
                monsterSleepCount++;

                if(monsterSleepCount == mBall.monsterSleepTime)
                {
                    if(monster_trick_time)
                    {
                        randomizeTrajectory();
                        monster_trick_time = false;
                    }
                    else
                    {
                        mBall.monsterAttackTrick = 0;
                        monster_trick_time = true;
                    }
                    monsterSleepCount++;
                    mBall.monsterSleepTime = 0;
                }
            }
            else if(mBall.monsterAttackTrick == 3)
            {
                monsterSleepCount = 1;

                /***/   is_game_over = mRocket.didRocketGetTheFinger(fingerX, fingerY);
                /***/   if(is_game_over)
                /***/   tryGameOver();

                if(rocketXhaustCount <= mRocket.rocketXhaustTime)
                {
                    mRocket.rocketTrackFinger(fingerX, fingerY);
                    rocketXhaustCount++;
                }
                else
                {
                    rocketXhaustCount = 1;
                    mRocket.rocketX = mScreenWidth + 80;
                    mRocket.rocketY = mScreenHeight + 80;
                    mRocket.rocketXhaustTime = 0;

                    mBall.monsterAttackTrick = 0;
                    mBall.monsterVelocity = random.nextInt(20) + 15;
                    mBall.monsterSleepTime = random.nextInt(10) + 5;
                    attackAtX = fingerX;
                    attackAtY = fingerY;
                }
            }
            else if(mBall.monsterAttackTrick == 2)
            {
                monsterSleepCount = 1;

                /***/   is_game_over = mFan1.didBulletGetTheFinger(fingerX, fingerY);
                /***/   if(is_game_over)
                /***/   tryGameOver();

                /***/   is_game_over = mFan2.didBulletGetTheFinger(fingerX, fingerY);
                /***/   if(is_game_over)
                /***/   tryGameOver();

                /***/   is_game_over = mFan3.didBulletGetTheFinger(fingerX, fingerY);
                /***/   if(is_game_over)
                /***/   tryGameOver();

                if(time_to_shoot_bullets)
                {
                    time_to_shoot_bullets = false;
                    bullets_on_screen = true;
                    bulletFansTimeGap = 1;

                    attackAtX = fingerX;
                    attackAtY = fingerY;
                    attackFromX = mBall.monsterPosition.x;
                    attackFromY = mBall.monsterPosition.y;

                    mFan1.initBullets(mBall, attackAtX, attackAtY);
                    mFan2.initBullets(mBall, attackAtX, attackAtY);
                    mFan3.initBullets(mBall, attackAtX, attackAtY);
                }

                if(bullets_on_screen)
                {
                    mFan1.setDirectionAndShoot();
                    bulletFansTimeGap++;

                    if(bulletFansTimeGap>5)
                        mFan2.setDirectionAndShoot();

                    if(bulletFansTimeGap>10)
                        mFan3.setDirectionAndShoot();

                    int howManyBulletsOnScreen = 0;

                    for(int i=0; i<7; i++)
                    {
                        if((mFan1.bulletPosition[i].x >= mScreenWidth-10 || mFan1.bulletPosition[i].x <= 10) && (mFan1.bulletPosition[i].y >= mScreenHeight-10 || mFan1.bulletPosition[i].y <= 10))
                            howManyBulletsOnScreen++;
                        if((mFan2.bulletPosition[i].x >= mScreenWidth-10 || mFan2.bulletPosition[i].x <= 10) && (mFan2.bulletPosition[i].y >= mScreenHeight-10 || mFan2.bulletPosition[i].y <= 10))
                            howManyBulletsOnScreen++;
                        if((mFan3.bulletPosition[i].x >= mScreenWidth-10 || mFan3.bulletPosition[i].x <= 10) && (mFan3.bulletPosition[i].y >= mScreenHeight-10 || mFan3.bulletPosition[i].y <= 10))
                            howManyBulletsOnScreen++;
                    }

                    if(howManyBulletsOnScreen >= 16)
                        bullets_on_screen = false;
                }
                else
                {
                    mBall.monsterAttackTrick = 0;
                    mBall.monsterVelocity = random.nextInt(20) + 15;
                    mBall.monsterSleepTime = random.nextInt(1) + 5;
                    attackAtX = fingerX;
                    attackAtY = fingerY;
                }
            }
            else if(mBall.monsterAttackTrick == 4)
            {
                monsterSleepCount = 1;

                if(time_for_some_heat)
                {
                    time_for_some_heat = false;
                    heat_waves_on_screen = true;
                    heatWaveTimeGap = 1;

                    mWave1.initHeatWave(mBall);
                    mWave2.initHeatWave(mBall);
                    mWave3.initHeatWave(mBall);
                    mWave4.initHeatWave(mBall);
                    mWave5.initHeatWave(mBall);
                }

                if(heat_waves_on_screen)
                {
                    heatWaveTimeGap++;
                    heatRect1 = mWave1.setHeatWaveSize(mBall.monsterPosition.x, mBall.monsterPosition.y);

                    if(heatWaveTimeGap > 12)
                    heatRect2 = mWave2.setHeatWaveSize(mBall.monsterPosition.x, mBall.monsterPosition.y);

                    if(heatWaveTimeGap > 24)
                    heatRect3 = mWave3.setHeatWaveSize(mBall.monsterPosition.x, mBall.monsterPosition.y);

                    if(heatWaveTimeGap > 36)
                    heatRect4 = mWave4.setHeatWaveSize(mBall.monsterPosition.x, mBall.monsterPosition.y);

                    if(heatWaveTimeGap > 48)
                    heatRect5 = mWave5.setHeatWaveSize(mBall.monsterPosition.x, mBall.monsterPosition.y);


                    /***/   is_game_over = mWave1.didHeatWaveBurnTheFinger(fingerX, fingerY, 1);
                    /***/   if(is_game_over)
                    /***/   tryGameOver();

                    /***/   is_game_over = mWave2.didHeatWaveBurnTheFinger(fingerX, fingerY, 2);
                    /***/   if(is_game_over)
                    /***/   tryGameOver();

                    /***/   is_game_over = mWave3.didHeatWaveBurnTheFinger(fingerX, fingerY, 1);
                    /***/   if(is_game_over)
                    /***/   tryGameOver();

                    /***/   is_game_over = mWave4.didHeatWaveBurnTheFinger(fingerX, fingerY, 2);
                    /***/   if(is_game_over)
                    /***/   tryGameOver();

                    /***/   is_game_over = mWave5.didHeatWaveBurnTheFinger(fingerX, fingerY, 1);
                    /***/   if(is_game_over)
                    /***/   tryGameOver();


                    if(mWave5.heatWaveRadius > 3*mScreenHeight/2)
                        heat_waves_on_screen = false;
                }
                else
                {
                    mBall.monsterAttackTrick = 0;
                    mBall.monsterVelocity = random.nextInt(20) + 15;
                    mBall.monsterSleepTime = random.nextInt(10) + 5;
                    attackAtX = fingerX;
                    attackAtY = fingerY;
                }
            }
            else
            {
                monsterSleepCount = 1;

                if (mBall.monsterPosition.x >= mScreenWidth || mBall.monsterPosition.y >= mScreenHeight || mBall.monsterPosition.x <= 0 || mBall.monsterPosition.y <= 0) {

                    // For preventing glitchy movement at the boundary.
                    if (mBall.monsterPosition.x > mScreenWidth)
                    mBall.monsterPosition.x = mScreenWidth;
                    else if (mBall.monsterPosition.x < 0)
                    mBall.monsterPosition.x = 0;
                    else if (mBall.monsterPosition.y > mScreenHeight)
                    mBall.monsterPosition.y = mScreenHeight;
                    else if (mBall.monsterPosition.y < 0)
                    mBall.monsterPosition.y = 0;


                    attackAtX = fingerX;
                    attackAtY = fingerY;
                    attackFromX = mBall.monsterPosition.x;
                    attackFromY = mBall.monsterPosition.y;

                    mBall.monsterVelocity = random.nextInt(20) + 15;
                    mBall.monsterSleepTime = random.nextInt(10) + 5;


                }

                mBall.attackFingerPosition(attackAtX, attackAtY, attackFromX, attackFromY);
            }
        }
    }

    public void draw(Canvas canvas)
    {
        canvas.drawColor(Color.BLACK);

        if(mFan1 != null && mBall.monsterAttackTrick == 2)
        {
            for(int i=0; i<7; i++)
            {
                canvas.drawCircle((float)mFan1.bulletPosition[i].x , (float)mFan1.bulletPosition[i].y , (float)mFan1.bulletsRadius , mFan1.bulletsPaint);
                canvas.drawCircle((float)mFan2.bulletPosition[i].x , (float)mFan2.bulletPosition[i].y , (float)mFan2.bulletsRadius , mFan2.bulletsPaint);
                canvas.drawCircle((float)mFan3.bulletPosition[i].x , (float)mFan3.bulletPosition[i].y , (float)mFan3.bulletsRadius , mFan3.bulletsPaint);
            }
        }

        if(mRocket != null && mBall.monsterAttackTrick == 3)
        canvas.drawCircle((float)mRocket.rocketX, (float)mRocket.rocketY, (float)mRocket.rocketRadius, mRocket.rocketPaint);

        if(mWave1 != null && mBall.monsterAttackTrick == 4)
        {
            mWave1.drawHeatWave(canvas, heatRect1, 30);

            if(heatWaveTimeGap > 12)
            mWave2.drawHeatWave(canvas, heatRect2, 0);

            if(heatWaveTimeGap > 24)
            mWave3.drawHeatWave(canvas, heatRect3, 30);

            if(heatWaveTimeGap > 36)
            mWave4.drawHeatWave(canvas, heatRect4, 0);

            if(heatWaveTimeGap > 48)
            mWave5.drawHeatWave(canvas, heatRect5, 30);
        }

        canvas.drawCircle((float)mBall.monsterPosition.x, (float)mBall.monsterPosition.y, (float)mBall.monsterRadius, mBall.monsterPaint);

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(!is_game_paused)
        {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    is_game_started = true;
                    break;

                case MotionEvent.ACTION_UP:
                    //Do Something here.
                    is_game_over = true;
                    tryGameOver();
                    break;

                case MotionEvent.ACTION_MOVE:
                    fingerX = (int)event.getX();
                    fingerY = (int)event.getY();
                    break;
            }
        }
        return true;
    }

    public void randomizeTrajectory()
    {
        mBall.monsterAttackTrick = random.nextInt(4) + 1;
        if(mBall.monsterAttackTrick == 2)
        {
            time_to_shoot_bullets = true;
        }
        else if(mBall.monsterAttackTrick == 3)
        {
            mRocket.initRocket(mBall);
        }
        else if(mBall.monsterAttackTrick == 4)
        {
            time_for_some_heat = true;
        }
    }

    public void tryGameOver()
    {
        if(is_game_over)
        {
            try {
                gameOver();
                is_game_over = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void gameOver() throws InterruptedException {
        mThread.setRunning(false);
        Intent intent = new Intent(mContext, GameOverActivity.class);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
    }
}
