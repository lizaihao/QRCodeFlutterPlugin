package flutterqrcode.lizaihao.qr_code_flutter_plugin.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.R;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.XQRCode;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.view.CaptureActivity;

/**
 * 自定义二维码扫描界面
 *
 * @author lizaihao
 * @since 2019/5/30 10:43
 */
public class CustomCaptureActivity extends CaptureActivity implements View.OnClickListener {

    private AppCompatImageView mIvFlashLight;
    private AppCompatImageView mIvFlashLight1;

    private boolean mIsOpen;

    /**
     * 开始二维码扫描
     *
     * @param fragment
     * @param requestCode 请求码
     * @param theme       主题
     */
    public static void start(Fragment fragment, int requestCode, int theme) {
        Intent intent = new Intent(fragment.getContext(), CustomCaptureActivity.class);
        intent.putExtra(KEY_CAPTURE_THEME, theme);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 开始二维码扫描
     *
     * @param activity
     * @param requestCode 请求码
     * @param theme       主题
     */
    public static void start(Activity activity, int requestCode, int theme) {
        Intent intent = new Intent(activity, CustomCaptureActivity.class);
        intent.putExtra(KEY_CAPTURE_THEME, theme);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getCaptureLayoutId() {
        return R.layout.activity_custom_capture;
    }

    @Override
    protected void beforeCapture() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        mIvFlashLight = findViewById(R.id.iv_flash_light);
        mIvFlashLight1 = findViewById(R.id.iv_flash_light1);
    }

    @Override
    protected void onCameraInitSuccess() {
        mIvFlashLight.setVisibility(View.VISIBLE);
        mIvFlashLight1.setVisibility(View.VISIBLE);

        mIsOpen = XQRCode.isFlashLightOpen();
        refreshFlashIcon();
        mIvFlashLight.setOnClickListener(this);
        mIvFlashLight1.setOnClickListener(this);
    }

    @Override
    protected void onCameraInitFailed() {
        mIvFlashLight.setVisibility(View.GONE);
        mIvFlashLight1.setVisibility(View.GONE);
    }

    private void refreshFlashIcon() {
        if (mIsOpen) {
            mIvFlashLight.setImageResource(R.drawable.ic_flash_light_on);
            mIvFlashLight1.setImageResource(R.drawable.ic_flash_light_open);
        } else {
            mIvFlashLight.setImageResource(R.drawable.ic_flash_light_off);
            mIvFlashLight1.setImageResource(R.drawable.ic_flash_light_close);
        }
    }

    private void switchFlashLight() {
        mIsOpen = !mIsOpen;
        try {
            XQRCode.switchFlashLight(mIsOpen);
            refreshFlashIcon();
        } catch (RuntimeException e) {
            e.printStackTrace();
           // ToastUtils.toast("设备不支持闪光灯!");
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_back) {
            finish();

        } else if (i == R.id.iv_flash_light || i == R.id.iv_flash_light1) {
            switchFlashLight();

        } else {
        }
    }
}
