/**
 * 
 */
package com.abubusoft.xenon.render.effects;

import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.Point3;
import com.abubusoft.xenon.misc.NormalizedTimer;
import com.abubusoft.xenon.misc.NormalizedTimer.TypeNormalizedTimer;
import com.abubusoft.xenon.render.AbstractEffect;
import com.abubusoft.xenon.render.UseShader;

/**
 * <p>Effetto onda. Supporta fino a 8 touch.</p>
 * 
 * <table>
 * 	<tr>
 * 		<td><img width="196px" src="doc-files/original.png"/></td>
 * 		<td><img width="196px" src="doc-files/effectRipple.png"/></td>
 * </tr>
 * </table>
 * <p>I parametri:</p>
 * <dl>
 * 	<dt>waveAmount</dt><dd>.Valore di default 80.0</dd>
 *  <dt>waveDistortion</dt><dd>. Valore di default 30.0</dd>
 *  <dt>waveSpeed</dt><dd>. Valore di default 5.0</dd>
 * </dl>

 * 
 * @author Francesco Benincasa
 * 
 */
@UseShader(RippleShader.class)
public class RippleEffect extends AbstractEffect<RippleShader> {

	public static final int MAX_TOUCH =2;

	public float waveAmount;

	public float waveDistortion;

	public float waveSpeed;

	/**
	 * durata in secondi del touch
	 */
	protected float touchDuration;

	public RippleEffect() {
		currentTouch = 0;

		touch = new Point3[MAX_TOUCH];
		touchTimer = new NormalizedTimer[MAX_TOUCH];

		touchDuration=4f;

		for (int i = 0; i < MAX_TOUCH; i++) {
			touchTimer[i] = new NormalizedTimer(TypeNormalizedTimer.ONE_TIME);
			touchTimer[i].setMaxTime((long) (touchDuration *NormalizedTimer.SECOND_IN_MILLISECONDS));
		}

		//waveAmount = 20f;
		//waveDistortion = 30f;
		//waveSpeed = 5.0f;
		
		waveAmount=8.0f;		
		waveDistortion=0.01f;
		waveSpeed=10.0f;
		
		//rippleEffect.waveAmount=20.0f;		
		//rippleEffect.waveDistortion=1.0f;
//		rippleEffect.waveSpeed=10.0f;
		
	}
	
	public void setTouchDuration(float value)
	{
		touchDuration=value;
		for (int i = 0; i < MAX_TOUCH; i++) {			
			touchTimer[i].setMaxTime((long) (touchDuration *NormalizedTimer.SECOND_IN_MILLISECONDS));
		}
	}
	
	public float getTouchDuration()
	{
		return touchDuration;
	}

	public NormalizedTimer[] touchTimer;

	public Point3[] touch;

	private int currentTouch;

	public void addTouch(Point3 center) {
		touch[currentTouch] = center;
		touchTimer[currentTouch].start();

		currentTouch = (currentTouch + 1) % MAX_TOUCH;
	}

	@Override
	protected void updateShader(RippleShader shader, long enlapsedTime, float speedAdapter) {
		shader.setWaveAmount(this.waveAmount);
		shader.setWaveDistortion(this.waveDistortion);
		shader.setWaveSpeed(this.waveSpeed);

		for (int i = 0; i < MAX_TOUCH; i++) {
			touchTimer[i].update(enlapsedTime);

			shader.setTouch(i, touch[i]);
			shader.setTouchEnabled(i, touchTimer[i].isStarted());
			shader.setTouchTime(i, 2f * XenonMath.PI * (touchTimer[i].getNormalizedEnlapsedTime()));
		}

	}

}
