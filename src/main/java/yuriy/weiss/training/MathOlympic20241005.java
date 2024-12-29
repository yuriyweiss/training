package yuriy.weiss.training;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class MathOlympic20241005 {

    public static void main( String[] args ) {
        for ( int i = 9999; i > 1000; i-- ) {
            int secondNumber = checkNumber( i );
            if ( secondNumber != -1 ) {
                log.info( "first: {}, second: {}", i, secondNumber );
            }
        }
    }

    private static int checkNumber( int firstNumber ) {
        if ( firstNumber % 9 != 0 || firstNumber % 2 == 0 ) {
            return -1;
        }

        int[] firstDigits = new int[4];
        firstDigits[0] = firstNumber % 10;
        firstDigits[1] = firstNumber % 100 / 10;
        firstDigits[2] = firstNumber % 1000 / 100;
        firstDigits[3] = firstNumber / 1000;
        log.trace( "first digits: {}", Arrays.toString( firstDigits ) );

        int secondNumber = firstNumber * 9;
        log.trace( "second number: {}", secondNumber );
        if ( secondNumber < 10000 ) {
            return -1;
        }

        int[] secondDigits = new int[5];
        secondDigits[0] = secondNumber % 10;
        secondDigits[1] = secondNumber % 100 / 10;
        secondDigits[2] = secondNumber % 1000 / 100;
        secondDigits[3] = secondNumber % 10000 / 1000;
        secondDigits[4] = secondNumber / 10000;
        log.trace( "second digits: {}", Arrays.toString( secondDigits ) );

        if ( firstDigits[0] == secondDigits[0]
                && firstDigits[1] == secondDigits[1]
                && firstDigits[2] == secondDigits[2]
                && firstDigits[3] == secondDigits[4] ) {
            return secondNumber;
        } else {
            return -1;
        }
    }
}
