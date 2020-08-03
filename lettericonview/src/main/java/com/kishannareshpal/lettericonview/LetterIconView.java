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
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

public class LetterIconView extends View {

    // Declare Utils
    private Context ctx;
    private String[] colors; // list of colors which indexes the english alphabet.

    private int defaultBackgroundColor = Color.parseColor("#DDDDDD");

    private RectF backgroundRect;
    private Paint backgroundPaint, textPaint;
    private int[] gradientColors;
    private LinearGradient linearGradient;

    // Declare Vars
    private Shape shape; // the shape of the icon. square, rounded_square or circle
    private float shapeCornerRadius; // the corner radius to use when the {@link LetterIconView#shape} is set to Shape.CUSTOM_RADIUS
    private Integer backgroundColor; // the primary background colorss
    private boolean isGradient; // if we should use gradient or not.
    private String letters = ""; // the letters. Will only show the first two letters if more provided.


    // Public Methods

    /**
     * Change the shape of the view.
     *
     * @see Shape for possible shapes
     * @param shape the shape you want to change to.
     */
    public LetterIconView shape(Shape shape) {
        this.shape = shape;
        invalidate();
        return this;
    }

    /**
     * Change the shape corner radius to a custom one.
     * Note that to use this attribute, you must set the {@link LetterIconView#shape} to {@link Shape#CUSTOM_RADIUS}
     *
     * @param cornerRadius the corner radius.
     */
    public LetterIconView cornerRadius(float cornerRadius) {
        this.shapeCornerRadius = cornerRadius;
        invalidate();
        return this;
    }

    /**
     * Change the letters shown in the shape.
     *
     * @param letters the letters to be shown in the shape.
     *                If the provided letters exceed the length of 2, only the first two letters will be shown.
     *                The first letter will always be capitalized.
     * @see LetterIconView#letters(String, boolean) if you don't want to automatically capitalize the first letter in the shape.
     */
    public LetterIconView letters(String letters) {
        changeLetters(letters, true);
        invalidate();
        return this;
    }

    /**
     * Change the letters shown in the shape, while capitalizing or not the first letter.
     *
     * @param letters the letters to be shown in the shape.
     *                If the provided letters exceed the length of 2, only the first two letters will be shown.
     * @param capitalize if you want to capitalize the first letter.
     * @see LetterIconView#letters(String) if you always want to capitalize the first letter.
     */
    public LetterIconView letters(String letters, boolean capitalize) {
        changeLetters(letters, capitalize);
        changeBackgroundColor(this.backgroundColor);
        invalidate();
        return this;
    }


    /**
     * Use gradient of the original color with the lighter version of it as the shape background color.
     *
     * @param use whether or not to use.
     */
    public LetterIconView useGradient(boolean use) {
        this.isGradient = use;
        invalidate();
        return this;
    }


    /**
     * Change the shape background color.
     *
     * @param color the color of the shape you want to change to.
     *              Use {@link ContextCompat#getColor(Context, int)} if you want to reference a color inside your values/colors.xml
     *              If null, the shape background color will automatically be set according to the first letter, or it will use #DDDDDD (silver) if no letters are set.
     */
    public LetterIconView backgroundColor(@Nullable @ColorInt Integer color) {
        this.changeBackgroundColor(color);
        // Automatically adjust the letters color based on the new background color.
        this.textPaint.setColor(autoTextColor());

        invalidate();
        return this;
    }


