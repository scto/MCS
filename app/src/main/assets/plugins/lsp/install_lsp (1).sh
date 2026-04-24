#!/bin/bash

# * @author nullij @ github.com/nullij

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log() {
  local tag="install_lsp"
  echo -e "${GREEN}[ $tag ]${NC} $1"
}

abort() {
  [ "$1" == "-nt" ] && {
      shift
      echo -e "$*"
  } || echo -e "${RED}[ Err ]${NC} $*"
  exit 1
}

install_nodejs() {
  log "Installing Node.js via NodeSource..."

  if ! command -v curl >/dev/null 2>&1; then
    apt update && apt install -y curl || abort "Failed to install curl"
  fi

  curl -fsSL https://deb.nodesource.com/setup_25.x | bash - \
    || abort "NodeSource setup failed"

  apt install -y nodejs || abort "Failed to install nodejs"

  log "Node.js installed: $(node -v), npm: $(npm -v)"
}

install_lsp() {
  local server="$1"

  case "$server" in
    ts|typescript)
      log "Installing TypeScript LSP..."
      npm install -g typescript typescript-language-server \
        || abort "Failed to install TypeScript LSP"
      ;;

    html)
      log "Installing HTML/CSS/JSON LSP..."
      npm install -g vscode-langservers-extracted \
        || abort "Failed to install vscode langservers"
      ;;

    bash)
      log "Installing Bash LSP..."
      if npm install -g bash-language-server; then
        apt install shfmt -y # for document formating 
      else
        abort "Failed to install bash-language-server"
      fi
      ;;
      
    python)
      log "Installing Python LSP..."
      npm install -g pyright
      ;;

    *)
      abort "Unknown server: $server
Available:
  ts | typescript
  html
  bash"
      ;;
  esac

  log "LSP ($server) installed successfully"
}

case "$1" in
  get)
    shift
    [ $# -eq 0 ] && abort "Usage: install_lsp get <server> [server...]"
    install_nodejs
    for server in "$@"; do
      install_lsp "$server"
    done
    ;;

  *)
    abort "Usage:
  install_lsp get <server> [server...]

Examples:
  install_lsp get ts
  install_lsp get html
  install_lsp get bash
  install_lsp get python
  install_lsp get html ts bash python"
    ;;
esac
