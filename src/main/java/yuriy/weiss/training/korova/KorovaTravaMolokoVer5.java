package yuriy.weiss.training.korova;

import lombok.extern.slf4j.Slf4j;
import yuriy.weiss.training.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static yuriy.weiss.training.korova.Constants.DISPLAY_EACH_RESULT;
import static yuriy.weiss.training.korova.ResultManipulation.*;

@Slf4j
public class KorovaTravaMolokoVer5 {

    private static final int MAX_DIGITS = 10;

    public static void main( String[] args ) {
        KorovaTravaMolokoVer5 builder = new KorovaTravaMolokoVer5();
        builder.runBuild();
    }

    private final int[][][] parent = new int[MAX_DIGITS][MAX_DIGITS][MAX_DIGITS];
    private final List<Map<String, Integer>> successCombinations = new ArrayList<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool( MAX_DIGITS );
    private final AtomicInteger threadsFinished = new AtomicInteger( 0 );

    private KorovaTravaMolokoVer5() {
        for ( int i = 0; i < MAX_DIGITS; i++ ) {
            for ( int j = 0; j < MAX_DIGITS; j++ ) {
                parent[i][0][j] = j;
            }
        }
        initZeroLevel();
    }

    private void initZeroLevel() {
        for ( int threadNum = 0; threadNum < MAX_DIGITS; threadNum++ ) {
            System.arraycopy( parent[threadNum][0], 0, parent[threadNum][1], 0, MAX_DIGITS );
            int i = threadNum;
            parent[threadNum][1][0] = parent[threadNum][0][i];
            for ( int j = 0; j < MAX_DIGITS; j++ ) {
                if ( j < i ) {
                    parent[threadNum][1][j + 1] = parent[threadNum][0][j];
                } else if ( j > i ) {
                    parent[threadNum][1][j] = parent[threadNum][0][j];
                }
            }
        }
    }

    private void runBuild() {
        log.info( "START" );
        for ( int i = 0; i < MAX_DIGITS; i++ ) {
            executorService.submit( new ChildrenBuilder( i ) );
        }
        while ( threadsFinished.get() < MAX_DIGITS ) {
            ThreadUtils.sleep( 200L );
            log.info( "threadsFinished: {}", threadsFinished.get() );
        }
        log.info( "FINISH" );
        log.info( "SUCCESS COMBINATIONS" );
        successCombinations.forEach( combination -> {
            ResultNumbers resultNumbers = buildNumbers( combination );
            displayResult( combination, resultNumbers, true );
        } );
        executorService.shutdown();
    }

    private void buildChildren( int threadNum, int level ) {
        if ( level == MAX_DIGITS - 2 ) {
            // final step
            System.arraycopy( parent[threadNum][level], 0, parent[threadNum][level + 1], 0, MAX_DIGITS );
            log.trace( "child1: {}", parent[threadNum][level + 1] );
            checkAndDisplayResult( parent[threadNum][level + 1] );
            parent[threadNum][level + 1][MAX_DIGITS - 2] = parent[threadNum][level][MAX_DIGITS - 1];
            parent[threadNum][level + 1][MAX_DIGITS - 1] = parent[threadNum][level][MAX_DIGITS - 2];
            log.trace( "child2: {}", parent[threadNum][level + 1] );
            checkAndDisplayResult( parent[threadNum][level + 1] );
        } else {
            System.arraycopy( parent[threadNum][level], 0, parent[threadNum][level + 1], 0, MAX_DIGITS );
            for ( int i = level; i < MAX_DIGITS; i++ ) {
                parent[threadNum][level + 1][level] = parent[threadNum][level][i];
                for ( int j = level; j < MAX_DIGITS; j++ ) {
                    if ( j < i ) {
                        parent[threadNum][level + 1][j + 1] = parent[threadNum][level][j];
                    } else if ( j > i ) {
                        parent[threadNum][level + 1][j] = parent[threadNum][level][j];
                    }
                }
                buildChildren( threadNum, level + 1 );
            }
        }
        if ( level == 1 ) {
            threadsFinished.getAndIncrement();
        }
    }

    private void checkAndDisplayResult( int[] result ) {
        ResultNumbers resultNumbers = buildNumbersOptimized( result );
        if ( DISPLAY_EACH_RESULT ) {
            displayResult( result, resultNumbers, false );
        }
        if ( checkResult( resultNumbers ) ) {
            addSuccessCombination( result );
        }
    }

    private synchronized void addSuccessCombination( int[] result ) {
        successCombinations.add( buildCombination( result ) );
    }

    private class ChildrenBuilder implements Runnable {
        private final int threadNumber;

        public ChildrenBuilder( int threadNumber ) {
            this.threadNumber = threadNumber;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            buildChildren( threadNumber, 1 );
            long timeElapsed = System.currentTimeMillis() - startTime;
            log.info( "ELAPSED {}: {}", threadNumber, timeElapsed );
        }
    }
}
