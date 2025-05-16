/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 Ivan Kniazkov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cqfn.astranaut.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.cqfn.astranaut.core.utils.FilesReader;
import org.junit.jupiter.api.Assertions;

/**
 * Common methods for tests that perform end-to-end testing of the entire application
 * by running it with some parameters and comparing the result.
 * @since 1.0.0
 */
class EndToEndTest {
    /**
     * Loads a resource (e.g., reference code to compare with generated code) from a file.
     * @param name Name of the resource
     * @return Resource as a string
     */
    String loadStringResource(final String name) {
        final Path path = Paths.get("src/test/resources/end_to_end", name);
        return new FilesReader(path.toFile().getAbsolutePath()).readAsStringNoExcept();
    }

    /**
     * Returns the contents of all files within a folder (including subfolders)
     *  as a single listing.
     * @param root Path to root folder containing files and subfolders
     * @return Contents of all files as a single string
     */
    String getAllFilesContent(final Path root) {
        String listing = "";
        final List<Path> paths = new LinkedList<>();
        try {
            Files.walk(root).filter(Files::isRegularFile).forEach(paths::add);
            Collections.sort(paths, Comparator.comparing(Path::toString));
            final StringBuilder builder = new StringBuilder();
            for (final Path file : paths) {
                builder
                    .append(
                        new FilesReader(file.toFile().getAbsolutePath()).readAsStringNoExcept()
                    )
                    .append('\n');
            }
            listing = builder.toString();
        } catch (final IOException ignored) {
        }
        return listing;
    }

    /**
     * Creates a directory at the given path and removes write permissions to make it
     *  write-protected.
     * @param dir The path to the directory to create and protect
     */
    void createWriteProtectedFolder(final Path dir) {
        boolean oops = false;
        try {
            Files.createDirectories(dir);
            if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win")) {
                final AclFileAttributeView view = Files.getFileAttributeView(
                    dir,
                    AclFileAttributeView.class
                );
                final AclEntry denied = AclEntry.newBuilder()
                    .setType(AclEntryType.DENY)
                    .setPrincipal(view.getOwner())
                    .setPermissions(AclEntryPermission.WRITE_DATA, AclEntryPermission.APPEND_DATA)
                    .build();
                final List<AclEntry> acl = new ArrayList<>(view.getAcl());
                acl.add(0, denied);
                view.setAcl(acl);
            } else {
                final Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(dir);
                permissions.remove(PosixFilePermission.OWNER_WRITE);
                permissions.remove(PosixFilePermission.GROUP_WRITE);
                permissions.remove(PosixFilePermission.OTHERS_WRITE);
                Files.setPosixFilePermissions(dir, permissions);
            }
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }

    /**
     * Restores write permissions for a previously write-protected directory.
     * @param dir The path to the directory whose write protection should be removed
     */
    void clearWriteProtectedFlag(final Path dir) {
        boolean oops = false;
        try {
            if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win")) {
                final AclFileAttributeView view = Files.getFileAttributeView(
                    dir,
                    AclFileAttributeView.class
                );
                final List<AclEntry> acl = new ArrayList<>(view.getAcl());
                acl.removeIf(entry -> entry.type() == AclEntryType.DENY);
                view.setAcl(acl);
            } else {
                Files.setPosixFilePermissions(
                    dir,
                    PosixFilePermissions.fromString("rwxr-xr-x")
                );
            }
        } catch (final IOException ignored) {
            oops = true;
        }
        Assertions.assertFalse(oops);
    }
}
