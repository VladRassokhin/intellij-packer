package org.intellij.plugins.packer;

import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

class PackerShellScriptToFilePsiReferenceProvider extends PsiReferenceProvider {

    private static final String TEMPLATE_DIR_VARIABLE = "{{template_dir}}";

    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull final ProcessingContext processingContext) {
        if (!(element instanceof JsonStringLiteral)) {
            return PsiReference.EMPTY_ARRAY;
        }
        final JsonStringLiteral literal = (JsonStringLiteral) element;

        boolean relativeOnly = false;
        String script = literal.getValue();
        int start = 1;
        if (script.startsWith(TEMPLATE_DIR_VARIABLE)) {
            script = script.substring(TEMPLATE_DIR_VARIABLE.length());
            start += TEMPLATE_DIR_VARIABLE.length();
            relativeOnly = true;
        }
        final FileReferenceSet set = new FileReferenceSet(script, element, start, null, true) {
            @Override
            protected boolean isSoft() {
                return false;
            }
        };
        if (relativeOnly) {
            set.addCustomization(FileReferenceSet.DEFAULT_PATH_EVALUATOR_OPTION, psiFile -> {
                PsiDirectory parent = psiFile.getParent();
                if (parent != null) {
                    return Collections.singleton(parent);
                }
                return Collections.emptySet();
            });
        }
        return set.getAllReferences();
    }
}
