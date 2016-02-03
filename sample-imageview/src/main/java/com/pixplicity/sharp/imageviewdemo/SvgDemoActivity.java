/*
    Copyright 2011, 2015 Pixplicity, Larva Labs LLC and Google, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    Sharp is heavily based on prior work. It was originally forked from
        https://github.com/pents90/svg-android
    And changes from other forks have been consolidated:
        https://github.com/b2renger/svg-android
        https://github.com/mindon/svg-android
        https://github.com/josefpavlik/svg-android
 */

package com.pixplicity.sharp.imageviewdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.pixplicity.sharp.OnSvgElementListener;
import com.pixplicity.sharp.Sharp;
import com.pixplicity.sharp.SharpDrawable;
import com.pixplicity.sharp.SharpPicture;

import java.util.ArrayList;
import java.util.Random;

import uk.co.senab.photoview.PhotoViewAttacher;

public class SvgDemoActivity extends AppCompatActivity {

    private static final String TAG = SvgDemoActivity.class.getSimpleName();

    private static final boolean USE_CACHE = false;

    private ImageView mImageView;
    private Button mButton;

    private PhotoViewAttacher mAttacher;
    private Sharp mSvg;
    private float mShadowRadius, mShadowDX, mShadowDY;
    private int mShadowColor;

    private final ArrayList<String> mShadowIdPrefixes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svg_demo);

        mShadowIdPrefixes.add("shirt");
        mShadowIdPrefixes.add("hat");
        mShadowIdPrefixes.add("pants");

        mShadowRadius = 3.0f;
        mShadowDX = 0;
        mShadowDY = 1;
        mShadowColor = Color.parseColor("#000000");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageView = (ImageView) findViewById(R.id.iv_image);
        mButton = (Button) findViewById(R.id.bt_button);

        mSvg = Sharp.loadResource(getResources(), R.raw.cartman);
        // If you want to load typefaces from assets:
        //          .withAssets(getAssets());

        // If you want to load an SVG from assets:
        //mSvg = Sharp.loadAsset(getAssets(), "cartman.svg");

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadSvg(true);
            }
        });

        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setMaximumScale(15f);

        mAttacher.setOnMatrixChangeListener(new PhotoViewAttacher.OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                if (USE_CACHE) {
                    String msg = rect.toString();
                    Log.d(TAG, msg);
                    Log.d(TAG, "scale=" + mAttacher.getScale());
                }
            }
        });

        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (USE_CACHE) {
                    Drawable drawable = mImageView.getDrawable();
                    if (drawable != null && drawable instanceof SharpDrawable) {
                        // set the scale factor on the SharpDrawable
                        ((SharpDrawable) drawable).setCacheScale(mAttacher.getScale());
                        ((SharpDrawable) drawable).resetCache();
                    }
                    mImageView.invalidate();
                }
            }
        });

        reloadSvg(false);
    }

    private void reloadSvg(final boolean changeColor) {
        mSvg.setOnElementListener(new OnSvgElementListener() {

            @Override
            public void onSvgStart(@NonNull Canvas canvas,
                                   @Nullable RectF bounds) {
            }

            @Override
            public void onSvgEnd(@NonNull Canvas canvas,
                                 @Nullable RectF bounds) {
            }

            @Override
            public <T> T onSvgElement(@Nullable String id,
                                      @NonNull T element,
                                      @Nullable RectF elementBounds,
                                      @NonNull Canvas canvas,
                                      @Nullable RectF canvasBounds,
                                      @Nullable Paint paint) {
                if (changeColor && paint != null && paint.getStyle() == Paint.Style.FILL && isPrefixIn(id, mShadowIdPrefixes)) {
                    Random random = new Random();
                    paint.setColor(Color.argb(255, random.nextInt(256),
                            random.nextInt(256), random.nextInt(256)));
                    paint.setShadowLayer(mShadowRadius, mShadowDX, mShadowDY, mShadowColor);
                }
                return element;
            }

            @Override
            public <T> void onSvgElementDrawn(@Nullable String id,
                                              @NonNull T element,
                                              @NonNull Canvas canvas,
                                              @Nullable Paint paint) {
                if (paint != null) {
                    paint.setShadowLayer(0, 0, 0, 0);
                }
            }

        });
        mSvg.getSharpPicture(new Sharp.PictureCallback() {
            @Override
            public void onPictureReady(SharpPicture picture) {
                {
                    SharpDrawable drawable = picture.getDrawable(mImageView);
                    if (USE_CACHE) {
                        drawable.setCaching(true);
                    }
                    mImageView.setImageDrawable(drawable);
                }

                {
                    // We don't want to use the same drawable, as we're specifying a custom size; therefore
                    // we call createDrawable() instead of getDrawable()
                    int iconSize = getResources().getDimensionPixelSize(R.dimen.icon_size);
                    Drawable drawable = picture.createDrawable(mButton, iconSize);
                    mButton.setCompoundDrawables(
                            drawable,
                            null, null, null);
                }

                mAttacher.update();
            }
        });
    }

    private boolean isPrefixIn(String id, ArrayList<String> prefixList) {
        if (id != null) {
            for (String prefix : prefixList) {
                if (id.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

}
