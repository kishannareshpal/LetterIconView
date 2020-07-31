package com.kishannareshpal.lettericonview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

public class LetterIconView extends View {

    // Declare Utils
    private Context ctx;
    private String[] colors; // list of colors which indexes the english alphabet.

    private RectF backgroundRect;
    private Paint backgroundPaint, textPaint;
    private int[] gradientColors;
    private LinearGradient linearGradient;

    // Declare Vars
    private Shape shape; // the shape of the icon. square, rounded_square or circle
    private int backgroundColor; // the primary background color.
    private Boolean isGradient; // if we should use gradient or not.
    private String letters; // the letters. Will only show the first two letters if more provided.


    // Public Methods

    /**
     * Change the shape of the view.
     *
     * @see Shape for possible shapes
     * @param shape the shape you want to change to.
     * @return ignore
     */
    public LetterIconView shape(Shape shape) {
        this.shape = shape;
        invalidate();
        return this;
    }

    public LetterIconView letters(String letters) {
        changeLetters(letters);
        invalidate();
        return this;
    }


    private void changeLetters(String letters) {
        if (letters == null || letters.isEmpty()) return;

        if (letters.length() > 2) {
            letters = letters.substring(0, 2);
        }
        this.letters = capitalizeString(letters);
    }


    public LetterIconView(Context context) {
        super(context);
        init(context, null);
    }

    public LetterIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context ctx, @Nullable AttributeSet attributeSet) {
        this.ctx = ctx;

        // List of colors to match the alphabet.
        colors = new String[] {
                "#832161", // A 0
                "#f09ae9", // B 1
                "#EA5B2E", // C 2
                "#7B0828", // D 3
                "#70F8BA", // E 4
                "#F686BD", // F 5
                "#BFEDC1", // G 6
                "#7D1538", // H 7
                "#B47AEA", // I 8
                "#FAA381", // J 9
                "#E63946", // K 10
                "#634133", // L 11
                "#F78764", // M 12
                "#6E44FF", // N 13
                "#0C8346", // O 14
                "#CFB3CD", // P 15
                "#CBBAED", // Q 16
                "#F4E9CD", // R 17
                "#2667FF", // S 18
                "#3D0B37", // T 19
                "#92DCE5", // U 20
                "#ECCFC3", // V 21
                "#36382E", // W 22
                "#F8F272", // X 23
                "#C6AD94", // Y 24
                "#472D30", // Z 25
        };

        // Grab the attributes values, if any was set.
        TypedArray ta = ctx.obtainStyledAttributes(attributeSet, R.styleable.LetterIconView);
        this.backgroundColor = ta.getColor(R.styleable.LetterIconView_liv_backgroundColor, -1); // todo red by default: Color.parseColor("#dd2c00")
        this.letters         = ta.getString(R.styleable.LetterIconView_liv_letters); // default: null
        this.isGradient      = ta.getBoolean(R.styleable.LetterIconView_liv_isGradient, true); // default: solid
        this.shape           = Shape.fromId(ta.getInt(R.styleable.LetterIconView_liv_shape, Shape.CIRCLE.getId())); // default: circle

        // Setup the attributes.
        // Make sure that we only show a maximum of two letters.
        changeLetters(letters);

        // If no color is set, then use the color according to the first letter of the letters.
        // otherwise, use the color that was set.
        if (this.backgroundColor == -1) {
            if (letters.isEmpty()) {
                backgroundColor = Color.LTGRAY;

            } else {
                int index = getIndexOfLetterFromAlphabet(letters);
                String colorHex = colors[index];
                backgroundColor = Color.parseColor(colorHex);
            }
        }

        if (isGradient) {
            gradientColors = new int[] {
                    lightenColor(backgroundColor),
                    backgroundColor,
            };
        }


        // Initialize Utils
        backgroundRect = new RectF();
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        //
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(autoTextColor());
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);


        if (attributeSet != null) {
            ta.recycle();
        }
    }





    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (isGradient) {
            linearGradient = new LinearGradient(0, w, 0, 0, gradientColors, null, Shader.TileMode.CLAMP);
        }
    }





    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int fullWidth = getWidth();
        int fullHeight = getHeight();

        backgroundRect.left = 0;
        backgroundRect.right = fullWidth;
        backgroundRect.top = 0;
        backgroundRect.bottom = fullHeight;

        if (isGradient) {
            // Use Gradient background color
            backgroundPaint.setShader(linearGradient);

        } else {
            // Use solid background color
            backgroundPaint.setColor(backgroundColor);
        }

        float cornerRadius = 0f;
        if (shape == Shape.CIRCLE) {
            cornerRadius = fullWidth / 2f;

        } else if (shape == Shape.ROUNDED_SQUARE) {
            cornerRadius = fullWidth / 3f;

        } else if (shape == Shape.SQUARE) {
            cornerRadius = 0f;
        }
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint);

        textPaint.setTextSize(fullWidth/2f);
        textPaint.setAlpha(200);
        canvas.drawText(letters, fullWidth/2f, (fullHeight/2f) - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
    }


    /**
     * Generate black or white color for text according to the darkness of the primary color.
     *
     * @return white color if the primary color is dark, otherwise very dark grk if it's light.
     */
    @ColorInt
    private int autoTextColor() {
        boolean isDark;
        if (isGradient) {
            // calculate the luminance of the lightest color.
            int color = lightenColor(this.backgroundColor);
            isDark = isColorDark(color);
        } else {
            isDark = isColorDark(this.backgroundColor);
        }

        return isDark ? Color.WHITE : Color.parseColor("#212529");
    }


    /**
     * Get the index of any letter of the english alphabet.
     * There are 26 letters on the english alphabet.
     * This method is zero-based-indexed: The first letter of alphabet (A) will be 0, and the last (Z) 25
     * E.g: A = 0, B = 1, C = 2, ...,  etc.
     *
     * @param letter the letter of the english alphabet you want the index of.
     * @return the zero-based index of the letter.
     */
    private int getIndexOfLetterFromAlphabet(String letter) {
        String lowerCaseChar = letter.toLowerCase();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        return alphabet.indexOf(lowerCaseChar.charAt(0));
    }


    /**
     * Returns whether the provided color is dark.
     * @param color the color you want to calculate the luminance of.
     * @return true if the provided color is dark, otherwise true, if the provided color is light.
     */
    private boolean isColorDark(@ColorInt int color){
        return ColorUtils.calculateLuminance(color) < 0.6F;
    }


    /**
     * Capitalize a word.
     *
     * @param str the word to capitalize
     * @return the capitalized word.
     */
    public static String capitalizeString(String str) {
        String retStr = str;
        try {
            // We can face index out of bound exception if the string is null
            retStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        }catch (Exception ignored){ }
        return retStr;
    }


    /**
     * Returns a lighter version of the provided color.
     * @param color the color you want the lighter version of.
     * @return a lighter color from the provided.
     */
    @ColorInt
    int lightenColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] /= 0.8f;
        return Color.HSVToColor(hsv);
    }
}
