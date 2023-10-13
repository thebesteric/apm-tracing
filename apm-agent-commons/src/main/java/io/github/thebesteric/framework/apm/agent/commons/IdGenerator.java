package io.github.thebesteric.framework.apm.agent.commons;

import java.util.Locale;
import java.util.UUID;

public class IdGenerator {

    private IdGenerator() {
        super();
    }

    public String generate() {
        return UUID.randomUUID().toString().toLowerCase(Locale.ROOT);
    }

    public static IdGenerator getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final IdGenerator INSTANCE = new IdGenerator();
    }
}