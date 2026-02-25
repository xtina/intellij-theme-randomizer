@file:Suppress("UnstableApiUsage")

package com.github.xtina.intellijthemerandomizer.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.project.DumbAware
import com.intellij.util.ui.JBUI
import com.github.xtina.intellijthemerandomizer.MyBundle
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.util.stream.Collectors
import javax.swing.*

class AppSettingsConfigurable : SearchableConfigurable, DumbAware {

    private var rootPanel: JPanel? = null

    private var pluginModeComboBox: JComboBox<PluginMode>? = null
    private var changeIntervalComboBox: JComboBox<ChangeIntervals>? = null
    private var changeThemeCheckbox: JCheckBox? = null
    private var randomOrderCheckbox: JCheckBox? = null
    private var animationCheckbox: JCheckBox? = null
    private var matchOSCheckBox: JCheckBox? = null
    private var localSyncCheckBox: JCheckBox? = null
    private var systemChangeSpinner: JSpinner? = null
    private var timedSettingsPanel: JPanel? = null
    private var systemMatchSettingsPanel: JPanel? = null

    private var lafListTree: PreferredLAFTree? = null
    private var blackListTree: PreferredLAFTree? = null

    private var savedInterval: String = AppSettings.instance.interval
    private var savedIsChangeTheme: Boolean = AppSettings.instance.isChangeTheme
    private var savedIsRandomOrder: Boolean = AppSettings.instance.isRandomOrder
    private var savedIsThemeTransition: Boolean = AppSettings.instance.isThemeTransition
    private var savedPluginMode: PluginMode = AppSettings.instance.pluginMode.toPluginMode()
    private var savedChangeOnSystemSwitches: Int = AppSettings.instance.changeOnSystemSwitches
    private var savedIsLocalSync: Boolean = AppSettings.instance.isLocalSync
    private var savedIsTimedMatchOS: Boolean = AppSettings.instance.isTimedMatchOS

    override fun getId(): String = "com.github.xtina.intellijthemerandomizer.settings"

    override fun getDisplayName(): String = MyBundle.message("settings.displayName")

    override fun createComponent(): JComponent {
        lafListTree = PreferredLAFTree { ThemeGatekeeper.instance.isPreferred(it) }
        blackListTree = PreferredLAFTree { ThemeGatekeeper.instance.isBlackListed(it) }

        val root = JPanel(BorderLayout())
        val tabbedPane = JTabbedPane()

        val generalPanel = buildGeneralPanel()
        tabbedPane.addTab(MyBundle.message("settings.general.tab"), generalPanel)

        root.add(tabbedPane, BorderLayout.CENTER)
        rootPanel = root
        return root
    }

    private fun buildGeneralPanel(): JPanel {
        val panel = JPanel(BorderLayout())

        val topSection = JPanel()
        topSection.layout = BoxLayout(topSection, BoxLayout.Y_AXIS)

        topSection.add(buildPluginModeRow())
        topSection.add(buildSettingsSection())

        val themeTabs = buildThemeTabs()

        panel.add(topSection, BorderLayout.NORTH)
        panel.add(themeTabs, BorderLayout.CENTER)
        return panel
    }

    private fun buildPluginModeRow(): JPanel {
        val row = JPanel(GridBagLayout())
        row.border = JBUI.Borders.empty(4)
        val gbc = GridBagConstraints()

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST
        gbc.insets = JBUI.insets(2, 4)
        row.add(JLabel(MyBundle.message("settings.general.mode")), gbc)

        pluginModeComboBox = JComboBox(PluginMode.entries.toTypedArray()).apply {
            selectedItem = savedPluginMode
            addActionListener { updateModeVisibility() }
        }
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.0
        row.add(pluginModeComboBox, gbc)

        gbc.gridx = 2; gbc.weightx = 1.0
        row.add(JPanel(), gbc)

        return row
    }

