package yuriy.weiss.training.korova;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static yuriy.weiss.training.korova.Constants.DISPLAY_EACH_RESULT;
import static yuriy.weiss.training.korova.ResultManipulation.*;

/**
 * Changed next available digits allocation from list to array.
 */
@Slf4j
public class KorovaTravaMolokoVer2 {

    // "А", "О", "В", "К", "Р", "Я", "Л", "Т", "Д", "М"
    private static final String[] LETTERS = { "A", "O", "V", "K", "R", "Y", "L", "T", "D", "M" };
    private static final List<Map<String, Integer>> successCombinations = new ArrayList<>();

    public static void main( String[] args ) {
        log.info( "START" );
        int[] availableDigits = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        long startTime = System.currentTimeMillis();
        fillCombinationAndCheckResult( new HashMap<>(), "A", 0, availableDigits );
        long timeElapsed = System.currentTimeMillis() - startTime;
        log.info( "ELAPSED: {}", timeElapsed );
        log.info( "\n\n\n\n" );
        log.info( "SUCCESS COMBINATIONS" );
        successCombinations.forEach( combination -> {
            ResultNumbers resultNumbers = buildNumbers( combination );
            displayResult( combination, resultNumbers, true );
        } );
    }

    private static void fillCombinationAndCheckResult( final Map<String, Integer> result,
            final String currentLetter, final int currentLevel, int[] availableDigits ) {
        if ( currentLevel == 9 ) {
            Map<String, Integer> clonedResult = new LinkedHashMap<>( result );
            int nextDigit = availableDigits[0];
            clonedResult.put( currentLetter, nextDigit );
            checkAndDisplayResult( clonedResult );
        } else {
            int[] nextAvailable = new int[availableDigits.length - 1];
            for ( int nextDigit : availableDigits ) {
                copyAvailableIgnoringOne( availableDigits, nextAvailable, nextDigit );
                Map<String, Integer> clonedResult = new LinkedHashMap<>( result );
                clonedResult.put( currentLetter, nextDigit );
                String nextLetter = LETTERS[currentLevel + 1];
                fillCombinationAndCheckResult( clonedResult, nextLetter, currentLevel + 1, nextAvailable );
            }
        }
    }

    private static void checkAndDisplayResult( Map<String, Integer> result ) {
        ResultNumbers resultNumbers = buildNumbers( result );
        if ( DISPLAY_EACH_RESULT ) {
            displayResult( result, resultNumbers, false );
        }
        if ( checkResult( resultNumbers ) ) {
            successCombinations.add( result );
        }
    }
}
