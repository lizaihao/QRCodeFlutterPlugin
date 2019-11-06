package flutterqrcode.lizaihao.qr_code_flutter_plugin.scan;

import android.content.Context;

import java.util.Map;

import flutterqrcode.lizaihao.qr_code_flutter_plugin.view.flutter.FlutterScanView;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class ScanViewFactory extends PlatformViewFactory {

    private BinaryMessenger mMessenger;

    private Context mContext;

    public ScanViewFactory(MessageCodec<Object> createArgsCodec) {
        super(createArgsCodec);
    }

    public ScanViewFactory(MessageCodec<Object> createArgsCodec, BinaryMessenger messenger) {
        super(createArgsCodec);
        this.mMessenger = messenger;
    }

    public ScanViewFactory(MessageCodec<Object> createArgsCodec, BinaryMessenger messenger, Context context) {
        super(createArgsCodec);
        this.mMessenger = messenger;
        this.mContext = context;
    }

    @Override
    public PlatformView create(Context context, int i, Object o) {
        Map<String, Object> params = (Map<String, Object>) o;
        return new FlutterScanView(mContext, context, mMessenger, i, params);
    }
}
