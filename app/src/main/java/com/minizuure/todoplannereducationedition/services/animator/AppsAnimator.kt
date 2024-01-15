package com.minizuure.todoplannereducationedition.services.animator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View

abstract class AppsAnimator(
    private val view: View
) {
    protected var objectAnimator : ObjectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.5f, 1f).apply {
        this.duration = 0
        this.repeatCount = 0
        this.repeatMode = ObjectAnimator.REVERSE
    }
        set(value) {
            field = value
            field.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    resetView()
                }
            })
        }

    fun start() {
        objectAnimator.start()
    }

    fun cancel() {
        objectAnimator.cancel()
        view.alpha = 1f
    }

    fun isRunning() : Boolean {
        return objectAnimator.isRunning
    }

    fun resetView() {
        view.alpha = 1f
    }
}