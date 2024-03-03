package io.github.pavelannin.keemun.plugin.template.templates.feature

import com.android.tools.idea.wizard.template.Category
import com.android.tools.idea.wizard.template.CheckBoxWidget
import com.android.tools.idea.wizard.template.Constraint
import com.android.tools.idea.wizard.template.EnumWidget
import com.android.tools.idea.wizard.template.FormFactor
import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.PackageNameWidget
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.TextFieldWidget
import com.android.tools.idea.wizard.template.WizardUiContext
import com.android.tools.idea.wizard.template.booleanParameter
import com.android.tools.idea.wizard.template.enumParameter
import com.android.tools.idea.wizard.template.impl.defaultPackageNameParameter
import com.android.tools.idea.wizard.template.stringParameter
import com.android.tools.idea.wizard.template.template
import io.github.pavelannin.keemun.plugin.template.core.FeatureName
import io.github.pavelannin.keemun.plugin.template.core.classPrefix
import io.github.pavelannin.keemun.plugin.template.core.combine
import io.github.pavelannin.keemun.plugin.template.core.fileString
import io.github.pavelannin.keemun.plugin.template.core.functionPrefix
import java.io.File

val featureTemplate: Template
    get() = template {
        minApi = 16
        name = "Keemun Feature"
        description = "Create new feature"
        category = Category.Other
        formFactor = FormFactor.Generic

        val featureName = stringParameter {
            name = "Feature Name"
            default = "Some"
            constraints = listOf(Constraint.NONEMPTY)
        }
        val storeStructure = enumParameter<FeatureTemplate.StoreStructure> {
            name = "Structure of the store files"
            default = FeatureTemplate.StoreStructure.Single
        }
        val msgStructure = enumParameter<FeatureTemplate.MsgStructure> {
            name = "Structure of the Msg class"
            default = FeatureTemplate.MsgStructure.Distributed
        }
        val isInputEventNeed = booleanParameter {
            name = "Add Input event"
            default = false
        }
        val isOutputEventNeed = booleanParameter {
            name = "Add Output event"
            default = false
        }
        val isUiComposeNeed = booleanParameter {
            name = "Add a file for the UI"
            default = true
        }
        val connectorType = enumParameter<FeatureTemplate.Connector> {
            name = "Connector type"
            default = FeatureTemplate.Connector.Decompose
        }
        val packageName = defaultPackageNameParameter

        screens = listOf(
            WizardUiContext.ActivityGallery,
            WizardUiContext.MenuEntry,
            WizardUiContext.NewProject,
            WizardUiContext.NewModule,
        )

        widgets(
            TextFieldWidget(featureName),
            EnumWidget(storeStructure),
            EnumWidget(msgStructure),
            CheckBoxWidget(isInputEventNeed),
            CheckBoxWidget(isOutputEventNeed),
            CheckBoxWidget(isUiComposeNeed),
            EnumWidget(connectorType),
            PackageNameWidget(packageName),
        )

        recipe = {
            setup(
                moduleData = it as ModuleTemplateData,
                template = FeatureTemplate(
                    name = FeatureName(featureName.value),
                    storeStructure = storeStructure.value,
                    msgStructure = msgStructure.value,
                    isInputEventNeed = isInputEventNeed.value,
                    isOutputEventNeed = isOutputEventNeed.value,
                    isUiComposeNeed = isUiComposeNeed.value,
                    connectorType = connectorType.value,

                ),
                packageName = packageName.value,
            )
        }
    }


data class FeatureTemplate(
    val name: FeatureName,
    val storeStructure: StoreStructure,
    val msgStructure: MsgStructure,
    val isInputEventNeed: Boolean,
    val isOutputEventNeed: Boolean,
    val isUiComposeNeed: Boolean,
    val connectorType: Connector,
) {

    enum class StoreStructure {
        Single, Multi;

        override fun toString(): String = when (this) {
            Single -> "One file"
            Multi -> "Multi files"
        }
    }

    enum class MsgStructure {
        Unified, Distributed;

        override fun toString(): String = when (this) {
            Unified -> "Unified Msg class"
            Distributed -> "Two Msg class (External & Internal)"
        }


    }

    enum class Connector { Decompose }
}

