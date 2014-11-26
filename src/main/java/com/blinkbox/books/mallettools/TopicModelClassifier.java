package com.blinkbox.books.mallettools;

import cc.mallet.types.*;
import cc.mallet.topics.*;

import java.util.*;
import java.io.*;

public class TopicModelClassifier {

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            System.err.println("Usage: TopicModelClassifier <topic model file> <epub root dir> <output file>");
            System.exit(-1);
        }

        File modelFile = new File(args[0]);
        System.out.println("Loading model from " + modelFile);
        ParallelTopicModel model = ParallelTopicModel.read(modelFile);
        System.out.println("Model loaded OK");

        TopicInferencer inferencer = model.getInferencer();

        // Read data from ePubs.
        File bookRootDir = new File(args[1]);
        Iterator<Instance> epubs = Epubs.bookIterator(bookRootDir, 100/* JUST TESTING! */);

        InstanceList instances = Common.createInstanceList();

        File outputFile = new File(args[2]);
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

                instances.addThruPipe(instance);

                double[] topicProbabilities = inferencer
                        .getSampledDistribution(instances.get(instanceCount), 1000, 1, 5);
                List<Integer> topCategories = Epubs.getTopCategories(topicProbabilities, 10, 0.009);

                System.out.println("Top categories for " + isbn + ": " + topCategories);
                writer.println(Epubs.formatTopics(isbn, topCategories));

                // // Dump probs for all the topics for this doc.
                // for (int tpIdx = 0; tpIdx < testProbabilities.length; tpIdx++) {
                // Formatter out = new Formatter(new StringBuilder(), Locale.US);
                // out.format("%d\t%.6f\t", tpIdx, testProbabilities[tpIdx]);
                // System.out.println(out);
                // }
                

                // Show the words in the first document with scores for each.
//                FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
//                LabelSequence topics = model.getData().get(0).topicSequence;
//                Formatter out = new Formatter(new StringBuilder(), Locale.US);
//                for (int position = 0; position < tokens.getLength(); position++) {
//                    out.format("%s-%d ", model.getAlphabet().lookupObject(tokens.getIndexAtPosition(position)),
//                            topics.getIndexAtPosition(position));
//                }
//                System.out.println(out);

                instanceCount++;
            }
        }

    }

}
