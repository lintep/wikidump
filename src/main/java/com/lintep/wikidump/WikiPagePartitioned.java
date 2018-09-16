package com.lintep.wikidump;

import tools.util.Str;
import tools.util.collection.KeyValueSimple;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class WikiPagePartitioned {

    WikiPage wikiPage;
    ArrayList<KeyValueSimple<String, String>> partitionedContent;

    public WikiPagePartitioned(WikiPage wikiPage) {
        this.wikiPage = wikiPage;
        this.partitionedContent = new ArrayList<KeyValueSimple<String, String>>();
        setPartitionedContent();
    }

    public ArrayList<KeyValueSimple<String, String>> getPartitionedContent() {
        return partitionedContent;
    }

    private void setPartitionedContent() {
        String[] splitsII = this.wikiPage.getText().split("===");
        for (int i = 0; i < splitsII.length; i += 2) {
            String[] splitsI = splitsII[i].split("==");
            if (splitsI.length > 1) {
                for (int j = 0; j < splitsI.length; j += 2) {
                    if (i == 0) {
                        if (j == 0) {
                            this.partitionedContent.add(new KeyValueSimple<String, String>("firstPart", splitsI[0].trim()));
                        } else {
                            this.partitionedContent.add(new KeyValueSimple<String, String>(splitsI[j - 1].trim(), splitsI[j].trim()));
                        }
                    } else {
                        if (j == 0) {
                            this.partitionedContent.add(new KeyValueSimple<String, String>(splitsII[i - 1].trim(), splitsI[0].trim()));
                        } else {
                            this.partitionedContent.add(new KeyValueSimple<String, String>(splitsI[j - 1].trim(), splitsI[j].trim()));
                        }
                    }
                }
            } else {
                if (i == 0) {
                    this.partitionedContent.add(new KeyValueSimple<String, String>("firstPart", splitsII[0].trim()));
                } else {
                    this.partitionedContent.add(new KeyValueSimple<String, String>(splitsII[i - 1].trim(), splitsII[i].trim()));
                }
            }
        }

    }

    public void writeToFileTitleContent(String fileAddress) throws Exception {
        tools.util.file.Write.stringToTextFile(this.wikiPage.getId() + "\n" + this.wikiPage.getTitle(), fileAddress, true);
        for (KeyValueSimple<String, String> partition : this.partitionedContent) {
            tools.util.file.Write.stringToTextFile("\n\n" + partition.getKey() + "\n" + partition.getValue() + "\n" + clearText(partition.getValue()), fileAddress, true);
        }
    }

    public static String clearText(String inputText) throws Exception {
        StringBuilder handledTags = handleTagsToRemove(inputText);

        StringBuilder removedDoubleAqualad = Str.returnRemovedStartEndLabelRecursive(handledTags, "{{", "}}");
        StringBuilder removedComments = Str.removeBetweenStartEndLabel(removedDoubleAqualad, "<!--", "-->");
        StringBuilder removedTables = Str.removeBetweenStartEndLabel(removedComments, "{|", "|}");

        StringBuilder removedStartEnd = handleStartEnd(removedTables);


        return removedStartEnd.toString().trim();
    }

    private static StringBuilder handleStartEnd(StringBuilder stringBuilder) throws Exception {
        LinkedBlockingQueue<KeyValueSimple<String,String>> removeTagQueue=new LinkedBlockingQueue<KeyValueSimple<String, String>>();
        removeTagQueue.add(new KeyValueSimple<String, String>("'''","'''"));
        removeTagQueue.add(new KeyValueSimple<String, String>("''","''"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<blockquote>","</blockquote>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<strong>","</strong>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<b>","</b>"));// informative
        removeTagQueue.add(new KeyValueSimple<String, String>("<center>","</center>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<pre","</pre>"));// informative

        return removeStartEndLabel(stringBuilder, removeTagQueue);
    }
    private static StringBuilder removeStartEndLabel(StringBuilder stringBuilder,LinkedBlockingQueue<KeyValueSimple<String,String>> removeTagQueue) throws Exception {
        if(removeTagQueue.isEmpty()){
            return stringBuilder;
        }
        KeyValueSimple<String, String> tag = removeTagQueue.remove();
        String startTag = tag.getKey();
        String endTag = tag.getValue();
        return removeStartEndLabel(Str.removeStartEndLabel(stringBuilder, startTag, endTag), removeTagQueue);
    }

    private static StringBuilder handleTagsToRemove(String inputText) throws Exception {

        //remove ref
        StringBuilder removedRefTags= new StringBuilder(DumpWikipediaProcessor.removeOneLevelTag("ref", inputText));

        LinkedBlockingQueue<KeyValueSimple<String,String>> removeTagQueue=new LinkedBlockingQueue<KeyValueSimple<String, String>>();
        removeTagQueue.add(new KeyValueSimple<String, String>("<div", "</div>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<noWiki","</noWiki>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<sub","</sub>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<gallery","</gallery>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<math","</math>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<source","</source>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<table","</table>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<span","</span>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<noinclude","</noinclude>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<sup","</sup>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<font","</font>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<code","</code>"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<includeonly","</includeonly)"));
        removeTagQueue.add(new KeyValueSimple<String, String>("<small","</small>"));// informative

        return removeAllBetweenSWtartEndLabel(removedRefTags,removeTagQueue);
    }

    private static StringBuilder removeAllBetweenSWtartEndLabel(StringBuilder stringBuilder,LinkedBlockingQueue<KeyValueSimple<String, String>> removeTagQueue) throws Exception {
        if(removeTagQueue.isEmpty()){
            return stringBuilder;
        }
        KeyValueSimple<String, String> tag = removeTagQueue.remove();
        String startTag = tag.getKey();
        String endTag = tag.getValue();
        return removeAllBetweenSWtartEndLabel(Str.removeBetweenStartEndLabel(stringBuilder, startTag, endTag),removeTagQueue);
    }
}
