import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

class PainterQR extends CustomPainter {
  final double size;
  final Color color;
  PainterQR(this.size,this.color);


  @override
  void paint(Canvas canvas, Size size) {

    //画棋盘背景
    var paint = Paint()
      ..isAntiAlias = true
      ..style = PaintingStyle.fill //填充
      ..color = Colors.transparent; //背景为纸黄色
    canvas.drawRect(Offset.zero & size, paint);

    double minX = 0;
    double minY = 0;
    double maxY = this.size;
    double maxX = this.size;
    double strokeWidth = 3.0;
    double mSize = 15;

    canvas.drawLine(
        Offset(minX, minY), Offset(minX + mSize, minY),
        Paint()..strokeWidth = strokeWidth..strokeCap = StrokeCap.round..color = color);
    canvas.drawLine(
        Offset(minX, minY), Offset(minX, minY + mSize),
        Paint()..strokeWidth = strokeWidth..strokeCap = StrokeCap.round..color = color);

    canvas.drawLine(
        Offset(maxX, minY), Offset(maxX - mSize, minY),
        Paint()..strokeWidth = strokeWidth..strokeCap = StrokeCap.round..color = color);
    canvas.drawLine(
        Offset(maxX, minY), Offset(maxX, minY + mSize),
        Paint()..strokeWidth = strokeWidth..strokeCap = StrokeCap.round..color = color);

    canvas.drawLine(
        Offset(maxX, maxY), Offset(maxX - mSize, maxY),
        Paint()..strokeWidth = strokeWidth..strokeCap = StrokeCap.round..color = color);
    canvas.drawLine(
        Offset(maxX, maxY), Offset(maxX, maxY - mSize),
        Paint()..strokeWidth = strokeWidth..strokeCap = StrokeCap.round..color = color);

    canvas.drawLine(
        Offset(minX, maxY), Offset(minX + mSize, maxY),
        Paint()..strokeWidth = strokeWidth..strokeCap = StrokeCap.round..color = color);
    canvas.drawLine(
        Offset(minX, maxY), Offset(minX, maxY - mSize),
        Paint()..strokeWidth = strokeWidth..strokeCap = StrokeCap.round..color = color);
  }

  @override
  bool shouldRepaint(CustomPainter oldDelegate) => false;
}