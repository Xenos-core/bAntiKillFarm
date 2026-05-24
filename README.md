
                  bAntiKillFarm v1.0.4                       
#              Advanced Kill Farm Protection                    


#📌 DESCRIPTION

bAntiKillFarm is a high-performance, fully asynchronous Minecraft 
plugin designed to detect and prevent kill farming on your server. 
It monitors player kills within configurable time windows and 
automatically executes punishment commands when thresholds are exceeded.

Built with performance in mind using dedicated thread pools and 
ConcurrentHashMap for thread-safe operations. Zero lag, maximum 
protection.


#⚡ FEATURES

• Full Async Processing - All operations run on dedicated thread pool
• Warning System - Warns players before punishment
• Customizable Commands - Execute any console command
• MiniMessage Support - Beautiful colored messages
• Permission Bypass - VIP players can be excluded
• Auto Cleanup - Automatic memory management
• File Logging - Track all detections and punishments
• Cooldown System - Prevent command spam
• Real-time Stats - Monitor plugin performance


#⚙️ CONFIGURATION

• Time Window - Set detection period (default: 10 minutes)
• Kill Threshold - Kills before punishment (default: 5)
• Warning Threshold - Kills before warning (default: 3)
• Punishment Cooldown - Time between punishments (default: 60s)
• Custom Commands - Kick, ban, mute, or any command
• Custom Messages - MiniMessage format with gradients & colors


#🔧 COMMANDS

• /bantkillfarm reload - Reload configuration
• /bantkillfarm status - View plugin statistics


#🔑 PERMISSIONS

• bantkillfarm.admin - Access admin commands (default: op)
• bantkillfarm.bypass - Skip detection (default: false)


#📋 REQUIREMENTS

• Minecraft: 1.21+
• Java: 21+
• Server: Paper/Spigot

