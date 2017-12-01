package com.mj.gpsclient.global;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Xml;

public abstract class XMLHelper {

	public static void getResult(String node,String xmlStr,CallBack callBack){
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new StringReader(xmlStr));
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (node.equals(parser.getName())) {
						try {
							callBack.getResult(parser.nextText());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				default:
					break;
				}
				try {
					type = parser.next();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}
	public static ArrayList<String> getListResult(String node,String xmlStr){
		ArrayList<String> list = new ArrayList<String>();
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(new StringReader(xmlStr));
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (node.equals(parser.getName())) {
						try {
							list.add(parser.nextText());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				default:
					break;
				}
				try {
					type = parser.next();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public abstract interface CallBack{
		public void getResult(String result);;
	}
	
}
