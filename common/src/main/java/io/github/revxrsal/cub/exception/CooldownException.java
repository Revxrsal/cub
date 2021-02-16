package io.github.revxrsal.cub.exception;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import static io.github.revxrsal.cub.core.Utils.n;

public class CooldownException extends CommandException {

    private final long timeLeft;
    private final String fancy;

    public CooldownException(long timeLeft) {
        this.timeLeft = timeLeft;
        fancy = formatTimeFancy(timeLeft);
    }

    public long getTimeLeftMillis() {
        return timeLeft;
    }

    public long getTimeLeft(@NotNull TimeUnit unit) {
        n(unit, "unit cannot be null!");
        return unit.convert(timeLeft, TimeUnit.MILLISECONDS);
    }

    public String getTimeFancy() {
        return fancy;
    }

    public static String formatTimeFancy(long time) {
        Duration d = Duration.ofMillis(time);
        long hours = d.toHours();
        long minutes = d.minusHours(hours).getSeconds() / 60;
        long seconds = d.minusMinutes(minutes).minusHours(hours).getSeconds();
        List<String> words = new ArrayList<>();
        if (hours != 0)
            words.add(hours + plural(hours, " hour"));
        if (minutes != 0)
            words.add(minutes + plural(minutes, " minute"));
        if (seconds != 0)
            words.add(seconds + plural(seconds, " second"));
        return toFancyString(words);
    }

    public static <T> String toFancyString(List<T> list) {
        StringJoiner builder = new StringJoiner(", ");
        if (list.isEmpty()) return "";
        if (list.size() == 1) return list.get(0).toString();
        for (int i = 0; i < list.size(); i++) {
            T el = list.get(i);
            if (i + 1 == list.size())
                return builder.toString() + " and " + el.toString();
            else
                builder.add(el.toString());
        }
        return builder.toString();
    }

    public static String plural(Number count, String thing) {
        if (count.intValue() == 1) return thing;
        if (thing.endsWith("y"))
            return thing.substring(0, thing.length() - 1) + "ies";
        return thing + "s";
    }
}
