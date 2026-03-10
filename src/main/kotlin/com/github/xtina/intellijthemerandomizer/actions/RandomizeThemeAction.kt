package com.github.xtina.intellijthemerandomizer.actions

import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.github.xtina.intellijthemerandomizer.settings.AppSettings
import com.github.xtina.intellijthemerandomizer.settings.ThemeGatekeeper

class RandomizeThemeAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val candidates = getCandidateThemes()
        if (candidates.isEmpty()) return

        val currentThemeId = LafManager.getInstance().currentUIThemeLookAndFeel?.id
        val filtered = if (candidates.size > 1) {
            candidates.filter { it.id != currentThemeId }
        } else {
            candidates
        }

        val chosen = filtered.random()
        LafManager.getInstance().setCurrentLookAndFeel(chosen, false)
        LafManager.getInstance().updateUI()
    }

    private fun getCandidateThemes(): List<UIThemeLookAndFeelInfo> {
        val gatekeeper = ThemeGatekeeper.instance
        val settings = AppSettings.instance
        val allThemes = LafManager.getInstance().installedThemes.toList()

        val hasPreferred = settings.selectedThemes.isNotBlank()
        return if (hasPreferred) {
            allThemes.filter { gatekeeper.isPreferred(it) }
        } else {
            allThemes.filter { !gatekeeper.isBlackListed(it) }
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = getCandidateThemes().isNotEmpty()
    }
}
