package com.moviescramble.nlp;

import com.moviescramble.nlp.domain.Lemma;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LemmaService {

    private final LemmaRepository lemmaRepository;

    @Autowired
    public LemmaService(LemmaRepository lemmaRepository) {
        this.lemmaRepository = lemmaRepository;
    }

    @Transactional
    public void saveLemmaSet(Set<Lemma> lemmas) {
        lemmas.forEach(lemmaRepository::save);
    }
}
