package io.github.pavelannin.keemun.plugin.template

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider
import io.github.pavelannin.keemun.plugin.template.templates.feature.featureTemplate

class KeemunTemplateProvider : WizardTemplateProvider() {
    override fun getTemplates(): List<Template> = listOf(featureTemplate)
}
