package yuriy.weiss.training.korova;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static yuriy.weiss.training.korova.Constants.DISPLAY_EACH_RESULT;
import static yuriy.weiss.training.korova.ResultManipulation.*;

/**
 * Straightforward recursion implementation.<br>
 * Data for each step is allocated in cloned lists and maps to avoid intersection.
 */
@Slf4j
public class KorovaTravaMolokoVer1 {

    // "А", "О", "В", "К", "Р", "Я", "Л", "Т", "Д", "М"
    private static final List<String> LETTERS = Arrays.asList( "A", "O", "V", "K", "R", "Y", "L", "T", "D", "M" );
    private static final List<Map<String, Integer>> successCombinations = new ArrayList<>();

    public static void main( String[] args ) {
        log.info( "START" );
        List<Integer> availableDigits = Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 );
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
            final String currentLetter, final int currentLevel, List<Integer> availableDigits ) {
        if ( availableDigits.isEmpty() ) {
            checkAndDisplayResult( result );
        } else {
            for ( Integer nextDigit : availableDigits ) {
                List<Integer> clonedAvailable = new ArrayList<>( availableDigits );
                clonedAvailable.remove( nextDigit );
                Map<String, Integer> clonedResult = new LinkedHashMap<>( result );
                clonedResult.put( currentLetter, nextDigit );
                String nextLetter = null;
                if ( !clonedAvailable.isEmpty() ) {
                    nextLetter = LETTERS.get( currentLevel + 1 );
                }
                fillCombinationAndCheckResult( clonedResult, nextLetter, currentLevel + 1, clonedAvailable );
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
