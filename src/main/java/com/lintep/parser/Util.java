package com.lintep.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;

public class Util {

	static public Set<String> getTagNames(String inString) {
		HashSet<String> result = new HashSet<String>();
		Document doc = Jsoup.parse(inString, "", Parser.xmlParser());
		Elements allElements = doc.getAllElements();
		for (int i = 0; i < allElements.size(); i++) {
			result.add(allElements.get(i).tagName());
//			System.out.println(allElements.get(i).tagName());
		}
		return result;
	}

	static public String removeAllTags(String inString) {
		Document doc = Jsoup.parse(inString, "", Parser.xmlParser());
		Elements allElements = doc.getAllElements();
		for (int i = 0; i < allElements.size(); i++) {
			doc.select(allElements.get(i).tagName()).remove();
		}
//		Jsoup.clean(doc.body().html(), Whitelist.basic())
		return doc.html();
	}

	public static void printPingStat(String entryIpAddress,int pingCount){
		for (int i = 0; i < pingCount; i++) {
			try {
				String command = "ping  " + entryIpAddress;
				long startTime=System.currentTimeMillis();
				Process process = Runtime.getRuntime().exec(command);
				String pingOutString = process.getOutputStream().toString();
				long pingMilliSecond = System.currentTimeMillis() - startTime;
				System.out.println(pingMilliSecond+"ms\t"+pingOutString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
