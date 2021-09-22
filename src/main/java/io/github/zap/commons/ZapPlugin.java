package io.github.zap.commons;

import com.google.inject.Injector;
import io.github.zap.commons.utils.LoadFailureException;
import org.apache.commons.lang3.time.StopWatch;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.logging.Level;

/**
 * A base plugin for *all* zap plugin projects
 */
public abstract class ZapPlugin extends JavaPlugin {
    private Injector injector;

    /**
     * Set the injector to use in this plugin
     * @param injector an injector
     */
    protected void setInjector(Injector injector) {
        this.injector = injector;
    }

    public Injector getInjector() {
        return injector;
    }

    @Override
    public final void onLoad() {
        try {
            getLogger().info(String.format("Loading %s", getName()));
            StopWatch timer = StopWatch.createStarted();
            doLoad();
            timer.stop();
            getLogger().info(String.format("%s loaded in %sms", getName(), timer.getTime()));
        }
        catch (LoadFailureException exception) {
            getLogger().log(Level.SEVERE, getName() + " failed to load", exception);
            getPluginLoader().disablePlugin(this, true);
        }
    }

    @Override
    public final void onEnable() {
        try {
            getLogger().info(String.format("Enabling %s", getName()));
            StopWatch timer = StopWatch.createStarted();
            doEnable();
            timer.stop();
            getLogger().info(String.format("%s enabled in %sms", getName(), timer.getTime()));
        } catch (LoadFailureException exception) {
            getLogger().log(Level.SEVERE, getName() + " failed to enable", exception);
            getPluginLoader().disablePlugin(this, true);
        }
    }

    @Override
    public final void onDisable() {
        getLogger().info(String.format("Disabling %s:", getName()));
        StopWatch timer = StopWatch.createStarted();
        doDisable();
        timer.stop();
        getLogger().info(String.format("%s disabled in %s", getName(), timer.getTime()));
    }

    abstract void doLoad() throws LoadFailureException;

    abstract void doEnable() throws LoadFailureException;

    abstract void doDisable();
}
