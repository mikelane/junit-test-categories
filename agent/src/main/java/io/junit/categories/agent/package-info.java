/**
 * ByteBuddy-based Java agent for runtime interception.
 *
 * <p>This package provides runtime interception of:
 *
 * <ul>
 *   <li>Network access (Socket, URL, SocketChannel)
 *   <li>Filesystem access (Files, File, streams)
 *   <li>Database access (DriverManager)
 *   <li>Subprocess spawning (ProcessBuilder, Runtime)
 *   <li>Sleep calls (Thread.sleep, TimeUnit.sleep)
 * </ul>
 *
 * <p>The agent is loaded via {@code -javaagent} JVM argument.
 */
package io.junit.categories.agent;
