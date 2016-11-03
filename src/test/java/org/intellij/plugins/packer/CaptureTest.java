package org.intellij.plugins.packer;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightPlatformTestCase;

public class CaptureTest extends LightPlatformTestCase {
  @Override
  public void setUp() throws Exception {
    super.setUp();
  }

  public void testPackerFileCapturedCorrectly() throws Exception {
    doCheckFileCapture("{}", false);
    doCheckFileCapture("{\"builders\":{}}", true);
  }

  private void doCheckFileCapture(String text, boolean expected) {
    PsiFile file = createFile("test.json", text);
    assertEquals(expected, PackerShellProvisionerReferenceContributor.PACKER_CONFIG_JSON_PATTERN.accepts(file));
  }
}
