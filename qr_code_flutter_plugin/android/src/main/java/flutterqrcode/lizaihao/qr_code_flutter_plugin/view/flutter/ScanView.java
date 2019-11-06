package flutterqrcode.lizaihao.qr_code_flutter_plugin.view.flutter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import androidx.annotation.Nullable;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.R;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.XQRCode;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.camera.CameraManager;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.decoding.CaptureViewHandler;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.decoding.InactivityTimer;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.utils.QRCodeAnalyzeUtils;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.view.CaptureFragment;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.view.ICaptureView;


@SuppressLint("ViewConstructor")
public class ScanView extends FrameLayout implements ICaptureView, SurfaceHolder.Callback {

    private static final String TAG = "FaceDetectView";
    private Context mContext;
    private FlutterScanViewListener mFlutterScanViewListener;
    public final int REQUEST_CODE_GET_PIC_URI = 0X12;
    private CaptureViewHandler mHandler;
    private FlutterViewfinderView flutterViewfinderView;
    private boolean mHasSurface;
    private Vector<BarcodeFormat> mDecodeFormats;
    private String mCharacterSet;
    private InactivityTimer mInactivityTimer;
    private MediaPlayer mMediaPlayer;
    private boolean mPlayBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean mVibrate;
    private SurfaceHolder mSurfaceHolder;
    @Nullable
    private CaptureFragment.CameraInitCallBack mCameraInitCallBack;
    private QRCodeAnalyzeUtils.AnalyzeCallback mAnalyzeCallback;
    private Camera mCamera;
    View view;

    public ScanView(Context context, Map<String, Object> flutterParams, FlutterScanViewListener flutterScanViewListener) {
        super(context);
        mFlutterScanViewListener = flutterScanViewListener;
        mContext = context;
        initCapture();
        beforeCapture();
        mInactivityTimer = new InactivityTimer(this.getActivity());
    }

