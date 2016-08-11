package org.intellij.plugins.packer;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonFile;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.patterns.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.XmlPatterns.*;


public class PackerShellProvisionerReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        final PsiFilePattern.Capture<JsonFile> filePattern = psiFile(JsonFile.class);

        final PsiElementPattern.Capture<JsonStringLiteral> script = PlatformPatterns.psiElement(JsonStringLiteral.class).inside(true, psiElement(JsonArray.class)).inside(true, psiElement(JsonProperty.class).withName("scripts"));

        registrar.registerReferenceProvider(script, new PackerShellScriptToFilePsiReferenceProvider());
    }

}
