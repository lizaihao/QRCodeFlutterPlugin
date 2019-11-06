/*
 * Copyright (C) 2008 ZXing authors
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

package flutterqrcode.lizaihao.qr_code_flutter_plugin.decoding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Vector;

import flutterqrcode.lizaihao.qr_code_flutter_plugin.R;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.camera.CameraManager;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.logs.QCLog;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.view.flutter.FlutterViewfinderView;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.view.ICaptureView;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.view.ViewfinderResultPointCallback;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.view.ViewfinderView;

/**
 * 扫描界面处理者
 *
 * @author lizaihao
 * @since 2019/1/17 上午12:09
 */
public final class CaptureViewHandler extends Handler {

    private static final String TAG = CaptureViewHandler.class.getSimpleName();

    private final ICaptureView mCaptureView;
    private final DecodeThread mDecodeThread;
    private State mState;

    private enum State {
        /**
         * 扫描预览中
         */
        PREVIEW,
        /**
         * 扫描成功
         */
        SUCCESS,
        /**
         * 结束
         */
        DONE
    }

    public CaptureViewHandler(ICaptureView captureView, Vector<BarcodeFormat> decodeFormats,
                              String characterSet, ViewfinderView viewfinderView) {
        mCaptureView = captureView;
        mDecodeThread = new DecodeThread(captureView, decodeFormats, characterSet,
                new ViewfinderResultPointCallback(viewfinderView));
        mDecodeThread.start();
        mState = State.SUCCESS;
        // Start ourselves capturing previews and decoding.
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }


    public CaptureViewHandler(ICaptureView captureView, Vector<BarcodeFormat> decodeFormats,
                              String characterSet, FlutterViewfinderView view) {
        mCaptureView = captureView;
        mDecodeThread = new DecodeThread(captureView, decodeFormats, characterSet,
                new ViewfinderResultPointCallback(view));
        mDecodeThread.start();
        mState = State.SUCCESS;
        // Start ourselves capturing previews and decoding.
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.auto_focus) {
            // When one auto focus pass finishes, start another. This is the closest thing to
            // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
            if (mState == State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        } else if (message.what == R.id.restart_preview) {
            QCLog.dTag(TAG, "Got restart preview message");
            restartPreviewAndDecode();
        } else if (message.what == R.id.decode_succeeded) {
            QCLog.dTag(TAG, "Got decode succeeded message");
            mState = State.SUCCESS;
            Bundle bundle = message.getData();

            /***********************************************************************/
            Bitmap barcode = bundle == null ? null :
                    (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);

            mCaptureView.handleDecode((Result) message.obj, barcode);
            /***********************************************************************/
        } else if (message.what == R.id.decode_failed) {
            // We're decoding as fast as possible, so when one decode fails, start another.
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
        } else if (message.what == R.id.return_scan_result) {
            QCLog.dTag(TAG, "Got return scan result message");
            mCaptureView.getActivity().setResult(Activity.RESULT_OK, (Intent) message.obj);
            mCaptureView.getActivity().finish();
        } else if (message.what == R.id.launch_product_query) {
            QCLog.dTag(TAG, "Got product query message");
            String url = (String) message.obj;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            mCaptureView.getActivity().startActivity(intent);
        }
    }

    public void quitSynchronously() {
        mState = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (mState == State.SUCCESS) {
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            mCaptureView.drawViewfinder();
        }
    }

}
