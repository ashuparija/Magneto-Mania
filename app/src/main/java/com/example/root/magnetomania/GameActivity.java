package com.example.root.magnetomania;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

    private Display mGameDisplay;
    public static Point mScreenSize = new Point(0,0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mGameDisplay = getWindowManager().getDefaultDisplay();
        mGameDisplay.getSize(mScreenSize);
        // My testing device has 480px width, 782px height.

        setContentView(new GameView(this));
    }

}