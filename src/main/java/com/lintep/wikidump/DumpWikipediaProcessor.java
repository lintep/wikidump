package com.lintep.wikidump;

import com.lintep.parser.Util;
import org.w3c.dom.Element;
import tools.util.Str;
import tools.util.collection.iterator.JsonIterableByLine;
import tools.util.collection.iterator.JsonIterableByLine.JsonIterator;
import tools.util.collection.iterator.JsonUpdatable;

import java.io.IOException;
import java.util.HashSet;

public class DumpWikipediaProcessor {

	private String xmlFileAddress;

	public DumpWikipediaProcessor(String xmlFileAddress) {
		this.xmlFileAddress = xmlFileAddress;
	}


	public static WikiPage getWikiDocument(Element xml_doc) throws Exception {
		String id = xml_doc.getElementsByTagName("id").item(0).getChildNodes()
				.item(0).getNodeValue();
		String title = xml_doc.getElementsByTagName("title").item(0)
				.getChildNodes().item(0).getNodeValue();
		WikiPage wikiPage = new WikiPage(id, title);

		try {
			String text = xml_doc.getElementsByTagName("text").item(0)
					.getChildNodes().item(0).getNodeValue();
			wikiPage.setText(text);
		} catch (NullPointerException e) {
		}

		try {
			String ns = xml_doc.getElementsByTagName("ns").item(0)
					.getChildNodes().item(0).getNodeValue();
			wikiPage.setNs(ns);
		} catch (NullPointerException e) {
		}

		try {
			String parentId = xml_doc.getElementsByTagName("parentid").item(0)
					.getChildNodes().item(0).getNodeValue();
			wikiPage.setParentId(parentId);
		} catch (NullPointerException e) {
		}

		try {
			String timestamp = xml_doc.getElementsByTagName("timestamp")
					.item(0).getChildNodes().item(0).getNodeValue();
			wikiPage.setTimestamp(timestamp);
		} catch (NullPointerException e) {
		}

		try {
			String revisionComment = xml_doc.getElementsByTagName("comment")
					.item(0).getChildNodes().item(0).getNodeValue();
			wikiPage.setComment(revisionComment);
		} catch (NullPointerException e) {
		}

		try {
			Element revisionElement = (Element) xml_doc.getElementsByTagName(
					"revision").item(0);

			try {
				String revisionText = revisionElement
						.getElementsByTagName("text").item(0).getChildNodes()
						.item(0).getNodeValue();
				wikiPage.setRevisionId(revisionText);
			} catch (NullPointerException e) {
			}

			try {
				String revisionId = revisionElement.getElementsByTagName("id")
						.item(0).getChildNodes().item(0).getNodeValue();
				wikiPage.setRevisionId(revisionId);
			} catch (NullPointerException e) {
			}

			try {
				String revisionParentId = revisionElement
						.getElementsByTagName("parentid").item(0)
						.getChildNodes().item(0).getNodeValue();
				wikiPage.setRevisionParentId(revisionParentId);
			} catch (NullPointerException e) {
			}

			try {
				String revisionTimestamp = revisionElement
						.getElementsByTagName("timestamp").item(0)
						.getChildNodes().item(0).getNodeValue();
				wikiPage.setRevisionTimestamp(revisionTimestamp);
			} catch (NullPointerException e) {
			}

			try {
				String revisionComment = revisionElement
						.getElementsByTagName("comment").item(0)
						.getChildNodes().item(0).getNodeValue();
				wikiPage.setRevisionComment(revisionComment);
			} catch (NullPointerException e) {
			}

		} catch (NullPointerException e) {
		}

		return wikiPage;
	}

	static StringBuilder stringBuilder = new StringBuilder(10000);

	public static String clearText(String text) throws Exception {
		stringBuilder.setLength(0);
		stringBuilder.append(text);
		return clearText(stringBuilder);
	}

