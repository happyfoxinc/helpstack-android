package com.tenmiles.helpstack.theme.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.StrictMath.min;

public class DrawingView extends View {

    private Path mPath;
    private Paint mPaint;
    private Canvas mCanvas;

    private Paint canvasPaint;
    private Bitmap canvasBitmap;

    private Bitmap cachedBitmap;

    private int paintColor = 0xFFF44336;
    private int brushSize = 20;
    private int resizedWidth;
    private int resizedHeight;
    private boolean isEdited;

    private ObserverInterface mObserver;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPath = new Path();
        mPaint = new Paint();

        mPaint.setColor(paintColor);

        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(brushSize);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setCanvasBitmap(Bitmap bitmap) {
        cachedBitmap = bitmap;
        mCanvas.drawColor(Color.BLACK);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        calculateResizeRatio(bitmap, mCanvas);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(mutableBitmap, resizedWidth, resizedHeight, false);

        int leftStart = getLeftStart(resizedWidth);
        int topStart = getTopStart(resizedHeight);

        mCanvas.drawBitmap(resizedBitmap, leftStart, topStart, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(mPath, mPaint);
                setIsEdited(true);
                mPath.reset();
                activateClearOptionInActivity(true);
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        invalidate();

        paintColor = Color.parseColor(newColor);
        mPaint.setColor(paintColor);
    }

    public void clearChanges() {
        mPath.reset();
        setCanvasBitmap(cachedBitmap);
        setIsEdited(false);
        activateClearOptionInActivity(false);
        invalidate();
    }

    private void setIsEdited(boolean value) {
        isEdited = value;
    }

    public boolean hasBeenEdited() {
        return isEdited;
    }

    private void calculateResizeRatio(Bitmap bitmap, Canvas drawCanvas) {
        double resizeRatio = min((double)drawCanvas.getWidth()/bitmap.getWidth(), (double)drawCanvas.getHeight()/bitmap.getHeight());
        resizedWidth = (int) (resizeRatio * bitmap.getWidth());
        resizedHeight = (int) (resizeRatio * bitmap.getHeight());
    }

    private int getTopStart(int resizedHeight) {
        int topStart = (this.getTop() + this.getBottom() - resizedHeight) >> 1;
        return topStart;
    }

    private int getLeftStart(int resizedWidth) {
        int leftStart = (this.getLeft() + this.getRight() - resizedWidth) >> 1;
        return leftStart;
    }

    public void setObserver(ObserverInterface observer){
        mObserver = observer;
    }

    private void activateClearOptionInActivity(boolean isEdited){
        if( mObserver != null ){
            mObserver.activateClearOption(isEdited);
        }
    }

    public interface ObserverInterface {
        void activateClearOption(boolean isEdited);
    }
}
