package tw.com.fcb.mimosa.tracing;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.util.StopWatch;
import org.springframework.util.StopWatch.TaskInfo;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@code Watcher} 的集合, 並提供了幾種 to-string 方式
 *
 * @see TracedCloseable
 * @author Matt Ho
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class Traces {

  final List<TracedCloseable> tasks = Lists.newLinkedList();
  final String id;
  @NonNull
  final TimeUnit timeUnit;

  void add(@NonNull TracedCloseable task) {
    tasks.add(task);
  }

  long getTime(long nanos) {
    return timeUnit.convert(nanos, NANOSECONDS);
  }

  String shortSummary() {
    return format(
        "Traced '%s': running time = %s %s",
        ofNullable(id).orElse(""),
        tasks.isEmpty() ? 0 : getTime(tasks.get(0).watch.getTotalTimeNanos()),
        abbreviate());
  }

  String prettyPrint() {
    var sb = new StringBuilder(shortSummary());
    sb.append('\n');
    if (tasks.isEmpty()) {
      sb.append("No traced info kept");
      return sb.toString();
    }
    sb.append("---------------------------------------------\n");
    sb.append(Strings.padEnd(abbreviate(), 7, ' '));
    sb.append("    %     Traced name\n");
    sb.append("---------------------------------------------\n");
    var tasks = getTaskInfo();
    var totalTimeNanos = tasks.get(0).getTimeNanos();
    var nf = NumberFormat.getNumberInstance();
    nf.setMinimumIntegerDigits(9);
    nf.setGroupingUsed(false);
    var pf = NumberFormat.getPercentInstance();
    pf.setMinimumIntegerDigits(3);
    pf.setGroupingUsed(false);
    sb.append(
        tasks.stream()
            .map(
                task -> {
                  var s = new StringBuffer();
                  s.append(nf.format(getTime(task.getTimeNanos()))).append("  ");
                  s.append(pf.format((double) task.getTimeNanos() / totalTimeNanos)).append("  ");
                  s.append(task.getTaskName());
                  return s.toString();
                })
            .collect(joining("\n")));
    return sb.toString();
  }

  @Override
  public String toString() {
    var sb = new StringBuilder(shortSummary());
    if (tasks.isEmpty()) {
      sb.append("; no traced info kept");
      return sb.toString();
    }
    var tasks = getTaskInfo();
    var totalTimeNanos = tasks.get(0).getTimeNanos();
    for (TaskInfo task : getTaskInfo()) {
      sb.append("; [")
          .append(task.getTaskName())
          .append("] took ")
          .append(getTime(task.getTimeNanos()))
          .append(" " + abbreviate());
      var percent = Math.round(100.0 * task.getTimeNanos() / totalTimeNanos);
      sb.append(" = ").append(percent).append("%");
    }
    return sb.toString();
  }

  List<TaskInfo> getTaskInfo() {
    return tasks.stream()
        .map(TracedCloseable::getWatch)
        .map(StopWatch::getTaskInfo)
        .flatMap(Arrays::stream)
        .collect(toList());
  }

  String abbreviate() {
    switch (timeUnit) {
      case NANOSECONDS:
        return "ns";
      case MICROSECONDS:
        return "μs";
      case MILLISECONDS:
        return "ms";
      case SECONDS:
        return "sec";
      case MINUTES:
        return "min";
      case HOURS:
        return "hour";
      case DAYS:
        return "day";
      default:
        throw new UnsupportedOperationException(
            "Unsupported abbreviate time-unit for: " + timeUnit);
    }
  }
}
