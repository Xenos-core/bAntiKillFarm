╔══════════════════════════════════════════════════════════════╗
║                    bAntiKillFarm v1.0.4                       ║
║              Advanced Kill Farm Protection                    ║
╚══════════════════════════════════════════════════════════════╝

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📌 DESCRIPTION

bAntiKillFarm is a high-performance, fully asynchronous Minecraft 
plugin designed to detect and prevent kill farming on your server. 
It monitors player kills within configurable time windows and 
automatically executes punishment commands when thresholds are exceeded.

Built with performance in mind using dedicated thread pools and 
ConcurrentHashMap for thread-safe operations. Zero lag, maximum 
protection.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

⚡ FEATURES

• Full Async Processing - All operations run on dedicated thread pool
• Warning System - Warns players before punishment
• Customizable Commands - Execute any console command
• MiniMessage Support - Beautiful colored messages
• Permission Bypass - VIP players can be excluded
• Auto Cleanup - Automatic memory management
• File Logging - Track all detections and punishments
• Cooldown System - Prevent command spam
• Real-time Stats - Monitor plugin performance

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

⚙️ CONFIGURATION

• Time Window - Set detection period (default: 10 minutes)
• Kill Threshold - Kills before punishment (default: 5)
• Warning Threshold - Kills before warning (default: 3)
• Punishment Cooldown - Time between punishments (default: 60s)
• Custom Commands - Kick, ban, mute, or any command
• Custom Messages - MiniMessage format with gradients & colors

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🎨 MESSAGE FORMATTING

Supports MiniMessage format with:
• Named colors: <red>, <yellow>, <green>, <aqua>, etc.
• Hex colors: <#FF6B35>, <#00FF00>, etc.
• Gradients: <gradient:#ff6b35:#f7c59f>TEXT</gradient>
• Rainbow: <rainbow>TEXT</rainbow>
• Styles: <bold>, <italic>, <underlined>

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔧 COMMANDS

• /bantkillfarm reload - Reload configuration
• /bantkillfarm status - View plugin statistics

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔑 PERMISSIONS

• bantkillfarm.admin - Access admin commands (default: op)
• bantkillfarm.bypass - Skip detection (default: false)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📊 EXAMPLE CONFIG

detection:
  time-window-minutes: 10
  kill-threshold: 5
  warning-threshold: 3
  punishment-cooldown-seconds: 60

commands:
  warning:
    - "say <yellow>[AntiKillFarm]</yellow> <red>{attacker}</red> stop kill farming!"
  punishment:
    - "kick {attacker}"
    - "ban {attacker} 1h Kill farming detected"

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

💾 LOGGING

All detections are logged to:
plugins/bAntiKillFarm/logs/bantkillfarm.log

Log format:
[2026-05-23 22:30:15] [WARNING] Player killed X times
[2026-05-23 22:30:45] [PUNISHMENT] Player punished for kill farming

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

📋 REQUIREMENTS

• Minecraft: 1.21+
• Java: 21+
• Server: Paper/Spigot

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

🔄 SUPPORT

For bugs, feature requests, or help:
Contact: Macronis

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Made with ❤️ by Macronis
