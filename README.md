# Wikidump
wikipedia dump handler

## step 1: Download wikipedia dump file
find and download wiki dump *meta*.xml.bz2 file from below addresses

https://dumps.wikimedia.org/

https://archive.org/

## setp 2: Clone the project
git clone https://github.com/lintep/wikidump.git

## step 3: Install the project
cd wikidump
mvn install

## step 4: Extract and do clean wiki documents from the dump file.
cd target

java -cp lintep-wikidump-1.0.jar:lib/* corpus.wikipedia.WikiDoumpHandlerMain {args[0]: dump file xml.bz2 file address}

each result file(*.newLineByLine) line is of a wikipedia raw document

## step 5: Removing useless tags and characters from the cleaned dump file.

java -cp lintep-wikidump-1.0.jar:lib/* corpus.wikipedia.spark.SparkExtractClearWikiText {args[0]: step 4 *.newLineByLine file address}
