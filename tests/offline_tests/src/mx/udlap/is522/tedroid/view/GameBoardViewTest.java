package mx.udlap.is522.tedroid.view;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;

import mx.udlap.is522.tedroid.view.model.DefaultShape;
import mx.udlap.is522.tedroid.view.model.Tetromino;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.LinkedList;
import java.util.Queue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../../app/AndroidManifest.xml")
public class GameBoardViewTest {
    
    @Test
    public void shouldNotRepeatMoreThan2EqualTetrominos() {
        GameBoardView gameBoardViewMock = mock(GameBoardView.class);
        
        final Activity dummyActivity = Robolectric.buildActivity(Activity.class).create().get();
        when(gameBoardViewMock.getContext()).thenReturn(dummyActivity);
        
        final Queue<Tetromino> expectedTetrominos = buildTestTetrominos(gameBoardViewMock);
        when(gameBoardViewMock.getRandomTetromino()).thenAnswer(new Answer<Tetromino>(){
            @Override
            public Tetromino answer(InvocationOnMock invocation) {
                return expectedTetrominos.poll();
            }
        });

        when(gameBoardViewMock.shouldGetAnotherRandomTetromino()).thenCallRealMethod();
        doCallRealMethod().when(gameBoardViewMock).setUpCurrentAndNextTetrominos();

        gameBoardViewMock.setUpCurrentAndNextTetrominos();
        assertFalse("It is the first and should return false", gameBoardViewMock.shouldGetAnotherRandomTetromino());
        
        gameBoardViewMock.setUpCurrentAndNextTetrominos();
        assertTrue("Three Os in a row", gameBoardViewMock.shouldGetAnotherRandomTetromino());
        
        gameBoardViewMock.setUpCurrentAndNextTetrominos();
        assertTrue("Four Os in a row", gameBoardViewMock.shouldGetAnotherRandomTetromino());
        
        gameBoardViewMock.setUpCurrentAndNextTetrominos();
        assertFalse("Should reset counter and return false", gameBoardViewMock.shouldGetAnotherRandomTetromino());
        
        gameBoardViewMock.setUpCurrentAndNextTetrominos();
        assertFalse("Should still return false", gameBoardViewMock.shouldGetAnotherRandomTetromino());
        
        gameBoardViewMock.setUpCurrentAndNextTetrominos();
        assertTrue("Three Ts in a row", gameBoardViewMock.shouldGetAnotherRandomTetromino());
    }
    
    private Queue<Tetromino> buildTestTetrominos(GameBoardView gameBoardView) {
        Queue<Tetromino> tetrominos = new LinkedList<Tetromino>();
        tetrominos.add(new Tetromino.Builder(gameBoardView)
            .use(DefaultShape.O)
            .build());
        tetrominos.add(new Tetromino.Builder(gameBoardView)
            .use(DefaultShape.O)
            .build());
        tetrominos.add(new Tetromino.Builder(gameBoardView)
            .use(DefaultShape.O)
            .build());
        tetrominos.add(new Tetromino.Builder(gameBoardView)
            .use(DefaultShape.O)
            .build());
        tetrominos.add(new Tetromino.Builder(gameBoardView)
            .use(DefaultShape.T)
            .build());
        tetrominos.add(new Tetromino.Builder(gameBoardView)
            .use(DefaultShape.T)
            .build());
        tetrominos.add(new Tetromino.Builder(gameBoardView)
            .use(DefaultShape.T)
            .build());
        return tetrominos;
    }
}