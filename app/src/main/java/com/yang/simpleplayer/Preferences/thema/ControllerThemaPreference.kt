package com.yang.simpleplayer.Preferences.thema

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.yang.simpleplayer.R
import com.yang.simpleplayer.SimplePlayerApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ControllerThemaPreference(context: Context, attrs: AttributeSet) :
    Preference(context, attrs) {
    init {
        layoutResource= R.layout.preference_controller_thema
    }
    private val userPreferenceRepository = (context.applicationContext as SimplePlayerApplication).appContainer.userPreferencesRepository
    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        holder.itemView.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        super.onBindViewHolder(holder)
        holder.itemView.isClickable = false
        val imageView:ImageView = holder.findViewById(R.id.controllerThemaImageView) as ImageView
        val summary:TextView = holder.findViewById(R.id.controllerThemaSummary) as TextView
        val buttonRadioBtn:RadioButton = (holder.findViewById(R.id.buttonControllerRadioBtn) as RadioButton).apply {
            setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    userPreferenceRepository.updateControllerThema(ControllerThema.BUTTON.ordinal)
                }
            }
        }
        val touchRadioBtn:RadioButton = (holder.findViewById(R.id.touchControllerRadioBtn) as RadioButton).apply {
            setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    userPreferenceRepository.updateControllerThema(ControllerThema.TOUCH.ordinal)
                }
            }
        }
        val setViews = {imageResId:Int, summaryResId:Int ->
            imageView.setImageResource(imageResId)
            summary.setText(summaryResId)
        }
        GlobalScope.launch(Dispatchers.Main) {
            userPreferenceRepository.userPreferencesFlow.collect {
                when(it.controllerThema) {
                    ControllerThema.BUTTON.ordinal -> {
                        setViews(R.drawable.button_controller_thema, R.string.button_controller_summary)
                        buttonRadioBtn.isChecked = true
                    }
                    ControllerThema.TOUCH.ordinal -> {
                        setViews(R.drawable.touch_controller_thema, R.string.touch_controller_summary)
                        touchRadioBtn.isChecked = true
                    }
                }
            }
        }
    }
}