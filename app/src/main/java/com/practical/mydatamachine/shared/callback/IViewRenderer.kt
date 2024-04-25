package com.practical.mydatamachine.shared.callback

interface IViewRenderer<STATE> {
    fun render(state: STATE)
}