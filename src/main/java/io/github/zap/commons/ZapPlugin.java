package io.github.zap.commons;

import com.google.inject.Injector;
import io.github.zap.commons.utils.TimeUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A base plugin for <i>all</i> zap plugin projects. Provides some standard utilities, such as timing enable, load, and
 * disable phases, and handling exceptions that occur during said phases.
 */
public abstract class ZapPlugin extends JavaPlugin {
    private Injector injector;

    /**
     * Set the injector to use in this plugin
     * @param injector an injector
     */
    protected void setInjector(@Nullable Injector injector) {
        this.injector = injector;
    }

    @Nullable
    public Injector getInjector() {
        return injector;
    }

    @Override
    public void onEnable() {
        doTimedCall(
                this::doEnable,
                "Enabling",
                String.format("%s enabled successfully!", getName()),
                "A fatal error occurred while enabling",
                true);
    }

    @Override
    public void onLoad() {
        doTimedCall(
                this::doLoad,
                "Loading",
                String.format("%s loaded successfully!", getName()),
                "A fatal error occurred while loading",
                true);
    }

    @Override
    public void onDisable() {
        doTimedCall(
                this::doDisable,
                "Disabling",
                String.format("%s disabled successfully!", getName()),
                "A fatal error occurred while disabling",
                false);
    }

    private void doTimedCall(ThrowableRunnable runnable, String startMsg, String successMsg, String failMsg,
                             boolean disablePluginOnFail) {
        Logger logger = getLogger();
        try {
            logger.info(startMsg);
            long elapsed = TimeUtils.measure(runnable);
            logger.info(successMsg);
            logger.info(String.format("~%sms elapsed", elapsed));
        } catch (TimeMeasurementException e) {
            logger.severe(String.format("Exception thrown after ~%sms", e.getTimeElapsed()));
            logger.log(Level.SEVERE, failMsg, e.getCause());
            if(disablePluginOnFail) {
                getPluginLoader().disablePlugin(this, true);
            }
        }
    }


    /**
     * Enabling plugin. Same purpose as {@link JavaPlugin#onEnable()}
     * @throws LoadFailureException any error encountered during plugin enable phase
     */
    public abstract void doEnable() throws LoadFailureException;

    /**
     * Loading plugin. Same purpose as {@link JavaPlugin#onLoad()}
     * @throws LoadFailureException any error encountered during plugin load phase
     */
    public abstract void doLoad() throws LoadFailureException;

    /**
     * Disabling plugin. Same purpose as {@link JavaPlugin#onDisable()}
     * @throws LoadFailureException any error encountered during plugin disable phase
     */
    public abstract void doDisable() throws LoadFailureException;
}
