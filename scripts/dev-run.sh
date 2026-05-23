#!/usr/bin/env bash
# Instala debug, abre la app y muestra logs útiles (Firebase + AppAuth + crashes).
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"
export ANDROID_HOME="${ANDROID_HOME:-$HOME/Android/Sdk}"
export PATH="$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator"

PKG="karta.gestion.eventos"
ACTIVITY="com.example.myapplicationeventoscomunitarios.MainActivity"

./gradlew :app:installDebug --no-daemon
adb shell am force-stop "$PKG" 2>/dev/null || true
adb shell am start -n "$PKG/$ACTIVITY" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER

echo ""
echo "=== Logcat (Ctrl+C para salir). Filtros: AppAuth, FirebaseAuth, FirebaseApp, AndroidRuntime ==="
echo ""
adb logcat -c
adb logcat -v time \
  AppAuth:D \
  FirebaseAuth:D \
  FirebaseApp:D \
  AndroidRuntime:E \
  '*:S'
