package com.github.xtina.intellijthemerandomizer.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.APP)
@State(
    name = "com.github.xtina.intellijthemerandomizer.settings.AppSettings",
    storages = [Storage("ThemeRandomizerSettings.xml")]
)
class AppSettings : PersistentStateComponent<AppSettings.State> {

    class State {
        var randomizeOnStartup: Boolean = false
        var randomizeOnProjectOpen: Boolean = false
        var excludeDarkThemes: Boolean = false
        var excludeLightThemes: Boolean = false
    }

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): AppSettings =
            ApplicationManager.getApplication().getService(AppSettings::class.java)
    }
}
