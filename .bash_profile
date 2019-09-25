if command -v tmux>/dev/null; then
  [[ ! xterm-256color =~ screen ]] && [ -z  ] && exec tmux
fi
