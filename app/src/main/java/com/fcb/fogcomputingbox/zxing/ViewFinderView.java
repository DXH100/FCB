package com.fcb.fogcomputingbox.zxing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.blankj.utilcode.util.SizeUtils;
import com.fcb.fogcomputingbox.R;
import com.fcb.fogcomputingbox.UIUtils;

import me.dm7.barcodescanner.core.DisplayUtils;
import me.dm7.barcodescanner.core.IViewFinder;

public class ViewFinderView extends View implements IViewFinder {
    private static final String TAG = "ViewFinderView";
    private Rect mFramingRect;
    private static final int MIN_FRAME_WIDTH = 240;
    private static final int MIN_FRAME_HEIGHT = 240;
    private static final float LANDSCAPE_WIDTH_RATIO = 0.625F;
    private static final float LANDSCAPE_HEIGHT_RATIO = 0.625F;
    private static final int LANDSCAPE_MAX_FRAME_WIDTH = 1200;
    private static final int LANDSCAPE_MAX_FRAME_HEIGHT = 675;
    private static final float PORTRAIT_WIDTH_RATIO = 0.875F;
    private static final float PORTRAIT_HEIGHT_RATIO = 0.375F;
    private static final int PORTRAIT_MAX_FRAME_WIDTH = 945;
    private static final int PORTRAIT_MAX_FRAME_HEIGHT = 720;
    private static final int[] SCANNER_ALPHA = new int[]{0, 64, 128, 192, 255, 192, 128, 64};
    private int scannerAlpha;
    private static final int POINT_SIZE = 10;
    private static final long ANIMATION_DELAY = 80L;
    private final int mDefaultLaserColor;
    private final int mDefaultMaskColor;
    private final int mDefaultBorderColor;
    private  int mDefaultBorderStrokeWidth;
    protected Paint mLaserPaint;
    protected Paint mFinderMaskPaint;
    protected Paint mBorderPaint;
    protected Paint mLinePaint;
    protected int mBorderLineLength;
    protected int mDefaultLineColor;

    private Bitmap bmpLine;

    public ViewFinderView(Context context) {
        super(context);
        this.mDefaultLaserColor = this.getResources().getColor(R.color.viewfinder_laser);
        this.mDefaultMaskColor = this.getResources().getColor(R.color.viewfinder_mask);
        this.mDefaultBorderColor = this.getResources().getColor(R.color.red);
        this.mDefaultLineColor = this.getResources().getColor(R.color.viewfinder_line_color);


        this.mDefaultBorderStrokeWidth = this.getResources().getInteger(R.integer.viewfinder_border_width);
        this.mBorderLineLength = this.getResources().getInteger(R.integer.viewfinder_border_length);


        this.init();
    }

