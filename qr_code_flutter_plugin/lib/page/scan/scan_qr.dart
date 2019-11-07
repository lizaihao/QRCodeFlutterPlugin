import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:qr_code_flutter_plugin/page/scan/scan_view.dart';
import 'package:qr_code_flutter_plugin/widegets/painter_qr.dart';
import 'package:qr_code_flutter_plugin/widegets/scan_painter.dart';

import 'utils/scan_utils.dart';

//打开扫描二维码页面
class ScanQR extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return _ScanQR();
  }
}

class _ScanQR extends State<ScanQR>
    with WidgetsBindingObserver, SingleTickerProviderStateMixin {
  ScanViewController _scanViewController;
  bool _clickBool = false;
  AnimationController _animationController;
  Animation animation;
  double size = 200;
  String open = "lib/images/icon_open.png";
  String close = "lib/images/icon_close.png";
  @override
  void initState() {
    super.initState();
    _animationController =
        AnimationController(duration: const Duration(seconds: 2), vsync: this);
    _animationController.addStatusListener((status) {
      if (status == AnimationStatus.completed) {
        _animationController.reset();
        _animationController.forward();
      }
    });
    animation = Tween(begin: Offset(0, -60), end: Offset(0, 60))
        .animate(_animationController);
    _animationController.forward();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.inactive: //前后台切换都执行
        break;
      case AppLifecycleState.paused: //前台到后台
        //需处理onPaused需知执行的，即相机资源释放
        _scanViewController.closeAutoFlash();
        setState(() {
          _clickBool = !_clickBool;
        });
        if (ModalRoute.of(context).isCurrent) {
          _scanViewController.stopScan();
        }
        break;
      case AppLifecycleState.resumed: //后台到前台
      //需处理onResumed需知执行的，即相机资源重新获取
        if (ModalRoute.of(context).isCurrent) {
          _scanViewController.restartScan();
        }
        break;
      case AppLifecycleState.suspending:
        break;
    }
    super.didChangeAppLifecycleState(state);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Colors.transparent,
        body: Stack(
          children: <Widget>[
            Container(
              child: ScanView(
                onScanCreated: _onScanCreated,
                onScanComplete: (String data) {
                  _handleCaptureComplete(data);
                },
              ),
            ),
            CustomPaint(
              painter: ScanPainter(),
            ),
            Center(
              child: CustomPaint(
                //使用CustomPaint
                size: Size(size, size),
                painter: PainterQR(size, Theme.of(context).accentColor),
              ),
            ),
            Center(
              //SlideTransition 用于执行平移动画
              child: SlideTransition(
                position: animation,
                //将要执行动画的子view
                child: Image.asset("lib/images/qr_line.png",
                    package: 'qr_code_flutter_plugin', width: size - 12),
              ),
            ),
            Container(
                color: Colors.transparent,
                height: MediaQuery.of(context).size.height,
                margin: EdgeInsets.only(top: 300),
                alignment: Alignment.center,
                child: GestureDetector(
                  child:  Image.asset(_clickBool ? open : close,
                      package: 'qr_code_flutter_plugin', width: 30,height: 30,),
                  onTap: _click,
                )),
            Container(
                margin: EdgeInsets.only(top: 36, left: 18),
                height: 40,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: <Widget>[
                    IconButton(
                        icon: const Icon(Icons.arrow_back),
                        color: Colors.white,
                        onPressed: () {
                          Navigator.pop(context);
                        }),
                    Expanded(
                      flex: 1,
                      child: Container(
                        alignment: Alignment.center,
                        child:
                        Text('二维码',style: TextStyle(fontSize: 18,color: Colors.white),textAlign: TextAlign.center,),
                      )),
                    IconButton(
                        icon: const Icon(Icons.add),
                        color: Colors.white,
                        onPressed: () {
                         _addPicture();
                        }),
                  ],
                )),
          ],
        ));
  }

  void _addPicture() async{
    final bool hasPermission = await ScanUtils.checkPicturePermission;
    if(!hasPermission) {
      ScanUtils.goToSetting(context);
    }else{
      if(Platform.isAndroid) {
        _scanViewController.addPicture().then((data) {
          Navigator.pop(context, data);
        });
      }
    }
  }

  void _click() {
    _clickBool = !_clickBool;
    if (_clickBool) {
      _scanViewController.openAutoFlash();
    } else {
      _scanViewController.closeAutoFlash();
    }
    setState(() {
    });
  }

  void _onScanCreated(ScanViewController controller) {
    _scanViewController = controller;
  }

  void _handleCaptureComplete(String data) {
    Navigator.pop(context, data);
  }

  @override
  void dispose() {
    if (_clickBool) {
      _scanViewController.closeAutoFlash();
    }
    _scanViewController.destroyScan();
    _animationController.dispose();
    super.dispose();
  }
}
