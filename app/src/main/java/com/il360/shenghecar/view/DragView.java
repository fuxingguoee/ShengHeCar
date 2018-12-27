package com.il360.shenghecar.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

public class DragView extends View {
    int lastX;
    int lastY;
    Scroller mscroller;
    public DragView(Context context)
    {
        super(context);
        initView(context);
    }
    public DragView(Context context, AttributeSet attributeSet)
    {
        super(context,attributeSet);
        initView(context);
    }
    public DragView(Context context,AttributeSet attributeSet,int defStyleAttr)
    {
        super(context,attributeSet,defStyleAttr);
        initView(context);
    }

    private void initView(Context context)
    {
        setBackgroundColor(Color.BLUE);
        mscroller=new Scroller(context);
    }
    @Override
    public void computeScroll(){
        super.computeScroll();
        if(mscroller.computeScrollOffset())
        {
            ((View)getParent()).scrollTo(mscroller.getCurrX(),mscroller.getCurrY());
            invalidate();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int x=(int )event.getX();
        int y=(int)event.getY();
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                lastX=x;
                lastY=y;
                break;

            case MotionEvent.ACTION_MOVE:
                int offsetX=x-lastX;
//                int offsetY=y-lastY;
//                ((View)getParent()).scrollBy(-offsetX,-offsetY);
                ((View)getParent()).scrollBy(-offsetX,0);
                break;
            case MotionEvent.ACTION_UP:
//                View viewGroup=(View)getParent();
//                mscroller.startScroll(viewGroup.getScrollX(),viewGroup.getScrollY(),-viewGroup.getScrollX(),-viewGroup.getScrollY());
                invalidate();
                break;
        }
        return true;
    }
}
