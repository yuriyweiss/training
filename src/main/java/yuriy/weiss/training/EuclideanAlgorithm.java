package yuriy.weiss.training;

import java.util.Scanner;

public class EuclideanAlgorithm {

    public static void main( String[] args ) {
        Scanner in = new Scanner( System.in );
        System.out.print( "First number: " ); //NOSONAR
        int a = in.nextInt();
        System.out.print( "Second number: " ); //NOSONAR
        int b = in.nextInt();
        System.out.println( "a: " + a + ", b: " + b ); //NOSONAR
        while ( a != b ) {
            if ( a > b ) {
                a = a - b;
            } else {
                b = b - a;
            }
            System.out.println( "a: " + a + ", b: " + b ); //NOSONAR
        }
        System.out.println( "result: " + a ); //NOSONAR
    }
}
