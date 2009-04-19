/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.helloWorld;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.preferences.Preferences;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Christian Bauer
 */
@Name("helloWorld")
@Scope(ScopeType.PAGE)
public class HelloWorld implements Serializable {

    @Logger
    Log log;

    @In
    Preferences preferences;

    @In
    Map<String,String> messages;

    String greeting;

    public String getHello(WikiPluginMacro pluginMacro) {
        HelloWorldPreferences prefs = preferences.get(HelloWorldPreferences.class, pluginMacro);
        return getGreeting() + " '" + prefs.getMessage() + "'";
    }

    public String getGreeting() {
        if (greeting == null) return messages.get("hw.defaultMessage");
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public void printAMessage() {
        log.info("### This is just a log message");
    }

    @Observer("Macro.helloWorld.callback.viewBuild")
    public void onViewBuild(WikiPluginMacro pluginMacro) {
        log.info("### View build event triggered: " + pluginMacro);
    }

    @Observer("Macro.helloWorld.callback.beforeViewRender")
    public void onBeforeViewRender(WikiPluginMacro pluginMacro) {
        log.info("### Before render event triggered" + pluginMacro);
    }

    @Observer("Macro.helloWorld.callback.afterViewRender")
    public void onAfterViewRender(WikiPluginMacro pluginMacro) {
        log.info("### After render event triggered" + pluginMacro);
    }

}