    private fun buildSettingsSection(): JPanel {
        val border = BorderFactory.createTitledBorder(MyBundle.message("settings.general.title"))
        val section = JPanel(GridBagLayout())
        section.border = border
        val gbc = GridBagConstraints()

        changeThemeCheckbox = JCheckBox(MyBundle.message("settings.general.settings.change-picture")).apply {
            isSelected = savedIsChangeTheme
        }
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST
        gbc.insets = JBUI.insets(2, 4)
        section.add(changeThemeCheckbox, gbc)

        timedSettingsPanel = buildTimedSettingsPanel()
        systemMatchSettingsPanel = buildSystemMatchSettingsPanel()

        val modeSettingsContainer = JPanel(BorderLayout())
        modeSettingsContainer.add(timedSettingsPanel, BorderLayout.NORTH)
        modeSettingsContainer.add(systemMatchSettingsPanel, BorderLayout.SOUTH)

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1; gbc.fill = GridBagConstraints.HORIZONTAL
        section.add(modeSettingsContainer, gbc)

        randomOrderCheckbox = JCheckBox(MyBundle.message("settings.general.setting.random-order")).apply {
            isSelected = savedIsRandomOrder
        }
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridheight = 1; gbc.fill = GridBagConstraints.NONE
        section.add(randomOrderCheckbox, gbc)

        localSyncCheckBox = JCheckBox(MyBundle.message("settings.general.setting.auto-sync")).apply {
            isSelected = savedIsLocalSync
        }
        gbc.gridx = 1; gbc.gridy = 1
        section.add(localSyncCheckBox, gbc)

        animationCheckbox = JCheckBox(MyBundle.message("settings.general.settings.laf-animation")).apply {
            isSelected = savedIsThemeTransition
        }
        gbc.gridx = 0; gbc.gridy = 2
        section.add(animationCheckbox, gbc)

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL
        section.add(JPanel(), gbc)

        updateModeVisibility()
        return section
    }

    private fun buildTimedSettingsPanel(): JPanel {
        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.anchor = GridBagConstraints.WEST
        gbc.insets = JBUI.insets(2, 4)

        changeIntervalComboBox = JComboBox(ChangeIntervals.entries.toTypedArray()).apply {
            ChangeIntervals.getValue(savedInterval)?.let { selectedItem = it }
        }
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL
        panel.add(changeIntervalComboBox, gbc)

        matchOSCheckBox = JCheckBox(MyBundle.message("settings.general.mode.timed.match.os")).apply {
            isSelected = savedIsTimedMatchOS
        }
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE
        panel.add(matchOSCheckBox, gbc)

        return panel
    }

    private fun buildSystemMatchSettingsPanel(): JPanel {
        val panel = JPanel(GridBagLayout())
        val gbc = GridBagConstraints()
        gbc.anchor = GridBagConstraints.WEST
        gbc.insets = JBUI.insets(2, 4)

        gbc.gridx = 0; gbc.gridy = 0
        panel.add(JLabel(MyBundle.message("settings.config.system-match.number-changes")), gbc)

        systemChangeSpinner = JSpinner(SpinnerNumberModel(savedChangeOnSystemSwitches, 0, Int.MAX_VALUE, 1))
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL
        panel.add(systemChangeSpinner, gbc)

        return panel
    }

    private fun buildThemeTabs(): JTabbedPane {
        val tabs = JTabbedPane()

        val preferredPanel = JPanel(BorderLayout()).apply {
            lafListTree?.component?.let { add(it, BorderLayout.CENTER) }
            preferredSize = JBUI.size(800, 600)
            border = JBUI.Borders.empty(10)
        }
        tabs.addTab(MyBundle.message("settings.general.preferred-themes"), preferredPanel)

        val blacklistPanel = JPanel(BorderLayout()).apply {
            blackListTree?.component?.let { add(it, BorderLayout.CENTER) }
            preferredSize = JBUI.size(800, 600)
            border = JBUI.Borders.empty(10)
        }
        tabs.addTab(MyBundle.message("settings.general.blacklist"), blacklistPanel)

        return tabs
    }

