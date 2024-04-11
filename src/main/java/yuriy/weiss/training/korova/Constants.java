package yuriy.weiss.training.korova;

import java.util.Arrays;
import java.util.List;

public class Constants {

    private Constants() {
    }

    // "А", "О", "В", "К", "Р", "Я", "Л", "Т", "Д", "М"
    static final List<String> ALL_LETTERS_LIST =
            Arrays.asList( "A", "O", "V", "K", "R", "Y", "L", "T", "D", "M" );

    static final String[] KOROVA = { "K", "O", "R", "O", "V", "A" };
    static final String[] TRAVA = { "T", "R", "A", "V", "A" };
    static final String[] DOYARKA = { "D", "O", "Y", "R", "K", "A" };
    static final String[] MOLOKO = { "M", "O", "L", "O", "K", "O" };

    static final boolean DISPLAY_EACH_RESULT = false;
}