    /**
     * 做二维码采集之前需要做的事情
     */
    protected void beforeCapture() {

    }

    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    /**
     * 初始化采集
     */
    protected void initCapture() {
        Activity activity = (Activity)mContext;
        if (activity != null) {
            // 防止锁屏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            //为了解决fragment里面放SurfaceView，第一次黑屏的问题
            activity.getWindow().setFormat(PixelFormat.TRANSLUCENT);
        }
        CameraManager.init(requireNonNull(mContext).getApplicationContext());
        mHasSurface = false;
        view = LayoutInflater.from(activity).inflate(R.layout.xqrcode_flutter_fragment_capture, null, false);
        flutterViewfinderView =  view.findViewById(R.id.viewfinder_view);
        view.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(view);
        SurfaceView surfaceView = view.findViewById(R.id.preview_view);
        mSurfaceHolder = surfaceView.getHolder();
        /**
         * 照相机初始化监听
         */
        mCameraInitCallBack = new CaptureFragment.CameraInitCallBack() {
            @Override
            public void callBack(@Nullable Exception e) {
                if (e != null) {
                    onCameraInitFailed();
                } else {
                    //照相机初始化成功
                    onCameraInitSuccess();
                }
            }
        };

        /**
         * 二维码解析回调函数
         */
        mAnalyzeCallback = analyzeCallback;
        onResume();
    }



    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            mCamera = CameraManager.get().getCamera();
        } catch (Exception e) {
            if (mCameraInitCallBack != null) {
                mCameraInitCallBack.callBack(e);
            }
            return;
        }
        if (mCameraInitCallBack != null) {
            //打开成功
            mCameraInitCallBack.callBack(null);
        }
        if (mHandler == null) {
            mHandler = new CaptureViewHandler(this, mDecodeFormats, mCharacterSet,flutterViewfinderView);
        }
    }

    public void onPause() {
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        CameraManager.get().closeDriver();
    }

    public void onDestroyView() {
        stopScan();
        if (mInactivityTimer != null) {
            mInactivityTimer.shutdown();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            //关键语句
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mHasSurface = false;

    }


    protected void onResume(){
        if (mHasSurface) {
            initCamera(mSurfaceHolder);
        } else {
            mSurfaceHolder.addCallback(this);
            mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        mDecodeFormats = null;
        mCharacterSet = null;

        mPlayBeep = true;
        AudioManager audioService = (AudioManager) requireNonNull(getActivity()).getSystemService(Context.AUDIO_SERVICE);
        if (audioService != null && audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            mPlayBeep = false;
        }
        initBeepSound();
        mVibrate = true;
    }

    private void initBeepSound() {
        if (mPlayBeep && mMediaPlayer == null) {
            requireNonNull(getActivity()).setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mBeepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener mBeepListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


    /**
     * 二维码解析回调函数
     */
    QRCodeAnalyzeUtils.AnalyzeCallback analyzeCallback = new QRCodeAnalyzeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap bitmap, String result) {
            Toast.makeText(mContext,result,Toast.LENGTH_SHORT).show();
            mFlutterScanViewListener.scanViewHandleResult(result);
            handleAnalyzeSuccess(bitmap, result);
        }

        @Override
        public void onAnalyzeFailed() {
            handleAnalyzeFailed();
        }
    };

    /**
     * 处理扫描成功
     *
     * @param bitmap
     * @param result
     */
    protected void handleAnalyzeSuccess(Bitmap bitmap, String result) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(XQRCode.RESULT_TYPE, XQRCode.RESULT_SUCCESS);
        bundle.putString(XQRCode.RESULT_DATA, result);
        resultIntent.putExtras(bundle);
    }

    /**
     * 处理解析失败
     */
    protected void handleAnalyzeFailed() {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(XQRCode.RESULT_TYPE, XQRCode.RESULT_FAILED);
        bundle.putString(XQRCode.RESULT_DATA, "");
        resultIntent.putExtras(bundle);
    }
    /**
     * 相机初始化成功
     */
    protected void onCameraInitSuccess() {

    }

    /**
     * 相机初始化失败
     */
    protected void onCameraInitFailed() {

    }

    /**
     * 开启闪光灯
     */
    public void openAutoFlash() {
        try {
            XQRCode.switchFlashLight(true);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭闪光灯
     */
    public void closeAutoFlash() {
        try {
            XQRCode.switchFlashLight(false);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    /**/
    public void stopScan() {
        onPause();
    }

    /**/
    public void restartScan() {
        onResume();
    }

    /*添加图片*/
    public void addPicture() {
        goPicture();
    }

    private void goPicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        Activity activity = (Activity)mContext;
        activity.startActivityForResult(intent, REQUEST_CODE_GET_PIC_URI);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
        if (mCamera != null) {
            if (CameraManager.get().isPreviewing()) {
                if (!CameraManager.get().isUseOneShotPreviewCallback()) {
                    mCamera.setPreviewCallback(null);
                }
                mCamera.stopPreview();
                CameraManager.get().getPreviewCallback().setHandler(null, 0);
                CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
                CameraManager.get().setPreviewing(false);
            }
        }
    }
    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(VIBRATE_DURATION);
            }
        }
    }

    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        mInactivityTimer.onActivity();
        playBeepSoundAndVibrate();

        if (result == null || TextUtils.isEmpty(result.getText())) {
            if (mAnalyzeCallback != null) {
                mAnalyzeCallback.onAnalyzeFailed();
            }
        } else {
            if (mAnalyzeCallback != null) {
                mAnalyzeCallback.onAnalyzeSuccess(barcode, result.getText());
            }
        }
    }

    @Override
    public Activity getActivity() {
        return (Activity) mContext;
    }

    @Override
    public void drawViewfinder() {

    }

    @Override
    public Handler getCaptureHandler() {
            return mHandler;
    }

    public interface FlutterScanViewListener {
        void scanViewHandleResult(String result);
    }
}
