package com.blinkbox.books.mallettools;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.CharSequenceReplace;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.types.InstanceList;

public class Common {

    public static InstanceList createInstanceList() {
        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, strip out crap, tokenize, remove stopwords, map to features
        pipeList.add(new CharSequenceLowercase());
        // Strip of word endings after apostrophes. Note: it's not just any ol' apostrophe!!!
        pipeList.add(new CharSequenceReplace(Pattern.compile("[’']s\\b"), ""));
        pipeList.add(new CharSequenceReplace(Pattern.compile("n[’']t\\b"), ""));
        pipeList.add(new CharSequenceReplace(Pattern.compile("[’']ll\\b"), ""));
        pipeList.add(new CharSequenceReplace(Pattern.compile("[’']m\\b"), ""));
        pipeList.add(new CharSequenceReplace(Pattern.compile("[’']d\\b"), ""));
        pipeList.add(new CharSequenceReplace(Pattern.compile("[’']ve\\b"), ""));
        pipeList.add(new CharSequenceReplace(Pattern.compile("[’']re\\b"), ""));
        pipeList.add(new CharSequenceReplace(Pattern.compile("\\b\\d+\\b"), ""));
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipeList.add(new TokenSequenceRemoveStopwords(new File(
                "/Users/jans/src/research/mallet-tools/src/main/resources/en.txt"), "UTF-8", false, false, false));
        pipeList.add(new TokenSequence2FeatureSequence());

        return new InstanceList(new SerialPipes(pipeList));
    }

}
