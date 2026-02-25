package com.github.xtina.intellijthemerandomizer.settings

import com.intellij.openapi.options.Configurable
import com.github.xtina.intellijthemerandomizer.MyBundle
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {

    private var component: AppSettingsComponent? = null

    override fun getDisplayName(): String = MyBundle.message("settings.displayName")

    override fun createComponent(): JComponent {
        component = AppSettingsComponent()
        return component!!.panel
    }

    override fun isModified(): Boolean {
        val state = AppSettings.getInstance().state
        val c = component ?: return false
        return c.randomizeOnStartupCheckBox.isSelected != state.randomizeOnStartup
                || c.randomizeOnProjectOpenCheckBox.isSelected != state.randomizeOnProjectOpen
                || c.excludeDarkThemesCheckBox.isSelected != state.excludeDarkThemes
                || c.excludeLightThemesCheckBox.isSelected != state.excludeLightThemes
    }

    override fun apply() {
        val state = AppSettings.getInstance().state
        val c = component ?: return
        state.randomizeOnStartup = c.randomizeOnStartupCheckBox.isSelected
        state.randomizeOnProjectOpen = c.randomizeOnProjectOpenCheckBox.isSelected
        state.excludeDarkThemes = c.excludeDarkThemesCheckBox.isSelected
        state.excludeLightThemes = c.excludeLightThemesCheckBox.isSelected
    }

    override fun reset() {
        val state = AppSettings.getInstance().state
        val c = component ?: return
        c.randomizeOnStartupCheckBox.isSelected = state.randomizeOnStartup
        c.randomizeOnProjectOpenCheckBox.isSelected = state.randomizeOnProjectOpen
        c.excludeDarkThemesCheckBox.isSelected = state.excludeDarkThemes
        c.excludeLightThemesCheckBox.isSelected = state.excludeLightThemes
    }

    override fun disposeUIResources() {
        component = null
    }
}
