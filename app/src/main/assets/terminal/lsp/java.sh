set -e

source "$LOCAL/bin/utils"

info 'Preparing...'
apt update && apt upgrade -y

install() {
  if ! command_exists node || ! command_exists npm; then
    install_nodejs
  fi

  info 'Installing Java language server...'
  npm install -g --prefix /usr @vscjava/java-language-server
  info 'Java language server installed successfully.'
  exit 0
}

uninstall() {
  info 'Uninstalling Java language server...'
  npm uninstall -g --prefix /usr  @vscjava/java-language-server
  info 'Java language server uninstalled successfully.'
  uninstall_nodejs
  exit 0
}

update() {
  info 'Updating Java language server...'
  npm update -g --prefix /usr  @vscjava/java-language-server
  info 'Java language server updated successfully.'
  exit 0
}

case "$1" in
  --uninstall) uninstall;;
  --update) update;;
  *) install;;
esac

