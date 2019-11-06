import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
//扫描结果页
class ResultPage extends StatefulWidget {
  final String data;

  ResultPage({
    this.data,
  });

  @override
  State<StatefulWidget> createState() {
    return _ResultPage();
  }
}

class _ResultPage extends State<ResultPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).accentColor,
        elevation:0,
        centerTitle:true,
        title:  Text("扫码结果"),
      ),
      body: _render(),);
  }

  Widget _render() {

    return Container(
        padding: EdgeInsets.all(20),
        alignment: Alignment.center,
        child: Text("扫描结果："+ widget.data));
  }
}