	public static String clearText(StringBuilder text) throws Exception {
		String tempString;
		int findIndex = -1;
		findIndex = text.indexOf("==پانویس");
		if (findIndex < 0)
			findIndex = text.indexOf("== پانویس");
		if (findIndex > 0)
			text.setLength(findIndex);
		findIndex = text.indexOf("==جستارهای وابسته");
		if (findIndex < 0)
			findIndex = text.indexOf("== جستارهای وابسته");
		if (findIndex > 0)
			text.setLength(findIndex);
		findIndex = text.indexOf("==منابع");
		if (findIndex < 0)
			findIndex = text.indexOf("== منابع");
		if (findIndex > 0)
			text.setLength(findIndex);

		tempString = Util.removeAllTags(text.toString());
		text.setLength(0);
		text.append(tempString);

		tempStringBuilder = Str.removeBetweenStartEndLabel(text, "{|||", "|||}");
		text.setLength(0);
		text.append(tempStringBuilder);

		tempStringBuilder = Str.removeBetweenStartEndLabel(text, "{|", "|}");
		text.setLength(0);
		text.append(tempStringBuilder);

		tempStringBuilder = Str.removeBetweenStartEndLabel(text, "<!--", "-->");
		text.setLength(0);
		text.append(tempStringBuilder);

		tempStringBuilder = handleDoubleAqualtNaghloGhol(text);
		text.setLength(0);
		text.append(tempStringBuilder);
		tempStringBuilder = handleDoubleBracket(text);
		text.setLength(0);
		text.append(tempStringBuilder);
		tempStringBuilder = handleSingleBracket(text);
		text.setLength(0);
		text.append(tempStringBuilder);
		tempStringBuilder = handleDoubleAqualt(text);
		text.setLength(0);
		text.append(tempStringBuilder);
		tempString = Str.replaceAll(text.toString(), "====", "\n");
		tempString = Str.replaceAll(tempString, "===", "\n");
		tempString = Str.replaceAll(tempString, "==", "\n");
		tempString = Str.replaceAll(tempString, "#", "\n");
		tempString = Str.replaceAll(tempString, "&quot;", "\"");
		tempString = Str.replaceAll(tempString, "'''", "\n");
		tempString = Str.replaceAll(tempString, "&raquo;", "»");
		tempString = Str.replaceAll(tempString, "&laquo;", "«");
		tempString = Str.replaceAll(tempString, "''", " ");
		text.setLength(0);
		text.append(tempString);
		return text.toString();
	}

	static StringBuilder tempStringBuilder = new StringBuilder();

	public static StringBuilder handleDoubleBracket(StringBuilder text) {
		String startStr = "[[";
		String endStr = "]]";
		tempStringBuilder.setLength(0);
		boolean tagStarted = false;
		int fromIndex = -1;
		int currentIndex = 0;
		int startIndex = 0;
		int prevStartIndex = 0;
		String betweenString = "";
		while (startIndex < text.length()) {
			if (!tagStarted) {
				startIndex = text.indexOf(startStr, currentIndex);
				if (startIndex < 0) {
					tempStringBuilder.append(text.substring(currentIndex));
					break;
				} else {
					tempStringBuilder.append(text.substring(currentIndex,
							startIndex));
					tagStarted = true;
				}
				currentIndex = startIndex + startStr.length();
			} else {
				prevStartIndex = startIndex;
				startIndex = text.indexOf(endStr, startIndex);
				if (startIndex < 0) {
					System.out.println("can not find " + endStr);// throw new
																	// Exception("can not find "
																	// +
																	// endStr);
					tempStringBuilder.append(text.substring(prevStartIndex));
					break;
				} else {
					tagStarted = false;
					betweenString = text.substring(currentIndex, startIndex);
					fromIndex = betweenString.indexOf('|');
					int endDelimIndex = 0;
					while (fromIndex >= 0) {
						endDelimIndex = fromIndex + 1;
						fromIndex = betweenString.indexOf('|', endDelimIndex);
					}
					tempStringBuilder.append(betweenString
							.substring(endDelimIndex));
					tempStringBuilder.append(' ');
					currentIndex = startIndex + endStr.length();
				}
			}
		}
		return tempStringBuilder;
	}

