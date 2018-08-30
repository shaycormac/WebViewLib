package com.assassin.webviewdemo.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.assassin.webviewdemo.R;

/**
 * Author: Shay-Patrick-Cormac
 * Email: fang47881@126.com
 * Ltd: 金螳螂企业（集团）有限公司
 * Date: 2018/8/8 0008 15:27
 * Version: 1.0
 * Description: 通用的带shape文件的自定义控件
 */
public class CommonShapeButton extends AppCompatButton {
    private static final int TOP_LEFT = 1;
    private static final int TOP_RIGHT = 2;
    private static final int BOTTOM_RIGHT = 4;
    private static final int BOTTOM_LEFT = 8;

    /**
     * shape模式
     * 矩形（rectangle）、椭圆形(oval)、线形(line)、环形(ring)
     */
    private int mShapeMode = 0;

    /**
     * 填充颜色
     */
    private int mFillColor = 0;

    /**
     * 按压颜色
     */
    private int mPressedColor = 0;

    /**
     * 描边颜色
     */
    private int mStrokeColor = 0;

    /**
     * 描边宽度
     */
    private int mStrokeWidth = 0;

    /**
     * 圆角半径
     */
    private int mCornerRadius = 0;
    /**
     * 圆角位置
     * topLeft、topRight、bottomRight、bottomLeft
     */
    private int mCornerPosition = -1;

    /**
     * 点击动效
     */
    private boolean mActiveEnable = false;

    /**
     * 起始颜色
     */
    private int mStartColor = 0;

    /**
     * 结束颜色
     */
    private int mEndColor = 0;

    /**
     * 渐变方向
     * 0-GradientDrawable.Orientation.TOP_BOTTOM
     * 1-GradientDrawable.Orientation.LEFT_RIGHT
     */
    private int mOrientation = 0;

    /**
     * drawable位置
     * -1-null、0-left、1-top、2-right、3-bottom
     */
    private int mDrawablePosition = -1;

    /**
     * 普通shape样式
     */
    private GradientDrawable normalGradientDrawable;
    /**
     * 按压shape样式
     */
    private GradientDrawable pressedGradientDrawable;
    /**
     * shape样式集合
     */
    private StateListDrawable stateListDrawable;
    /**
     * button内容总宽度
     */
    private float contentWidth = 0f;
    /**
     * button内容总高度
     */
    private float contentHeight = 0f;

    private Context mContext;


    public CommonShapeButton(Context context) {
        this(context, null);
    }

