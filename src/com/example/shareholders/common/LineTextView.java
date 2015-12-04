package com.example.shareholders.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.shareholders.R;

public class LineTextView extends TextView {
	private Paint linePaint;
	private float margin;
	private int paperColor;
	private Rect mRect;

	@SuppressLint("ResourceAsColor")
	public LineTextView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		mRect = new Rect();
		linePaint = new Paint();
		linePaint.setColor(R.color.detail_line);
	}

	protected void onDraw(Canvas paramCanvas) {
		paramCanvas.drawColor(this.paperColor);
		Rect r = mRect;
		int lineCount = getLineCount();
		int height = getHeight();
		int lineHeight = getLineHeight();
		int m = height / lineHeight;
		if (lineCount < m)
			lineCount = m;
		int n = getLineBounds(0, r);
		paramCanvas.drawLine(0.0F, 10 + n, getRight(), 10 + n, this.linePaint);
		for (int i = 0;; i++) {

			if (i >= lineCount - 2) {
				super.onDraw(paramCanvas);
				paramCanvas.restore();
				return;
			}

			n += lineHeight;
			paramCanvas.drawLine(0.0F, 10 + n, getRight(), 10 + n,
					this.linePaint);
			paramCanvas.save();
		}

	}
}