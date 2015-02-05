package com.blinkbox.books.mallettools;

import cc.mallet.types.*;

import java.util.*;
import java.io.*;

/**
 * Simple command line tool for converting text content of ePub files to CSV files.
 */
public class EpubToCsvConverter {

    public static void main(String[] args) throws Exception {

        if (args.length != 4) {
            System.err.println("Usage: EpubToCsvConverter <epub root dir> <ISBN list file> <output file> <number of files to read>");
            System.exit(-1);
        }

        // Read data from ePubs.
        File bookRootDir = new File(args[0]);
        File isbns = new File(args[1]);
        File outputFile = new File(args[2]);
        int numBooks = Integer.parseInt(args[3]);

        Iterator<Instance> epubs = Epubs.bookIterator(bookRootDir, numBooks, Epubs.isbns(isbns));

        try (OutputStream output = new FileOutputStream(outputFile);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"))) {

            while (epubs.hasNext()) {
                Instance instance = epubs.next();

                String isbn = Epubs.isbnFromFilename((String) instance.getName());
                if (isbn == null) {
                    System.out.println("Skipping file " + instance.getName());
                    continue;
                }
            }

        }

    }

}
