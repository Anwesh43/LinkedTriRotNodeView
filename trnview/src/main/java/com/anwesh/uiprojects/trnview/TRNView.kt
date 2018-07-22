package com.anwesh.uiprojects.trnview

/**
 * Created by anweshmishra on 22/07/18.
 */

import android.app.Activity
import android.view.View
import android.content.Context
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

fun Canvas.drawTriNode(i: Int, scale : Float, paint : Paint) {
    paint.color = Color.parseColor("#0097A7")
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / nodes
    val size : Float = gap / 5
    val sc1 : Float = Math.min(0.5f, scale) * 2
    val sc2 : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f)) * 2
    val factor : Int = 1 - 2 * (i % 2)
    save()
    translate(i * gap + gap/2, h/2)
    rotate(90f * sc1 * factor)
    save()
    translate(sc2 * (h/2 + size), 0f)
    val path : Path = Path()
    path.moveTo(-size, -size)
    path.lineTo(size, 0f)
    path.lineTo(-size, size)
    drawPath(path, paint)
    restore()
    restore()
}

val nodes : Int = 5
val DELAY : Long = 60

class TRNView(ctx : Context) : View(ctx) {

    var onAnimationListener : OnAnimationListener? = null

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    fun addOnAnimationListener(onComplete : (Int) -> Unit, onReset : (Int) -> Unit) {
        onAnimationListener = OnAnimationListener(onComplete, onReset)
    }

    override fun onTouchEvent(event : MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(stopcb : (Float) -> Unit) {
            scale += dir * 0.1f
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                stopcb(prevScale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {
        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(DELAY)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class TRNNode(var i : Int, val state : State = State()) {

        var next : TRNNode? = null

        var prev : TRNNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = TRNNode(i + 1)
                next?.prev = this
            }
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            state.update {
                stopcb(i, it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : TRNNode {
            var curr : TRNNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawTriNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

    }

    data class LinkedTRNNode(var i : Int) {

        private var curr : TRNNode = TRNNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit)  {
            curr.update {i, scale ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(i, scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : TRNView) {

        private val linkedTRN : LinkedTRNNode = LinkedTRNNode(0)

        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            linkedTRN.draw(canvas, paint)
            animator.animate {
                linkedTRN.update {i, scale ->
                    animator.stop()
                    when (scale) {
                        0f -> view.onAnimationListener?.onReset?.invoke(i)
                        1f -> view.onAnimationListener?.onComplete?.invoke(i)
                    }
                }

            }
        }

        fun handleTap() {
            linkedTRN.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : TRNView  {
            val view : TRNView = TRNView(activity)
            activity.setContentView(view)
            return view
        }
    }

    data class OnAnimationListener(var onComplete : (Int) -> Unit, var onReset : (Int) -> Unit)
}