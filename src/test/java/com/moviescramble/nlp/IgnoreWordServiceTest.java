package com.moviescramble.nlp;

import edu.stanford.nlp.ling.CoreLabel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = IgnoreWordService.class)
@RunWith(SpringRunner.class)
public class IgnoreWordServiceTest {
    @Autowired
    IgnoreWordService ignoreWordService;

    @Test
    public void isIgnored_trueSample() {
        assertThat(ignoreWordService.isIgnored(CoreLabel.wordFromString("!!"))).isTrue();
    }

    @Test
    public void isIgnored_falseSample() {
        assertThat(ignoreWordService.isIgnored(CoreLabel.wordFromString("Artemis"))).isFalse();
    }
}