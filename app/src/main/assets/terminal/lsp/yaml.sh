set -e

source "$LOCAL/bin/utils"

info 'Preparing...'
apt update && apt upgrade -y

install() {
  if ! command_exists node || ! command_exists npm; then
    install_nodejs
  fi

  info 'Installing Yaml language server...'
  npm install -g --prefix /usr yaml-language-server
  info 'Yaml language server installed successfully.'
  exit 0
}

uninstall() {
  info 'Uninstalling Yaml language server...'
  npm uninstall -g --prefix /usr yaml-language-server @vscjava/java-language-server
  info 'Yaml language server uninstalled successfully.'
  uninstall_nodejs
  exit 0
}

update() {
  info 'Updating Yaml language server...'
  npm update -g --prefix /usr yaml-language-server
  info 'Yaml language server updated successfully.'
  exit 0
}

case "$1" in
  --uninstall) uninstall;;
  --update) update;;
  *) install;;
esac