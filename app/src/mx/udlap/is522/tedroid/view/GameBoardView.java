package mx.udlap.is522.tedroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import mx.udlap.is522.tedroid.view.model.DefaultShape;
import mx.udlap.is522.tedroid.view.model.Direction;
import mx.udlap.is522.tedroid.view.model.Tetromino;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tablero del juego donde los tetrominos se acumlan.
 * 
 * @author Daniel Pedraza-Arcega
 * @since 1.0
 */
public class GameBoardView extends View {

    private static final float MOVE_SENSITIVITY = 3.5f;
    private static final long DEFAULT_SPEED = 500l;
    private static final int DROPDOWN_FACTOR = 4;
    private static final int DEFAULT_COLUMNS = 10;
    private static final int DEFAULT_ROWS = 20;
    private static final String TAG = GameBoardView.class.getSimpleName();

    private List<Tetromino> tetrominos;
    private Tetromino currentTetromino;
    private Tetromino nextTetromino;
    private NextTetrominoView nextTetrominoView;
    private int[][] boardMatrix;
    private int rows;
    private int columns;
    private long speed;
    private float width;
    private float height;
    private boolean startDropingTetrominos;
    private boolean isPaused;
    private GestureDetector gestureDetector;
    private MoveDownCurrentTetrominoTask moveDownCurrentTetrominoTask;
    private Paint background;

    /**
     * Construye un tablero de juego.
     * 
     * @see android.view.View#View(Context)
     */
    public GameBoardView(Context context) {
        super(context);
        setUp();
    }

