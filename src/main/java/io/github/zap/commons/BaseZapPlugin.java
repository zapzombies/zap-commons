package io.github.zap.commons;

import com.google.inject.Injector;
import io.github.zap.commons.utils.TimeUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

/**
 * A base plugin for *all* zap plugin projects
 */
public abstract class BaseZapPlugin extends JavaPlugin {
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
                String.format("Enabling %s:", getName()),
                String.format("%s enabled successfully!", getName()),
                "A fatal error occurred that prevented the plugin from enabling properly!",
                true);
    }

    @Override
    public void onLoad() {
        doTimedCall(
                this::doLoad,
                String.format("Loading %s:", getName()),
                String.format("%s loaded successfully!", getName()),
                "A fatal error occurred that prevented the plugin from loading properly!",
                true);
    }

    @Override
    public void onDisable() {
        doTimedCall(
                this::doDisable,
                String.format("Disabling %s:", getName()),
                String.format("%s disabled successfully!", getName()),
                "A fatal error occurred that prevented the plugin from disabling properly!",
                false);
    }

    private void doTimedCall(ThrowableRunnable runnable, String startMsg, String successMsg, String failMsg, boolean disablePluginOnFail) {
        try {
            getLogger().info(startMsg);
            long elapsed = TimeUtils.measure(this::doLoad);
            getLogger().info(successMsg);
            getLogger().info(String.format("Time elapsed: %sms!", elapsed));
        } catch (Throwable e) {
            getLogger().severe(failMsg);
            e.printStackTrace();
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
     * Enabling plugin. Same purpose as {@link JavaPlugin#onEnable()}
     * @throws LoadFailureException any error encountered during plugin enable phase
     */
    public abstract void doLoad() throws LoadFailureException;

    /**
     * Enabling plugin. Same purpose as {@link JavaPlugin#onEnable()}
     * @throws LoadFailureException any error encountered during plugin enable phase
     */
    public abstract void doDisable() throws LoadFailureException;

}
