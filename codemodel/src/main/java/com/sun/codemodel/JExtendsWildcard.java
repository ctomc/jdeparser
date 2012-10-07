/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
/* Modified by Red Hat */

package com.sun.codemodel;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a wildcard type like "? extends Foo" or "? super Foo".
 *
 * <p>
 * Instances of this class can be obtained from {@link JClass#wildcard()}
 *
 * <p>
 * Our modeling of types are starting to look really ugly.
 * ideally it should have been done somewhat like APT,
 * but it's too late now.
 *
 * @author Kohsuke Kawaguchi
 */
final class JExtendsWildcard extends JClass {

    private final JClass[] bounds;

    JExtendsWildcard(final JClass... bounds) {
        super(bounds[0].owner());
        this.bounds = bounds;
    }

    public String name() {
        StringBuilder b = new StringBuilder();
        b.append("? extends ");
        int i;
        for (i = 0; i < bounds.length - 1; i++) {
            b.append(bounds[i].name()).append(" & ");
        }
        b.append(bounds[i].name());
        return b.toString();
    }

    public String fullName() {
        StringBuilder b = new StringBuilder();
        b.append("? extends ");
        int i;
        for (i = 0; i < bounds.length - 1; i++) {
            b.append(bounds[i].fullName()).append(" & ");
        }
        b.append(bounds[i].fullName());
        return b.toString();
    }

    public JPackage _package() {
        return null;
    }

    /**
     * Returns the class bound of this variable.
     *
     * <p>
     * If no bound is given, this method returns {@link Object}.
     */
    public JClass _extends() {
        return bounds[0];
    }

    /**
     * Returns the interface bounds of this variable.
     */
    public Iterator<JClass> _implements() {
        return Arrays.asList(bounds).subList(1, bounds.length).iterator();
    }

    public boolean isInterface() {
        return false;
    }

    public boolean isAbstract() {
        return false;
    }

    protected JClass substituteParams(JTypeVar[] variables, List<JClass> bindings) {
        JClass[] copiedBounds = new JClass[bounds.length];
        for (int i = 0; i < bounds.length; i++) {
            copiedBounds[i] = bounds[i].substituteParams(variables, bindings);
        }
        return new JExtendsWildcard(copiedBounds);
    }

    public void generate(JFormatter f) {
        if (bounds.length == 1 && bounds[0]._extends() == null) {
            f.p("?");
        } else {
            f.p("? extends");
            int i;
            final int length = bounds.length;
            for (i = 0; i < length - 1; i++) {
                f.g(bounds[i]);
                f.p("&");
            }
            f.g(bounds[i]);
        }
    }
}
