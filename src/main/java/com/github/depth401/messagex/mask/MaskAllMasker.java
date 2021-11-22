package com.github.depth401.messagex.mask;

public class MaskAllMasker implements Masker {

    private static final String MASK_MESSAGE = "[CAUTION] THIS MESSAGE IS MASKED BY messagex";

    @Override
    public String mask(Throwable e, String message) {
        return MASK_MESSAGE;
    }
}
