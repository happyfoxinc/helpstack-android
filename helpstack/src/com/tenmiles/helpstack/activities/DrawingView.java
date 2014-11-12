package com.tenmiles.helpstack.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static java.lang.StrictMath.max;
import static java.lang.StrictMath.min;


public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint;
    private Canvas drawCanvas;

    private Paint canvasPaint;
    private Bitmap canvasBitmap;

    private Bitmap cachedBitmap;

    private int paintColor = 0xFFF44336;
    private int brushSize = 20;
    private int resizedWidth;
    private int resizedHeight;
    private boolean isEdited;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(paintColor);

        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setCanvasBitmap(Bitmap bitmap) {
        cachedBitmap = bitmap;
        drawCanvas.drawColor(Color.BLACK);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        calculateResizeRatio(bitmap, drawCanvas);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(mutableBitmap, resizedWidth, resizedHeight, false);

        int leftStart = getLeftStart(resizedWidth);
        int topStart = getTopStart(resizedHeight);

        drawCanvas.drawBitmap(resizedBitmap, leftStart, topStart, drawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                setIsEdited(true);
                drawPath.reset();
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
        drawPaint.setColor(paintColor);
    }

    public void clearChanges() {
        drawPath.reset();
        setCanvasBitmap(cachedBitmap);
        setIsEdited(false);
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
}
