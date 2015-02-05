package com.blinkbox.books.mallettools;

import cc.mallet.types.*;
import cc.mallet.topics.*;

import java.util.*;
import java.io.*;

/**
 * Based on a previously built topic model, classify the ePub files in the given root directory
 * by writing the top categories for each book to the output.
 *
 */
public class TopicModelClassifier {

    public static void main(String[] args) throws Exception {

        if (args.length != 4) {
            System.err
                    .println("Usage: TopicModelClassifier <topic model file> <epub root dir> <isbns file> <output file>");
            System.exit(-1);
        }

        File modelFile = new File(args[0]);
        System.out.println("Loading model from " + modelFile);
        ParallelTopicModel model = ParallelTopicModel.read(modelFile);
        System.out.println("Model loaded OK");

        TopicInferencer inferencer = model.getInferencer();

        // Read data from ePubs.
        File bookRootDir = new File(args[1]);
        File isbnsFile = new File(args[2]);
        Iterator<Instance> epubs = Epubs.bookIterator(bookRootDir, 250000/* Limit for testing */, Epubs.isbns(isbnsFile));

        InstanceList instances = Common.createInstanceList();

        File outputFile = new File(args[3]);
        System.out.println("Writing categories to " + outputFile);
        try (OutputStream output = new FileOutputStream(outputFile);
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"))) {

            int instanceCount = 0;
            while (epubs.hasNext()) {
                Instance instance = epubs.next();

                String isbn = Epubs.isbnFromFilename((String) instance.getName());
                if (isbn == null) {
                    System.out.println("Skipping file " + instance.getName());
                    continue;
                }

                // System.out.println("Book content:\n" + instance.getData());

                instances.addThruPipe(instance);

                double[] topicProbabilities = inferencer.getSampledDistribution(instances.get(instanceCount), 1000, 1,
                        5);
                List<IDSorter> topCategories = Epubs.getTopCategories(topicProbabilities, 15, 0.01);

                System.out.println("Top categories for " + isbn + ": " + Epubs.topicDetails(topCategories));
                writer.println(Epubs.formatTopics(isbn, topCategories));
                writer.flush();

                instanceCount++;
            }
        }

    }

}
