import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/cupertino.dart';

import '../scan_qr.dart';

class ScanUtils {
  static const MethodChannel _channel = const MethodChannel('qr_code_flutter_plugin');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> get checkPermission async {
    final bool hasPermission = await _channel.invokeMethod('checkPermission');
    return hasPermission;
  }

  static void goSystemSetting() async {
    print("goSystemSetting");
    await _channel.invokeMethod<void>('goSystemSetting');
  }

  static Future<bool> get checkPicturePermission async {
    final bool hasPermission = await _channel.invokeMethod('checkPicturePermission');
    return hasPermission;
  }

  static void goScanQR(BuildContext context, {dynamic popCallback}) async {
    bool hasPermission = await checkPermission;
    //无相机权限将无法进入ocr扫描页面，会弹出框提示用户去设置权限
    if (hasPermission) {
      Navigator.push(context, CupertinoPageRoute(
          builder: (context) => ScanQR())).then((data){
        if(popCallback != null)
          popCallback(data);
      });
    } else {
      goToSetting(context);
    }
  }

  static void goNativeScanQR1(Function callback)async{
    _channel.invokeMethod<void>('gotoNativeScan1').then((data){
      callback(data);
    });
  }

  static void goNativeScanQR2(Function callback)async{
     _channel.invokeMethod<void>('gotoNativeScan2').then((data){
       callback(data);
     });
  }

  static void goNativeScanQR3(Function callback)async{
    _channel.invokeMethod<void>('gotoNativeScan3').then((data){
      callback(data);
    });
  }

  static void goNativeScanQR4(Function callback)async{
    _channel.invokeMethod<void>('gotoNativeScan4').then((data){
      callback(data);
    });
  }

  static void goNativeScanQR5(Function callback)async{
    _channel.invokeMethod<void>('gotoNativeScan5').then((data){
      callback(data);
    });
  }

  static void goNativeScanQR6(Function callback)async{
    _channel.invokeMethod<void>('gotoNativeScan6').then((data){
      callback(data);
    });
  }
  static void goNativeScanQR7(Function callback)async{
    _channel.invokeMethod<void>('gotoNativeScan7').then((data){
      callback(data);
    });
  }

  static void goToSetting(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (builder) {
        return GestureDetector(
          child: new Container(
              height: 200,
              padding: EdgeInsets.all(10),
              child: Column(
                  mainAxisSize: MainAxisSize.max,
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: <Widget>[
                    new Container(
                      width: double.infinity,
                      child: Center(
                          child: Text(
                            "设置权限",
                            textAlign: TextAlign.center,
                            style:
                            TextStyle(color: Color(0xCC000000), fontSize: 20),
                          )),
                    ),
                    Container(
                      margin: EdgeInsets.all(10),
                      width: 200,
                      height: 40,
                      child: new RaisedButton(
                        onPressed: goSystemSetting,
                        child: Text(
                          '去设置',
                        ),
                      ),
                    ),
                    Container(
                      margin: EdgeInsets.all(10),
                      width: 200,
                      height: 40,
                      child: new RaisedButton(
                        onPressed: () {
                          Navigator.pop(context);
                        },
                        child: Text(
                          '取消',
                        ),
                      ),
                    ),
                  ])),
          onTap: () => false,
        );
      },
    );
  }
}
