package com.blinkbox.books.mallettools;

import cc.mallet.types.*;
import cc.mallet.topics.*;

import java.util.*;
import java.io.*;

/**
 * Display content of a Mallet model previously written to a file.
 */
public class TopicModelDump {

    private static final int NUM_WORDS_PER_TOPIC = 15;

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("Usage: TopicModelDump <model file>");
            System.exit(-1);
        }

        ParallelTopicModel model = ParallelTopicModel.read(new File(args[0]));

        // The data alphabet maps word IDs to strings
        Alphabet dataAlphabet = model.getAlphabet();

        Formatter out = new Formatter(new StringBuilder(), Locale.US);

        // Estimate the topic distribution of the first instance,
        // given the current Gibbs state.
        double[] topicDistribution = model.getTopicProbabilities(0);

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

        // Show top words in topics with proportions for the first document
        for (int topic = 0; topic < model.getNumTopics(); topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

            out = new Formatter(new StringBuilder(), Locale.US);
            out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
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
