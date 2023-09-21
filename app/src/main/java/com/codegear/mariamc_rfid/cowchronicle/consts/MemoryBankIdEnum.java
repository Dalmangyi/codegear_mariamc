package com.codegear.mariamc_rfid.cowchronicle.consts;

import java.util.Arrays;

public enum MemoryBankIdEnum {

    NONE("NONE"),
    RESERVED("RESERVED"),
    EPC("EPC"),
    TID("TID"),
    USER("USER");

    private final String name;
    private MemoryBankIdEnum(String str){
        name = str;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString(){
        return this.name;
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
