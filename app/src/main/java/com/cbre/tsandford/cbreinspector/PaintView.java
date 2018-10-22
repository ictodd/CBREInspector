package com.cbre.tsandford.cbreinspector;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class PaintView extends View {

    public enum BrushSize{
        Small,
        Medium,
        Large
    }

    public enum BrushColour{
        Black,
        Blue,
        Red,
        White
    }

    public static int BRUSH_SIZE = 5;
    public static int ERASER_SIZE = 60;

    public static final int DEFAULT_COLOR = Color.BLACK;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint_dither = new Paint(Paint.DITHER_FLAG);
    private Paint mBitmapPaint_filter = new Paint(Paint.FILTER_BITMAP_FLAG);
    private Bitmap mBitmapBackground;
    private Matrix origin = new Matrix();

    private DisplayMetrics metrics;


    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
    }

    public void init(DisplayMetrics metrics) {

        this.metrics = metrics;

        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
    }

    public void clear() {
        setDrawingCacheEnabled(false);
        mBitmapBackground = null;
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        invalidate();
    }


    public void setNewBrushSize(BrushSize brushSize){
        if(eraserMode){
            this.currentColor = oldColour;
            this.strokeWidth = oldStrokeWidth;
            eraserMode = false;
        }
        switch(brushSize){
            case Small:
                this.strokeWidth = 5;
                break;
            case Medium:
                this.strokeWidth = 12;
                break;
            case Large:
                this.strokeWidth = 20;
                break;
        }
    }

    public void setNewBrushColour(BrushColour brushColour){
        if(eraserMode){
            this.currentColor = oldColour;
            this.strokeWidth = oldStrokeWidth;
            eraserMode = false;
        }
        switch(brushColour){
            case Black:
                this.currentColor = Color.BLACK;
                break;
            case Red:
                this.currentColor = Color.RED;
                break;
            case Blue:
                this.currentColor = Color.BLUE;
                break;
            case White:
                this.currentColor = Color.WHITE;
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        if(mBitmapBackground != null)
            mCanvas.drawBitmap(mBitmapBackground, origin, mBitmapPaint_filter);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);
            mCanvas.drawPath(fp.path, mPaint);
        }

        canvas.drawBitmap(mBitmap, origin, mBitmapPaint_dither);
        canvas.restore();
        setDrawingCacheEnabled(true);
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        FingerPath fp = new FingerPath(currentColor, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN :
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE :
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP :
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    static boolean eraserMode = false;
    int oldStrokeWidth;
    int oldColour;
    public void SetEraserMode(){

        oldStrokeWidth = this.strokeWidth;
        oldColour = this.currentColor;

        this.strokeWidth = ERASER_SIZE;
        this.currentColor = Color.WHITE;
        eraserMode = true;
    }

    public void SetHighlighterMode(){
        mPaint.setAlpha(50);
        this.currentColor = Color.YELLOW;
    }

    public Bitmap getCurrentImage(){
        Bitmap result = getDrawingCache();
        return result;
    }

    public void loadBitmapToCanvas(Bitmap bitmap){
        clear();
        if(this.mBitmapBackground != null){
            this.mBitmapBackground.recycle();
            this.mBitmapBackground = null;
        }
        this.mBitmapBackground = bitmap;
    }

    private Bitmap overlayBitmaps(Bitmap background, Bitmap foreground){
        Bitmap overlaidResult = Bitmap.createBitmap( metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlaidResult);

        canvas.drawBitmap(foreground, new Matrix(), null);
        canvas.drawBitmap(background, new Matrix(), null);

        return  overlaidResult;
    }
}
