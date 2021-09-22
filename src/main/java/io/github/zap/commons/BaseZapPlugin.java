package io.github.zap.commons;

import com.google.inject.Injector;
import io.github.zap.commons.utils.LoadFailureException;
import org.apache.commons.lang3.time.StopWatch;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;

/**
 * A base plugin for *all* zap plugin projects
 */
public abstract class BaseZapPlugin extends JavaPlugin {
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
    public void onEnable() {
        try {
            getLogger().info(String.format("Enabling %s:", getName()));
            StopWatch timer = StopWatch.createStarted();
            doEnable();
            timer.stop();
            getLogger().info(String.format("%s enabled successfully in: %sms!", getName(), timer.getTime()));
        } catch (LoadFailureException e) {
            getLogger().severe(String.format("A fatal error occurred that prevented the plugin from enabling properly: '%s'.",
                    e.getMessage()));
            getPluginLoader().disablePlugin(this, true);
        }
    }

    /**
     * Enabling plugin. Same purpose as {@link JavaPlugin#onEnable()}
     * @throws LoadFailureException any error encountered during plugin enable phase
     */
    abstract void doEnable() throws LoadFailureException;
}
