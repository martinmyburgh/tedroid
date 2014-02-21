package mx.udlap.is522.tedroid.view;

import java.util.ArrayList;
import java.util.List;

import mx.udlap.is522.tedroid.view.model.Tetromino;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Tablero del juego donde los tetrominos se acumlan.
 * 
 * @author Daniel Pedraza-Arcega
 * @since 1.0
 */
public class GameBoardView extends View {

    private static final int DEFAULT_DIMENSIONS = 20;
    
    private List<Tetromino> tetrominos;
    private Tetromino curretnTetromino;
    private int[][] boardMatrix;

    /**
     * Construye un tablero de juego.
     * @see android.view.View#View(Context)
     */
    public GameBoardView(Context context) {
	super(context);
	setUpLayout();
    }

    /**
     * Construye un tablero de juego mediante XML
     * @see android.view.View#View(Context, AttributeSet)
     */
    public GameBoardView(Context context, AttributeSet attrs) {
	super(context, attrs);
	setUpLayout();
    }

    /**
     * Construye un tablero de juego mediante XML y aplicando un estilo.
     * @see android.view.View#View(Context, AttributeSet, int)
     */
    public GameBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
	super(context, attrs, defStyleAttr);
	setUpLayout();
    }

    /**
     * Inicializa el layout de este tablero.
     */
    protected void setUpLayout() {
	// TODO: crear matrix por medio de setters o atributo xml
	boardMatrix = new int[DEFAULT_DIMENSIONS][DEFAULT_DIMENSIONS];
	
	tetrominos = new ArrayList<Tetromino>();
	curretnTetromino = new Tetromino.Builder(this)
		.useRandomDefaultShape()
		.build();
	
	// TODO: implementar GameBoardView.setUpLayout()
    }
    
    public int[][] getBoardMatrix() {
	return boardMatrix;
    }
}