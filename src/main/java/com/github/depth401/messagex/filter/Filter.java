package com.github.depth401.messagex.filter;

public interface Filter {

  Filter ACCEPT = new AcceptFilter();

  Filter DENY = new DenyFilter();

  FilterReply decide(Throwable e);
}
