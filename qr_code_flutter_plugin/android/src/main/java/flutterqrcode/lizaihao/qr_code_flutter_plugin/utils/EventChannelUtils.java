package flutterqrcode.lizaihao.qr_code_flutter_plugin.utils;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

public class EventChannelUtils {
    private static EventChannelUtils mSingleton = null;
    private EventChannelUtils () {}

    private EventChannel mEventChannel;
    private EventChannel.EventSink mEventSink;
    private  MethodChannel.Result result;

    public static EventChannelUtils getInstance() {
        if (mSingleton == null) {
            synchronized (EventChannelUtils.class) {
                if (mSingleton == null) {
                    mSingleton = new EventChannelUtils();
                }
            }
        }
        return mSingleton;
    }

   public void setmEventChannel(EventChannel eventChannel){
       eventChannel.setStreamHandler(new InputStreamHandler());
   }

   public EventChannel.EventSink getEventSink(){
        return mEventSink;
    }

    public void setResultChannel( MethodChannel.Result result){
        this.result = result;
    }

    public MethodChannel.Result getResultChannel(){
        return result;
    }

    class InputStreamHandler implements EventChannel.StreamHandler {
        @Override
        public void onListen(Object o, EventChannel.EventSink eventSink) {
            mEventSink = eventSink;
        }

        @Override
        public void onCancel(Object o) {

        }
    }

}
