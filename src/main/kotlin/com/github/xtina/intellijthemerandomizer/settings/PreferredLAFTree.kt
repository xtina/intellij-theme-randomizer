
package com.github.xtina.intellijthemerandomizer.settings

import com.intellij.ide.CommonActionsManager
import com.intellij.ide.DefaultTreeExpander
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.laf.UIThemeLookAndFeelInfo
import com.intellij.ide.ui.search.SearchUtil
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.ui.CheckboxTree
import com.intellij.ui.CheckedTreeNode
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.intellij.util.ui.tree.TreeUtil
import com.github.xtina.intellijthemerandomizer.MyBundle
import java.awt.BorderLayout
import java.awt.EventQueue
import java.util.LinkedList
import java.util.function.Predicate
import java.util.stream.Stream
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeNode

data class ThemeGroupData(
    val name: String,
    val lookAndFeels: List<UIThemeLookAndFeelInfo>
)

class PreferredLAFTree(
    private val selectionPredicate: Predicate<UIThemeLookAndFeelInfo>
) {
    private val themeCheckStatus: MutableMap<String, Boolean> = HashMap()
    val component: JComponent = JPanel(BorderLayout())
    private val myTree: CheckboxTree = createTree()
    private val myFilter: JBTextField = JBTextField()
    private val toolbarPanel: JPanel = JPanel(BorderLayout())
    private val myToggleAll = JBCheckBox()
    private val expandedPaths: MutableList<Any> = mutableListOf()

    init {
        initTree()
    }

    private fun initTree() {
        val scrollPane = ScrollPaneFactory.createScrollPane(myTree)
        myFilter.text = ""
        toolbarPanel.add(myFilter, BorderLayout.CENTER)
        toolbarPanel.border = JBUI.Borders.emptyBottom(2)

        // Add document listener for filtering
        myFilter.document.addDocumentListener(object : javax.swing.event.DocumentListener {
            override fun insertUpdate(e: javax.swing.event.DocumentEvent?) = performFiltering()
            override fun removeUpdate(e: javax.swing.event.DocumentEvent?) = performFiltering()
            override fun changedUpdate(e: javax.swing.event.DocumentEvent?) = performFiltering()
        })

        val group = DefaultActionGroup()
        val actionManager = CommonActionsManager.getInstance()
        val treeExpander = DefaultTreeExpander(myTree)

        group.add(actionManager.createExpandAllAction(treeExpander, myTree))
        group.add(actionManager.createCollapseAllAction(treeExpander, myTree))
        toolbarPanel.add(
            ActionManager.getInstance().createActionToolbar("PreferredThemeTree", group, true).component,
            BorderLayout.WEST
        )

        myToggleAll.isSelected = getAllNodes()
            .map { it.isChecked }
            .reduce { a, b -> a && b }
            .orElse(false)

        myToggleAll.text = MyBundle.message("settings.general.preferred-themes.toggle-all")
        myToggleAll.addActionListener {
            ApplicationManager.getApplication().invokeLater {
                getAllNodes().forEach { node -> node.isChecked = myToggleAll.isSelected }
            }
        }
        toolbarPanel.add(myToggleAll, BorderLayout.EAST)

        component.add(toolbarPanel, BorderLayout.NORTH)
        component.add(scrollPane, BorderLayout.CENTER)

        reset(copyAndSort(getThemeList()))
        myToggleAll.isSelected = getAllNodes()
            .map { it.isChecked }
            .reduce { a, b -> a && b }
            .orElse(false)
    }

    private fun performFiltering() {
        val filter = myFilter.text
        val filtered = filterModel(filter, true)
        refreshCheckStatus(myTree.model.root as CheckedTreeNode)
        reset(copyAndSort(filtered))
    }

    private fun createTree() = CheckboxTree(
        object : CheckboxTree.CheckboxTreeCellRenderer(true) {
            override fun customizeRenderer(
                tree: JTree,
                value: Any,
                selected: Boolean,
                expanded: Boolean,
                leaf: Boolean,
                row: Int,
                hasFocus: Boolean
            ) {
                if (value !is CheckedTreeNode) return

                val attributes = when (value.userObject) {
                    is ThemeGroupData -> SimpleTextAttributes.REGULAR_ATTRIBUTES
                    else -> SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES
                }

                val text = getNodeText(value)
                val background = UIUtil.getTreeBackground(selected, true)

                UIUtil.changeBackGround(this, background)
                SearchUtil.appendFragments(
                    myFilter.text,
                    text,
                    attributes.style,
                    attributes.fgColor,
                    background,
                    textRenderer
                )
            }
        },
        CheckedTreeNode(null)
    )

    fun filterModel(filter: String?, force: Boolean): List<ThemeGroupData> {
        if (filter.isNullOrEmpty()) return getThemeList()
        val result = getThemeList { it.name.contains(filter, ignoreCase = true) }
        return result.ifEmpty { if (force) getThemeList() else emptyList() }
    }

    fun filter(intentionsToShow: List<ThemeGroupData>) {
        refreshCheckStatus(myTree.model.root as CheckedTreeNode)
        reset(copyAndSort(intentionsToShow))
    }

    fun reset() {
        themeCheckStatus.clear()
        reset(copyAndSort(getThemeList()))
    }

    private fun getThemeList(predicate: (UIThemeLookAndFeelInfo) -> Boolean = { true }) =
        LafManager.getInstance().installedThemes
            .filter(predicate)
            .sortedBy { it.name }
            .groupBy { it.isDark }
            .map { ThemeGroupData(if (it.key) "Dark Themes" else "Light Themes", it.value) }

    private fun reset(sortedThemeData: List<ThemeGroupData>) {
        if (!EventQueue.isDispatchThread()) return

        val root = CheckedTreeNode(null)
        val treeModel = myTree.model as DefaultTreeModel

        sortedThemeData.forEach { themeData ->
            val themeRoot = CheckedTreeNode(themeData.name)
            themeData.lookAndFeels.forEach { uiLookAndFeel ->
                val themeNode = CheckedTreeNode(uiLookAndFeel)
                themeNode.isChecked = selectionPredicate.test(uiLookAndFeel)
                treeModel.insertNodeInto(themeNode, themeRoot, themeRoot.childCount)
            }

            themeRoot.isChecked = themeData.lookAndFeels.all { selectionPredicate.test(it) }
            treeModel.insertNodeInto(themeRoot, root, root.childCount)
        }

        treeModel.setRoot(root)
        treeModel.nodeChanged(root)
        TreeUtil.expandAll(myTree)
        myTree.setSelectionRow(0)
    }

    private val root: CheckedTreeNode
        get() = myTree.model.root as CheckedTreeNode

    fun getSelected(): List<UIThemeLookAndFeelInfo> = getSelectedThemes(root)

    private fun refreshCheckStatus(root: CheckedTreeNode) {
        when (val userObject = root.userObject) {
            is UIThemeLookAndFeelInfo -> themeCheckStatus[userObject.name] = root.isChecked
            else -> visitChildren(root) { refreshCheckStatus(it) }
        }
    }

    val isModified: Boolean
        get() = isModified(root, selectionPredicate)

    fun dispose() {
        // JBTextField doesn't need explicit disposal
    }

    private fun getAllNodes(): Stream<CheckedTreeNode> {
        val bob = Stream.builder<CheckedTreeNode>()
        traverseTree(root, bob::add)
        return bob.build()
    }


    companion object {

        private fun copyAndSort(data: List<ThemeGroupData>): List<ThemeGroupData> =
            data.sortedBy { it.name }

        private fun getNodeText(node: CheckedTreeNode): String =
            when (val userObject = node.userObject) {
                is UIThemeLookAndFeelInfo -> userObject.name
                is String -> userObject
                else -> "???"
            }

        private fun getSelectedThemes(root: CheckedTreeNode): List<UIThemeLookAndFeelInfo> {
            val selectedThemes = LinkedList<UIThemeLookAndFeelInfo>()
            traverseTree(root) {
                val userObject = it.userObject
                if (it.isChecked && userObject is UIThemeLookAndFeelInfo) {
                    selectedThemes.push(userObject)
                }
            }
            return selectedThemes
        }

        private fun traverseTree(root: CheckedTreeNode, consumer: (CheckedTreeNode) -> Unit) {
            val visitQueue = LinkedList<CheckedTreeNode>()
            visitQueue.push(root)
            while (visitQueue.isNotEmpty()) {
                val current = visitQueue.pop()
                consumer(current)
                val currentChildren = current.children()
                while (currentChildren.hasMoreElements()) {
                    visitQueue.push(currentChildren.nextElement() as CheckedTreeNode)
                }
            }
        }

        private fun isModified(
            root: CheckedTreeNode,
            selectionPredicate: Predicate<UIThemeLookAndFeelInfo>
        ): Boolean = when (val userObject = root.userObject) {
            is UIThemeLookAndFeelInfo -> selectionPredicate.test(userObject) != root.isChecked
            else -> {
                var modified = false
                visitChildren(root) { modified = modified or isModified(it, selectionPredicate) }
                modified
            }
        }

        private fun visitChildren(node: TreeNode, visitor: (CheckedTreeNode) -> Unit) {
            val children = node.children()
            while (children.hasMoreElements()) {
                visitor(children.nextElement() as CheckedTreeNode)
            }
        }
    }
}
