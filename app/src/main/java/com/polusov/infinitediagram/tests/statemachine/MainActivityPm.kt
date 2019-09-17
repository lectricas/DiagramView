package com.polusov.infinitediagram.tests.statemachine

import me.dmdev.rxpm.PresentationModel

class MainActivityPm : PresentationModel() {

    val stateMachine = State<SandwichState>(
        SandwichList(
            listOf()
        )
    )
    val stateAction = Action<Act>()

    override fun onCreate() {
        super.onCreate()

        stateAction.observable
            .map {
                stateMachine.value.consumeAction(it)
            }
            .subscribe(stateMachine.consumer)
            .untilDestroy()
    }

    interface SandwichState {
        fun consumeAction(action: Act): SandwichState
    }

    sealed class Act {
        class AddSandwichClicked : Act()
        class SandwichTypeSelected(val type: SandwichType) : Act()
        class PredefinedSandwichSelected(val sandwich: Sandwich) : Act()
        class SubmitSandwichClicked(val sandwichName: String) : Act()
    }

    data class Sandwich(val name: String, val type: SandwichType)

    enum class SandwichType {
        GRINDER,
        WRAP
    }

    class SandwichList(private val sandwiches: List<Sandwich>) :
        SandwichState {
        override fun consumeAction(action: Act): SandwichState {
            return when (action) {
                is Act.AddSandwichClicked -> AddSandwich(
                    sandwiches
                )
                else -> throw IllegalStateException("Invalid action $action passed to state $this")
            }
        }
    }

    class AddSandwich(private val sandwiches: List<Sandwich>) :
        SandwichState {
        override fun consumeAction(action: Act): SandwichState {
            return when (action) {
                is Act.SandwichTypeSelected -> NameSandwich(
                    sandwiches,
                    action.type
                )
                is Act.PredefinedSandwichSelected -> {
                    SandwichList(sandwiches + action.sandwich)
                }
                else -> throw IllegalStateException("Invalid action $action passed to state $this")
            }
        }
    }

    class NameSandwich(
        private val sandwiches: List<Sandwich>,
        private val newSandwichType: SandwichType
    ) : SandwichState {

        override fun consumeAction(action: Act): SandwichState {
            return when (action) {
                is Act.SubmitSandwichClicked -> {
                    val newSandwich = Sandwich(
                        action.sandwichName,
                        newSandwichType
                    )
                    SandwichList(sandwiches + newSandwich)
                }
                else -> throw IllegalStateException("Invalid action $action passed to state $this")
            }
        }
    }
}