    public ViewFinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mDefaultLaserColor = this.getResources().getColor(R.color.viewfinder_laser);
        this.mDefaultMaskColor = this.getResources().getColor(R.color.viewfinder_mask);
        this.mDefaultBorderColor = this.getResources().getColor(R.color.color_them);
        this.mDefaultBorderStrokeWidth = this.getResources().getInteger(R.integer.viewfinder_border_width);
        this.mBorderLineLength = this.getResources().getInteger(R.integer.viewfinder_border_length);
        this.init();
    }

    private void init() {
        this.mLaserPaint = new Paint();
        this.mLaserPaint.setColor(this.mDefaultLaserColor);
        this.mLaserPaint.setStyle(Paint.Style.FILL);


        this.mFinderMaskPaint = new Paint();
        this.mFinderMaskPaint.setColor(this.mDefaultMaskColor);
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setColor(this.mDefaultBorderColor);
        this.mBorderPaint.setStyle(Paint.Style.FILL);
        mDefaultBorderStrokeWidth=8;
        this.mBorderPaint.setStrokeWidth((float) this.mDefaultBorderStrokeWidth);

        mLinePaint = new Paint();
        mLinePaint.setColor(mDefaultLineColor);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setStrokeWidth((float) this.mDefaultBorderStrokeWidth / 6.0f);
        mLinePaint.setFilterBitmap(true);

        x = 0;
        y = 0;
    }

    public void setLaserColor(int laserColor) {
        this.mLaserPaint.setColor(laserColor);
    }

    public void setMaskColor(int maskColor) {
        this.mFinderMaskPaint.setColor(maskColor);
    }

    public void setBorderColor(int borderColor) {
        this.mBorderPaint.setColor(borderColor);
    }

    public void setBorderStrokeWidth(int borderStrokeWidth) {
        this.mBorderPaint.setStrokeWidth((float) borderStrokeWidth);
    }

    private void setBorderLineLength(int borderLineLength) {
        this.mBorderLineLength = borderLineLength;
    }

    public void setupViewFinder() {
        this.updateFramingRect();
        this.invalidate();
    }

    public Rect getFramingRect() {
        return this.mFramingRect;
    }

    public void onDraw(Canvas canvas) {
        if (this.mFramingRect != null) {
            this.drawViewFinderMask(canvas);
            this.drawViewFinderBorder(canvas);
            this.drawLaser(canvas);
        }
    }

    public void drawViewFinderMask(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        canvas.drawRect(0.0F, 0.0F, (float) width, (float) this.mFramingRect.top, this.mFinderMaskPaint);
        canvas.drawRect(0.0F, (float) this.mFramingRect.top, (float) this.mFramingRect.left, (float) (this.mFramingRect.bottom + 1), this.mFinderMaskPaint);
        canvas.drawRect((float) (this.mFramingRect.right + 1), (float) this.mFramingRect.top, (float) width, (float) (this.mFramingRect.bottom + 1), this.mFinderMaskPaint);
        canvas.drawRect(0.0F, (float) (this.mFramingRect.bottom + 1), (float) width, (float) height, this.mFinderMaskPaint);
    }

    public void drawViewFinderBorder(Canvas canvas) {
//        mBorderLineLength=10;
        mBorderLineLength = SizeUtils.dp2px(14);
        float sub = mDefaultBorderStrokeWidth/2;
//        canvas.drawLine((float) (this.mFramingRect.left - sub), (float) (this.mFramingRect.top - sub), (float) (this.mFramingRect.left - sub), (float) (this.mFramingRect.top - sub + this.mBorderLineLength), this.mBorderPaint);
//        canvas.drawLine((float) (this.mFramingRect.left - sub), (float) (this.mFramingRect.top - sub), (float) (this.mFramingRect.left - sub + thi s.mBorderLineLength), (float) (this.mFramingRect.top - sub), this.mBorderPaint);
//        canvas.drawLine((float) (this.mFramingRect.left - sub), (float) (this.mFramingRect.bottom + sub), (float) (this.mFramingRect.left - sub), (float) (this.mFramingRect.bottom + sub - this.mBorderLineLength), this.mBorderPaint);
//        canvas.drawLine((float) (this.mFramingRect.left - sub), (float) (this.mFramingRect.bottom + sub), (float) (this.mFramingRect.left - sub + this.mBorderLineLength), (float) (this.mFramingRect.bottom + sub), this.mBorderPaint);
//        canvas.drawLine((float) (this.mFramingRect.right + sub), (float) (this.mFramingRect.top - sub), (float) (this.mFramingRect.right + sub), (float) (this.mFramingRect.top - sub + this.mBorderLineLength), this.mBorderPaint);
//        canvas.drawLine((float) (this.mFramingRect.right + sub), (float) (this.mFramingRect.top - sub), (float) (this.mFramingRect.right + sub - this.mBorderLineLength), (float) (this.mFramingRect.top - sub), this.mBorderPaint);
//        canvas.drawLine((float) (this.mFramingRect.right + sub), (float) (this.mFramingRect.bottom + sub), (float) (this.mFramingRect.right + sub), (float) (this.mFramingRect.bottom + sub - this.mBorderLineLength), this.mBorderPaint);
//        canvas.drawLine((float) (this.mFramingRect.right + sub), (float) (this.mFramingRect.bottom + sub), (float) (this.mFramingRect.right + sub - this.mBorderLineLength), (float) (this.mFramingRect.bottom + sub), this.mBorderPaint);

        canvas.drawLine(this.mFramingRect.left, this.mFramingRect.top, this.mFramingRect.left, this.mFramingRect.bottom, this.mLinePaint);
        canvas.drawLine(this.mFramingRect.left, this.mFramingRect.top, this.mFramingRect.right, this.mFramingRect.top, this.mLinePaint);
        canvas.drawLine(this.mFramingRect.left, this.mFramingRect.bottom, this.mFramingRect.right, this.mFramingRect.bottom, this.mLinePaint);
        canvas.drawLine(this.mFramingRect.right, this.mFramingRect.top, this.mFramingRect.right, this.mFramingRect.bottom, this.mLinePaint);


        canvas.drawLine(mFramingRect.left, mFramingRect.top, mFramingRect.left, mFramingRect.top + mBorderLineLength, mBorderPaint);
        canvas.drawLine(mFramingRect.left - sub, mFramingRect.top, mFramingRect.left + mBorderLineLength, mFramingRect.top, mBorderPaint);


        canvas.drawLine(mFramingRect.left, mFramingRect.bottom - mBorderLineLength, mFramingRect.left, mFramingRect.bottom, mBorderPaint);
        canvas.drawLine(mFramingRect.left - sub, mFramingRect.bottom, mFramingRect.left + mBorderLineLength, mFramingRect.bottom, mBorderPaint);

        canvas.drawLine(mFramingRect.right - mBorderLineLength, mFramingRect.top, mFramingRect.right, mFramingRect.top, mBorderPaint);
        canvas.drawLine(mFramingRect.right, mFramingRect.top - sub, mFramingRect.right, mFramingRect.top + mBorderLineLength, mBorderPaint);

        canvas.drawLine(mFramingRect.right - mBorderLineLength, mFramingRect.bottom, mFramingRect.right, mFramingRect.bottom, mBorderPaint);
        canvas.drawLine(mFramingRect.right, mFramingRect.bottom - mBorderLineLength, mFramingRect.right, mFramingRect.bottom + sub, mBorderPaint);


    }

    public void drawLaser(Canvas canvas) {
//        this.mLaserPaint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
//        this.scannerAlpha = (this.scannerAlpha + guide1) % SCANNER_ALPHA.length;
//        int middle = this.mFramingRect.height() / guide2 + this.mFramingRect.top;
//        canvas.drawRect((float) (this.mFramingRect.left + guide2), (float) (middle - guide1), (float) (this.mFramingRect.right - guide1), (float) (middle + guide2), this.mLaserPaint);
//        this.postInvalidateDelayed(80L, this.mFramingRect.left - 10, this.mFramingRect.top - 10, this.mFramingRect.right + 10, this.mFramingRect.bottom + 10);
        if (bmpLine == null || mFramingRect == null) {
            return;
        }
        if (x == 0 ) {
            x = mFramingRect.centerX() - (bmpLine.getWidth()) / 2;

        }
        if ( y == 0){
            y = mFramingRect.top + bmpLine.getHeight();
        }

        y += 5.5f;
        if (y >= mFramingRect.bottom - bmpLine.getHeight()) {
            y = mFramingRect.top + bmpLine.getHeight();
        }
        canvas.drawBitmap(bmpLine, x, y, mLinePaint);
        invalidate();
    }

    private float x = 0, y = 0;

    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        this.updateFramingRect();
        if (bmpLine == null && mFramingRect != null) {
            bmpLine = BitmapFactory.decodeResource(getResources(), R.drawable.icon_scan_line);
            Matrix m = new Matrix();
            float sx = (mFramingRect.right - mFramingRect.left- UIUtils.dp2Px(34)+0.5f) / (bmpLine.getWidth()+0.5f);
            m.postScale(sx, 1);
            bmpLine = Bitmap.createBitmap(bmpLine, 0, 0, bmpLine.getWidth(), bmpLine.getHeight(), m, true);
        }

    }

    public synchronized void updateFramingRect() {
        Point viewResolution = new Point(this.getWidth(), this.getHeight());
        int orientation = DisplayUtils.getScreenOrientation(this.getContext());
        int width;
        int height;
        if (orientation != 1) {
            width = findDesiredDimensionInRange(0.625F, viewResolution.x, 240, 1200);
            height = findDesiredDimensionInRange(0.625F, viewResolution.y, 240, 675);
        } else {
            width = findDesiredDimensionInRange(0.675F, viewResolution.x, 240, 945);
            height = findDesiredDimensionInRange(0.375F, viewResolution.y, 240, 720);
        }

        int leftOffset =  (viewResolution.x - width) / 2;
        int topOffset = (viewResolution.y - height) / 2;
        this.mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
    }

    private static int findDesiredDimensionInRange(float ratio, int resolution, int hardMin, int hardMax) {
        int dim = (int) (ratio * (float) resolution);
        return dim < hardMin ? hardMin : (dim > hardMax ? hardMax : dim);
    }
}