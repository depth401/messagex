package com.github.depth401.messagex.filter;

public enum FilterReply {
    ACCEPT, DENY, NEUTRAL;

    public FilterReply combine(FilterReply another) {
        switch (this) {
            case DENY:
                return DENY;
            case ACCEPT:
                return another == DENY ? DENY : ACCEPT;
            case NEUTRAL:
                return another;
        }

        throw new UnsupportedOperationException("Unsupported FilterReply");
    }
}
