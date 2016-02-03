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

package com.pixplicity.sharp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@SuppressWarnings("unused")
public interface OnSvgElementListener {

    /**
     * Called when reading of the SVG starts.
     * @param canvas Canvas onto which the element is drawn
     * @param bounds Bounds of the canvas in {@link RectF}
     */
    void onSvgStart(@NonNull Canvas canvas,
                    @Nullable RectF bounds);

    /**
     * Called when reading of the SVG ends.
     * @param canvas Canvas onto which the element is drawn
     * @param bounds Bounds of the canvas in {@link RectF}
     */
    void onSvgEnd(@NonNull Canvas canvas,
                  @Nullable RectF bounds);

    /**
     * Called when an SVG element is encountered. Take care to return the element if you wish for it
     * to be drawn.
     * <p>Note that this method may be called twice if an element has both a fill and a stroke.</p>
     * @param id ID of the element
     * @param element Element itself
     * @param elementBounds Bounds of the element in {@link RectF}
     * @param canvas Canvas onto which the element is drawn
     * @param canvasBounds Bounds of the canvas in {@link RectF}
     * @param paint Paint to be applied to this element
     * @return This method should return the element; return {@code null} if it should not be drawn.
     */
    @Nullable
    <T> T onSvgElement(@Nullable String id,
                       @NonNull T element,
                       @Nullable RectF elementBounds,
                       @NonNull Canvas canvas,
                       @Nullable RectF canvasBounds,
                       @Nullable Paint paint);

    /**
     * Called when an SVG element has been drawn.
     * @param id ID of the element
     * @param element Element itself
     * @param canvas Canvas onto which the element is drawn
     * @param paint Paint to be applied to this element
     */
    <T> void onSvgElementDrawn(@Nullable String id,
                               @NonNull T element,
                               @NonNull Canvas canvas,
                               @Nullable Paint paint);

}