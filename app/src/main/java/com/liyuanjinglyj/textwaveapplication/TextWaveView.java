package com.liyuanjinglyj.textwaveapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class TextWaveView extends View {
    private Paint paint;//画笔工具
    private Path path;//路径
    private int waveLength=200;//波长
    private int dx=0,dy=0;//记录动画位置
    private Bitmap srcBitmap,dstBitmap;//源图像，目标图像
    public TextWaveView(Context context) {
        super(context);
    }

    public TextWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.paint=new Paint();
        this.path=new Path();
        this.paint.setColor(Color.GREEN);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.srcBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.text_name);
        this.dstBitmap=Bitmap.createBitmap(this.srcBitmap.getWidth(),this.srcBitmap.getHeight(),Bitmap.Config.ARGB_8888);
        this.startAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWavePath();//绘制波纹
        Canvas c=new Canvas(this.dstBitmap);
        c.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        c.drawPath(this.path,this.paint);//将波纹绘制到目标图像上
        canvas.drawBitmap(this.srcBitmap,0,0,this.paint);//绘制源图像
        //离屏绘制的代码在canvas.save与canvas.restoreToCount中间
        int layerId=canvas.saveLayer(0,0,getWidth(),getHeight(),null,Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(this.dstBitmap,0,0,this.paint);//首先绘制目标图像
        this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//设置画笔的图像模式
        canvas.drawBitmap(this.srcBitmap,0,0,this.paint);//绘制源图像
        this.paint.setXfermode(null);//设置画笔的图像模式为空
        canvas.restoreToCount(layerId);
    }

    /***
     * 绘制波浪
     */
    private void drawWavePath(){
        this.path.reset();//清除之前绘制的path
        int originY=this.srcBitmap.getHeight()/2;
        int halfLength=this.waveLength/2;
        //设置波浪起始点，加上动画的偏移量，形成动画效果
        this.path.moveTo(-this.waveLength+dx,originY-dy);
        //循环设置整个屏幕+waveLength长的波浪
        for(int i=-this.waveLength;i<=getWidth()+this.waveLength;i+=this.waveLength){
            //两句代码组成一个波长，中间到波峰在到中间是第一个rQuadTo,中间到波谷到中间是第二rQuadTo
            this.path.rQuadTo(halfLength/2,-50,halfLength,0);
            this.path.rQuadTo(halfLength/2,50,halfLength,0);
        }
        //闭合区域
        this.path.lineTo(this.srcBitmap.getWidth(),this.srcBitmap.getHeight());
        this.path.lineTo(0,this.srcBitmap.getHeight());
        this.path.close();
    }

    /***
     * 动画初始化
     */
    private void startAnim(){
        ValueAnimator waveAnim=ValueAnimator.ofInt(0,this.waveLength);//波浪动画，一个波长
        waveAnim.setDuration(2000);//动画事件2秒
        waveAnim.setRepeatCount(ValueAnimator.INFINITE);//无限循环
        waveAnim.setInterpolator(new LinearInterpolator());//匀速变化，插值器，可以加速，减速以及自定义
        waveAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dx=(Integer)animation.getAnimatedValue();//获取动画进度
                postInvalidate();//重绘
            }
        });
        //覆盖文字动画
        ValueAnimator CoverAnim=ValueAnimator.ofInt(0,this.waveLength/4,this.waveLength/4,this.waveLength/3,this.waveLength/3,this.waveLength/2,this.waveLength/2,this.waveLength);
        CoverAnim.setDuration(5000);//动画事件为5秒
        CoverAnim.setRepeatCount(ValueAnimator.INFINITE);//无线循环
        CoverAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                dy=(Integer)animation.getAnimatedValue();//获取动画进度
                postInvalidate();//重绘
            }
        });
        //开始动画
        waveAnim.start();
        CoverAnim.start();
    }

    public TextWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
