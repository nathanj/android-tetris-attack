/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.snake;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

/**
 * Snake: a simple game that everyone can enjoy.
 * <p/>
 * This is an implementation of the classic Game "Snake", in which you control a serpent roaming
 * around the garden looking for apples. Be careful, though, because when you catch one, not only
 * will you become longer, but you'll move faster. Running into yourself or the walls will end the
 * game.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Snake extends Activity {

    /**
     * Constants for desired direction of moving the snake
     */
    public static int MOVE_LEFT = 0;
    public static int MOVE_UP = 1;
    public static int MOVE_DOWN = 2;
    public static int MOVE_RIGHT = 3;

    private static String ICICLE_KEY = "snake-view";

    private SnakeView mSnakeView;
    private BoardView boardView;

    /**
     * Called when Activity is first created. Turns off the title bar, sets up the content views,
     * and fires up the SnakeView.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.snake_layout);

        boardView = (BoardView) findViewById(R.id.board);

//        mSnakeView = (SnakeView) findViewById(R.id.snake);
//        mSnakeView.setDependentViews((TextView) findViewById(R.id.text),
//                findViewById(R.id.arrowContainer), findViewById(R.id.background));

        if (savedInstanceState == null) {
            // We were just launched -- set up a new game
//            mSnakeView.setMode(SnakeView.READY);
        } else {
            // We are being restored
//            Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
//            if (map != null) {
//                mSnakeView.restoreState(map);
//            } else {
//                mSnakeView.setMode(SnakeView.PAUSE);
//            }
        }
        boardView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Normalize x,y between 0 and 1
                float x = event.getX() / v.getWidth();
                float y = event.getY() / v.getHeight();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        System.out.println("event = " + event);
//                    	mSnakeView.moveApple(event.getX(), event.getY());
                        break;
                }

                return false;
            }
        });


        boardView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause the game along with the activity
        //mSnakeView.setMode(SnakeView.PAUSE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store the game state
        //outState.putBundle(ICICLE_KEY, mSnakeView.saveState());
    }

}
