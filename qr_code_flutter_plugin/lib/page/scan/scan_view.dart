import 'dart:async';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';


typedef void ScanViewCreatedCallback(ScanViewController controller);

class ScanView extends StatefulWidget {
  ScanView({
    Key key,
    this.onScanCreated,
    this.onScanComplete,
  }) : super(key: key);
  final ScanViewCreatedCallback onScanCreated;
  ValueChanged<String> onScanComplete;


  @override
  State<StatefulWidget> createState() => _ScanView();
}

class ScanViewController {
  ScanViewController._(int id)
      : _methodChannel = new MethodChannel('paff_scan_method_$id'),
        _eventChannel = new EventChannel('paff_scan_event_$id');

  final MethodChannel _methodChannel;
  final EventChannel _eventChannel;
  ValueChanged<String> _onScanComplete;


  StreamSubscription _stream;

  void dispose(){
    if(_stream != null){
      _stream.cancel();
    }
  }

 void  _addListener(ValueChanged<String> onRegCompleted) {
    _stream = _eventChannel.receiveBroadcastStream().listen(_onEvent);
    _onScanComplete = onRegCompleted;
 }

   _onEvent(data) {
    debugPrint('_onEvent _onEvent _onEvent 数据来了 data = ' + data.toString());
    if(_onScanComplete != null){
      _onScanComplete(data.toString());
    }
  }

  //打开闪光
  Future<void> openAutoFlash() async {
    return _methodChannel.invokeMethod("openAutoFlash");
  }

  Future<String>  addPicture() async {
    return _methodChannel.invokeMethod('addPicture');
  }

  //关闭闪光
  Future<void> closeAutoFlash() async {
    return _methodChannel.invokeMethod("closeAutoFlash");
  }

  //停止扫描
  Future<void> stopScan() async {
    return _methodChannel.invokeMethod("stopScan");
  }

  //销毁扫描
  Future<void> destroyScan() async {
    return _methodChannel.invokeMethod("destroyScan");
  }

  //打开扫描
  Future<void> restartScan() async {
    return _methodChannel.invokeMethod("restartScan");
  }
}

class _ScanView extends State<ScanView> {

  ScanViewController controller;

  @override
  Widget build(BuildContext context) {

    Map<String, dynamic> initParams =<String, dynamic>{};

    if (defaultTargetPlatform == TargetPlatform.iOS) {
      return UiKitView(
        viewType: 'scan_qr_view',
        onPlatformViewCreated: _onPlatformViewCreated,
        creationParams: initParams,
        creationParamsCodec:new StandardMessageCodec(),
      );
    }
    if (defaultTargetPlatform == TargetPlatform.android) {
      return AndroidView (
        viewType: 'scan_qr_view',
        onPlatformViewCreated: _onPlatformViewCreated,
        creationParams: initParams,
        creationParamsCodec:new StandardMessageCodec(),
      );
    }
    return Text(
        '$defaultTargetPlatform is not yet supported by the text_view plugin');
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onScanCreated == null) {
      return;
    }

    controller = new ScanViewController._(id);
    controller._addListener(widget.onScanComplete);
    widget.onScanCreated(controller);
  }
}