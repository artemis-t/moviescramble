package com.moviescramble.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;

import edu.stanford.nlp.ling.CoreLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class IgnoreWordService {
    private static final String STOP_WORDS_RESOURCE = "classpath:/stopwords.txt";

    private final Map<String, Boolean> ignoredWords;

    public IgnoreWordService(@Value(STOP_WORDS_RESOURCE) Resource stopWords) throws IOException {
        log.info("Loading known stop-words");

        try (Stream<String> lines = new BufferedReader(
                new InputStreamReader(stopWords.getInputStream()))
                .lines()
        ) {
            ignoredWords = lines
                    .map(line -> {
                        String[] tokens = line.split("\\R", -1);
                        return tokens[0];
                    })
                    .collect(Collectors.toMap(
                            line -> line,
                            line -> true,
                            (a, b) -> true
                    ));
        }
    }

    public boolean isIgnored(@NotNull CoreLabel coreLabel) {
        Boolean fromOriginalText =
                Optional.ofNullable(ignoredWords.get(coreLabel.originalText().toLowerCase())).orElse(false);

        // a lemma might not be available from NLP
        String lemma = coreLabel.lemma();
        Boolean fromLemma = false;
        if (!StringUtils.isEmpty(lemma)) {
            fromLemma = Optional.ofNullable(ignoredWords.get(lemma.toLowerCase())).orElse(false);
        }

        return fromOriginalText || fromLemma;
    }
}
