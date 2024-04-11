package yuriy.weiss.training.korova;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.ToIntFunction;

import static yuriy.weiss.training.korova.Constants.*;

@Slf4j
public class ResultManipulation {

    private ResultManipulation() {
    }

    public static void copyAvailableIgnoringOne( int[] availableDigits, int[] nextAvailable, int nextDigit ) {
        int nextPos = 0;
        for(int availableDigit : availableDigits) {
            if (availableDigit != nextDigit) {
                nextAvailable[nextPos] = availableDigit;
                nextPos++;
            }
        }
    }

    public static boolean checkResult( ResultNumbers resultNumbers ) {
        return resultNumbers.korova() + resultNumbers.trava() + resultNumbers.doyarka() == resultNumbers.moloko();
    }

    public static void displayResult( Object result, ResultNumbers resultNumbers, boolean full ) {
        log.info( "" );
        if ( checkResult( resultNumbers ) ) {
            log.info( "SUCCESS" );
        } else {
            log.info( "FAILURE" );
        }
        log.info( "result: {}", result );
        if ( full ) {
            log.info( "KOROVA: {}", resultNumbers.korova() );
            log.info( " TRAVA:  {}", resultNumbers.trava() );
            log.info( "DOYRKA: {}", resultNumbers.doyarka() );
            log.info( "MOLOKO: {}", resultNumbers.moloko() );
        }
    }

    public static ResultNumbers buildNumbers( Map<String, Integer> combination ) {
        return buildNumbers( combination::get );
    }

    public static ResultNumbers buildNumbers( int[] digitsCombination ) {
        return buildNumbers( letter -> digitsCombination[ALL_LETTERS_LIST.indexOf( letter )] );
    }

    private static ResultNumbers buildNumbers( ToIntFunction<String> mappingFunction ) {
        return new ResultNumbers(
                buildNumber( mappingFunction, KOROVA ),
                buildNumber( mappingFunction, TRAVA ),
                buildNumber( mappingFunction, DOYARKA ),
                buildNumber( mappingFunction, MOLOKO ) );
    }

    private static int buildNumber( ToIntFunction<String> mappingFunction, String[] word ) {
        int result = 0;
        for ( String nextLetter : word ) {
            result = result * 10 + mappingFunction.applyAsInt( nextLetter );
        }
        return result;
    }

    public static Map<String, Integer> buildCombination( int[] digitsCombination ) {
        Map<String, Integer> result = new LinkedHashMap<>();
        for ( int i = 0; i < 10; i++ ) {
            result.put( ALL_LETTERS_LIST.get( i ), digitsCombination[i] );
        }
        return result;
    }

    public static ResultNumbers buildNumbersOptimized( int[] digitsCombination ) {
        return new ResultNumbers(
                buildNumberOptimized( digitsCombination, new int[] {3, 1, 4, 1, 2, 0} ),
                buildNumberOptimized( digitsCombination, new int[] {7, 4, 0, 2, 0} ),
                buildNumberOptimized( digitsCombination, new int[] {8, 1, 5, 4, 3, 0} ),
                buildNumberOptimized( digitsCombination, new int[] {9, 1, 6, 1, 3, 1} ) );
    }

    private static int buildNumberOptimized( int[] digitsCombination, int[] wordIndexes ) {
        int result = 0;
        for ( int nextIndex : wordIndexes ) {
            result = result * 10 + digitsCombination[nextIndex];
        }
        return result;
    }
}
