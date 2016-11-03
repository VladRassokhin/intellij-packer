package org.intellij.plugins.packer;

import com.intellij.json.psi.*;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.PsiFilePattern;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.XmlPatterns.*;


public class PackerShellProvisionerReferenceContributor extends PsiReferenceContributor {

    public static final PsiFilePattern.Capture<JsonFile> PACKER_CONFIG_JSON_PATTERN =
        psiFile(JsonFile.class)
            .inVirtualFile(virtualFile().notNull())
            .withChild(psiElement(JsonObject.class).withChild(psiElement(JsonProperty.class).withName("builders")));

    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        final PsiElementPattern.Capture<JsonStringLiteral> scripts = PlatformPatterns.psiElement(JsonStringLiteral.class)
            .inFile(PACKER_CONFIG_JSON_PATTERN)
            .withParent(psiElement(JsonArray.class).withParent(psiElement(JsonProperty.class).withName("scripts")));
        registrar.registerReferenceProvider(scripts, new PackerShellScriptToFilePsiReferenceProvider());
        final PsiElementPattern.Capture<JsonStringLiteral> script = PlatformPatterns.psiElement(JsonStringLiteral.class)
            .inFile(PACKER_CONFIG_JSON_PATTERN)
            .withParent(psiElement(JsonProperty.class).withName("script"));
        registrar.registerReferenceProvider(script, new PackerShellScriptToFilePsiReferenceProvider());
    }

}
