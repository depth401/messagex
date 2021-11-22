package com.github.depth401.messagex.filter;

public final class AcceptFilter implements Filter {

    @Override
    public FilterReply decide(Throwable e) {
        return FilterReply.ACCEPT;
    }
}
