/*
 * #%L
 * NICTA t3as Named-Entity Recognition library
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
package org.t3as.ner.resource;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.t3as.ner.classifier.feature.Feature;
import org.t3as.ner.classifier.feature.FeatureMap;
import org.t3as.ner.NameType;

import javax.annotation.concurrent.Immutable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** This class specifies the configurations. */
@Immutable
public class Configuration {

    public static final String DEFAULT_CONFIG_RESOURCE = "config";

    /** NameType array specifies the possible types of names. */
    private final ImmutableList<NameType> name_types;

    private final FeatureMap feature_map;

    public Configuration() throws IOException { this(DEFAULT_CONFIG_RESOURCE); }

    /** Constructor. Read in the config file. */
    public Configuration(final String configResource) throws IOException {
        final Pattern COLONS = Pattern.compile(":");
        final Splitter SPACES = Splitter.on(' ').trimResults().omitEmptyStrings();

        // name type texts
        final List<NameType> nameTypes = new ArrayList<>();
        // w texts
        List<String> wTexts = ImmutableList.of();
        // Feature array specifies the features used in name type recognition.
        final List<Feature> features = new ArrayList<>();

        try (final BufferedReader br = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream(configResource)))) {

            // read each line from the file and put the information
            // in the temperate variables
            // need further process to extract the information
            for (String line; (line = br.readLine()) != null; ) {
                if (line.startsWith("#")) continue;
                if (line.trim().isEmpty()) continue;

                final String[] parts = COLONS.split(line, 2);
                switch (parts[0]) {
                    case "Name Types":
                        for (final String s : SPACES.split(parts[1])) {
                            nameTypes.add(NameType.valueOf(s));
                        }
                        break;

                    case "Feature":
                        final List<String> c = SPACES.splitToList(parts[1]);
                        if (c.size() != 2) {
                            throw new IllegalArgumentException("Config File Syntax Error: '" + line + "'");
                        }
                        features.add(Feature.generateFeatureByName(c.get(0), c.get(1)));
                        break;

                    case "w":
                        wTexts = SPACES.splitToList(parts[1].replace("| ", ""));
                        break;

                    default:
                        throw new IllegalArgumentException("Unexpected config keyword: '" + parts[0] + "'");
                }
            }
        }

        if (nameTypes.isEmpty() || features.isEmpty() || wTexts.isEmpty())
            throw new IllegalArgumentException("Config File Syntax Error, no Name Types, Features, or w params.");

        // if syntax error, throw exception.
        if (wTexts.size() != features.size() * nameTypes.size())
            throw new IllegalArgumentException(
                    "Config File Syntax Error, number of w params do not equal (num Name Types * num Features)");

        /* w is the coefficient array. */
        final double[][] w = new double[nameTypes.size()][features.size()];

        // name_types information
        name_types = ImmutableList.copyOf(nameTypes);

        // weight array
        int wi = 0;
        for (int i = 0; i < name_types.size(); i++) {
            for (int j = 0; j < features.size(); j++) {
                w[i][j] = Double.parseDouble(wTexts.get(wi));
                wi++;
            }
        }

        // create feature map
        feature_map = new FeatureMap(features, w);
    }

    public FeatureMap getFeatureMap() { return feature_map; }

    public ImmutableList<NameType> getNameTypes() { return name_types; }
}
