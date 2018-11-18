package com.example.muhammed.edittextunderline;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;

public class UnderLineEditText extends AppCompatEditText implements View.OnFocusChangeListener {
    private static final int DEFAULT_CODE_LENGTH = 8;
    private static final int DEFAULT_TEXT_BOTTOM_MARGIN = 20;


    private Paint textPaint;
    private Paint underlinePaint;

    private float textSize;
    private float textPosY;
    private int textColor;
    private float sectionWidth;
    private int codeLength;
    private float underlineReductionScale;
    private int underlineDefaultColor;
    private int underlineSelectedColor;

    private float underlinePosY;
    private Rect textBounds = new Rect();
    private float underlineStrokeWidth;

    public UnderLineEditText(Context context) {
        super(context);
        init(null, context);
    }


    public UnderLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public UnderLineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context) {
        initDefaultAttrs(context);
        initCustomAttrs(context, attrs);
        initPaints();

    }

    private void initPaints() {
        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        underlinePaint = new Paint();
        underlinePaint.setColor(underlineDefaultColor);
        underlinePaint.setStrokeWidth(underlineStrokeWidth);
    }

    private void initCustomAttrs(Context context, AttributeSet attributeSet) {
        if (attributeSet == null) return;
        TypedArray attributes = context.obtainStyledAttributes(
                attributeSet, R.styleable.UnderLineEditText);

        underlineDefaultColor = attributes.getColor(R.styleable.UnderLineEditText_underline_default_color, Color.BLACK);
        underlineSelectedColor = attributes.getColor(R.styleable.UnderLineEditText_underline_selected_color, Color.RED);
        underlineStrokeWidth = attributes.getDimension(R.styleable.UnderLineEditText_underline_stroke_width, 5);
        codeLength = attributes.getInt(R.styleable.UnderLineEditText_char_length, DEFAULT_CODE_LENGTH);
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(codeLength)});
    }


    private void initDefaultAttrs(Context context) {
        Resources resources = context.getResources();
        textSize = resources.getDimension(R.dimen.code_text_size);
        textColor = ContextCompat.getColor(context, R.color.text_main_color);
        codeLength = DEFAULT_CODE_LENGTH;
        setOnFocusChangeListener(this);


        setCursorVisible(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int measureSpec) {
        int size = (int) (getPaddingBottom()
                + getPaddingTop()
                + textBounds.height()
                + textSize
                + underlineStrokeWidth);
        return resolveSizeAndState(size, measureSpec, 0);
    }

    private int measureWidth(int measureSpec) {
        int size = (int) ((getPaddingLeft() + getPaddingRight() + textSize) * codeLength + 5);
        return resolveSizeAndState(size, measureSpec, 0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measureSizes(w, h);
    }

    private void measureSizes(int viewWidth, int viewHeight) {
        if (underlineReductionScale > 1) underlineReductionScale = 1;
        if (underlineReductionScale < 0) underlineReductionScale = 0;

        if (codeLength <= 0) {
            throw new IllegalArgumentException("Code length must be over than zero");
        }

        sectionWidth = viewWidth / codeLength;
        underlinePosY = viewHeight;
        textPosY = (viewHeight - DEFAULT_TEXT_BOTTOM_MARGIN) + textBounds.height();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawUnderline(canvas);
        drawText(canvas);
//        super.onDraw(canvas);
    }

    private void drawText(Canvas canvas) {
        for (int i = 0; i < getText().length(); i++) {
            char[] symbol = {getText().charAt(i)};
            float textPosX = sectionWidth * i + sectionWidth / 2 / 2;
            canvas.drawText(symbol, 0, 1, textPosX, textPosY, textPaint);
        }
    }


    private void drawUnderline(Canvas canvas) {
        for (int i = 0; i < codeLength; i++) {
            float startPosX = sectionWidth * i;
            float endPosX = startPosX + sectionWidth * 2;
            underlinePaint.setColor(underlineDefaultColor);
            canvas.drawLine(startPosX, underlinePosY, endPosX, underlinePosY, underlinePaint);

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            underlineDefaultColor = underlineSelectedColor;
        } else {
            underlineDefaultColor = Color.BLACK;
        }

        invalidate();
    }
}
