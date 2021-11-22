package com.github.depth401.messagex.filter;

public final class DenyFilter implements Filter {

    @Override
    public FilterReply decide(Throwable e) {
        return FilterReply.DENY;
    }
}
