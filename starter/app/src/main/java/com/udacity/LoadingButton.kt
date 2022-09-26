package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var buttonBackgroundColor = 0
    private var buttonClickedColor = 0
    private var textColor = Color.WHITE

    // Light green color for starting button
    private var paintButton: Paint

    // White text color for button
    private var paintText: Paint

    // Dark green color for loading button
    private var paintLoadingButton: Paint

    // Yellow color for loading circle
    private var paintLoadingCircle: Paint

    private var sweep = 0f

    private var currentWidth = 0

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                // Nothing
            }
            ButtonState.Loading -> {
                valueAnimator.duration = 2000L

                valueAnimator.setValues(
                    PropertyValuesHolder.ofInt("buttonWidth", 0, widthSize),
                    PropertyValuesHolder.ofFloat("loadingCircle", 0F, 360F)
                )

                valueAnimator.addUpdateListener {
                    currentWidth = it.getAnimatedValue("buttonWidth") as Int
                    sweep = it.getAnimatedValue("loadingCircle") as Float
                    invalidate()
                }

                valueAnimator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        // Start of loading
                        isEnabled = false
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        // End of Animation, reset button state to complete
                        isEnabled = true
                        setState(ButtonState.Completed)
                    }
                })

                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
            }
        }
    }

    fun setState(state: ButtonState) {
        buttonState = state

        if (state is ButtonState.Completed) {
            invalidate()
        }
    }

    init {
        valueAnimator.setFloatValues(0f, 1f)

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_normal, 0)
            buttonClickedColor = getColor(R.styleable.LoadingButton_loading, 0)
        }

        paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = buttonBackgroundColor
        }

        paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
            textSize = context.resources.getDimension(R.dimen.default_text_size)
            color = textColor
        }

        paintLoadingButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = buttonClickedColor
        }

        paintLoadingCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(R.color.yellow)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        
        when(buttonState) {
            ButtonState.Clicked -> {
                // Nothing
            }
            ButtonState.Loading -> {
                drawLoadingButton(canvas)
                drawButtonText(canvas, context.getString(R.string.downloading_text))
                drawLoadingCircle(canvas)
            }
            ButtonState.Completed -> {
                drawButtonText(canvas, context.getString(R.string.download_text))
                drawButton(canvas)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    // Draw method that creates the loading circle
    private fun drawLoadingCircle(canvas: Canvas?) {
        val circle = RectF()
        val diameter = 50f
        circle.set((widthSize - 325f), (heightSize / 2) - diameter, widthSize - 225f, (heightSize / 2) + diameter)
        canvas?.drawArc(circle, 0F, sweep, true, paintLoadingCircle)
    }

    // Draw method that creates the starting button
    private fun drawButton(canvas: Canvas?) {
        canvas?.drawColor(paintButton.color)
        drawButtonText(canvas, context.getString(R.string.download_text))
    }

    // Draw method to create the button's color and loading function
    private fun drawLoadingButton(canvas: Canvas?) {
        canvas?.drawColor(paintButton.color)
        canvas?.drawRect(0f, 0f, currentWidth.toFloat(), heightSize.toFloat(), paintLoadingButton)
    }

    // Draw method to create the button's text
    private fun drawButtonText(canvas: Canvas?, text: String) {
        val textHeight = paintText.descent() - paintText.ascent()
        val textOffset = textHeight / 2 - paintText.descent()
        canvas?.drawText(text, widthSize.toFloat() / 2, heightSize.toFloat() / 2 + textOffset , paintText)
    }

}