#!/usr/bin/env sh
set -eu

if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven nao encontrado. Instale Maven 3.6.3+ ou gere o Maven Wrapper completo." >&2
  exit 1
fi

exec mvn "$@"
