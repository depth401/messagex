package com.github.depth401.messagex;

import com.github.depth401.messagex.filter.Filter;
import com.github.depth401.messagex.mask.Masker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MessagexException extends Exception {

  private static final long serialVersionUID = -8100899894045009685L;

  private final List<Filter> exceptionFilters;

  private final List<Filter> messageFilters;

  private final List<Masker> maskers;

  private static final List<Filter> DEFAULT_EXCEPTION_FILTERS = List.of();

  private static final List<Filter> DEFAULT_MESSAGE_FILTERS = List.of();

  private static final List<Masker> DEFAULT_MASKERS = List.of(Masker.MASK_ALL);

  public MessagexException() {
    this(null, null, DEFAULT_EXCEPTION_FILTERS, DEFAULT_MESSAGE_FILTERS, DEFAULT_MASKERS);
  }

  public MessagexException(String message) {
    this(message, DEFAULT_MASKERS);
  }

  public MessagexException(String message, List<Masker> maskers) {
    this(message, null, DEFAULT_EXCEPTION_FILTERS, DEFAULT_MESSAGE_FILTERS, maskers);
  }

  public MessagexException(String message, Throwable cause) {
    this(message, cause, DEFAULT_EXCEPTION_FILTERS, DEFAULT_MESSAGE_FILTERS, DEFAULT_MASKERS);
  }

  public MessagexException(
      String message,
      Throwable cause,
      List<Filter> exceptionFilters,
      List<Filter> messageFilters,
      List<Masker> maskers) {
    this(message, cause, true, true, exceptionFilters, messageFilters, maskers);
  }

  public MessagexException(Throwable cause) {
    this(cause, DEFAULT_EXCEPTION_FILTERS, DEFAULT_MESSAGE_FILTERS, DEFAULT_MASKERS);
  }

  public MessagexException(
      Throwable cause,
      List<Filter> exceptionFilters,
      List<Filter> messageFilters,
      List<Masker> maskers) {
    this(null, cause, true, true, exceptionFilters, messageFilters, maskers);
  }

  public MessagexException(
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace,
      List<Filter> exceptionFilters,
      List<Filter> messageFilters,
      List<Masker> maskers) {
    super(message, cause, enableSuppression, writableStackTrace);

    this.exceptionFilters = Collections.unmodifiableList(exceptionFilters);
    this.messageFilters = Collections.unmodifiableList(messageFilters);
    this.maskers = Collections.unmodifiableList(maskers);

    MessagexUtil.recursiveMaskMessage(this, exceptionFilters, messageFilters, maskers);
  }

  public List<Filter> getExceptionFilters() {
    return this.exceptionFilters;
  }

  public List<Filter> getMessageFilters() {
    return this.messageFilters;
  }

  public List<Masker> getMaskers() {
    return this.maskers;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String message;

    private Throwable cause;

    private boolean enableSuppression = true;

    private boolean writableStackTrace = true;

    private final List<Filter> exceptionFilters = new ArrayList<>();

    private final List<Filter> messageFilters = new ArrayList<>();

    private final List<Masker> maskers = new ArrayList<>();

    public Builder message(String message) {
      this.message = message;
      return this;
    }

    public Builder cause(Throwable cause) {
      this.cause = cause;
      return this;
    }

    public Builder enableSuppression(boolean enableSuppression) {
      this.enableSuppression = enableSuppression;
      return this;
    }

    public Builder writableStackTrace(boolean writableStackTrace) {
      this.writableStackTrace = writableStackTrace;
      return this;
    }

    public Builder addExceptionFilter(Filter filter) {
      this.exceptionFilters.add(filter);
      return this;
    }

    public Builder addMessageFilter(Filter filter) {
      this.messageFilters.add(filter);
      return this;
    }

    public Builder addMasker(Masker masker) {
      maskers.add(masker);
      return this;
    }

    public MessagexException build() {
      return new MessagexException(
          this.message,
          this.cause,
          this.enableSuppression,
          this.writableStackTrace,
          this.exceptionFilters,
          this.messageFilters,
          this.maskers);
    }
  }
}
