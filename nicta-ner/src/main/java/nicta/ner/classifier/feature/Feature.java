/*
 * #%L
 * NICTA Named Entity Recogniser library
 * %%
 * Copyright (C) 2010 - 2014 NICTA
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package nicta.ner.classifier.feature;

import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import nicta.ner.data.Phrase;
import nicta.ner.util.Dictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * This abstract class is a parent of features.
 */
public abstract class Feature {

    /**
     * Returns a score of the phrase according to the particular feature.
     */
    public abstract double score(Phrase _p);

    /**
     * Factory method create features by name.
     */
    public static Feature generateFeatureByName(final String feature, final String resource)
            throws IllegalArgumentException, IOException {
        final Feature f;
        switch (feature) {
            case "RuledWordFeature":
                f = new RuledWordFeature(resource);
                break;
            case "RuledPhraseFeature":
                f = new RuledPhraseFeature(resource);
                break;
            case "PrepositionContextFeature":
                f = new PrepositionContextFeature(resource);
                break;
            case "ExistingPhraseFeature":
                f = new ExistingPhraseFeature(resource);
                break;
            default:
                throw new IllegalArgumentException("Unknown feature: '" + feature + "'");
        }
        return f;
    }

    /**
     * Return a HashSet contains phrases (multi-words).
     */

    protected static Set<String> createPhraseSet(final String resource) throws IOException {
        try {
            return Resources.readLines(Resources.getResource(Feature.class, resource),
                                       Charset.forName("UTF-8"), new LineReader());
        }
        catch (final IOException ioe) {
            throw new IOException("Error in reading Feature file: " + resource, ioe);
        }
    }

    /**
     * Returns a HashSet contains only single words.
     */
    protected static Set<String> createSingleWordSet(final String filename, final boolean eliminatePrepAndConj) {
        final Dictionary dict = Dictionary.getSharedDictionary();
        final Set<String> set = new HashSet<>();
        try (final BufferedReader br = new BufferedReader(
                new InputStreamReader(Feature.class.getResourceAsStream(filename)))) {

            for (String line; (line = br.readLine()) != null; ) {
                if (line.startsWith("#")) continue;
                final String[] splited = line.split(" ");
                for (final String word : splited) {
                    final String wordType = dict.checkup(word);
                    if (eliminatePrepAndConj && wordType != null && (wordType.startsWith("IN")
                                                                     || wordType.startsWith("CC"))) {
                        continue;
                    }
                    set.add(word);
                }
            }
        }
        catch (final IOException e1) {
            System.out.println("ERROR: Error in reading Feature file: " + filename);
            System.out.println("       at Configuration File: Feature");
            // e1.printStackTrace();
            System.exit(-1);
        }
        return set;
    }

    private static class LineReader implements LineProcessor<Set<String>> {
        Set<String> s = new HashSet<>();

        @Override
        @SuppressWarnings("NullableProblems")
        public boolean processLine(final String line) throws IOException {
            final String l = line.trim();
            if (!l.startsWith("#") && !"".equals(l)) s.add(l);
            return true;
        }

        @Override
        public Set<String> getResult() {
            return s;
        }
    }
}
