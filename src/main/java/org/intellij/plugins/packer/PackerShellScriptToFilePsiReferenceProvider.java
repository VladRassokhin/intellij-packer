package org.intellij.plugins.packer;

import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonProperty;
import com.intellij.json.psi.JsonStringLiteral;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

class PackerShellScriptToFilePsiReferenceProvider extends PsiReferenceProvider {
    @NotNull
    @Override
    public PsiReference[] getReferencesByElement(@NotNull final PsiElement element, @NotNull final ProcessingContext processingContext) {
        if (!(element instanceof JsonStringLiteral)) {
            return PsiReference.EMPTY_ARRAY;
        }
        if (!(element.getParent() instanceof JsonArray)) {
            return PsiReference.EMPTY_ARRAY;
        }
        if (!(element.getParent().getParent() instanceof JsonProperty)) {
            return PsiReference.EMPTY_ARRAY;
        }
        final JsonProperty property = (JsonProperty) element.getParent().getParent();
        if (!"scripts".equals(property.getName())) {
            return PsiReference.EMPTY_ARRAY;
        }

        final JsonStringLiteral literal = (JsonStringLiteral) element;
//        final PsiFile file = literal.getContainingFile();
//
//        if (file == null) {
//            return PsiReference.EMPTY_ARRAY;
//        }

        final String script = literal.getValue();
        return new FileReferenceSet(script, element, 1, null, true) {
            @Override
            protected boolean isSoft() {
                return true;
            }
        }.getAllReferences();
    }
}
