package com.github.xtina.intellijthemerandomizer.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil

@Service(Service.Level.APP)
@State(
    name = "Theme-Randomizer-Config",
    storages = [Storage("ThemeRandomizerSettings.xml")]
)
class AppSettings : PersistentStateComponent<AppSettings> {
    var interval: String = ChangeIntervals.DAY.name
    var isChangeTheme: Boolean = true
    var isRandomOrder: Boolean = true
    var isThemeTransition: Boolean = false
    var pluginMode: String = PluginMode.TIMED.displayName
    var selectedThemes: String = ""
    var blacklistedThemes: String = ""
    var changeOnSystemSwitches: Int = 1
    var isLocalSync: Boolean = false
    var isTimedMatchOS: Boolean = false

    override fun getState(): AppSettings = this

    override fun loadState(state: AppSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        const val DEFAULT_DELIMITER = ","

        @JvmStatic
        val instance: AppSettings
            get() = service()
    }
}
