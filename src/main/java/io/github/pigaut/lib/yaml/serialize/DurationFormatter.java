package io.github.pigaut.lib.yaml.serialize;

import org.jetbrains.annotations.*;

import java.time.*;
import java.util.regex.*;

public class DurationFormatter implements StringParser<Duration>, StringSerializer<Duration> {

    private static final Pattern SPACE_PATTERN = Pattern.compile(
            "\\s*(\\d+)d\\s*|"+
                    "\\s*(\\d+)h\\s*|"+
                    "\\s*(\\d+)m\\s*|"+
                    "\\s*(\\d+)s\\s*"
    );

    private static final Pattern COLON_PATTERN = Pattern.compile(
            "(\\d+):(\\d+):(\\d+):(\\d+)|" +  // days:hours:minutes:seconds
                    "(\\d+):(\\d+):(\\d+)|" +          // hours:minutes:seconds
                    "(\\d+):(\\d+)|" +                  // minutes:seconds
                    "(\\d+)"                            // seconds
    );

    @Override
    public Duration parse(@NotNull String string) {
        Long days = 0L, hours = 0L, minutes = 0L, seconds = 0L;

        StringParser<Long> longParser = StringParser.LONG;
        Matcher spaceMatcher = SPACE_PATTERN.matcher(string);
        while (spaceMatcher.find()) {
            if (spaceMatcher.group(1) != null) { // days
                days = longParser.parse(spaceMatcher.group(1));
            }
            if (spaceMatcher.group(2) != null) { // hours
                hours = longParser.parse(spaceMatcher.group(2));
            }
            if (spaceMatcher.group(3) != null) { // minutes
                minutes = longParser.parse(spaceMatcher.group(3));
            }
            if (spaceMatcher.group(4) != null) { // seconds
                seconds = longParser.parse(spaceMatcher.group(4));
            }
        }

        Matcher colonMatcher = COLON_PATTERN.matcher(string);
        if (colonMatcher.find()) {
            if (colonMatcher.group(1) != null) { // days:hours:minutes:seconds
                days = longParser.parse(colonMatcher.group(1));
                hours = longParser.parse(colonMatcher.group(2));
                minutes = longParser.parse(colonMatcher.group(3));
                seconds = longParser.parse(colonMatcher.group(4));
            } else if (colonMatcher.group(5) != null) { // hours:minutes:seconds
                hours = longParser.parse(colonMatcher.group(5));
                minutes = longParser.parse(colonMatcher.group(6));
                seconds = longParser.parse(colonMatcher.group(7));
            } else if (colonMatcher.group(8) != null) { // minutes:seconds
                minutes = longParser.parse(colonMatcher.group(8));
                seconds = longParser.parse(colonMatcher.group(9));
            } else if (colonMatcher.group(10) != null) { // seconds
                seconds = longParser.parse(colonMatcher.group(10));
            }
        }

        if (days == null || hours == null || minutes == null || seconds == null) {
            return null;
        }

        return Duration.ofDays(days)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);
    }

    @Override
    public @NotNull String serialize(@NotNull Duration duration) {
        return duration.toSeconds() + "s";
    }

}
