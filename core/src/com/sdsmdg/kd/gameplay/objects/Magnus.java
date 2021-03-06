package com.sdsmdg.kd.gameplay.objects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.sdsmdg.kd.gameplay.controllers.MagnusController;
import com.sdsmdg.kd.gameplay.utilities.Geometry;
import com.sdsmdg.kd.helpers.InputHandler;
import com.sdsmdg.kd.screens.GameScreen;

public class Magnus {
    /** CLASS MEMBERS *******************************************************/
    public Vector2             magnusPosition;
    MagnusController           magnusController;
    public double              magnusVelocity;
    public int                 magnusRadius;
    public int                 magnusSleepTime;
    float                      screenWidth;
    float                      screenHeight;
    private RandomXS128        random;
    private Vector2            mVelocityComponent;
    public static boolean      temp;
    /**--------------------------------------------------------------------**/

    /** CONSTRUCTOR *********************************************************/
    public Magnus() {
        this.screenWidth       = Gdx.graphics.getWidth();
        this.screenHeight      = Gdx.graphics.getHeight();
        this.random            = new RandomXS128();
        this.magnusController  = new MagnusController(this);
        this.temp              = false;
        this.magnusSleepTime   = random.nextInt(15) + 15;
        int gameWidth          = 136;
        int gameHeight         = (int)((screenHeight/screenWidth) * gameWidth);

        int x = random.nextInt(2);
        if(x == 1) {
            x = (int)screenWidth;
        }
        int y = random.nextInt((int)screenHeight + 1);

        this.magnusPosition     = new Vector2(x, y);
        this.magnusRadius       = (int)(Math.sqrt((screenWidth*screenHeight) / (int)(12 * Math.PI)));
        this.magnusVelocity     = random.nextInt(15) + 15;
    }
    /**--------------------------------------------------------------------**/

    /**MOTION REGULATIONS *****************************************************/
    public void prepareForSleepAndAttack(){
        if (magnusPosition.x >= screenWidth-5 || magnusPosition.x <= 5 ||
                magnusPosition.y >= screenHeight-5 || magnusPosition.y <= 5) {

            // For preventing glitchy movement at the boundary.
            if (magnusPosition.x > screenWidth) {
                magnusPosition.x = screenWidth;
                mVelocityComponent.set(0,0);
                GameScreen.isTouched = false;
            }
            if (magnusPosition.x < 0) {
                magnusPosition.x = 0;
                mVelocityComponent.set(0,0);
                GameScreen.isTouched = false;
            }
            if (magnusPosition.y > screenHeight) {
                magnusPosition.y = screenHeight;
                mVelocityComponent.set(0,0);
                GameScreen.isTouched = false;
            }
            if (magnusPosition.y < 0) {
                magnusPosition.y = 0;
                mVelocityComponent.set(0,0);
                GameScreen.isTouched = false;
            }

            magnusController.destinationPoint = new Vector2(InputHandler.touch.x,InputHandler.touch.y);
            magnusController.initialPoint = magnusPosition;
            magnusVelocity = random.nextInt(15) + 15 + (int)(magnusController.score / 1000);
            magnusSleepTime = random.nextInt(15) + 15;
        }
    }

    public void attackFingerPosition(){
        if (!temp){
            setVelocityComponent();
            temp = true;
        }
        magnusVelocity               -= 0.05;
        magnusPosition.x             += mVelocityComponent.x;
        magnusPosition.y             += mVelocityComponent.y;

    }

    public void setVelocityComponent(){
        mVelocityComponent    = Geometry.calcVelocityComponents(magnusController.destinationPoint,magnusController.initialPoint,(int)magnusVelocity);
    }
    /**--------------------------------------------------------------------**/
}
