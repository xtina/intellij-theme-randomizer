package com.github.xtina.intellijthemerandomizer.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.util.ui.FormBuilder
import com.github.xtina.intellijthemerandomizer.MyBundle
import javax.swing.JPanel

class AppSettingsComponent {

    val panel: JPanel
    val randomizeOnStartupCheckBox = JBCheckBox(MyBundle.message("settings.randomizeOnStartup"))
    val randomizeOnProjectOpenCheckBox = JBCheckBox(MyBundle.message("settings.randomizeOnProjectOpen"))
    val excludeDarkThemesCheckBox = JBCheckBox(MyBundle.message("settings.excludeDarkThemes"))
    val excludeLightThemesCheckBox = JBCheckBox(MyBundle.message("settings.excludeLightThemes"))

    init {
        panel = FormBuilder.createFormBuilder()
            .addComponent(randomizeOnStartupCheckBox, 1)
            .addComponent(randomizeOnProjectOpenCheckBox, 1)
            .addSeparator()
            .addComponent(excludeDarkThemesCheckBox, 1)
            .addComponent(excludeLightThemesCheckBox, 1)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
}
