@file:Suppress("UnstableApiUsage")

package com.github.xtina.intellijthemerandomizer.settings

import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service(Service.Level.APP)
class ThemeGatekeeper : Disposable {

    private val preferredThemeIds: Set<String>
        get() = AppSettings.instance.selectedThemes
            .split(AppSettings.DEFAULT_DELIMITER)
            .filter { it.isNotBlank() }
            .toSet()

    private val blackListedThemeIds: Set<String>
        get() = AppSettings.instance.blacklistedThemes
            .split(AppSettings.DEFAULT_DELIMITER)
            .filter { it.isNotBlank() }
            .toSet()

    fun isPreferred(lookAndFeelInfo: UIThemeLookAndFeelInfo): Boolean =
        preferredThemeIds.contains(lookAndFeelInfo.id)

    fun isBlackListed(lookAndFeelInfo: UIThemeLookAndFeelInfo): Boolean =
        blackListedThemeIds.contains(lookAndFeelInfo.id)

    override fun dispose() = Unit

    companion object {
        val instance: ThemeGatekeeper
            get() = service()

        @JvmStatic
        fun getId(lookAndFeelInfo: UIThemeLookAndFeelInfo): String = lookAndFeelInfo.id
    }
}
