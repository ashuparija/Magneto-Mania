package com.sdsmdg.kd.helpers;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector3;
import com.sdsmdg.kd.gameplay.controllers.MagnusController;
import com.sdsmdg.kd.gameplay.objects.Magnus;
import com.sdsmdg.kd.gameworld.GameRenderer;
import com.sdsmdg.kd.screens.GameScreen;

/**
 * Created by Haresh on 09-03-2016.
 */
public class InputHandler implements InputProcessor {
    /**CLASS MEMBERS*******************************************************/
    private Magnus magnus;
    public static Vector3 touch = new Vector3(0,0,0);
    /**-------------------------------------------------------------------**/

    /**CONSTRUCTOR **********************************************************/
    public InputHandler(Magnus magnus) {
        this.magnus = magnus;
    }
    /**-------------------------------------------------------------------**/

    /**DEFAULT METHODS ***************************************************/
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        GameScreen.isTouched = true;
        GameRenderer.cam.unproject(touch.set(screenX, screenY, 0));
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    /**-------------------------------------------------------------------**/
}