val FeatureTemplate.viewStateTransformName: String get() = "${name.functionPrefix}StateTransform"
val FeatureTemplate.viewStateName: String get() = "${name.classPrefix}ViewState"
val FeatureTemplate.stateName: String get() = "${name.classPrefix}State"
val FeatureTemplate.effectName: String get() = "${name.classPrefix}Effect"
val FeatureTemplate.effectHandlerName: String get() = "${name.functionPrefix}EffectHandler"
val FeatureTemplate.inputEventName: String get() = "${name.classPrefix}InputEvent"
val FeatureTemplate.outputEventName: String get() = "${name.classPrefix}OutputEvent"
val FeatureTemplate.msgName: String get() = "${name.classPrefix}Msg"
val FeatureTemplate.externalMsgName: String get() = "${name.classPrefix}ExternalMsg"
val FeatureTemplate.internalMsgName: String get() = "${name.classPrefix}InternalMsg"
val FeatureTemplate.updateName: String get() = "${name.functionPrefix}Update"
val FeatureTemplate.externalUpdateName: String get() = "${name.functionPrefix}ExternalUpdate"
val FeatureTemplate.internalUpdateName: String get() = "${name.functionPrefix}InternalUpdate"
val FeatureTemplate.storeParamsName: String get() = "${name.functionPrefix}StoreParams"
val FeatureTemplate.uiName: String get() = "${name.classPrefix}Ui"

fun RecipeExecutor.setup(
    moduleData: ModuleTemplateData,
    template: FeatureTemplate,
    packageName: String
) {
    val srcOut = File(moduleData.srcDir.absolutePath.replace("java","kotlin"))

    val storePackage = "$packageName.store"
    val uiPackage = "$packageName.ui"

    when (template.storeStructure) {
        FeatureTemplate.StoreStructure.Single -> {
            save(
                listOfNotNull(
                    template.storeParams(),
                    template.update(),
                    template.effectHandler(),
                    template.state(),
                    template.inputEvent().takeIf { template.isInputEventNeed },
                    template.outputEvent().takeIf { template.isOutputEventNeed },
                    template.msg(),
                    template.effect(),
                ).combine().fileString(storePackage),
                srcOut.resolve("store/${template.name.classPrefix}StoreParams.kt"),
            )
            open(srcOut.resolve("store/${template.name.classPrefix}StoreParams.kt"))
        }

        FeatureTemplate.StoreStructure.Multi -> {
            save(
                listOfNotNull(
                    template.storeParams(),
                    template.state(),
                    template.inputEvent().takeIf { template.isInputEventNeed },
                    template.outputEvent().takeIf { template.isOutputEventNeed },
                ).combine().fileString(storePackage),
                srcOut.resolve("store/${template.name.classPrefix}StoreParams.kt"),
            )
            open(srcOut.resolve("store/${template.name.classPrefix}StoreParams.kt"))

            save(
                listOfNotNull(
                    template.update(),
                    template.msg(),
                ).combine().fileString(storePackage),
                srcOut.resolve("store/${template.name.classPrefix}Update.kt"),
            )
            open(srcOut.resolve("store/${template.name.classPrefix}Update.kt"))

            save(
                listOfNotNull(
                    template.effectHandler(),
                    template.effect(),
                ).combine().fileString(storePackage),
                srcOut.resolve("store/${template.name.classPrefix}Effect.kt"),
            )
            open(srcOut.resolve("store/${template.name.classPrefix}Effect.kt"))
        }
    }

    save(
        listOfNotNull(
            template.viewStateTransform(),
            template.viewState(),
        ).combine().fileString(storePackage),
        srcOut.resolve("store/${template.name.classPrefix}ViewState.kt"),
    )
    open(srcOut.resolve("store/${template.name.classPrefix}ViewState.kt"))

    if (template.isUiComposeNeed) {
        save(
            template.ui(storePackage).fileString(uiPackage),
            srcOut.resolve("ui/${template.name.classPrefix}Ui.kt"),
        )
        open(srcOut.resolve("ui/${template.name.classPrefix}Ui.kt"))
    }
}
