package com.minizuure.todoplannereducationedition.services.animator

import android.animation.ObjectAnimator
import android.view.View

class ObjectBlink(
    private val view: View
) : AppsAnimator(view) {

    /**
     * @param duration in milliseconds
     * @param repeat in count
     *
     * @return [ObjectBlink]
     */
    fun setAsBlink(
        duration : Long = 500L,
        repeat : Int = 4
    ) : ObjectBlink {
        objectAnimator = ObjectAnimator.ofFloat(view, View.ALPHA, 0.5f, 1f).apply {
            this.duration = duration
            this.repeatCount = repeat
            this.repeatMode = ObjectAnimator.REVERSE
        }

        return this
    }

}