    /**
     * Construye un tablero de juego mediante XML
     * 
     * @see android.view.View#View(Context, AttributeSet)
     */
    public GameBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
    }

    /**
     * Construye un tablero de juego mediante XML y aplicando un estilo.
     * 
     * @see android.view.View#View(Context, AttributeSet, int)
     */
    public GameBoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w / ((float) boardMatrix[0].length);
        height = h / ((float) boardMatrix.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!startDropingTetrominos) {
            startDropingTetrominos = true;
            stopDropingTaskIfNeeded();
            currentTetromino = getNextTetromino();
            nextTetromino = getNextTetromino();
            if (nextTetrominoView != null) nextTetrominoView.setTetromino(nextTetromino);
            tetrominos.add(currentTetromino);
            startDropingTask(speed);
        }

        drawTetrominos(canvas);
        drawBackground(canvas);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                stopDropingTaskIfNeeded();
                startDropingTask(speed);
                return true;
            default: return super.onTouchEvent(event);
        }
    }

    /**
     * Inicializa el layout de este tablero.
     */
    protected void setUp() {
        if (rows == 0 || columns == 0) {
            rows = DEFAULT_ROWS;
            columns = DEFAULT_COLUMNS;
        }

        if (speed == 0) speed = DEFAULT_SPEED;

        boardMatrix = new int[rows][columns];
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
        tetrominos = new ArrayList<Tetromino>();
        isPaused = false;
        background = new Paint();
        background.setStyle(Paint.Style.STROKE);
        background.setStrokeWidth(2);
        background.setColor(getContext().getResources().getColor(android.R.color.black));
    }

    /**
     * Dibuja el fondo del tablero.
     * 
     * @param canvas un canvas para dibujar.
     */
    protected void drawBackground(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);
    }

    /**
     * Dibuja todos los tetrominos que se han acumulado.
     * 
     * @param canvas un canvas para dibujar.
     */
    protected void drawTetrominos(Canvas canvas) {
        for (Tetromino t : tetrominos) {
            t.drawOnParentGameBoardView(canvas);
        }
    }

    /**
     * Actualiza la matriz del tablero con los valores del tetromino actual.
     */
    protected void updateBoardMatrix() {
        for (int row = 0; row < currentTetromino.getShapeMatrix().length; row++) {
            for (int column = 0; column < currentTetromino.getShapeMatrix()[0].length; column++) {
                int boardMatrixRow = currentTetromino.getPositionOnBoard().getY() + row;
                int boardMatrixColumn = currentTetromino.getPositionOnBoard().getX() + column;
                boardMatrix[boardMatrixRow][boardMatrixColumn] = currentTetromino.getShapeMatrix()[row][column];
            }
        }
    }

    /**
     * Detiene la caida del tetromino actual si esta callendo.
     */
    protected void stopDropingTaskIfNeeded() {
        if (moveDownCurrentTetrominoTask != null && moveDownCurrentTetrominoTask.getStatus() == AsyncTask.Status.RUNNING) {
            moveDownCurrentTetrominoTask.cancel(true);
            moveDownCurrentTetrominoTask = null;
        }
    }

    /**
     * Inicia la caida del tetromino actual.
     */
    protected void startDropingTask(long speed) {
        moveDownCurrentTetrominoTask = new MoveDownCurrentTetrominoTask();
        moveDownCurrentTetrominoTask.execute(speed);
    }

    /**
     * @return tetromino escogiendo al azar una de las figuras predefinadas.
     */
    protected Tetromino getNextTetromino() {
        int randomIndex = new Random().nextInt(DefaultShape.values().length);
        DefaultShape randomShape = DefaultShape.values()[randomIndex];
        return new Tetromino.Builder(this)
            .use(randomShape)
            .build();
    }

    public void restartGame() {
        stopDropingTaskIfNeeded();
        boardMatrix = new int[rows][columns];
        tetrominos = new ArrayList<Tetromino>();
        startDropingTetrominos = false;
        isPaused = false;
        invalidate();
    }

    /**
     * @return la matriz del tablero.
     */
    public int[][] getBoardMatrix() {
        return boardMatrix;
    }

    /**
     * @return la altura del tablero.
     */
    public float getBoardHeight() {
        return height;
    }

    /**
     * @return la anchura del tablero.
     */
    public float getBoardWidth() {
        return width;
    }

    /**
     * @return si el juego esta pausado o no
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Pausa el juego.
     */
    public void pauseGame() {
        isPaused = true;
    }

    /**
     * Reanuda el juego.
     */
    public void resumeGame() {
        isPaused = false;
    }

    /**
     * Detiene el juego y no se podrá reinciar más
     */
    public void stopGame() {
        stopDropingTaskIfNeeded();
    }

    /**
     * Inicializa las dimensiones del tablero de juego.
     * 
     * @param rows cuantas filas.
     * @param columns cuantas columnas.
     */
    public void setCustomDimensions(int rows, int columns) {
        boardMatrix = new int[rows][columns];
        this.rows = rows;
        this.columns = columns;
    }

    /**
     * Inicializa la velocidad del juego
     * 
     * @param speed velociadad en milisegundos.
     */
    public void setSpeed(long speed) {
        this.speed = speed;
    }

    /**
     * Inicializa la vista para el siguiente tetromino en caer.
     * 
     * @param nextTetrominoView la vista para el siguiente tetromino en caer.
     */
    public void setNextTetrominoView(NextTetrominoView nextTetrominoView) {
        this.nextTetrominoView = nextTetrominoView;
    }

    /**
     * Tarea que lleva la cuenta de la velocidad de caida del tetromino en
     * juego.
     * 
     * @author Daniel Pedraza-Arcega
     * @since 1.0
     */
    private class MoveDownCurrentTetrominoTask extends AsyncTask<Long, Void, Void> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground(Long... params) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(params[0]);
                    publishProgress();
                } catch (InterruptedException e) { }
            }

            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onProgressUpdate(Void... values) {
            if (!isPaused) {
                if (!currentTetromino.moveTo(Direction.DOWN)) {
                    Log.d(TAG, "New randow tetromino");
                    updateBoardMatrix();
                    currentTetromino = nextTetromino;
                    nextTetromino = getNextTetromino();
                    if (nextTetrominoView != null)
                        nextTetrominoView.setTetromino(nextTetromino);
                    tetrominos.add(currentTetromino);
                } else {
                    Log.d(TAG, "Move down tetromino");
                }
                
                invalidate();
            }
        }
    }

    /**
     * Escucha los eventos del tablero para mover el tetromino en juego.
     * 
     * @author Daniel Pedraza-Arcega
     * @since 1.0
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            stopDropingTaskIfNeeded();
            startDropingTask(speed / DROPDOWN_FACTOR);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (distanceX < -MOVE_SENSITIVITY) {
                Log.d(TAG, "Move tetromino to the right");
                currentTetromino.moveTo(Direction.RIGHT);
                invalidate();
                return true;
            } else if (distanceX > MOVE_SENSITIVITY) {
                Log.d(TAG, "Move tetromino to the left");
                currentTetromino.moveTo(Direction.LEFT);
                invalidate();
                return true;
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "Rotate tetromino");
            currentTetromino.rotate();
            invalidate();
            return true;
        }
    }
}