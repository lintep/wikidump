package com.lintep.wikidump;

import org.apache.commons.compress.compressors.CompressorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import tools.util.Time;
import tools.util.collection.KeyValueSimple;
import tools.util.file.Bz2CompressFileIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Created by rahmani on 1/23/18.
 */
public class WikiDoumpHandlerMain {

    final static String saveAddressSuffix=".newLineByLine";

    /**
     * This method process bz2 wiki dump file and extract text value of
     * @param bz2FileAddress
     * @throws IOException
//     * @throws CompressorException
     */
    public static void handleDump(String bz2FileAddress) throws IOException, CompressorException {

        System.out.println("start to handle dump");

        String resultFileAddress=bz2FileAddress+saveAddressSuffix;

        String startBreaker = "<page>";
        String endBreaker = "</page>";

        Bz2CompressFileIterator bz2CompressFileIterator = new Bz2CompressFileIterator(bz2FileAddress, startBreaker, endBreaker);
        int docCounter=0;
        int filterDocCounter=0;
        String delim="@*@*@";
        StringBuilder stringBuilder=new StringBuilder();
        while (bz2CompressFileIterator.hasNext())
            try {
                docCounter++;
                String newXmlDocument = bz2CompressFileIterator.next();
                WikiPage wikiPage = getWikiPage(newXmlDocument);

                if(wikiPage.isFilterForNlp()) {
                    filterDocCounter++;
                    continue;
                }

                stringBuilder.setLength(0);

                stringBuilder.append(wikiPage.getTitle());
                stringBuilder.append('\t');

                for (KeyValueSimple<String, String> partitionNameValue : new WikiPagePartitioned(wikiPage).getPartitionedContent()) {
                    String clearedText="";
                    try {
                        clearedText=WikiPagePartitioned.clearText(partitionNameValue.getValue());
                    }catch (Exception e){
//                        e.printStackTrace();
                    }
                    if(clearedText.length()>0) {
                        stringBuilder.append(clearedText);
                        stringBuilder.append(delim);
                    }
                }

                tools.util.file.Write.stringToTextFile(stringBuilder.toString(),resultFileAddress,true);
                tools.util.file.Write.stringToTextFile(Time.getTimeStampAsName()+"\t"+docCounter+"/"+filterDocCounter+")\t"+wikiPage.getId()+"\t"+wikiPage.getTitle(),bz2FileAddress+".newLineByLine.log",true);

                if(docCounter%1000==0){
                    System.out.println(docCounter+" handled to now ("+filterDocCounter+")");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        bz2CompressFileIterator.close();

        System.out.println("Operation complete.");
    }

    /**
     * This method process bz2 wiki dump file and extract Page Out Links
     * @param bz2FileAddress
     * @throws IOException
//     * @throws CompressorException
     */
    public static void handleDumpToExtractGraph(String bz2FileAddress) throws IOException, CompressorException {

        System.out.println("start to handle dump");

        String resultFileAddress=bz2FileAddress+".outTitleLinks";

//        StringBuilder result=new StringBuilder();

        String startBreaker = "<page>";
        String endBreaker = "</page>";

        Bz2CompressFileIterator bz2CompressFileIterator = new Bz2CompressFileIterator(bz2FileAddress, startBreaker, endBreaker);
        int docCounter=0;
        int filterDocCounter=0;
        StringBuilder stringBuilder=new StringBuilder();
        while (bz2CompressFileIterator.hasNext())
            try {
                docCounter++;
                String newXmlDocument = bz2CompressFileIterator.next();
                WikiPage wikiPage = getWikiPage(newXmlDocument);

                if(wikiPage.isFilterForNlp()) {
                    filterDocCounter++;
                    continue;
                }


                stringBuilder.setLength(0);

                stringBuilder.append(wikiPage.getTitle());
                stringBuilder.append('\t');

                List<String> titles = wikiPage.getTitleLinks();

                if(titles==null){
                    tools.util.file.Write.stringToTextFile(wikiPage.title,resultFileAddress+".null",true);
                }

                for (String title : titles) {
                        stringBuilder.append(title);
                        stringBuilder.append('\t');
                }

                stringBuilder.setLength(stringBuilder.length()-1);

                tools.util.file.Write.stringToTextFile(stringBuilder.toString(),resultFileAddress,true);
                tools.util.file.Write.stringToTextFile(Time.getTimeStampAsName()+"\t"+docCounter+"/"+filterDocCounter+")\t"+wikiPage.getId()+"\t"+wikiPage.getTitle(),resultFileAddress+".log",true);

                if(docCounter%1000==0){
                    System.out.println(docCounter+" handled to now ("+filterDocCounter+")");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        bz2CompressFileIterator.close();

        System.out.println("Operation complete.");
    }


    public static WikiPage getWikiPage(String wikiDpumpXmlDocument) throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(wikiDpumpXmlDocument));

        Document xmlDoc = db.parse(is);
        Element pageElement = (Element) xmlDoc
                .getElementsByTagName("page").item(0);
        return DumpWikipediaProcessor.getWikiDocument(pageElement);
    }

    public static void main(String[] args) throws IOException, CompressorException {
        WikiDoumpHandlerMain.handleDump(args[0]);
    }
}
