import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:qr_code_flutter_plugin/page/scan/scan_qr.dart';
import 'package:qr_code_flutter_plugin/qr_code_flutter_plugin.dart';

import 'main_page.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await QrCodeFlutterPlugin.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }
  Color accentColor = Color(0xffFF822A);
  Color disabledAccentColor = Color(0x80FF822A);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter QRCode Demo',
      theme: ThemeData(
        accentColor:accentColor,
        buttonColor: accentColor,
        indicatorColor: accentColor,
        buttonTheme: ButtonThemeData(//按钮
            textTheme: ButtonTextTheme.primary,
            splashColor:accentColor,
            buttonColor:accentColor,
            highlightColor: accentColor,
            height: 46,
            disabledColor: disabledAccentColor,
            colorScheme: ColorScheme.light(primary:accentColor)
        ),
      ),
      home: MainPage(),
    );
  }
}
