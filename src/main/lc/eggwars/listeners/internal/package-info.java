/*
 * Efficient listener register
 * <p>
 * Because the normal {@link org.bukkit.plugin.PluginManager#registerEvents}
 * Use reflection to execute the method and incorporate useless timing checks 
 * </p>
 * @since 0.0.1
 * @autor minelc
 */

package lc.eggwars.listeners.internal;