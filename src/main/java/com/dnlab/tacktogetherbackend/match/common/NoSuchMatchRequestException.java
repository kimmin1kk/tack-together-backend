package com.dnlab.tacktogetherbackend.match.common;

import java.util.NoSuchElementException;

public class NoSuchMatchRequestException extends NoSuchElementException {
    public NoSuchMatchRequestException() {
        super("찾을 수 없는 매칭정보 아이디");
    }

    public NoSuchMatchRequestException(String s) {
        super(s);
    }
}
