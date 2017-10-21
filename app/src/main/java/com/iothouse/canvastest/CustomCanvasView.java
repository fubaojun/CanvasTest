package com.iothouse.canvastest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

/**
 * Created by bjfu on 2017/10/19.
 */
public class CustomCanvasView extends View {
    private static final String TAG = CustomCanvasView.class.getName();
    private Paint mPaint;

    private float mRadius;
    private int PostThread = 0;

    private Canvas mCanvas;

    private float mHours;
    private float mMinutes;
    private float mSeconds;

    private Thread mThread;

    public CustomCanvasView(Context context) {
        super(context);
        Log.e(TAG,"--CustomCanvasView-default-1");
        //this(context, 50);
    }

    public CustomCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.e(TAG,"--CustomCanvasView-default-2");
        CustomCanvasViewinit(context, 380);
    }

    public CustomCanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.e(TAG,"--CustomCanvasView-default-3");
        //this(context, 50);
    }

    public CustomCanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.e(TAG,"--CustomCanvasView-default-4");
        //this(context, 50);
    }

    public void CustomCanvasViewinit(Context context, float radius) {
        //super(context);
        Log.e(TAG,"--CustomCanvasView--");
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);

        mRadius = radius;
    }

    // 在这里我们将测试canvas提供的绘制图形方法
    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG,"--onDraw--");
        mCanvas = canvas;

        drawCompass(mCanvas);
        refreshClock();
    }

    private void refreshClock() {
        if (mThread != null)
            return;
        PostThread++;
        Log.e(TAG,"--PostThread--"+PostThread);
        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        handler.sendEmptyMessage(0x123);

                        sleep(1000);//控制 view的刷新率，设为 5ms 持续20s左右未见崩溃
                        //只不过 刷新时间间隔并不均匀，可能跟系统本身有关系
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Calendar c = Calendar.getInstance();
            mHours = c.getTime().getHours();
            mMinutes = c.getTime().getMinutes();
            mSeconds = c.getTime().getSeconds();
            invalidate(); //请求重新绘制view控件--这里触发控件刷新，否则控件不会刷新

            c = null;
        };
    };

    /**
     * 绘制罗盘
     */
    private void drawCompass(Canvas canvas) {
//        Log.e(TAG,"--drawCompass--");
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
//        canvas.drawColor(Color.CYAN);
//        mPaint.setStrokeWidth(2);
//        float edge_x = canvas.getWidth();
//        float edge_y = canvas.getHeight();
//        canvas.drawLine(0,0,edge_x,edge_y,mPaint);// / 辅助线
//        canvas.drawLine(edge_x,0,0,edge_y,mPaint);// \
//        canvas.drawLine(0,0,edge_x,0,mPaint);
//        canvas.drawLine(0,edge_y/2,edge_x,edge_y/2,mPaint);//水平中线
//        canvas.drawLine(0,0,0,edge_y,mPaint);
//        canvas.drawLine(edge_x/2,0,edge_x/2,edge_y,mPaint);
//        canvas.drawLine(edge_x,0,edge_x,edge_y,mPaint);
//        canvas.drawLine(0,edge_y,edge_x,edge_y,mPaint);
        mPaint.setStrokeWidth(5);
//        canvas.translate(canvas.getWidth() / 2, mRadius + 300); // 平移罗盘
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2); // 平移罗盘
        canvas.drawCircle(0, 0, mRadius, mPaint); // 画圆圈
        // 使用path绘制路径文字
        canvas.save();
//        canvas.drawLine(0,55,78,55,mPaint);// / canvas 无变化
        drawLabel(canvas);
        canvas.restore();
        canvas.save();
