import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:qr_code_flutter_plugin/qr_code_flutter_plugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('qr_code_flutter_plugin');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await QrCodeFlutterPlugin.platformVersion, '42');
  });
}
