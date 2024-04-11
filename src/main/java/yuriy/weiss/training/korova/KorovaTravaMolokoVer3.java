package yuriy.weiss.training.korova;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static yuriy.weiss.training.korova.Constants.DISPLAY_EACH_RESULT;
import static yuriy.weiss.training.korova.ResultManipulation.*;

/**
 * Result storage changed from map to array.<br>
 * ResultNumbers calculation optimized by using letter index in result array.
 */
@Slf4j
public class KorovaTravaMolokoVer3 {

    private static final List<Map<String, Integer>> successCombinations = new ArrayList<>();
    private static final int[] resultDigits = new int[10];

    public static void main( String[] args ) {
        log.info( "START" );
        long startTime = System.currentTimeMillis();
        fillCombinationAndCheckResult( 0, new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 } );
        long timeElapsed = System.currentTimeMillis() - startTime;
        log.info( "ELAPSED: {}", timeElapsed );
        log.info( "\n\n\n\n" );
        log.info( "SUCCESS COMBINATIONS" );
        successCombinations.forEach( combination -> {
            ResultNumbers resultNumbers = buildNumbers( combination );
            displayResult( combination, resultNumbers, true );
        } );
    }

    private static void fillCombinationAndCheckResult( int currentLevel, int[] availableDigits ) {
        if ( currentLevel == 9 ) {
            int nextDigit = availableDigits[0];
            resultDigits[currentLevel] = nextDigit;
            checkAndDisplayResult( resultDigits );
        } else {
            int[] nextAvailable = new int[availableDigits.length - 1];
            for ( int nextDigit : availableDigits ) {
                copyAvailableIgnoringOne( availableDigits, nextAvailable, nextDigit );
                resultDigits[currentLevel] = nextDigit;
                fillCombinationAndCheckResult( currentLevel + 1, nextAvailable );
            }
        }
    }

    private static void checkAndDisplayResult( int[] result ) {
        ResultNumbers resultNumbers = buildNumbersOptimized( result );
        if ( DISPLAY_EACH_RESULT ) {
            displayResult( result, resultNumbers, false );
        }
        if ( checkResult( resultNumbers ) ) {
            successCombinations.add( buildCombination( result ) );
        }
    }
}
