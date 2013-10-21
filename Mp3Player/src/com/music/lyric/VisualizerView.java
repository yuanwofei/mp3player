package com.music.lyric;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class VisualizerView extends View{

	private byte waveforms[] = null;
	private float[] waveformPoints = null;
	private Paint paint = null;
	private Rect mRect = new Rect();
	
	public VisualizerView(Context context) {
		super(context);
		init();
	}

	public void refreshVisualizer(byte[] waveform) {
		this.waveforms  = waveform;
		invalidate();
	}
	 
	private void init() {
		paint.setStrokeWidth(1.5f);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);		
		
		if (waveforms == null) {
			return;
		}
		
		if (waveformPoints == null || waveformPoints.length < waveforms.length * 4) {
			waveformPoints = new float[waveforms.length * 4];
		}

		mRect.set(0, 0, getWidth(), getHeight());

		for (int i = 0; i < waveforms.length - 1; i++) {
			waveformPoints[i * 4] = mRect.width() * i / (waveforms.length - 1);
			waveformPoints[i * 4 + 1] = mRect.height() / 2
					+ ((byte) (waveforms[i] + 128)) * (mRect.height() / 2)
					/ 128;
			waveformPoints[i * 4 + 2] = mRect.width() * (i + 1)
					/ (waveforms.length - 1);
			waveformPoints[i * 4 + 3] = mRect.height() / 2
					+ ((byte) (waveforms[i + 1] + 128)) * (mRect.height() / 2)
					/ 128;
		}
		canvas.drawLines(waveformPoints, paint);
	}
}
