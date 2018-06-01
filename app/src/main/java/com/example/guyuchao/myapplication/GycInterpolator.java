package com.example.guyuchao.myapplication;

import android.view.animation.LinearInterpolator;

public class GycInterpolator extends LinearInterpolator {
	private float factor;

	public GycInterpolator() {
		this.factor = 0.15f;
	}

	@Override
	public float getInterpolation(float input) {
		return (float) (Math.pow(2, -10 * input)
				* Math.sin((input - factor / 4) * (2 * Math.PI) / factor) + 1);
	}
}
