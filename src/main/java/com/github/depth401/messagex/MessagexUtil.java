package com.github.depth401.messagex;

import com.github.depth401.messagex.filter.Filter;
import com.github.depth401.messagex.filter.FilterReply;
import com.github.depth401.messagex.mask.Masker;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

class MessagexUtil {

  private MessagexUtil() {}

  static void recursiveMaskMessage(Throwable e, List<Filter> exceptionFilters, List<Filter> messageFilters, List<Masker> maskers) {
    var devaVu = Collections.<Throwable>newSetFromMap(new IdentityHashMap<>());

    recursiveMaskMessage(e, devaVu, exceptionFilters, messageFilters, maskers);
  }

  static void setThrowableMessage(Throwable e, String message) {
    try {
      var field = Throwable.class.getDeclaredField("detailMessage");
      field.setAccessible(true);
      field.set(e, message);
      field.setAccessible(false);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new RuntimeException(
          String.format(
              "Failed to set %s#detailMessage as '%s'. "
                  + "Check your JDK's implementation of Throwable and please consider contributing to no-messagex if you want to fix.",
              e.getCause(), message));
    }
  }

  static void setThrowableCause(Throwable e, Throwable cause) {
    try {
      var field = Throwable.class.getDeclaredField("cause");
      field.setAccessible(true);
      field.set(e, cause);
      field.setAccessible(false);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      throw new RuntimeException(
          String.format(
              "Failed to set %s#cause as 'cause'. "
                  + "Check your JDK's implementation of Throwable and please consider contributing to no-messagex if you want to fix.",
              e.getCause()));
    }
  }

  static FilterReply applyFilters(Throwable e, List<Filter> filters) {
    if (e == null) {
      return FilterReply.NEUTRAL;
    }

    if (filters == null) {
      return FilterReply.NEUTRAL;
    }

    return filters.stream()
        .sequential()
        .reduce(
            FilterReply.NEUTRAL,
            (reply, filter) -> reply == FilterReply.NEUTRAL ? filter.decide(e) : reply,
            FilterReply::combine);
  }

  static String maskMessage(Throwable e, List<Masker> maskers) {
    if (e == null || e.getMessage() == null) {
      return null;
    }

    if (maskers == null) {
      return e.getMessage();
    }

    return maskers.stream()
        .sequential()
        .reduce(
            e.getMessage(),
            (message, masker) -> masker.mask(e, message),
            (existing, replacement) -> replacement);
  }

  static void recursiveMaskMessage(
      Throwable e,
      Set<Throwable> devaVu,
      List<Filter> exceptionFilters,
      List<Filter> messageFilters,
      List<Masker> maskers) {
    if (e == null) {
      return;
    }

    // filter e's message
    var messageFilterReply = applyFilters(e, messageFilters);
    switch (messageFilterReply) {
      case ACCEPT:
      case NEUTRAL:
        break;
      case DENY:
        setThrowableMessage(e, null);
        break;
      default:
        throw new UnsupportedOperationException("Unsupported FilterReply");
    }

    // mask e's message
    setThrowableMessage(e, maskMessage(e, maskers));

    if (devaVu.contains(e)) {
      throw new RuntimeException("[CIRCULAR REFERENCE: " + e.getClass().getName() + "]");
    }

    devaVu.add(e);

    for (Throwable se : e.getSuppressed()) {
      recursiveMaskMessage(se, devaVu, exceptionFilters, messageFilters, maskers);
    }

    var cause = e.getCause();
    if (cause != null) {
      var exceptionFilterReply = applyFilters(cause, exceptionFilters);
      switch (exceptionFilterReply) {
        case ACCEPT:
        case NEUTRAL:
          recursiveMaskMessage(cause, devaVu, exceptionFilters, messageFilters, maskers);
          break;
        case DENY:
          setThrowableCause(cause, null);
          setThrowableMessage(cause, null);
          break;
        default:
          throw new UnsupportedOperationException("Unsupported FilterReply");
      }
    }
  }
}
