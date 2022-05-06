package com.thana.sugarapi.common.macro;

import com.thana.sugarapi.common.api.injector.NotRecord;

public class MacroObject extends NotRecord {

    private final boolean valid;
    private final Task[] tasks;

    public MacroObject(boolean valid, Task[] tasks) {
        this.valid = valid;
        this.tasks = tasks;
    }

    public boolean isValid() {
        return valid;
    }

    public Task[] getTasks() {
        return tasks;
    }

    public static class Task extends NotRecord {

        private final String type;
        private final String optional;
        private final int delay;

        public Task(String key, String optional, int delay) {
            this.type = key;
            this.optional = optional;
            this.delay = delay;
        }

        public String getKey() {
            return type;
        }

        public String getOptional() {
            return optional;
        }

        public int getDelay() {
            return delay;
        }
    }
}
