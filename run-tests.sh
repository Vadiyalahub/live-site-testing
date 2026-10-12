#!/usr/bin/env bash
# Shortcut for the common test runs.
#
# Usage: ./run-tests.sh [smoke|regression|known-bugs|all] [--headed]
#   --headed  opens a visible browser window so you can watch the tests

set -euo pipefail

SUITE="${1:-smoke}"
HEADLESS=true
if [[ "${2:-}" == "--headed" ]]; then
  HEADLESS=false
fi

run_ui() {
  mvn -B test -Dtest=RunCucumberTest -Dheadless="$HEADLESS" -Dcucumber.filter.tags="$1"
}

case "$SUITE" in
  smoke)      run_ui "@smoke" ;;
  regression) run_ui "(@smoke or @regression) and not @known-bug" ;;
  known-bugs) run_ui "@known-bug" ;;
  all)        run_ui "not @known-bug" ;;
  *)
    echo "Unknown suite: $SUITE"
    echo "Usage: ./run-tests.sh [smoke|regression|known-bugs|all] [--headed]"
    exit 1
    ;;
esac

echo
echo "HTML report: target/cucumber-reports/cucumber.html"
