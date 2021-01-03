package com.example.diploma.enums;

public class GlobalSettings {

    public enum Code {
        MULTIUSER_MODE("Многопользовательский режим"),
        POST_PREMODERATION("Премодерация постов"),
        STATISTICS_IS_PUBLIC("Показывать всем статистику блога");
        private final String name;

        Code(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public enum Value {
        YES("Да", true),
        NO("Нет", false);
        private final String name;
        private final Boolean value;

        Value(String name, Boolean value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Boolean getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "GlobalSet{" +
                    "MULTIUSER_MODE='" + super.name() + '\'' + "VAL: " + this.name + " " + this.value +
                    '}';
        }
    }
}
