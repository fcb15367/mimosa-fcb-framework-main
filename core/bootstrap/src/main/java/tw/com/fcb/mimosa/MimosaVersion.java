package tw.com.fcb.mimosa;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * @author Steven Wang <steven.wang@softleader.com.tw>
 * @since 1.0.0
 */
public final class MimosaVersion {

  private MimosaVersion() {
  }

  public static String getVersion() {
    return determineMimosaVersion();
  }

  private static String determineMimosaVersion() {
    String implementationVersion = MimosaVersion.class.getPackage().getImplementationVersion();
    if (Objects.nonNull(implementationVersion)) {
      return implementationVersion;
    }

    var codeSource = MimosaVersion.class.getProtectionDomain().getCodeSource();
    if (Objects.isNull(codeSource)) {
      return null;
    }

    var codeSourceLocation = codeSource.getLocation();
    try {
      var connection = codeSourceLocation.openConnection();
      if (connection instanceof JarURLConnection) {
        return getImplementationVersion(((JarURLConnection) connection).getJarFile());
      }
      try (JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()))) {
        return getImplementationVersion(jarFile);
      }
    } catch (Exception ex) {
      return null;
    }
  }

  private static String getImplementationVersion(JarFile jarFile) throws IOException {
    return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
  }
}
