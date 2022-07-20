package com.katic.rssfeedapp.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import com.katic.rssfeedapp.R

/**
 * A simple and lightweight class that extends SimpleCallBack and can be used to attach a swipe listener to RecyclerView items.
 * Currently, it only supports left or right swipes with a LinearLayoutManager.
 *
 * Taken from
 *
 * [https://gist.github.com/hafizmdyasir/cfcb971eb6177b442a80d8bc753e7ce5#file-recyclerswipehelper-java]
 */
abstract class RecyclerSwipeHelper(
    @ColorInt private val swipeRightColor: Int = R.color.white,
    @ColorInt private val swipeLeftColor: Int = R.color.delete_color,
    @DrawableRes swipeRightIconResource: Int? = null,
    @DrawableRes swipeLeftIconResource: Int? = null,
    private val context: Context
) : ItemTouchHelper.SimpleCallback(
    0,
    (if (swipeLeftIconResource != null) LEFT else 0) or (if (swipeRightIconResource != null) RIGHT else 0)
) {
    private val clearPaint: Paint = Paint()
    private val swipeRightIcon: Drawable?
    private val swipeLeftIcon: Drawable?
    private val background = ColorDrawable()
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    companion object {
        const val TAG_NO_SWIPE = "don't swipe this item"
    }

    init {
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        swipeRightIcon = if (swipeRightIconResource == null) null else ContextCompat.getDrawable(
            context,
            swipeRightIconResource
        )
        swipeLeftIcon = if (swipeLeftIconResource == null) null else ContextCompat.getDrawable(
            context,
            swipeLeftIconResource
        )
    }

    override fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView: View = viewHolder.itemView
        val itemHeight: Int = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive
        if (isCanceled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false)
            return
        }
        if (dX < 0) {
            // swipe left
            background.apply {
                color = ContextCompat.getColor(context, swipeLeftColor)
                setBounds(
                    (itemView.right + dX).toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                draw(c)
            }
            swipeLeftIcon?.let {
                val intrinsicHeight = it.intrinsicHeight
                val intrinsicWidth = it.intrinsicWidth
                val itemTop: Int = itemView.top + (itemHeight - intrinsicHeight) / 2
                val itemMargin = (itemHeight - intrinsicHeight) / 2
                val itemLeft: Int = itemView.right - itemMargin - intrinsicWidth
                val itemRight: Int = itemView.right - itemMargin
                val itemBottom = itemTop + intrinsicHeight
                var a = (-itemView.translationX / itemView.width * 510).toInt()
                if (a > 255) a = 255
                it.apply {
                    alpha = a
                    setBounds(itemLeft, itemTop, itemRight, itemBottom)
                    draw(c)
                }
            }
        } else {
            // swipe right
            background.apply {
                color = ContextCompat.getColor(context, swipeRightColor)
                setBounds(
                    (itemView.left + dX).toInt(),
                    itemView.top,
                    itemView.left,
                    itemView.bottom
                )
                draw(c)
            }
            swipeRightIcon?.let {
                val intrinsicHeight = it.intrinsicHeight
                val intrinsicWidth = it.intrinsicWidth
                val itemTop: Int = itemView.top + (itemHeight - intrinsicHeight) / 2
                val itemMargin = (itemHeight - intrinsicHeight) / 2
                val itemLeft: Int = itemView.left + itemMargin
                val itemRight: Int = itemView.left + itemMargin + intrinsicWidth
                val itemBottom = itemTop + intrinsicHeight
                var a = (itemView.translationX / itemView.width * 510).toInt()
                if (a > 255) a = 255
                it.apply {
                    alpha = a
                    setBounds(itemLeft, itemTop, itemRight, itemBottom)
                    draw(c)
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (TAG_NO_SWIPE == viewHolder.itemView.tag) 0 else super.getSwipeDirs(
            recyclerView,
            viewHolder
        )
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

}

fun RecyclerView.addOnRemovedListener(callback: (position: Int) -> Unit) {
    ItemTouchHelper(object :
        RecyclerSwipeHelper(
            swipeLeftIconResource = R.drawable.ic_delete,
            context = context
        ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            callback.invoke(viewHolder.adapterPosition)
        }

    }).attachToRecyclerView(this)
}