package net.bc100dev.commons.utils;

public class StringGenerator {

    public static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String NUMBERS = "0123456789";
    public static final String SPECIAL_CHARACTERS = "(){}[]*+~#'-_.,:;|<>!\"§$%&/=?\\`´°^@€µ";
    public static final String DEFAULT_CHARACTER_MAP = UPPERCASE + LOWERCASE + NUMBERS + SPECIAL_CHARACTERS;
    public static final String DEFAULT_CHARACTER_MAP_NO_SPECIALS = UPPERCASE + LOWERCASE + NUMBERS;

    public static String generateString(int length) {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i < length; i++)
            str.append(DEFAULT_CHARACTER_MAP.charAt(Utility.getRandomInteger(0, DEFAULT_CHARACTER_MAP.length() - 1)));

        return str.toString();
    }

    public static String generateString(int length, String charMap) {
        if (charMap == null)
            return "";

        if (charMap.isEmpty())
            return "";

        StringBuilder str = new StringBuilder();

        for (int i = 0; i < length; i++)
            str.append(charMap.charAt(Utility.getRandomInteger(0, charMap.length() - 1)));

        return str.toString();
    }

    public static String[] generateStrings(int strings, int length) {
        String[] data = new String[strings];

        for (int i = 0; i < strings; i++)
            data[i] = generateString(length);

        return data;
    }

    public static String[] generateStrings(int strings, int length, String charMap) {
        String[] data = new String[strings];

        for (int i = 0; i < strings; i++)
            data[i] = generateString(length, charMap);

        return data;
    }

}
