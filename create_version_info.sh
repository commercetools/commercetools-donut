#!/bin/bash

VERSION_FILE="public/version.txt"

cat >"${VERSION_FILE}" <<EOF
Date: $(date)
Build: ${TRAVIS_JOB_NUMBER}
Version: ${TRAVIS_COMMIT}
EOF

git config user.name "Travis CI"
git config user.email "automation@commercetools.de"
git commit -m "Add generated version.txt." "${VERSION_FILE}"
