package com.lintep.wikidump;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import tools.util.Str;
import tools.util.Sys;

public class SparkExtractClearWikiText {

        static final String fieldDelimiter="@*@*@";//"_@_&_@_&_@_";



    //Main function
    /*
     * This method extract clear Wiki document as line in below format
     * [line] -> [WikiTitle][\t\t\t][Field1Text][\t\t][Field2Text][\t\t] ... [FieldKText]
     * [Field1Text] -> [Paragraph1][fieldDelimiter][Paragraph1][fieldDelimiter] ... [Paragraph1]
     * */
    public static void main(String args[]) throws Exception {
        if(Sys.osIsWin()) {
            System.setProperty("hadoop.home.dir", args[1]);//"c:\\hadoop\\"
        }

        SparkConf sparkConf = new SparkConf()
                .setAppName("quote extractor")
                .set("spark.local.ip", "127.0.0.1")
                .set("spark.driver.host", "127.0.0.1")
                .setMaster("local[3]")
                .set("spark.executor.memory", "4g")
                ;

        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        String resultPath = args[0] + "_clearedText";

        JavaRDD<String> rawWiki = sparkContext.textFile(args[0]);


        rawWiki.map(wikiText -> {
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder parStringBuilder = new StringBuilder();
            if(wikiText.split("\t").length<2){
                return null;
            }
            for (String field : wikiText.split("\t")[1].toString().split(fieldDelimiter)) {
                parStringBuilder.setLength(0);
                try {
                    String text = field;
                    text = Str.removeStartEndLabel(text, "[[", "]]");
                    text = Str.removeStartEndLabel(text, "[", "]");
                    text = Str.removeStartEndLabel(text, "(", ")");
                    text = text.replaceAll("\\|", " ");
                    for (String split : text.split("\\* ")) {
                        String paragraph = split.replaceAll("  ", " ").trim();
                        if (paragraph.indexOf("http://") < 0 && //remove sentence with http link
                                paragraph.indexOf("#") != 0 &&
                                paragraph.length() > 2 &&
                                paragraph.split(" ").length > 1)//remove sentence with onr token
                        {
                            parStringBuilder.append(paragraph + "\t\t");
                        }

                    }
                } catch (Exception e) {
//                    System.out.println(e.getMessage());
                }
                if (parStringBuilder.length() > 0) {
                    parStringBuilder.setLength(parStringBuilder.length() - 2);
                    stringBuilder.append(parStringBuilder.toString());
                    stringBuilder.append(fieldDelimiter);
                }
            }
            return stringBuilder.length()>0?wikiText.split("\t")[0]+"\t\t\t"+stringBuilder.toString():null;
        }).filter(s -> s!=null).saveAsTextFile(resultPath);
        System.out.println("Operation complete.");
    }

}