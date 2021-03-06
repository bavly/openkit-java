/**
 * Copyright 2018-2019 Dynatrace LLC
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

package com.dynatrace.openkit.api;

/**
 * This interface provides logging functionality to OpenKit. By subclassing OpenKit
 * can make use of custom loggers.
 */
public interface Logger {

    /**
     * Log with a given level and message
     *
     * @param level     the level of the log entry
     * @param message   the message to write to the log
     */
    void log(LogLevel level, String message);

    /**
     * Log with a given level, message and throwable
     *
     * @param level     the level of the log entry
     * @param message   the message to write to the log
     * @param throwable an instance of a throwable to be attached to the output.
     */
    void log(LogLevel level, String message, Throwable throwable);

    /**
     * Log with level 'error'.<br>
     * This is a convenience method for {@link #log(LogLevel, String)}
     *
     * @param message the message to write to the log
     */
    void error(String message);

    /**
     * Log with level 'error'.<br>
     * This is a convenience method for {@link #log(LogLevel, String, Throwable)}
     *
     * @param message the message to write to the log
     * @param t       an instance of a throwable to be attached to the output
     */
    void error(String message, Throwable t);

    /**
     * Log with level 'warning'.<br>
     * This is a convenience method for {@link #log(LogLevel, String)}
     *
     * @param message the message to write to the log
     */
    void warning(String message);

    /**
     * Log with level 'info'.<br>
     * This is a convenience method for {@link #log(LogLevel, String)}
     *
     * @param message the message to write to the log
     */
    void info(String message);

    /**
     * Log with level 'debug'.<br>
     * This is a convenience method for {@link #log(LogLevel, String)}
     *
     * @param message the message to write to the log
     */
    void debug(String message);

    /**
     * Return a flag if 'error' level messages are currently printed
     *
     * @return {@code true} if 'error' level messages are printed, {@code false} if not
     */
    boolean isErrorEnabled();

    /**
     * Return a flag if 'warn' level messages are currently printed
     *
     * @return {@code true} if 'warn' level messages are printed, {@code false} if not
     */
    boolean isWarnEnabled();

    /**
     * Return a flag if 'info' level messages are currently printed
     *
     * @return {@code true} if 'info' level messages are printed, {@code false} if not
     */
    boolean isInfoEnabled();

    /**
     * Return a flag if 'debug' level messages are currently printed
     *
     * @return {@code true} if 'debug' level messages are printed, {@code false} if not
     */
    boolean isDebugEnabled();


}
