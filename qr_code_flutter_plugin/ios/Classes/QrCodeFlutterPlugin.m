#import "QrCodeFlutterPlugin.h"
#import <qr_code_flutter_plugin/qr_code_flutter_plugin-Swift.h>

@implementation QrCodeFlutterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftQrCodeFlutterPlugin registerWithRegistrar:registrar];
}
@end