	public static StringBuilder handleSingleBracket(StringBuilder text) {
		String startStr = "[";
		String endStr = "]";
		tempStringBuilder.setLength(0);
		boolean tagStarted = false;
		int currentIndex = 0;
		int startIndex = 0;
		int prevStartIndex = 0;
		String betweenString = "";
		while (startIndex < text.length()) {
			if (!tagStarted) {
				startIndex = text.indexOf(startStr, currentIndex);
				if (startIndex < 0) {
					tempStringBuilder.append(text.substring(currentIndex));
					break;
				} else {
					tempStringBuilder.append(text.substring(currentIndex,
							startIndex));
					tagStarted = true;
				}
				currentIndex = startIndex + startStr.length();
			} else {
				prevStartIndex = startIndex;
				startIndex = text.indexOf(endStr, startIndex);
				if (startIndex < 0) {
					System.out.println("can not find " + endStr);// throw new
																	// Exception("can not find "
																	// +
																	// endStr);
					tempStringBuilder.append(text.substring(prevStartIndex));
					break;
				} else {
					tagStarted = false;
					betweenString = text.substring(currentIndex, startIndex);
					int endDelimIndex = 0;
					if (betweenString.indexOf("http") == 0)
						endDelimIndex = betweenString.indexOf(' ');
					if (endDelimIndex > 0)
						tempStringBuilder.append(betweenString
								.substring(endDelimIndex));
					tempStringBuilder.append(' ');
					currentIndex = startIndex + endStr.length();
				}
			}
		}
		return tempStringBuilder;
	}

	public static StringBuilder handleDoubleAqualtNaghloGhol(StringBuilder text) {
		// in -> abc {{ *** {{ *** }} *** {{ *** }} *** }} def {{ *** }} gh
		// out -> abc def gh
		String startStr = "{{نقل قول|''";
		String middleStr = "''||";
		String endStr = "]]|}}";

		tempStringBuilder.setLength(0);
		boolean tagStarted = false;
		int forIndex = -1;
		int currentIndex = 0;
		int startIndex = 0;
		int prevStartIndex = 0;
		String betweenString = "";
		while (startIndex < text.length()) {
			if (!tagStarted) {
				startIndex = text.indexOf(startStr, currentIndex);
				if (startIndex < 0) {
					tempStringBuilder.append(text.substring(currentIndex));
					break;
				} else {
					tempStringBuilder.append(text.substring(currentIndex,
							startIndex));
					tagStarted = true;
				}
				currentIndex = startIndex + startStr.length();
			} else {
				prevStartIndex = startIndex;
				startIndex = text.indexOf(endStr, startIndex);
				if (startIndex < 0) {
					System.out.println("can not find " + endStr);// throw new
																	// Exception("can not find "
																	// +
																	// endStr);
					tempStringBuilder.append(text.substring(prevStartIndex));
					break;
				} else {
					tagStarted = false;
					betweenString = text.substring(currentIndex, startIndex);
					forIndex = betweenString.indexOf(middleStr);
					tempStringBuilder.append(betweenString.substring(0,
							forIndex));
					tempStringBuilder.append(' ');
					currentIndex = startIndex + endStr.length();
				}
			}
		}
		return tempStringBuilder;
	}

