package com.github.xtina.intellijthemerandomizer.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.github.xtina.intellijthemerandomizer.ThemeRandomizer

class RandomizeThemeAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        ThemeRandomizer.randomize()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = ThemeRandomizer.getCandidateThemes().isNotEmpty()
    }
}
