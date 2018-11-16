package com.example.muhammed.edittextunderline;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.View;

public class UnderLineEditText extends AppCompatEditText implements View.OnFocusChangeListener {
    private static final int DEFAULT_CODE_LENGTH = 10;
    private static final String DEFAULT_CODE_MASK = "*";
    private static final String DEFAULT_CODE_SYMBOL = "0";
    private static final String DEFAULT_REGEX = "[^0-9]";
    private static final float DEFAULT_REDUCTION_SCALE = 0.5f;


    private Paint textPaint;
    private Paint underlinePaint;
    private Paint cursorPaint;

    private float textSize;
    private float textPosY;
    private int textColor;
    private float sectionWidth;
    private int codeLength;
    private float symbolWidth;
    private float symbolMaskedWidth;
    private float underlineHorizontalPadding;
    private float underlineReductionScale;
    private float underlineStrokeWidth;
    private int underlineBaseColor;
    private int underlineSelectedColor;
    private int underlineFilledColor;
    private int underlineCursorColor;
    private float underlinePosY;
    private int fontStyle;
    private boolean cursorEnabled;
    private boolean codeHiddenMode;
    private boolean isSelected;
    private String codeHiddenMask;
    private Rect textBounds = new Rect();

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
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, fontStyle));
        textPaint.setAntiAlias(true);

        underlinePaint = new Paint();
        underlinePaint.setColor(underlineBaseColor);
        underlinePaint.setStrokeWidth(underlineStrokeWidth);

        cursorPaint = new Paint();
        cursorPaint.setColor(underlineBaseColor);
        cursorPaint.setStrokeWidth(underlineStrokeWidth);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {

    }


    private void initDefaultAttrs(Context context) {
        Resources resources = context.getResources();

        underlineReductionScale = DEFAULT_REDUCTION_SCALE;
        underlineStrokeWidth = resources.getDimension(R.dimen.underline_stroke_width);
        underlineBaseColor = ContextCompat.getColor(context, R.color.underline_base_color);
        underlineFilledColor = ContextCompat.getColor(context, R.color.underline_filled_color);
        underlineCursorColor = ContextCompat.getColor(context, R.color.underline_cursor_color);
        underlineSelectedColor = ContextCompat.getColor(context, R.color.underline_selected_color);
        textSize = resources.getDimension(R.dimen.code_text_size);
        textColor = ContextCompat.getColor(context, R.color.text_main_color);
        codeLength = DEFAULT_CODE_LENGTH;
        codeHiddenMask = DEFAULT_CODE_MASK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setLetterSpacing(0.42f);
        }
        setOnFocusChangeListener(this);

        setFilters(new InputFilter[]{new InputFilter.LengthFilter(codeLength)});
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

        symbolWidth = textPaint.measureText(DEFAULT_CODE_SYMBOL);
        symbolMaskedWidth = textPaint.measureText(codeHiddenMask);
        textPaint.getTextBounds(DEFAULT_CODE_SYMBOL, 0, 1, textBounds);
        sectionWidth = viewWidth / codeLength;
        underlinePosY = viewHeight;
        underlineHorizontalPadding = sectionWidth * underlineReductionScale / 2;
        textPosY = viewHeight / 2 + textBounds.height() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawUnderline(canvas);
//        drawText(canvas);
        super.onDraw(canvas);
    }

//    private void drawText(Canvas canvas) {
//        for (int i = 0; i < getText().length(); i++) {
//            char[] symbol = {getText().charAt(i)};
//            float textPosX = sectionWidth * i + sectionWidth / 2 - symbolWidth / 2;
//            canvas.drawText(symbol, 0, 1, textPosX, textPosY, textPaint);
//        }
//    }


    private void drawUnderline(Canvas canvas) {
        for (int i = 0; i < codeLength; i++) {
            float startPosX = sectionWidth * i;
            float endPosX = startPosX + sectionWidth * 2;
            underlinePaint.setColor(underlineSelectedColor);
            canvas.drawLine(startPosX, underlinePosY, endPosX, underlinePosY, underlinePaint);

        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            underlineSelectedColor = ContextCompat.getColor(getContext(), R.color.underline_filled_color);
        } else {
            underlineSelectedColor = ContextCompat.getColor(getContext(), R.color.text_main_color);
        }

        invalidate();
    }
}
