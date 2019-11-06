import 'package:flutter/material.dart' hide Image;
import 'dart:ui';

class ScanPainterWidget extends StatefulWidget {
  @override
  _ScanPainterWidget createState() => _ScanPainterWidget();
}

class _ScanPainterWidget extends State<ScanPainterWidget> {
  @override
  Widget build(BuildContext context) {
    return CustomPaint(
      painter: ScanPainter(),
    );
  }
}

class ScanPainter extends CustomPainter {

  Paint _paint = new Paint()
    ..color = Color(0x80000000)
    ..strokeCap = StrokeCap.round
    ..isAntiAlias = true
    ..strokeWidth = 2
    ..style = PaintingStyle.fill;
  @override
  void paint(Canvas canvas, Size size) {
    double mSize = 200;
    final top = (window.physicalSize.height/window.devicePixelRatio - mSize)/2;
    final width = mSize;
    final height = mSize;
    final left = (window.physicalSize.width/window.devicePixelRatio - mSize)/2;
    Rect rect1 = Rect.fromLTWH(0, 0, window.physicalSize.width/window.devicePixelRatio, window.physicalSize.height/window.devicePixelRatio);
    Rect rect2 = Rect.fromLTWH(left,top,width,height);
    //debugPrint("width:${window.physicalSize.width/window.devicePixelRatio}");
    //debugPrint("size:$top,$left,$width,$height");
    //分别绘制外部圆角矩形和内部的圆角矩形
    RRect outer = RRect.fromRectAndRadius(rect1, Radius.circular(0.0));
    RRect inner = RRect.fromRectAndRadius(rect2, Radius.circular(0.0));
    canvas.drawDRRect(outer, inner, _paint);
  }
  
  @override
  bool shouldRepaint(CustomPainter oldDelegate) {
    return false;
  }

}