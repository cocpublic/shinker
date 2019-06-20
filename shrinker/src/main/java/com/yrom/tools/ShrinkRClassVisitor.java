/*
 * Copyright (c) 2017 Yrom Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yrom.tools;

import org.gradle.api.logging.LogLevel;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author yrom
 * @version 2017/11/29
 */
class ShrinkRClassVisitor extends ClassVisitor {

    private final RSymbols rSymbols;
    private String classname;

    ShrinkRClassVisitor(ClassWriter cv, RSymbols rSymbols) {
        super(Opcodes.ASM5, cv);
        this.rSymbols = rSymbols;
    }

    /**
     * @return true if name matches pattern like {@code .+/R$.+}
     */
    static boolean isRClass(String className) {
        int $ = className.lastIndexOf('$');
        int slash = className.lastIndexOf('/', $);
        return $ > slash && $ < className.length() && (className.charAt(slash + 1) | className.charAt($ - 1)) == 'R';
    }

    private static void pushInt(MethodVisitor mv, int i) {
        if (0 <= i && i <= 5) {
            mv.visitInsn(Opcodes.ICONST_0 + i); //  ICONST_0 ~ ICONST_5
        } else if (i <= Byte.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.BIPUSH, i);
        } else if (i <= Short.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.SIPUSH, i);
        } else {
            mv.visitLdcInsn(i);
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        classname = name;
        ShrinkerPlugin.logger.debug("T_ ShrinkRClassVisitor visit name:{}", name);
//        ShrinkerPlugin.logger.debug("processing class " + name);

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return cv.visitField(access, name, desc, signature, value);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        if (access == 0x19 /*ACC_PUBLIC | ACC_STATIC | ACC_FINAL*/
                && isRClass(name)) {
            ShrinkerPlugin.logger.debug("T_ ShrinkRClassVisitor ---remove visit inner class {} in {}", name, classname);
            return;
        }
        cv.visitInnerClass(name, outerName, innerName, access);
    }

    /**
     * 内联 int 字面值：
     *
     * @param access
     * @param name
     * @param desc
     * @param signature
     * @param exceptions
     * @return
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        return new MethodVisitor(Opcodes.ASM5,
                super.visitMethod(access, name, desc, signature, exceptions)) {


            @Override
            public void visitFieldInsn(int opcode, String owner, String fieldName,
                                       String fieldDesc) {
                if (opcode != Opcodes.GETSTATIC || owner.startsWith("java/lang/")) {
                    // skip!
                    this.mv.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
                    return;
                }
                String typeName = owner.substring(owner.lastIndexOf('/') + 1);
                String key = typeName + '.' + fieldName;
                ShrinkerPlugin.logger.debug("T_ ShrinkRClassVisitor visitFieldInsn owner:{}，fieldName:{}，fieldDesc:{}，key:{}", owner, fieldName, fieldDesc, key);

                if (rSymbols.containsKey(key)) {
                    ShrinkerPlugin.logger.debug("T_ ShrinkRClassVisitor visitFieldInsn 1");

                    Integer value = rSymbols.get(key);
                    if (value == null)
                        throw new UnsupportedOperationException("value of " + key + " is null!");
                    if (ShrinkerPlugin.logger.isEnabled(LogLevel.DEBUG)) {
                        ShrinkerPlugin.logger.debug("replace {}.{} to 0x{}", owner, fieldName, Integer.toHexString(value));
                    }

                    pushInt(this.mv, value);
                } else if (owner.endsWith("/R$styleable")) { // replace all */R$styleable ref!
                    ShrinkerPlugin.logger.debug("T_ ShrinkRClassVisitor visitFieldInsn 2");

                    this.mv.visitFieldInsn(opcode, RSymbols.R_STYLEABLES_CLASS_NAME, fieldName, fieldDesc);
                } else {
                    ShrinkerPlugin.logger.debug("T_ ShrinkRClassVisitor visitFieldInsn 3");

                    this.mv.visitFieldInsn(opcode, owner, fieldName, fieldDesc);
                }
            }
        };
    }
}
