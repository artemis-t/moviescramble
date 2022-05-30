package com.moviescramble.global.feignconfig;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Type;

public class PageableQueryEncoder implements Encoder {
    private final Encoder delegate;

    PageableQueryEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (object instanceof Pageable) {
            Pageable pageable = (Pageable) object;
            template.query("page", String.valueOf(pageable.getPageNumber() + 1));
        } else {
            delegate.encode(object, bodyType, template);
        }
    }
}