	public static StringBuilder handleDoubleAqualt(StringBuilder text) {
		// in -> abc {{ *** {{ *** }} *** {{ *** }} *** }} def {{ *** }} gh
		// out -> abc def gh
		String startStr = "{{";
		int startStrCount = 0;
		String endStr = "}}";
		// int endStrCount = 0;
		tempStringBuilder.setLength(0);
		// boolean tagStarted = false;
		// int fromIndex = -1;
		int currentIndex = 0;
		int startIndex = text.indexOf(startStr, currentIndex);
		if (startIndex < 0) {
			tempStringBuilder.append(text);
			return tempStringBuilder;
		} else {
			tempStringBuilder.append(text.substring(currentIndex, startIndex));
			currentIndex = startIndex + startStr.length();
			startStrCount++;
		}
		int endIndex = 0;
		// String betweenString = "";
		boolean startCountReset = false;
		while (startIndex < text.length()) {
			if (startCountReset) {
				startIndex = text.indexOf(startStr, currentIndex);// not }}
																	// found
				if (startIndex < 0) {
					tempStringBuilder.append(text.substring(currentIndex));
					return tempStringBuilder;
				} else {
					tempStringBuilder.append(text.substring(currentIndex,
							startIndex));
					currentIndex = startIndex + startStr.length();
					startStrCount++;
				}
				startCountReset = false;
				continue;
			}
			endIndex = text.indexOf(endStr, currentIndex);
			startIndex = text.indexOf(startStr, currentIndex);
			if (endIndex < 0 && startStrCount > 0) {
				System.out.println("not found " + endStr);// w new
															// Exception("not found "
															// + endStr);
				startStrCount = 0;
			}

			if (endIndex == startIndex) {// not found
				tempStringBuilder.append(text.substring(currentIndex));
				break;
			} else {
				if ((endIndex < startIndex && endIndex != -1)
						|| startIndex == -1) {
					if (startStrCount == 0)
						System.out.println(endStr + " pair not found");// throw
																		// new
																		// Exception(endStr
																		// +
																		// " pair not found");
					else
						startStrCount--;
					currentIndex = endIndex + endStr.length();
					if (startStrCount == 0) {
						startCountReset = true;
						continue;
					}
				} else {
					startStrCount++;
					currentIndex = startIndex + startStr.length();
				}
			}
		}
		return tempStringBuilder;
	}

public static void getJsonFileTextFieldSentence(
			String wikiPageJsonFileAddress, String resultFileAddress,
			HashSet<Character> delimiters) throws IOException {
		JsonIterableByLine<WikiPage> jsonIterableByLine = new JsonIterableByLine<WikiPage>(
				wikiPageJsonFileAddress, new WikiPage("", ""));
		JsonIterator iterator = (JsonIterator) jsonIterableByLine.iterator();
		String wikiPageText;
		int counter = 0;
		int sentenceCounter = 0;
		while (iterator.hasNext()) {
			counter++;
			JsonUpdatable next = iterator.next();
			WikiPage nextWikiPage = (WikiPage) next;
			wikiPageText = nextWikiPage.getText();
			sentenceCounter += Str.writeSentencesToFile(wikiPageText,
					delimiters, resultFileAddress);
			if (counter % 10000 == 0)
				System.out.println(counter + " WikiPage handled  with "
				+ sentenceCounter + " sentence .");
		}
		jsonIterableByLine.close();
		System.out.println(counter + " WikiPage handled with "
				+ sentenceCounter + " sentence .");
		System.out.println("operation complete.");
	}


	public static String removeOneLevelTag(String tag,String inputString) throws IOException {
		Integer counter=0;
		return removeOneLevelTag(tag, inputString,counter);
	}

    public static String removeOneLevelTag(String tag,String inputString,Integer counter) throws IOException {
		counter++;
		if(counter>1000){
			throw new IOException("Mor than 1000 loop needed to remove "+tag+" tag.");
		}
        int refIndex=inputString.indexOf("<"+tag);
        if(refIndex>=0){
            int endSingleRefIndex=inputString.indexOf("/>",refIndex);
            int endStartRefIndex=inputString.indexOf(">",refIndex);

            if(endSingleRefIndex<0 && endStartRefIndex<0){
                throw new IOException("Not closed "+ tag +" TAG");
            }else{

                if(endSingleRefIndex>=0 && endSingleRefIndex<endStartRefIndex){//single ref correct
                    return inputString.substring(0,refIndex) + removeOneLevelTag(tag,inputString.substring(endSingleRefIndex + 2),counter);
                }
                else{

                    int newRefIndex=inputString.indexOf("<"+tag,refIndex+1+tag.length());
                    int endRefIndex=inputString.indexOf("</"+tag+">",refIndex+1+tag.length());

                    if(newRefIndex>=0 && newRefIndex<endRefIndex){
                        throw new IOException("Inner "+tag+" TAG");
                    }
                    else{
                        return inputString.substring(0,refIndex) + removeOneLevelTag(tag,inputString.substring(endRefIndex + 3+tag.length()),counter);
                    }
                }

            }

        }
        else{
            return inputString;
        }

    }
}