package com.layer.atlas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.FrameLayout;


/**
 * @author Oleg Orlov
 * @since 8 May 2015
 */
public class ShapedFrameLayout extends FrameLayout {

    private static final String TAG = ShapedFrameLayout.class.getSimpleName();
    private static final boolean debug = true;
    
    private float[] corners = new float[] { 0, 0, 0, 0 };
    private boolean refreshShape = true;
    private Path shaper = new Path();
    
    private RectF pathRect = new RectF();
    
    public ShapedFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapedFrameLayout(Context context) {
        super(context);
    }

    public ShapedFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCornersDp(float[] cornerRadii) {
        System.arraycopy(cornerRadii, 0, this.corners, 0, 4);
        refreshShape = true;
    }
    
    public void setCornerRadiusDp(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        this.corners[0] = topLeft;
        this.corners[1] = topRight;
        this.corners[2] = bottomRight;
        this.corners[3] = bottomLeft;
    }
    
    public boolean drawCalledFrom = false;
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        // clipPath according to shape
        
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (android.os.Build.VERSION.SDK_INT < 18) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            if (debug)  Log.d(TAG, "dispatchDraw() size: " + width + "x" + height + " software rendering...");
        } else {
            if (debug)  Log.d(TAG, "dispatchDraw() size: " + width + "x" + height);
        }
        //if (debug && drawCalledFrom) Log.d(TAG, "dispatchDraw() from:" + Log.printStackTrace());
        
        if (refreshShape) {
            shaper.reset();
            pathRect = new RectF(0, 0, width, height);
            float[] roundRectRadii = roundRectRadii(corners);
            shaper.addRoundRect(pathRect, roundRectRadii,  Direction.CW);
            
            refreshShape = false;
        }
        
        int saved = canvas.save();
        canvas.clipPath(shaper);
        
        super.dispatchDraw(canvas);
        
        canvas.restoreToCount(saved);
    }
    
    
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshShape = true;
    }

    private float[] roundRectRadii(float[] cornerRadiusDp) {
        float[] result = new float[8];
        for (int i = 0; i < cornerRadiusDp.length; i++) {
            result[i * 2] = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cornerRadiusDp[i], getResources().getDisplayMetrics());
            result[i * 2 + 1] = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, cornerRadiusDp[i], getResources().getDisplayMetrics());
        }
        return result;
    }

}
