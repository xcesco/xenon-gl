package com.abubusoft.xenon.animations;

import com.abubusoft.xenon.math.Vector3;

public class TranslationHandler extends AnimationHandler<TranslationFrame> {
	@Override
	public TranslationFrame value(TranslationFrame current, long enlapsedTime, TranslationFrame next) {
		if (next != null) {
			float perc = current.interpolation.getPercentage(enlapsedTime, current.duration * this.rate);

			// temp viene sempre scritto, non importa cosa c'è prima
			Vector3.multiply(next.translation, perc, temp.translation);
			return temp;
		} else {
			// siamo sull'ultimo frame, dopo non c'è niente. La traslazione è 0
			temp.translation.setCoords(0, 0, 0);
			return temp;
		}
	}

	@Override
	public TranslationFrame buildFrame() {
		return new TranslationFrame();
	}
}
