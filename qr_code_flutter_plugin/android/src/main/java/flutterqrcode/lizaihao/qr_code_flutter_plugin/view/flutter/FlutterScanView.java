package flutterqrcode.lizaihao.qr_code_flutter_plugin.view.flutter;

import android.content.Context;
import android.view.View;

import java.util.Map;

import flutterqrcode.lizaihao.qr_code_flutter_plugin.utils.EventChannelUtils;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class FlutterScanView implements PlatformView {

    private Context mContext;

    private Context mFactoryContext;

    private ScanView mScanView;


    private MethodChannel mMethodChannel;
    private EventChannel mEventChannel;
    private EventChannel.EventSink mEventSink;
    EventChannelUtils eventChannelUtils;

    public FlutterScanView(Context context, Context factoryContext, final BinaryMessenger messenger, int id, Map<String, Object> params) {
        mContext = context;
        mFactoryContext = factoryContext;

        mScanView = new ScanView(mContext, params, new ScanView.FlutterScanViewListener() {
            @Override
            public void scanViewHandleResult(String result) {
                if(eventChannelUtils.getEventSink() != null) {
                    eventChannelUtils.getEventSink().success(result);
                }
            }
        });
        mMethodChannel = new MethodChannel(messenger, "scan_method_"+id);
        mMethodChannel.setMethodCallHandler(new InputMethodCallHandler());

        eventChannelUtils = EventChannelUtils.getInstance();
        eventChannelUtils.setmEventChannel(new EventChannel(messenger, "scan_event_"+id));
    }


    class InputMethodCallHandler implements MethodChannel.MethodCallHandler {
        @Override
        public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
            switch (methodCall.method) {
                case "openAutoFlash":
                    mScanView.openAutoFlash();
                    break;
                case "closeAutoFlash":
                    mScanView.closeAutoFlash();
                    break;
                case "stopScan":
                    mScanView.stopScan();
                    break;
                case "restartScan":
                    mScanView.restartScan();
                    break;
                case "destroyScan":
                    mScanView.onDestroyView();
                    break;
                case "addPicture":
                    eventChannelUtils.setResultChannel(result);
                    mScanView.addPicture();
                    break;
                default:
                    result.notImplemented();
            }
        }
    }



    @Override
    public View getView() {
        return mScanView;
    }

    @Override
    public void dispose() {
        if(mScanView != null) {
        }
    }

}
