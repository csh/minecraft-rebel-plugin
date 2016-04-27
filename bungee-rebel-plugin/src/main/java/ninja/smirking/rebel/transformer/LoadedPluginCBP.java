/*
 * Copyright 2016 Connor Spencer Harries
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.smirking.rebel.transformer;

import net.md_5.bungee.api.plugin.PluginClassloader;
import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtField;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.CtNewMethod;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

public class LoadedPluginCBP extends JavassistClassBytecodeProcessor {
    private static final String PLUGIN_CLASS_NAME = "net.md_5.bungee.api.plugin.Plugin";

    @Override
    public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
        if (cl == ClassLoader.getSystemClassLoader() || !(cl instanceof PluginClassloader)) {
            return;
        }

        CtClass cursor = ctClass;
        while (true) {
            if (cursor.getName().equals(PLUGIN_CLASS_NAME)) {
                break;
            }
            cursor = ctClass.getSuperclass();
            if (cursor == null || cursor.getName().equals("java.lang.Object")) {
                return;
            }
        }

        LoggerFactory.getInstance().echo(String.format("Instrumenting class \"%s\"", ctClass.getName()));

        // region State tracking
        ctClass.addInterface(cp.get("ninja.smirking.rebel.StatefulPlugin"));

        CtField field = CtField.make("private volatile boolean minecraft_rebel_enabled = false;", ctClass);
        ctClass.addField(field);

        CtMethod isEnabledMethod = CtNewMethod.make("public boolean _rebel_isEnabled() { return this.minecraft_rebel_enabled; }", ctClass);
        ctClass.addMethod(isEnabledMethod);

        CtMethod setEnabledMethod = CtNewMethod.make("public void _rebel_setEnabled(boolean enabled) { this.minecraft_rebel_enabled = enabled; }", ctClass);
        ctClass.addMethod(setEnabledMethod);
        // endregion State tracking

        boolean implementsDisable = false;
        boolean implementsEnable = false;
        for (CtMethod method : ctClass.getDeclaredMethods()) {
            if (method.getName().equals("onEnable")) {
                implementsEnable = true;
                method.setName("_rebel_onEnable");
                method = CtNewMethod.make(
                        "public void onEnable() { " +
                                "_rebel_onEnable(); " +
                                "_rebel_setEnabled(true); " +
                                "}", ctClass);
                ctClass.addMethod(method);
            } else if (method.getName().equals("onDisable")) {
                implementsDisable = true;
                method.setName("_rebel_onDisable");
                method = CtNewMethod.make(
                        "public void onDisable() { " +
                                "try { " +
                                "_rebel_onDisable(); " +
                                "} finally { " +
                                "_rebel_setEnabled(false); " +
                                "} " +
                                "}", ctClass);
                ctClass.addMethod(method);
            }
        }

        if (!implementsEnable) {
            LoggerFactory.getInstance().warnEcho("{} does not implement onEnable", ctClass.getName());
            CtMethod onEnable = CtNewMethod.make(
                    "public void onEnable() { " +
                            "_rebel_setEnabled(true); " +
                            "}", ctClass);
            ctClass.addMethod(onEnable);
        }

        if (!implementsDisable) {
            CtMethod onDisable = CtNewMethod.make(
                    "public void onDisable() { " +
                            "_rebel_setEnabled(false); " +
                            "}", ctClass);
            ctClass.addMethod(onDisable);
        }
    }
}
