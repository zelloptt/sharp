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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.pixplicity.sharp.OnSvgElementListener;
import com.pixplicity.sharp.Sharp;
import com.pixplicity.sharp.SharpPicture;

import java.util.Random;

import uk.co.senab.photoview.PhotoViewAttacher;

public class SvgDemoActivity extends AppCompatActivity {

    private static final String TAG = SvgDemoActivity.class.getSimpleName();

    private ImageView mImageView;
    private Button mButton;

    private PhotoViewAttacher mAttacher;
    private Sharp mSvg;

    private static final boolean FLIP = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svg_demo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageView = (ImageView) findViewById(R.id.iv_image);
        mButton = (Button) findViewById(R.id.bt_button);

        mSvg = Sharp.loadResource(getResources(), R.drawable.text)
                // If you want to load typefaces from assets:
                .withAssets(getAssets());

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadSvg(true);
            }
        });

        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setMaximumScale(10f);

        reloadSvg(false);
    }

    private void reloadSvg(final boolean changeColor) {
        mSvg.setOnElementListener(new OnSvgElementListener() {

            @Override
            public void onSvgStart(Canvas canvas, RectF bounds) {
                if (FLIP) {
                    canvas.save();
                    canvas.scale(-1f, 1f, bounds.width() / 2, 0);
                }
            }

            @Override
            public void onSvgEnd(Canvas canvas, RectF bounds) {
                if (FLIP) {
                    canvas.restore();
                }
            }

            @Override
            public <T> T onSvgElement(String id, T element, RectF elementBounds, Canvas canvas, RectF canvasBounds, Paint paint) {
                if (changeColor && ("shirt".equals(id) || "hat".equals(id) || "pants".equals(id))) {
                    Random random = new Random();
                    paint.setColor(Color.argb(255, random.nextInt(256),
                            random.nextInt(256), random.nextInt(256)));
                }
                if (needsFlip(id, element)) {
                    canvas.save();
                    RectF r = new RectF(canvasBounds);
                    Matrix m = canvas.getMatrix();
                    m.invert(m);
                    m.mapRect(r);
                    float px = (r.left + r.right) / 2;
                    canvas.scale(-1f, 1f, px, 0);
                }
                return element;
            }

            @Override
            public <T> void onSvgElementDrawn(String id, T element, Canvas canvas) {
                if (needsFlip(id, element)) {
                    canvas.restore();
                }
            }

            private <T> boolean needsFlip(String id, T element) {
                if (!FLIP) {
                    return false;
                }
                return element instanceof Sharp.SvgHandler.SvgText || (id != null && id.endsWith("noflip"));
            }

        });
        SharpPicture picture = mSvg.getSharpPicture();

        {
            Drawable drawable = picture.createDrawable(mImageView);
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
        // Not applicable to this sample
        mButton.setVisibility(View.GONE);

        mAttacher.update();
    }

}
