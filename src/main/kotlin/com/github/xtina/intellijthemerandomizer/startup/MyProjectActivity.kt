package com.github.xtina.intellijthemerandomizer.startup

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class MyProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {
        ThemeSchedulerService.instance.start()
    }
}