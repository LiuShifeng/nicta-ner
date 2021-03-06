/*
 * #%L
 * NICTA t3as NER CoNLL 2003 evaluation
 * %%
 * Copyright (C) 2014 NICTA
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
package org.t3as.ner.conll2003;

import com.google.common.collect.ImmutableMap;
import org.t3as.ner.EntityClass;

import java.util.Map;

public class NerClassification {

    public final String nerToken;
    public final EntityClass type;
    public final int phraseStartIndex;
    public final ImmutableMap<EntityClass, Double> scores;

    public NerClassification(final String nerToken, final EntityClass type, final int phraseStartIndex,
                             final Map<EntityClass, Double> scores) {
        this.nerToken = nerToken;
        this.type = type;
        this.phraseStartIndex = phraseStartIndex;
        this.scores = ImmutableMap.copyOf(scores);
    }
}
