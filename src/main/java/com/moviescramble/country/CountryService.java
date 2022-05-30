package com.moviescramble.country;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CountryService {

    private final CountryRepository countryRepository;

    @Value("classpath:/countries.csv")
    private Resource countriesResource;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public void populateCountries() throws IOException {
        log.info("Updating known country list...");

        try (Stream<String> lines = new BufferedReader(
                new InputStreamReader(countriesResource.getInputStream()))
                .lines()
        ) {
            lines.skip(1).forEach(s -> {
                String[] tokens = s.split(",");
                countryRepository.save(
                        Country.builder()
                                .isoCode(tokens[0])
                                .countryName(tokens[1])
                                .build());
            });
        }
    }
}
