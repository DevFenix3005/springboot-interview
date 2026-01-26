#!/usr/bin/env bash
set -euo pipefail

if ! command -v openssl >/dev/null 2>&1; then
  echo "openssl is required to generate keys" >&2
  exit 1
fi

KEY_DIR=${1:-jwt-keys}
KEY_SIZE=${KEY_SIZE:-2048}

mkdir -p "${KEY_DIR}"

PRIVATE_PEM="${KEY_DIR}/jwt-private.pem"
PUBLIC_PEM="${KEY_DIR}/jwt-public.pem"

if [ -f "${PRIVATE_PEM}" ] || [ -f "${PUBLIC_PEM}" ]; then
  echo "Key files already exist in ${KEY_DIR}. Remove them to re-generate." >&2
  exit 1
fi

openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:"${KEY_SIZE}" -out "${PRIVATE_PEM}"
openssl rsa -in "${PRIVATE_PEM}" -pubout -out "${PUBLIC_PEM}"

ACTIVE_PRIVATE=$(openssl pkcs8 -topk8 -inform PEM -outform DER -in "${PRIVATE_PEM}" -nocrypt | base64 | tr -d '\n')
ACTIVE_PUBLIC=$(openssl rsa -in "${PRIVATE_PEM}" -pubout -outform DER | base64 | tr -d '\n')

cat <<OUTPUT
Generated keys in ${KEY_DIR}

Export the following variables:

export SECURITY_JWT_KEYS_ACTIVE_PRIVATE='${ACTIVE_PRIVATE}'
export SECURITY_JWT_KEYS_ACTIVE_PUBLIC='${ACTIVE_PUBLIC}'

Optional (when rotating):
export SECURITY_JWT_KEYS_PREVIOUS_PUBLIC='<BASE64_PREVIOUS_PUBLIC_KEY>'
OUTPUT