    private fun updateModeVisibility() {
        val mode = pluginModeComboBox?.selectedItem as? PluginMode ?: PluginMode.TIMED
        timedSettingsPanel?.isVisible = mode == PluginMode.TIMED
        systemMatchSettingsPanel?.isVisible = mode == PluginMode.SYSTEM_MATCH
    }

    override fun isModified(): Boolean {
        val settings = AppSettings.instance
        return changeThemeCheckbox?.isSelected != settings.isChangeTheme
                || randomOrderCheckbox?.isSelected != settings.isRandomOrder
                || animationCheckbox?.isSelected != settings.isThemeTransition
                || localSyncCheckBox?.isSelected != settings.isLocalSync
                || matchOSCheckBox?.isSelected != settings.isTimedMatchOS
                || (pluginModeComboBox?.selectedItem as? PluginMode)?.displayName != settings.pluginMode
                || (changeIntervalComboBox?.selectedItem as? ChangeIntervals)?.name != settings.interval
                || (systemChangeSpinner?.value as? Int) != settings.changeOnSystemSwitches
                || (lafListTree?.isModified == true)
                || (blackListTree?.isModified == true)
    }

    override fun apply() {
        val settings = AppSettings.instance
        settings.isChangeTheme = changeThemeCheckbox?.isSelected ?: settings.isChangeTheme
        settings.isRandomOrder = randomOrderCheckbox?.isSelected ?: settings.isRandomOrder
        settings.isThemeTransition = animationCheckbox?.isSelected ?: settings.isThemeTransition
        settings.isLocalSync = localSyncCheckBox?.isSelected ?: settings.isLocalSync
        settings.isTimedMatchOS = matchOSCheckBox?.isSelected ?: settings.isTimedMatchOS
        settings.pluginMode = (pluginModeComboBox?.selectedItem as? PluginMode)?.displayName ?: settings.pluginMode
        settings.interval = (changeIntervalComboBox?.selectedItem as? ChangeIntervals)?.name ?: settings.interval
        settings.changeOnSystemSwitches = (systemChangeSpinner?.value as? Int) ?: settings.changeOnSystemSwitches

        settings.selectedThemes = lafListTree?.getSelected()
            ?.joinToString(AppSettings.DEFAULT_DELIMITER) { ThemeGatekeeper.getId(it) } ?: settings.selectedThemes
        settings.blacklistedThemes = blackListTree?.getSelected()
            ?.joinToString(AppSettings.DEFAULT_DELIMITER) { ThemeGatekeeper.getId(it) } ?: settings.blacklistedThemes

        savedInterval = settings.interval
        savedIsChangeTheme = settings.isChangeTheme
        savedIsRandomOrder = settings.isRandomOrder
        savedIsThemeTransition = settings.isThemeTransition
        savedPluginMode = settings.pluginMode.toPluginMode()
        savedChangeOnSystemSwitches = settings.changeOnSystemSwitches
        savedIsLocalSync = settings.isLocalSync
        savedIsTimedMatchOS = settings.isTimedMatchOS
    }

    override fun reset() {
        val settings = AppSettings.instance
        changeThemeCheckbox?.isSelected = settings.isChangeTheme
        randomOrderCheckbox?.isSelected = settings.isRandomOrder
        animationCheckbox?.isSelected = settings.isThemeTransition
        localSyncCheckBox?.isSelected = settings.isLocalSync
        matchOSCheckBox?.isSelected = settings.isTimedMatchOS
        pluginModeComboBox?.selectedItem = settings.pluginMode.toPluginMode()
        ChangeIntervals.getValue(settings.interval)?.let { changeIntervalComboBox?.selectedItem = it }
        systemChangeSpinner?.value = settings.changeOnSystemSwitches
        lafListTree?.reset()
        blackListTree?.reset()
        updateModeVisibility()
    }

    override fun disposeUIResources() {
        lafListTree?.dispose()
        blackListTree?.dispose()
        lafListTree = null
        blackListTree = null
        rootPanel = null
    }
}
