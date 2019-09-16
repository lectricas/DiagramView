package com.polusov.infinitediagram.pmstuff

import android.os.Bundle
import com.jakewharton.rxbinding2.view.clicks
import com.polusov.infinitediagram.R
import com.polusov.infinitediagram.pmstuff.MainActivityPm.Act.AddSandwichClicked
import com.polusov.infinitediagram.pmstuff.MainActivityPm.Act.PredefinedSandwichSelected
import com.polusov.infinitediagram.pmstuff.MainActivityPm.Act.SandwichTypeSelected
import com.polusov.infinitediagram.pmstuff.MainActivityPm.Act.SubmitSandwichClicked
import com.polusov.infinitediagram.pmstuff.MainActivityPm.AddSandwich
import com.polusov.infinitediagram.pmstuff.MainActivityPm.NameSandwich
import com.polusov.infinitediagram.pmstuff.MainActivityPm.Sandwich
import com.polusov.infinitediagram.pmstuff.MainActivityPm.SandwichList
import com.polusov.infinitediagram.pmstuff.MainActivityPm.SandwichType.GRINDER
import com.polusov.infinitediagram.pmstuff.MainActivityPm.SandwichType.WRAP
import com.polusov.visible
import kotlinx.android.synthetic.main.activity_pm.addSandwichClicked
import kotlinx.android.synthetic.main.activity_pm.newState
import kotlinx.android.synthetic.main.activity_pm.predefinedSandwichSelected
import kotlinx.android.synthetic.main.activity_pm.sandwichTypeSelected
import kotlinx.android.synthetic.main.activity_pm.submitSandwichClicked
import me.dmdev.rxpm.base.PmSupportActivity
import timber.log.Timber

class RxPmActivity : PmSupportActivity<MainActivityPm>() {
    override fun onBindPresentationModel(pmActivity: MainActivityPm) {

        addSandwichClicked.clicks()
            .map { AddSandwichClicked() }
            .bindTo(pmActivity.stateAction.consumer)

        sandwichTypeSelected.clicks()
            .map { SandwichTypeSelected(GRINDER) }
            .bindTo(pmActivity.stateAction.consumer)

        predefinedSandwichSelected.clicks()
            .map { PredefinedSandwichSelected(Sandwich("it", WRAP)) }
            .bindTo(pmActivity.stateAction.consumer)

        submitSandwichClicked.clicks()
            .map { SubmitSandwichClicked("it") }
            .bindTo(pmActivity.stateAction.consumer)

        pmActivity.stateMachine.bindTo { state ->
            when(state) {
                is SandwichList -> {
                    addSandwichClicked.visible = true
                    sandwichTypeSelected.visible = false
                    predefinedSandwichSelected.visible = false
                    submitSandwichClicked.visible = false
                }
                is AddSandwich -> {
                    addSandwichClicked.visible = false
                    sandwichTypeSelected.visible = true
                    predefinedSandwichSelected.visible = true
                    submitSandwichClicked.visible = false
                }

                is NameSandwich -> {
                    addSandwichClicked.visible = false
                    sandwichTypeSelected.visible = false
                    predefinedSandwichSelected.visible = false
                    submitSandwichClicked.visible = true
                }
            }
            newState.text = state.toString()
        }
    }

    override fun providePresentationModel(): MainActivityPm {
        return MainActivityPm()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pm)
        Timber.d("AppStarts")
    }
}
