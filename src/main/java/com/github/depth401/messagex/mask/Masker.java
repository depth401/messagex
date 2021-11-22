package com.github.depth401.messagex.mask;

@FunctionalInterface
public interface Masker {

    Masker MASK_ALL = new MaskAllMasker();

    String mask(Throwable e, String message);
}
