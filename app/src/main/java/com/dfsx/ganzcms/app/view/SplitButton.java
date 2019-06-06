package com.dfsx.ganzcms.app.view;

/**
 * Created by heyang on 2015/3/27.
 */

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.dfsx.core.common.Util.Util;
import com.dfsx.ganzcms.app.R;


public class SplitButton extends View
        implements OnTouchListener
{
    private Rect Btn_Off;
    private Rect Btn_On;
    private OnChangedListener ChgLsn;
    private float DownX;
    private boolean NowChoose = false;
    private float NowX;
    private boolean OnSlip = false;
    private Bitmap bg_off;
    private Bitmap bg_on;
    private boolean isChecked;
    private boolean isChgLsnOn = false;
    private Bitmap slip_btn;

    public SplitButton(Context paramContext)
    {
        super(paramContext);
        init();
    }

    public SplitButton(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        init();
    }

    public SplitButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h)
    {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    private void init()
    {
       // BitmapFactory.decodeResource(getResources())
        this.bg_on = BitmapFactory.decodeResource(getResources(), R.drawable.split_left_1);
        this.bg_off = BitmapFactory.decodeResource(getResources(), R.drawable.split_right_1);
        this.slip_btn = BitmapFactory.decodeResource(getResources(), R.drawable.split_1);
        this.slip_btn=resizeImage(this.slip_btn, Util.dp2px(getContext(), 30),Util.dp2px(getContext(),29));
        this.bg_on=resizeImage(this.bg_on, Util.dp2px(getContext(), 55),Util.dp2px(getContext(),30));
        this.bg_off =resizeImage(this.bg_off,Util.dp2px(getContext(),55),Util.dp2px(getContext(),30));
        this.Btn_On = new Rect(0, 0, this.slip_btn.getWidth(), this.slip_btn.getHeight());
        this.Btn_Off = new Rect(this.bg_off.getWidth() - this.slip_btn.getWidth(), 0, this.bg_off.getWidth(), this.slip_btn.getHeight());
        setOnTouchListener(this);
    }

    public void SetOnChangedListener(OnChangedListener paramOnChangedListener)
    {
        this.isChgLsnOn = true;
        this.ChgLsn = paramOnChangedListener;
    }

    protected void onDraw(Canvas paramCanvas)
    {
        super.onDraw(paramCanvas);
        Matrix localMatrix = new Matrix();
        Paint localPaint = new Paint();
        if (this.NowX >= this.bg_on.getWidth() / 2)
        {
        //    (this.bg_on.getWidth() - this.slip_btn.getWidth() / 2);
            paramCanvas.drawBitmap(this.bg_on, localMatrix, localPaint);
        }
        else
        {
       //     (this.NowX - this.slip_btn.getWidth() / 2);
            paramCanvas.drawBitmap(this.bg_off, localMatrix, localPaint);
        }
        float f;
        if (!this.OnSlip)
        {
            if (!this.NowChoose)
            {
                f = this.Btn_On.left;
            }
            else
            {
                f = this.Btn_Off.left;
                paramCanvas.drawBitmap(this.bg_on, localMatrix, localPaint);
            }
        }
        else if (this.NowX < this.bg_on.getWidth())
        {
            if (this.NowX >= 0.0F)
                f = this.NowX - this.slip_btn.getWidth() / 2;
            else
                f = 0.0F;
        }
        else
            f = this.bg_on.getWidth() - this.slip_btn.getWidth() / 2;
        if (this.isChecked)
        {
            paramCanvas.drawBitmap(this.bg_on, localMatrix, localPaint);
            f = this.Btn_Off.left;
            boolean bool;
            if (!this.isChecked)
                bool = true;
            else
                bool = false;
            this.isChecked = bool;
        }
        if (f >= 0.0F)
        {
            if (f > this.bg_on.getWidth() - this.slip_btn.getWidth())
                f = this.bg_on.getWidth() - this.slip_btn.getWidth();
        }
        else
            f = 0.0F;
        paramCanvas.drawBitmap(this.slip_btn, f, 0.0F, localPaint);
    }

    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
        boolean bool1 = false;
        boolean bool2;
        switch (paramMotionEvent.getAction())
        {
            case 0:
                if ((paramMotionEvent.getX() > this.bg_on.getWidth()) || (paramMotionEvent.getY() > this.bg_on.getHeight()))
                    break ;
                this.OnSlip = true;
                this.DownX = paramMotionEvent.getX();
                this.NowX = this.DownX;
                break;
            case 1:
                this.OnSlip = bool1;
                bool2 = this.NowChoose;
                if (paramMotionEvent.getX() < this.bg_on.getWidth() / 2)
                {
                    this.NowX -= this.slip_btn.getWidth() / 2;
                    this.NowChoose = bool1;
                }
                else
                {
                    this.NowX = (this.bg_on.getWidth() - this.slip_btn.getWidth() / 2);
                    this.NowChoose = true;
                }
                if ((!this.isChgLsnOn) || (bool2 == this.NowChoose))
                    break;
                this.ChgLsn.OnChanged(this.NowChoose);
                break;
            case 2:
                this.NowX = paramMotionEvent.getX();
                break;
            case 3:
                this.OnSlip = bool1;
                bool2 = this.NowChoose;
                if (this.NowX < this.bg_on.getWidth() / 2)
                {
                    this.NowX -= this.slip_btn.getWidth() / 2;
                    this.NowChoose = bool1;
                }
                else
                {
                    this.NowX = (this.bg_on.getWidth() - this.slip_btn.getWidth() / 2);
                    this.NowChoose = true;
                }
                if ((!this.isChgLsnOn) || (bool2 == this.NowChoose))
                    break;
                this.ChgLsn.OnChanged(this.NowChoose);
        }
        invalidate();
        bool1 = true;
        label336: return bool1;
    }

    public void setCheck(boolean paramBoolean)
    {
        this.isChecked = paramBoolean;
        this.NowChoose = paramBoolean;
    }

    public static abstract interface OnChangedListener
    {
        public abstract void OnChanged(boolean paramBoolean);
    }
}