    private void changeBackgroundColor(@Nullable @ColorInt Integer color) {
        if (color == null) {
            // If no color is defined
            if (this.letters.isEmpty()) {
                // Use a default color.
                if (this.backgroundColor == null) {
                    this.backgroundColor = defaultBackgroundColor;
                }

            } else {
                // Automatically choose a color according to the first letter.
                int index = getIndexOfLetterFromAlphabet(this.letters);

                if (index != -1) {
                    String colorHex = this.colors[index];
                    this.backgroundColor = Color.parseColor(colorHex);

                } else {
                    // Use a default color.
                    this.backgroundColor = defaultBackgroundColor;
                }
            }

        } else {
            // Otherwise use the defined color here.
            this.backgroundColor = color;
        }

        if (this.isGradient) {
            this.gradientColors = new int[]{
                    lightenColor(this.backgroundColor),
                    this.backgroundColor,
            };
        }


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
        if (ta.hasValue(R.styleable.LetterIconView_liv_backgroundColor)) {
            this.backgroundColor = ta.getColor(R.styleable.LetterIconView_liv_backgroundColor, Color.parseColor("#DDDDDD")); // default: silver
        }
        this.letters           = ta.getString(R.styleable.LetterIconView_liv_letters); // default: null
        this.isGradient        = ta.getBoolean(R.styleable.LetterIconView_liv_isGradient, true); // default: solid
        this.shape             = Shape.fromId(ta.getInt(R.styleable.LetterIconView_liv_shape, Shape.CIRCLE.getId())); // default: circle
        this.shapeCornerRadius = ta.getFloat(R.styleable.LetterIconView_liv_cornerRadius, -1f); // default: none set.

        // Setup the attributes.
        // Make sure that we only show a maximum of two letters.
        changeLetters(letters, true);
        changeBackgroundColor(this.backgroundColor);

        // If no color is set, then use the color according to the first letter of the letters.
        // otherwise, use the color that was set.
//        if (this.backgroundColor == null) {
//            // If no color is defined
//            if (this.letters.isEmpty()) {
//                // Use a default color.
//                this.backgroundColor = Color.GRAY;
//
//            } else {
//                // Automatically choose a color according to the first letter.
//                int index = getIndexOfLetterFromAlphabet(this.letters);
//                String colorHex = this.colors[index];
//                this.backgroundColor = Color.parseColor(colorHex);
//            }
//        }
//
//        if (this.isGradient) {
//            this.gradientColors = new int[] {
//                    lightenColor(this.backgroundColor),
//                    this.backgroundColor,
//            };
//        }


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
            linearGradient = new LinearGradient(0, fullWidth, 0, 0, gradientColors, null, Shader.TileMode.CLAMP);
            backgroundPaint.setShader(linearGradient);

        } else {
            // Use solid background color
            backgroundPaint.setShader(null); // remove the gradient shader if it was set before.
            backgroundPaint.setColor(backgroundColor);
        }

        float cornerRadius = 0f;
        if (shape == Shape.CIRCLE) {
            cornerRadius = fullWidth / 2f;

        } else if (shape == Shape.ROUNDED_SQUARE) {
            cornerRadius = fullWidth / 3f;

        } else if (shape == Shape.SQUARE) {
            cornerRadius = 0f;

        } else if (shape == Shape.CUSTOM_RADIUS) {
            // se (fullwidth/2) fica redondo.
            // entao ..... ???


            // ao colocar cornerRadius = 100 deve ficar igual a redondo


//            redondo estaPara 100
//            x       estaPara cornerRadius
//
//
//                    ahmmmmm jh sei.
//
//            x * 100 = redondo * cornerRadius
//
//
//                    x = redondo * cornerRadius / 100
//
//                            num faz sentido
//
//                    fiz isso mas n aceitou.. vou tentar denovo
//

            float redondo = fullWidth / 2;
            cornerRadius = redondo * this.shapeCornerRadius / 100;

//            cornerRadius = this.shapeCornerRadius;
        }

        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint);

        textPaint.setTextSize(fullWidth/2f);
        canvas.drawText(letters, fullWidth/2f, (fullHeight/2f) - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
    }





    /** Private Methods **/
    private void changeLetters(String l, boolean capitalize) {
        this.letters = l;
        if (l == null) {
            // If letters are null, than initialize it as empty
            this.letters = "";
            return;
        }

        if (l.length() > 2) {
            // Limit to 2 letters.
            this.letters = l.substring(0, 2);
        }
        // Make the first of the letters uppercase.
        if (capitalize) {
            this.letters = capitalizeString(this.letters);
        }
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

        return isDark ? Color.parseColor("#FFFFFF") : Color.parseColor("#212529");
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
