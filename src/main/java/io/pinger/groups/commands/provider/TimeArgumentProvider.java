package io.pinger.groups.commands.provider;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.parametric.DrinkProvider;
import io.pinger.groups.timer.TimeUtil;
import io.pinger.groups.timer.Timer;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimeArgumentProvider extends DrinkProvider<Timer> {

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public @Nullable Timer provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) {
        final String timeAsString = arg.get();
        final long seconds = TimeUtil.parseTimeIntoSeconds(timeAsString);
        final long milliseconds = TimeUnit.SECONDS.toMillis(seconds);
        return Timer.builder().withMilliseconds(milliseconds).build();
    }

    @Override
    public String argumentDescription() {
        return "A time duration, ex. 4d7m23s";
    }
}
