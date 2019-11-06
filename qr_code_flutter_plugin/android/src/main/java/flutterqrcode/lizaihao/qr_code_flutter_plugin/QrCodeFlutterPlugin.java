package flutterqrcode.lizaihao.qr_code_flutter_plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

import flutterqrcode.lizaihao.qr_code_flutter_plugin.activity.CustomCaptureActivity;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.scan.ScanViewFactory;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.utils.EventChannelUtils;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.utils.PathUtils;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.utils.QRCodeAnalyzeUtils;
import flutterqrcode.lizaihao.qr_code_flutter_plugin.view.CaptureActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StandardMessageCodec;

import static flutterqrcode.lizaihao.qr_code_flutter_plugin.view.CaptureActivity.KEY_CAPTURE_THEME;

/** PaffScanPlugin */
public class QrCodeFlutterPlugin implements MethodCallHandler,PluginRegistry.ActivityResultListener {
  /** Plugin registration. */
  public static Registrar sRegister;
  private Context mContext;
  private final int RESULT_OK = -1;
  public final int REQUEST_CODE_GET_PIC_URI = 0X12;
  private static final String FACE_VIEW_TYPE = "scan_qr_view";
  public static final int REQUEST_CODE = 111;
  public static final int REQUEST_CODE_GET_PIC_URI_FLUTTER = 0X13;


  Result result;

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "qr_code_flutter_plugin");
    final QrCodeFlutterPlugin instance = new QrCodeFlutterPlugin(registrar.activity());
    registrar.addActivityResultListener(instance);

    channel.setMethodCallHandler(instance);
    registrar.platformViewRegistry().registerViewFactory(FACE_VIEW_TYPE,
            new ScanViewFactory(new StandardMessageCodec(), registrar.messenger(), registrar.activity()));
    sRegister = registrar;

  }


  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      if (requestCode == REQUEST_CODE_GET_PIC_URI) {
        if (data != null) {
          Uri uri = data.getData();
          getAnalyzeQRCodeResult(uri);
        }
      }if (requestCode == REQUEST_CODE_GET_PIC_URI_FLUTTER) {
        if (data != null) {
          Uri uri = data.getData();
          getAnalyzeQRCodeResultFlutter(uri);
        }
      }else if(requestCode == REQUEST_CODE){
        if (data != null) {
          handleScanResult(data);
        }
      }
    }
    return false;
  }

  private void getAnalyzeQRCodeResult(Uri uri) {
    XQRCode.analyzeQRCode(PathUtils.getFilePathByUri(mContext, uri), new QRCodeAnalyzeUtils.AnalyzeCallback() {
      @Override
      public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
        EventChannelUtils eventChannelUtils = EventChannelUtils.getInstance();
        eventChannelUtils.getResultChannel().success(result);
        Toast.makeText(mContext,"扫描结果:"+result, Toast.LENGTH_LONG).show();
      }

      @Override
      public void onAnalyzeFailed() {
        Toast.makeText(mContext,"解析二维码失败", Toast.LENGTH_LONG).show();
      }
    });
  }

  private void getAnalyzeQRCodeResultFlutter(Uri uri) {
    XQRCode.analyzeQRCode(PathUtils.getFilePathByUri(mContext, uri), new QRCodeAnalyzeUtils.AnalyzeCallback() {
      @Override
      public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
        QrCodeFlutterPlugin.this.result.success(result);
        Toast.makeText(mContext,"扫描结果:"+result, Toast.LENGTH_LONG).show();
      }

      @Override
      public void onAnalyzeFailed() {
        Toast.makeText(mContext,"解析二维码失败", Toast.LENGTH_LONG).show();
      }
    });
  }

  /**
   * 处理二维码扫描结果
   *
   * @param data
   */
  private void handleScanResult(Intent data) {
    if (data != null) {
      Bundle bundle = data.getExtras();
      if (bundle != null) {
        if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_SUCCESS) {
          String result = bundle.getString(XQRCode.RESULT_DATA);
          this.result.success(result);
          Toast.makeText(mContext,"解析结果:" + result, Toast.LENGTH_LONG).show();
        } else if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_FAILED) {
          Toast.makeText(mContext,"解析解析二维码失败结果:", Toast.LENGTH_LONG).show();
        }
      }
    }
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if(call.method.equals("checkPermission")) {
      checkPermission(sRegister.context(), result);
    } else if(call.method.equals("goSystemSetting")) {
      goSystemSetting(sRegister.context());
    } else if(call.method.equals("checkPicturePermission")) {
      checkPicturePermission(sRegister.context(), result);
    }else if(call.method.equals("gotoNativeScan1")) {
      this.result = result;
      Activity activity = (Activity) mContext;
      Intent intent = new Intent(activity, CaptureActivity.class);
      intent.putExtra(KEY_CAPTURE_THEME, R.style.XQRCodeTheme);
      activity.startActivityForResult(intent, REQUEST_CODE);
    }else if(call.method.equals("gotoNativeScan2")) {
      this.result = result;
      Activity activity = (Activity) mContext;
      Intent intent = new Intent(activity, CustomCaptureActivity.class);
      intent.putExtra(KEY_CAPTURE_THEME, R.style.XQRCodeTheme_Custom);
      activity.startActivityForResult(intent, REQUEST_CODE);
    }else if(call.method.equals("gotoNativeScan3")) {
      Toast.makeText(mContext,"待实现",Toast.LENGTH_SHORT).show();
    }else if(call.method.equals("gotoNativeScan4")) {
      Toast.makeText(mContext,"待实现",Toast.LENGTH_SHORT).show();
    }else if(call.method.equals("gotoNativeScan5")) {
      this.result = result;
      Activity activity = (Activity) mContext;
      Intent intent = new Intent(XQRCode.ACTION_DEFAULT_CAPTURE);
      activity.startActivityForResult(intent, REQUEST_CODE);
    }else if(call.method.equals("gotoNativeScan6")) {
      Toast.makeText(mContext,"待实现",Toast.LENGTH_SHORT).show();
    }else if(call.method.equals("gotoNativeScan7")) {
      this.result = result;
      Activity activity = (Activity) mContext;
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.setType("image/*");
      activity.startActivityForResult(intent, REQUEST_CODE_GET_PIC_URI_FLUTTER);
    }else{
      result.notImplemented();
    }
  }

  private void goSystemSetting(Context context) {
    context.startActivity(new Intent(Settings.ACTION_SETTINGS));
  }

  private QrCodeFlutterPlugin(Activity activity) {
    this.mContext = activity;
  }

  public void checkPermission(Context context, final Result result) {
    AndPermission.with(context)
            .permission(Manifest.permission.CAMERA)
            .rationale(new Rationale() {
              @Override
              public void showRationale(Context context, List<String> permissions, RequestExecutor executor) {
                executor.execute();
              }
            })
            .onGranted(new Action() {
              @Override
              public void onAction(List<String> permissions) {
                result.success(true);
              }
            })
            .onDenied(new Action() {
              @Override
              public void onAction(List<String> permissions) {
                result.success(false);
              }
            })
            .start();

  }

  public void checkPicturePermission(Context context, final Result result) {
    AndPermission.with(context)
            .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .rationale(new Rationale() {
              @Override
              public void showRationale(Context context, List<String> permissions, RequestExecutor executor) {
                executor.execute();
              }
            })
            .onGranted(new Action() {
              @Override
              public void onAction(List<String> permissions) {
                result.success(true);
              }
            })
            .onDenied(new Action() {
              @Override
              public void onAction(List<String> permissions) {
                result.success(false);
              }
            })
            .start();

  }


}

