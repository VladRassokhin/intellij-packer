package org.intellij.plugins.packer;

import com.intellij.json.psi.*;
import com.intellij.patterns.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.XmlPatterns.*;


public class PackerShellProvisionerReferenceContributor extends PsiReferenceContributor {

    public static final PsiFilePattern.Capture<JsonFile> PACKER_CONFIG_JSON_PATTERN =
        psiFile(JsonFile.class)
            .inVirtualFile(virtualFile().notNull())
            .withChild(psiElement(JsonObject.class).withChild(psiElement(JsonProperty.class).withName("builders")));
    private static final ElementPattern<? extends PsiElement> SHELL_PROVISIONER = psiElement(JsonObject.class)
        .withSuperParent(2, psiElement(JsonProperty.class).withName("provisioners"))
        .withChild(psiElement(JsonProperty.class).withName("type").with(new PatternCondition<JsonProperty>("JsonPropertyWithStringValue(shell)") {
            @Override
            public boolean accepts(@NotNull JsonProperty jsonProperty, ProcessingContext context) {
                JsonValue value = jsonProperty.getValue();
                if (value == null) return false;
                if (!(value instanceof JsonStringLiteral)) return false;
                JsonStringLiteral literal = (JsonStringLiteral) value;
                return "shell".equals(literal.getValue());
            }
        }));

    @Override
    public void registerReferenceProviders(@NotNull final PsiReferenceRegistrar registrar) {
        final PsiElementPattern.Capture<JsonStringLiteral> scripts = PlatformPatterns.psiElement(JsonStringLiteral.class)
            .inFile(PACKER_CONFIG_JSON_PATTERN)
            .withParent(psiElement(JsonArray.class))
            .withSuperParent(2, psiElement(JsonProperty.class).withName("scripts"))
            .withSuperParent(3, SHELL_PROVISIONER);
        registrar.registerReferenceProvider(scripts, new PackerShellScriptToFilePsiReferenceProvider());
        final PsiElementPattern.Capture<JsonStringLiteral> script = PlatformPatterns.psiElement(JsonStringLiteral.class)
            .inFile(PACKER_CONFIG_JSON_PATTERN)
            .withParent(psiElement(JsonProperty.class).withName("script"))
            .withSuperParent(2, SHELL_PROVISIONER);
        registrar.registerReferenceProvider(script, new PackerShellScriptToFilePsiReferenceProvider());
    }

}
