import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class ServiceProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {
    private var builderGenerated = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("builderGenerated=$builderGenerated resolver.getAllFiles()=${resolver.getAllFiles().count()}")
        if (!builderGenerated) {
            val file = codeGenerator.createNewFile(Dependencies.ALL_FILES, "com.example", "BuilderFactory")
            file.appendText("package com.example\n")
            file.appendText("class ServiceFactory{\n")
            file.appendText("fun getService(name:String):IService?{\n")
            resolver.getSymbolsWithAnnotation("Service").filterIsInstance<KSClassDeclaration>().forEach {
                file.appendText("if(name==\"${it.simpleName.asString()}\"){\n")
                file.appendText("return ${it.simpleName.asString()}()\n")
                file.appendText("}\n")
            }
            file.appendText("return null")
            file.appendText("}")
            file.appendText("}")
        }
        builderGenerated = true
        return emptyList()
    }


}

class ServiceProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ServiceProcessor(environment.codeGenerator, environment.logger)
    }

}