    public CommonShapeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonShapeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonShapeButton);
        mShapeMode = a.getInt(R.styleable.CommonShapeButton_csb_shapeMode, 0);
        mFillColor = a.getColor(R.styleable.CommonShapeButton_csb_fillColor, Color.parseColor
                ("#FFFFFF"));
        mPressedColor = a.getColor(R.styleable.CommonShapeButton_csb_pressedColor, Color
                .parseColor("#666666"));
        mStrokeColor = a.getColor(R.styleable.CommonShapeButton_csb_strokeColor, Color.parseColor
                ("#00000000"));
        mStrokeWidth = a.getDimensionPixelSize(R.styleable.CommonShapeButton_csb_strokeWidth, 0);
        mCornerRadius = a.getDimensionPixelSize(R.styleable.CommonShapeButton_csb_cornerRadius, 0);
        mCornerPosition = a.getInt(R.styleable.CommonShapeButton_csb_cornerPosition, -1);
        mActiveEnable = a.getBoolean(R.styleable.CommonShapeButton_csb_activeEnable, false);
        mDrawablePosition = a.getInt(R.styleable.CommonShapeButton_csb_drawablePosition, -1);
        mStartColor = a.getColor(R.styleable.CommonShapeButton_csb_startColor, Color.parseColor
                ("#FFFFFF"));
        mEndColor = a.getColor(R.styleable.CommonShapeButton_csb_endColor, Color.parseColor
                ("#FFFFFF"));
        mOrientation = a.getColor(R.styleable.CommonShapeButton_csb_orientation, 0);
        mContext = context;
        a.recycle();
        //初始化
        normalGradientDrawable = new GradientDrawable();
        pressedGradientDrawable = new GradientDrawable();
        stateListDrawable = new StateListDrawable();
    }

    //重新测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //设置渐变色
        if (mStartColor != Color.parseColor("#FFFFFF") && mEndColor != Color.parseColor
                ("#FFFFFF")) {
            normalGradientDrawable.setColors(new int[]{mStartColor, mEndColor});
            normalGradientDrawable.setOrientation(mOrientation == 0 ? GradientDrawable
                    .Orientation.TOP_BOTTOM : GradientDrawable.Orientation.LEFT_RIGHT);
        }
        // 填充色
        else {
            normalGradientDrawable.setColor(mFillColor);
        }
        switch (mShapeMode) {
            case 0:
                normalGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                break;
            case 1:
                normalGradientDrawable.setShape(GradientDrawable.OVAL);
                break;
            case 2:
                normalGradientDrawable.setShape(GradientDrawable.LINE);
                break;
            case 3:
                normalGradientDrawable.setShape(GradientDrawable.RING);
                break;
            default:
                normalGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                break;
        }

        // 统一设置圆角半径,四个方向都设置
        if (mCornerPosition == -1) {
            float cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                    mCornerRadius, mContext.getResources().getDisplayMetrics());
            normalGradientDrawable.setCornerRadius(cornerRadius);

        }
        // 根据圆角位置设置圆角半径
        else {
            normalGradientDrawable.setCornerRadii(getCornerRadiusByPosition());
        }
        // 默认的透明边框不绘制,否则会导致没有阴影
        if (mStrokeColor != Color.parseColor("#00000000")) {
            normalGradientDrawable.setStroke(mStrokeWidth, mStrokeColor);
        }


        // 是否开启点击动效
        if (mActiveEnable) {
            // 5.0以上水波纹效果
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                RippleDrawable tmpDrawable = new RippleDrawable(ColorStateList.valueOf
                        (mPressedColor), normalGradientDrawable, null);
                setBackground(tmpDrawable);
            }
            // 5.0以下变色效果
            else {
                // 初始化pressed状态

                pressedGradientDrawable.setColor(mPressedColor);
                switch (mShapeMode) {
                    case 0:
                        pressedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                        break;
                    case 1:
                        pressedGradientDrawable.setShape(GradientDrawable.OVAL);
                        break;
                    case 2:
                        pressedGradientDrawable.setShape(GradientDrawable.LINE);
                        break;
                    case 3:
                        pressedGradientDrawable.setShape(GradientDrawable.RING);
                        break;
                    default:
                        pressedGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                        break;
                }

                float cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 
                        mCornerRadius, mContext.getResources().getDisplayMetrics());
                pressedGradientDrawable.setCornerRadius(cornerRadius);
                pressedGradientDrawable.setStroke(mStrokeWidth, mStrokeColor);
                

                // 注意此处的add顺序，normal必须在最后一个，否则其他状态无效
                // 设置pressed状态
                stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, 
                        pressedGradientDrawable);
                stateListDrawable.addState(new int[]{}, normalGradientDrawable);

            }
        } else {
            setBackground(normalGradientDrawable);
        }

    }

    //重新布局
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 如果xml中配置了drawable则设置padding让文字移动到边缘与drawable靠在一起
        // button中配置的drawable默认贴着边缘
        if (mDrawablePosition > -1) 
        {
            Drawable[] compoundDrawableList = getCompoundDrawables();
            Drawable positionDrawable = compoundDrawableList[mDrawablePosition];
            int drawablePadding = getCompoundDrawablePadding();
            switch(mDrawablePosition){
                case 0:  
                case 2:
                    // 图片宽度
                    int drawableWidth = positionDrawable.getIntrinsicWidth();
                    // 获取文字宽度
                    float textWidth = getPaint().measureText(getText().toString());
                    
                    // 内容总宽度
                    contentWidth = textWidth + drawableWidth + drawablePadding;
                    int rightPadding = (int) (getWidth() - contentWidth);
                    // 图片和文字全部靠在左侧
                    setPadding(0, 0, rightPadding, 0);
                    break;
                case 1:  
                case 3:
                    // 图片高度
                    int drawableHeight = positionDrawable.getIntrinsicHeight();
                    // 获取文字高度
                    Paint.FontMetrics fm = getPaint().getFontMetrics();
                    // 单行高度
                    double singeLineHeight = Math.ceil(fm.descent - fm.ascent);
                    // 总的行间距
                    float totalLineSpaceHeight = (getLineCount() - 1) * getLineSpacingExtra();
                    float textHeight = (float) (singeLineHeight * getLineCount() + totalLineSpaceHeight);
                    // 内容总高度
                    contentHeight = textHeight + drawableHeight + drawablePadding;
                    // 图片和文字全部靠在上侧
                    int bottomPadding = (int) (getHeight() - contentHeight);
                    setPadding(0, 0, 0, bottomPadding);
                    break;
                default:break;
            }
            
        }
        // 内容居中
        setGravity(Gravity.CENTER);
        // 可点击
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) 
    {
        // 让图片和文字居中
        
       if (contentWidth > 0 && (mDrawablePosition == 0 || mDrawablePosition == 2)){
           canvas.translate((getWidth() - contentWidth) / 2, 0f);  
       }
        if (contentHeight > 0 && (mDrawablePosition == 1 || mDrawablePosition == 3))
        {
            canvas.translate(0f, (getHeight() - contentHeight) / 2); 
        }
    
        super.onDraw(canvas);
    }

    /**
     * 根据圆角位置获取圆角半径
     */
    private float[] getCornerRadiusByPosition() {
        float[] result = new float[]{0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        float cornerRadius = mCornerRadius;
        if (containsFlag(mCornerPosition, TOP_LEFT)) {
            result[0] = cornerRadius;
            result[1] = cornerRadius;
        }
        if (containsFlag(mCornerPosition, TOP_RIGHT)) {
            result[2] = cornerRadius;
            result[3] = cornerRadius;
        }
        if (containsFlag(mCornerPosition, BOTTOM_RIGHT)) {
            result[4] = cornerRadius;
            result[5] = cornerRadius;
        }
        if (containsFlag(mCornerPosition, BOTTOM_LEFT)) {
            result[6] = cornerRadius;
            result[7] = cornerRadius;
        }
        return result;
    }

    /**
     * 是否包含对应flag
     */
    private boolean containsFlag(int flagSet, int flag) {
        return flag == flagSet;
    }
}
