/*
 * Copyright (c) 2018
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

package org.acra.plugins;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.acra.ACRA;
import org.acra.config.CoreConfiguration;

import java.util.*;

import static org.acra.ACRA.LOG_TAG;

/**
 * Utility to load {@link Plugin}s
 *
 * @author F43nd1r
 * @since 18.04.18
 */
public class PluginLoader {
    private final CoreConfiguration config;

    /**
     * Creates a new loader
     *
     * @param config the current config. If null, all plugins including disabled plugins will be loaded
     */
    public PluginLoader(@Nullable CoreConfiguration config) {
        this.config = config;
    }

    public <T extends Plugin> List<T> load(@NonNull Class<T> clazz) {
        List<T> plugins = new ArrayList<>();
        //noinspection ForLoopReplaceableByForEach
        for (final Iterator<T> iterator = ServiceLoader.load(clazz, getClass().getClassLoader()).iterator(); iterator.hasNext(); ) {
            try {
                final T plugin = iterator.next();
                if (config == null || !(plugin instanceof AllowsDisablePlugin) || ((AllowsDisablePlugin) plugin).enabled(config)) {
                    if (ACRA.DEV_LOGGING) ACRA.log.d(ACRA.LOG_TAG, "Loaded " + clazz.getSimpleName() + " of type " + plugin.getClass().getName());
                    plugins.add(plugin);
                } else if (ACRA.DEV_LOGGING) ACRA.log.d(LOG_TAG, "Ignoring disabled " + clazz.getSimpleName() + " of type " + plugin.getClass().getSimpleName());
            } catch (ServiceConfigurationError e) {
                ACRA.log.e(ACRA.LOG_TAG, "Unable to load " + clazz.getSimpleName(), e);
            }
        }
        return plugins;
    }
}
