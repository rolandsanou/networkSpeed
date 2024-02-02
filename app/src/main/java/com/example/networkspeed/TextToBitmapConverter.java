package com.example.networkspeed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class TextToBitmapConverter {
    public static Bitmap convertTextToBitmapWithCircle(String text, int textSize, int textColor, int circleColor) {
        // Set up the Paint objects
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        textPaint.setFakeBoldText(true);

        Paint circlePaint = new Paint();
        circlePaint.setColor(circleColor);

        // Calculate the desired width for the text layout
        int desiredWidth = 500; // Adjust based on your requirements

        // Create a StaticLayout to handle multiline text
        StaticLayout staticLayout = new StaticLayout(text, textPaint, desiredWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // Calculate the circle diameter based on the text dimensions
        int circleDiameter = Math.max(staticLayout.getWidth(), staticLayout.getHeight()) + 20; // Add some padding

        // Create a bitmap with a transparent background
        Bitmap bitmap = Bitmap.createBitmap(circleDiameter, circleDiameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the circle background
        canvas.drawCircle(circleDiameter / 2f, circleDiameter / 2f, circleDiameter / 2f, circlePaint);

        // Draw the text layout onto the bitmap
        canvas.save();
        float textX = (circleDiameter - staticLayout.getWidth()) / 2f;
        float textY = (circleDiameter - staticLayout.getHeight()) / 2f;
        canvas.translate(textX, textY);
        staticLayout.draw(canvas);
        canvas.restore();

        return bitmap;
    }
}
