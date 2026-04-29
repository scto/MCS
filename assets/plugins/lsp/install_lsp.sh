#!/bin/bash

# * @author nullij @ github.com/nullij
# Vollständiger Installer für LSPs, Kotlin-Tools (kfmt) und VS Code Erweiterungen
# Bevorzugt stabile GitHub-Releases für Kotlin, um npm 404-Fehler zu vermeiden.

# Farben für die Terminal-Ausgabe
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Verzeichnis des Skripts ermitteln für lokalen Temp-Ordner
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEMP_DIR="$SCRIPT_DIR/lsp_temp_build"

# Funktion für Statusmeldungen
log() {
  local tag="install_lsp"
  echo -e "${GREEN}[ $tag ]${NC} $1"
}

# Funktion für Fehlermeldungen und Programmabbruch
abort() {
  # Lokalen Temp-Ordner bei Abbruch entfernen
  [ -d "$TEMP_DIR" ] && rm -rf "$TEMP_DIR"
  
  [ "$1" == "-nt" ] && {
      shift
      echo -e "$*"
  } || echo -e "${RED}[ Fehler ]${NC} $*"
  exit 1
}

# Prüfung auf Root-Rechte (wichtig für /opt, /usr/local/bin und apt)
if [[ $EUID -ne 0 ]]; then
   echo -e "${YELLOW}Hinweis: Dieses Skript benötigt Root-Rechte für System-Installationen.${NC}"
   #abort "Bitte führe das Skript mit 'sudo ./install_lsp.sh ...' aus."
fi

# Node.js Installation (v25)
install_nodejs() {
  log "Prüfe Node.js Installation..."

  if ! command -v node >/dev/null 2>&1; then
    log "Node.js nicht gefunden. Installiere via NodeSource..."

    if ! command -v curl >/dev/null 2>&1; then
      apt update && apt install -y curl || abort "Installation von curl fehlgeschlagen"
    fi

    curl -fsSL https://deb.nodesource.com/setup_25.x | bash - \
      || abort "NodeSource Setup fehlgeschlagen"

    apt install -y nodejs || abort "Installation von nodejs fehlgeschlagen"
  fi

  log "Node.js bereit: $(node -v), npm: $(npm -v)"
}

# Kernfunktion zur Installation der Tools
install_tool() {
  local tool="$1"

  case "$tool" in
    ts|typescript)
      log "Installiere TypeScript LSP..."
      npm install -g typescript typescript-language-server \
        || abort "TypeScript LSP Installation fehlgeschlagen"
      ;;

    html)
      log "Installiere HTML/CSS/JSON LSP..."
      npm install -g vscode-langservers-extracted \
        || abort "VSCode Langserver Installation fehlgeschlagen"
      ;;

    bash)
      log "Installiere Bash LSP..."
      if npm install -g bash-language-server; then
        apt install shfmt -y 
      else
        abort "Bash LSP Installation fehlgeschlagen"
      fi
      ;;
      
    python)
      log "Installiere Python LSP (Pyright)..."
      npm install -g pyright \
        || abort "Pyright Installation fehlgeschlagen"
      ;;

    kt|kotlin|kt-direct)
      log "Installiere Kotlin LSP via Direkt-Download (GitHub Release)..."
      # Wir nutzen hier den stabilen Weg via GitHub, da das npm-Paket oft 404 wirft
      apt update && apt install -y unzip curl
      
      # Suche nach der server.zip URL via GitHub API
      local latest_url
      latest_url=$(curl -s https://api.github.com/repos/fwcd/kotlin-language-server/releases/latest | grep -oP 'https://github.com/fwcd/kotlin-language-server/releases/download/[^"]+server\.zip' | head -n 1)
      
      [ -z "$latest_url" ] && abort "Konnte Download-URL für 'server.zip' auf GitHub nicht finden."
      
      mkdir -p "$TEMP_DIR"
      local local_zip="$TEMP_DIR/kotlin-lsp.zip"
      
      log "Lade herunter: $latest_url"
      curl -Lf "$latest_url" -o "$local_zip" || abort "Download fehlgeschlagen"
      
      mkdir -p /opt/kotlin-language-server
      unzip -o "$local_zip" -d /opt/kotlin-language-server || abort "Entpacken fehlgeschlagen"
      
      # Symlink erstellen
      ln -sf /opt/kotlin-language-server/server/bin/kotlin-language-server /usr/local/bin/kotlin-language-server
      chmod +x /opt/kotlin-language-server/server/bin/kotlin-language-server
      log "Kotlin LSP erfolgreich in /opt/ installiert."
      ;;

    kfmt)
      log "Installiere kfmt (Kotlin Formatter von Meta)..."
      apt update && apt install -y curl
      
      local kfmt_url
      kfmt_url=$(curl -s https://api.github.com/repos/facebook/kfmt/releases/latest | grep -oP 'https://github.com/facebook/kfmt/releases/download/[^"]+all\.jar' | head -n 1)
      
      [ -z "$kfmt_url" ] && abort "Konnte kfmt JAR URL auf GitHub nicht finden."
      
      mkdir -p /opt/kfmt
      curl -Lf "$kfmt_url" -o /opt/kfmt/kfmt.jar || abort "kfmt Download fehlgeschlagen"
      
      # Wrapper erstellen
      cat <<EOF > /usr/local/bin/kfmt
#!/bin/bash
java -jar /opt/kfmt/kfmt.jar "\$@"
EOF
      chmod +x /usr/local/bin/kfmt
      log "kfmt erfolgreich unter /usr/local/bin/kfmt installiert."
      ;;

    vscode-kt)
      log "Installiere Kotlin Erweiterung für VS Code..."
      if command -v code >/dev/null 2>&1; then
        sudo -u "$SUDO_USER" code --install-extension fwcd.kotlin --force
      else
        log "${YELLOW}Warnung: 'code' Befehl nicht gefunden.${NC}"
      fi
      ;;

    vscode-debug)
      log "Installiere Kotlin Debugger für VS Code..."
      if command -v code >/dev/null 2>&1; then
        sudo -u "$SUDO_USER" code --install-extension fwcd.kotlin-debug --force
      else
        log "${YELLOW}Warnung: 'code' Befehl nicht gefunden.${NC}"
      fi
      ;;

    *)
      abort "Unbekanntes Tool: $tool
Verfügbar:
  ts | html | bash | python | kt | kfmt | vscode-kt | vscode-debug"
      ;;
  esac

  log "Tool ($tool) erfolgreich eingerichtet."
}

# Hauptmenü
case "$1" in
  get)
    shift
    [ $# -eq 0 ] && abort "Verwendung: install_lsp get <tool> [tool...]"
    
    # Node.js Check (nur für npm-basierte Tools nötig)
    local needs_node=false
    for t in "$@"; do
      case "$t" in
        ts|typescript|html|bash|python) needs_node=true ;;
      esac
    done
    [ "$needs_node" = true ] && install_nodejs

    # Lokalen Temp-Ordner erstellen
    mkdir -p "$TEMP_DIR"

    for tool in "$@"; do
      install_tool "$tool"
    done
    
    # Aufräumen
    log "Bereinige temporäre Dateien..."
    rm -rf "$TEMP_DIR"
    ;;

  *)
    abort "Verwendung:
  install_lsp get <tool> [tool...]

Beispiele:
  install_lsp get kt kfmt          # Kotlin LSP & Formatter (Stabile Version)
  install_lsp get vscode-kt        # VS Code Erweiterung
  install_lsp get ts bash python"
    ;;
esac