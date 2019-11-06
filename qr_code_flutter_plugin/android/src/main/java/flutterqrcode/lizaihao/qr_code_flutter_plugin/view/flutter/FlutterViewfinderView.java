/*
 * Copyright (C) 2018 lizaihaojys(lizaihaojys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package flutterqrcode.lizaihao.qr_code_flutter_plugin.view.flutter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.Collection;
import java.util.HashSet;

import flutterqrcode.lizaihao.qr_code_flutter_plugin.R;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.camera.CameraManager;

/**
 * 自定义view flutter端使用
 *
 * @author lizaihao
 * @since 2019/5/17 17:54
 */
public final class FlutterViewfinderView extends View {

    private Collection<ResultPoint> mPossibleResultPoints;

    public FlutterViewfinderView(Context context) {
        this(context, null);
    }

    public FlutterViewfinderView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ViewfinderViewStyle);
    }

    public FlutterViewfinderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPossibleResultPoints = new HashSet<>(5);
        initInner(context, attrs, defStyleAttr);
    }

    /**
     * 初始化内部框的大小
     *
     * @param context
     * @param attrs
     */
    private void initInner(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ViewfinderView, defStyleAttr, 0);
        // 扫描框的宽度
        CameraManager.FRAME_WIDTH = ta.getDimensionPixelSize(R.styleable.ViewfinderView_inner_width, getDefaultScanSize(getContext()));
        // 扫描框的高度
        CameraManager.FRAME_HEIGHT = ta.getDimensionPixelSize(R.styleable.ViewfinderView_inner_height, getDefaultScanSize(getContext()));
        ta.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
    }
    /**
     * 扫描线
     */

    public void addPossibleResultPoint(ResultPoint point) {
        mPossibleResultPoints.add(point);
    }
    /**
     * 获取默认扫描的尺寸
     * @return
     */
    public int getDefaultScanSize(Context context) {
        return Math.min(getScreenWidth(context), getScreenHeight(context)) * 3 / 4;
    }

    /**
     * 得到设备屏幕的宽度
     */
    private static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    private static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

}
