package com.dnlab.tacktogetherbackend.match.common;

import java.util.NoSuchElementException;

public class NoSuchMatchRequestException extends NoSuchElementException {
    public NoSuchMatchRequestException() {
        super("해당 매칭요청을 찾을 수 없습니다.");
    }

    public NoSuchMatchRequestException(String s) {
        super(s);
    }
}
