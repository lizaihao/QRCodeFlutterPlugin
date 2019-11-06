import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:qr_code_flutter_plugin/page/scan/utils/scan_utils.dart';
import 'result_page.dart';

/**
 * 首页
 *
 */
class MainPage extends StatelessWidget {
  BuildContext context;

  List<String> _industryList = <String>[
    "flutter绘制界面 扫码",
    "native 默认扫码界面",
    "native 默认扫码界面(自定义主题)",
    "flutter 二维码生成",
    "native 条形码生成",
    "native 远程扫描界面",
    "native 生成二维码图片",
    "native 选择二维码进行解析",
  ];
  @override
  Widget build(BuildContext context) {
    this.context = context;
    return Scaffold(
        appBar: AppBar(
          backgroundColor: Theme.of(context).accentColor,
          elevation: 0,
          centerTitle: true,
          title: Text("测试扫码"),
        ),
        body: ListView.builder(
            itemCount: _industryList.length,
            itemBuilder: (context, i) {
              return _renderItem(i);
            }));
  }

  Widget _renderItem(int i) {
    return Column(children: <Widget>[
      Container(
        padding: const EdgeInsets.all(2.0),
        child: ListTile(
          title: Text(_industryList[i],
              style: TextStyle(color: Theme.of(context).accentColor)),
          onTap: (){
            _itemClick(i);
          },
        ),
      ),
      Divider(height: 1, color: Colors.black26),
    ]);
  }

  void _itemClick(int i){
    switch(i){
      case 0:
        _scan();
        break;
      case 1:
        _nativeScan1();
        break;
      case 2:
        _nativeScan2();
        break;
      case 3:
        _nativeScan3();
        break;
      case 4:
        _nativeScan4();
        break;
      case 5:
        _nativeScan5();
        break;
      case 6:
        _nativeScan6();
        break;
      case 7:
        _nativeScan7();
        break;
    }
  }
  void _scan() {
    ScanUtils.goScanQR(context, popCallback: (data) {
      if (!(data == null || data.toString().isEmpty)) {
        Navigator.push(
            context,
            CupertinoPageRoute(
                builder: (context) => ResultPage(
                      data: data,
                    )));
      }
    });
  }
  void _nativeScan1(){
    ScanUtils.goNativeScanQR1((data) {
      print("-------------");
      print(data);
      print("-------------");
      Navigator.push(
          context,
          CupertinoPageRoute(
              builder: (context) => ResultPage(
                data: data,
              )));
    });
  }
  void _nativeScan2() {
    ScanUtils.goNativeScanQR2((data) {
      print("-------------");
      print(data);
      print("-------------");
      Navigator.push(
          context,
          CupertinoPageRoute(
              builder: (context) => ResultPage(
                    data: data,
                  )));
    });
  }
  void _nativeScan3(){
    ScanUtils.goNativeScanQR3((data) {
      print("-------------");
      print(data);
      print("-------------");
      Navigator.push(
          context,
          CupertinoPageRoute(
              builder: (context) => ResultPage(
                data: data,
              )));
    });
  }
  void _nativeScan4(){
    ScanUtils.goNativeScanQR4((data) {
      print("-------------");
      print(data);
      print("-------------");
    });
  }
  void _nativeScan5(){
    ScanUtils.goNativeScanQR5((data) {
      print("-------------");
      print(data);
      print("-------------");
      Navigator.push(
          context,
          CupertinoPageRoute(
              builder: (context) => ResultPage(
                data: data,
              )));
    });
  }
  void _nativeScan6(){
    ScanUtils.goNativeScanQR6((data) {
      print("-------------");
      print(data);
      print("-------------");
    });
  }
  void _nativeScan7(){
    ScanUtils.goNativeScanQR7((data) {
      print("-------------");
      print(data);
      print("-------------");
      Navigator.push(
          context,
          CupertinoPageRoute(
              builder: (context) => ResultPage(
                data: data,
              )));
    });
  }


}