//        canvas.drawLine(55,0,55,55,mPaint);// /canvas 无变化
        drawDividing(canvas);
        canvas.restore();

        canvas.save();
        drawSecondHand(canvas);
        canvas.restore();
        canvas.save();
        drawMinuteHand(canvas);
        canvas.restore();
        canvas.save();
        drawHourHand(canvas);
        canvas.restore();
        canvas = null;
    }

    /**
     * 绘制罗盘内侧的标签文本
     */
    private void drawLabel(Canvas canvas) {
//        Log.e(TAG,"--drawLabel--");
        float tvSize = 18*(mRadius/100);
        canvas.translate(-mRadius + tvSize, -mRadius + tvSize);//半径
        Path path = new Path();
//        path.addArc(new RectF(0, 0, mRadius + 100, mRadius + 100), -180, 180);
        path.addArc(new RectF(0, 0, (mRadius - tvSize)*2, (mRadius - tvSize)*2), -140, 180); //直径
        Paint citePaint = new Paint(mPaint);
        citePaint.setTextSize(tvSize);
//        citePaint.setStyle(Paint.Style.FILL);//设置风格为实体填充
        citePaint.setColor(Color.DKGRAY);
        citePaint.setStrokeWidth(1);
//        canvas.drawTextOnPath("http://blog.csdn.net/lemon_tree", path, 35, 0, citePaint);
        canvas.drawTextOnPath("被改造的手表例子", path, 0, 0, citePaint);

        path = null;
        citePaint = null;
        canvas = null;
    }

    /**
     * 绘制刻度
     */
    private void drawDividing(Canvas canvas) {
//        Log.e(TAG,"--drawDividing--");
        Paint divdPaint = new Paint(mPaint); // 小刻度画笔对象
        divdPaint.setStrokeWidth(1);
        float tvSize = 10*(mRadius/100);
        Paint tvPaint = new Paint(mPaint);
        tvPaint.setTextSize(tvSize);
        tvPaint.setStyle(Paint.Style.FILL);//设置风格为实体填充
        tvPaint.setColor(Color.MAGENTA);
        tvPaint.setStrokeWidth(1);

        float y = mRadius;//y 坐标起关键作用
        int count = 60; // 总刻度数
//        //这只是其中一种绘制方式
//        {
//            canvas.rotate(35 * 360 / count, 0f, 0f);//确定 1点钟的位置 ，先绘制的是1点的线
//
//            for (int i = 0; i < count; i++) {
//                if (i % 5 == 0) {
//                    canvas.drawLine(0f, y, 0, y + 20f, mPaint);//y 坐标起关键作用
//                    canvas.drawText(String.valueOf(i / 5 + 1), -4f, y + 55f, divdPaint);
//                } else {
//                    canvas.drawLine(0f, y, 0f, y + 15f, divdPaint);
//                }
//                canvas.rotate(360 / count, 0f, 0f); // 旋转画纸
//            }
//        }

        //我自己修改的方式
        {
//            canvas.drawText(String.valueOf(12), 0, 0, divdPaint);//字符是向上 正方向绘制的
            //减少这个 的寻址位置
//            canvas.rotate(35 * 360 / count, 0f, 0f);//确定 1点钟的位置 ，先绘制的是1点的线
//直接从一分钟位置开始起绘
            for (int i = 1; i <= count; i++) {
                canvas.rotate(360 / count, 0f, 0f); // 旋转画纸 确定每分钟的位置
                if (i % 5 == 0) {
                    canvas.drawLine(0f, -y, 0, -(y + 20f), mPaint);//y 坐标起关键作用 需要往上绘图
                    //让字在线段的再上面10 个单位
                    canvas.drawText(String.valueOf(i / 5), -(tvSize/3), -(y+20f+10), tvPaint);//这里满五分钟则一个字
                } else {
                    canvas.drawLine(0f, -y, 0f, -(y + 15f), divdPaint);
                }
            }
        }
        divdPaint = null;
        canvas = null;
    }

    /**
     * 绘制分针
     */
    private void drawSecondHand(Canvas canvas) {
        Paint handPaint = new Paint(mPaint);
        handPaint.setStrokeWidth(2);
        handPaint.setTextSize(30);

        handPaint.setColor(Color.GRAY);
        handPaint.setStrokeWidth(4);
        canvas.drawCircle(0, 0, 10, handPaint);
        handPaint.setStyle(Paint.Style.FILL);
        handPaint.setColor(Color.YELLOW);

        canvas.drawCircle(0, 0, 5, handPaint);
        handPaint.setColor(Color.GRAY);
        handPaint.setStrokeWidth(2);
        canvas.rotate(mSeconds * 6, 0f, 0f);
        canvas.drawLine(0, 10*(mRadius/100), 0, -(95*(mRadius/100)), handPaint);

        handPaint = null;
        canvas = null;
    }

    /**
     * 绘制分针
     */
    private void drawMinuteHand(Canvas canvas) {
        Paint handPaint = new Paint(mPaint);
        handPaint.setColor(Color.BLUE);
        handPaint.setStrokeWidth(4);
        //handPaint.setStyle(Paint.Style.FILL);

        float angle = mMinutes * 6 + mSeconds/10; // 计算角度
        canvas.rotate(angle, 0f, 0f);
        canvas.drawLine(0, 10*(mRadius/100), 0, -(75*(mRadius/100)), handPaint);
    }

    /**
     * 绘制时针
     */
    private void drawHourHand(Canvas canvas) {
        Paint handPaint = new Paint(mPaint);
        handPaint.setStyle(Paint.Style.FILL);
        handPaint.setColor(Color.DKGRAY);
        handPaint.setStrokeWidth(8);

        float angle = (mHours % 12) * 30 + mMinutes/2 ; // 计算角度
//        float angle = (((mHours % 12) * 5 + 25) * 6) + (mMinutes * 6 * 5 / 60); // 计算角度
        canvas.rotate(angle, 0f, 0f);
        canvas.drawLine(0, 10*(mRadius/100), 0, -(65*(mRadius/100)), handPaint);
    }
}

