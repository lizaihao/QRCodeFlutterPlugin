/*
 * Copyright (C) 2010 ZXing authors
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

package flutterqrcode.lizaihao.qr_code_flutter_plugin.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

import flutterqrcode.lizaihao.qr_code_flutter_plugin.XQRCode;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.logs.QCLog;


/**
 * 自动聚焦的回掉
 *
 * @author lizaihao
 * @since 2019-05-18 17:23
 */
public final class AutoFocusCallback implements Camera.AutoFocusCallback {

    private static final String TAG = AutoFocusCallback.class.getSimpleName();

    public static final long AUTO_FOCUS_INTERVAL_MS = 1500L;

    private Handler autoFocusHandler;
    private int autoFocusMessage;

    public void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
        this.autoFocusHandler = autoFocusHandler;
        this.autoFocusMessage = autoFocusMessage;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (autoFocusHandler != null) {
            Message message = autoFocusHandler.obtainMessage(autoFocusMessage, success);
            autoFocusHandler.sendMessageDelayed(message, XQRCode.getAutoFocusInterval());
            autoFocusHandler = null;
        } else {
            QCLog.dTag(TAG, "Got auto-focus callback, but no handler for it");
        }
    }

}
