package com.minizuure.todoplannereducationedition.services.animator

import android.view.View
import android.view.ViewPropertyAnimator

class ObjectFade  (
    private val view : View,
    private val viewPropertyAnimator: ViewPropertyAnimator
) {

    constructor(view: View) : this(view, view.animate())

    fun start() {
        viewPropertyAnimator.start()
    }

    fun fadeOut(duration : Long = 150L) : ObjectFade {
        viewPropertyAnimator
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
            }

        return this
    }

    fun fadeIn(duration : Long = 400L) : ObjectFade {
        viewPropertyAnimator
            .alpha(1f)
            .setDuration(duration)
            .withStartAction {
                view.visibility = View.VISIBLE
            }

        return this
    }
}