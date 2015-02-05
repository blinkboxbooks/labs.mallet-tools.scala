package com.blinkbox.books.mallettools;

import cc.mallet.types.*;
import cc.mallet.topics.*;

import java.util.*;
import java.io.*;

public class BookTopicModel {

    private static final int NUM_WORDS_PER_TOPIC = 15;

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: BookTopicModel <epub root dir> <known ISBN list>");
            System.exit(-1);
        }

        // Begin by importing documents from text to feature sequences
        InstanceList instances = Common.createInstanceList();

        // Read data from ePubs.
        File bookRootDir = new File(args[0]);
        File isbnFile = new File(args[1]);
        Set<String> knownIsbns = Epubs.isbns(isbnFile);
        System.out.println("Filtering epubs with " + knownIsbns.size() + " known ISBNs");

        instances.addThruPipe(Epubs.bookIterator(bookRootDir, 25000/*!!!*/, knownIsbns));

        // Create a model with the given number of topics, alpha_t = 0.01, beta_w = 0.01
        // Note that the first parameter is passed as the sum over topics, while
        // the second is the parameter for a single dimension of the Dirichlet prior.
        int numTopics = 250;
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        // statistics after every iteration.
        model.setNumThreads(2);

        // Run the model for N iterations and stop (50 is for testing only,
        // for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(1000);
        model.estimate();

        // Write model to file.
        File modelFile = new File("/Users/jans/src/research/mallet-tools/src/main/resources/BookTopicModel.dump");
        model.write(modelFile);
        System.out.println("Wrote model to file " + modelFile);

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = instances.getDataAlphabet();

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

        // Show top words in topics.
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

            Formatter out = new Formatter(new StringBuilder(), Locale.US);
            out.format("%d\t", topic);
            int rank = 0;
            while (iterator.hasNext() && rank < NUM_WORDS_PER_TOPIC) {
                IDSorter idCountPair = iterator.next();
                out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            System.out.println(out);
        }

    }

}
