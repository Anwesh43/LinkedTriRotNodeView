package com.anwesh.uiprojects.trnview

/**
 * Created by anweshmishra on 22/07/18.
 */

import android.view.View
import android.content.Context
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

val nodes : Int = 5

class TRNView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}