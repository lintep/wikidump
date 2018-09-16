package com.lintep.wikidump;//package nlp.spark;

//import nlp.preprocess.fa.PersianPreprocessor;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import tools.util.Sys;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Saeed on 11/13/2016.
 */
public class SparkExtractParagraphsFromClearedWikiText {

    public static void main(String[] args) throws Exception {
        System.out.println("Start SparkExtractLines");

        if(Sys.osIsWin()) {
            System.setProperty("hadoop.home.dir", args[1]);//"c:\\hadoop\\"
        }

        SparkConf sparkConf = new SparkConf()
                .setAppName("SparkExtractLines")
                .set("spark.local.ip", "127.0.0.1").set("spark.driver.host", "127.0.0.1").setMaster("local[3]").set("spark.executor.memory", "4g")
//                .registerKryoClasses(new Class[]{PersianPreprocessor.class})
                ;

        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);

        String inputPath = args[0];//"/ssd/wiki/dewiki-20161020-pages-articles.xml.bz2_sequencefiles_splitSize_500000";//args[0];

        sparkContext.textFile(inputPath).flatMap(s -> {
            List<String> paragraphs=new ArrayList<>();
            for (String paragraph : s.split("\t\t\t")[1].split("\t\t")) {
                paragraphs.add(paragraph);
            }
            return paragraphs;
        }).saveAsTextFile(inputPath+"_paragraph");

    }

}
