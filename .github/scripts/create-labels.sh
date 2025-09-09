#!/bin/bash

# Script to create GitHub labels for BCrypt Java Library
# Usage: ./create-labels.sh [owner/repo] [token]
# Example: ./create-labels.sh lucimber/bcrypt-java $GITHUB_TOKEN

REPO=${1:-lucimber/bcrypt-java}
TOKEN=${2:-$GITHUB_TOKEN}

if [ -z "$TOKEN" ]; then
    echo "Error: GitHub token is required"
    echo "Usage: $0 [owner/repo] [github-token]"
    exit 1
fi

API_URL="https://api.github.com/repos/$REPO/labels"

echo "Creating labels for repository: $REPO"

# Function to create a label
create_label() {
    local name=$1
    local color=$2
    local description=$3
    
    echo "Creating label: $name"
    
    curl -s -X POST "$API_URL" \
        -H "Authorization: token $TOKEN" \
        -H "Accept: application/vnd.github.v3+json" \
        -d "{\"name\":\"$name\",\"color\":\"$color\",\"description\":\"$description\"}" \
        > /dev/null
}

# Type labels
create_label "type: bug" "d73a4a" "Something isn't working"
create_label "type: feature" "0e8a16" "New feature or request"
create_label "type: enhancement" "a2eeef" "Improvement to existing functionality"
create_label "type: documentation" "0075ca" "Improvements or additions to documentation"
create_label "type: question" "d876e3" "Further information is requested"
create_label "type: discussion" "f9d0c4" "Discussion or proposal"
create_label "type: chore" "fef2c0" "Maintenance tasks, refactoring, etc."

# Priority labels
create_label "priority: critical" "b60205" "Critical issue requiring immediate attention"
create_label "priority: high" "ff9f1c" "High priority issue"
create_label "priority: medium" "ffcc00" "Medium priority issue"
create_label "priority: low" "c5def5" "Low priority issue"

# Status labels
create_label "status: needs-triage" "e6e6e6" "Needs to be triaged and prioritized"
create_label "status: confirmed" "0e8a16" "Issue has been confirmed and reproduced"
create_label "status: in-progress" "fbca04" "Work is currently being done"
create_label "status: blocked" "d93f0b" "Blocked by another issue or external factor"
create_label "status: needs-review" "fbca04" "Needs code review"
create_label "status: needs-testing" "7057ff" "Needs testing verification"
create_label "status: ready" "0e8a16" "Ready to be merged/deployed"
create_label "status: stale" "795548" "No recent activity"

# Area labels
create_label "area: core" "1d76db" "Core BCrypt algorithm implementation"
create_label "area: api" "5319e7" "Public API surface"
create_label "area: security" "d4c5f9" "Security-related changes or issues"
create_label "area: performance" "006b75" "Performance improvements or issues"
create_label "area: tests" "bfd4f2" "Test suite and coverage"
create_label "area: integration" "c2e0c6" "Integration with other libraries"
create_label "area: build" "fad8c7" "Build system, Gradle configuration"
create_label "area: ci/cd" "98f5ff" "Continuous Integration/Deployment"
create_label "area: dependencies" "0366d6" "Dependency updates or issues"

# Component labels
create_label "component: BCryptService" "d4c5f9" "BCryptService class"
create_label "component: Password" "d4c5f9" "Password value object"
create_label "component: Hash" "d4c5f9" "Hash value object"
create_label "component: Salt" "d4c5f9" "Salt value object"
create_label "component: BCryptEngine" "d4c5f9" "BCryptEngine implementation"

# Version labels
create_label "version: 1.0" "cccccc" "Version 1.0.x"
create_label "version: 1.1" "cccccc" "Version 1.1.x"
create_label "version: 2.0" "cccccc" "Version 2.0.x (future)"

# Breaking change labels
create_label "breaking-change" "d73a4a" "Introduces breaking changes"
create_label "semver: major" "b60205" "Major version bump required"
create_label "semver: minor" "ff9f1c" "Minor version bump required"
create_label "semver: patch" "0e8a16" "Patch version bump required"

# Platform labels
create_label "platform: windows" "bfdadc" "Windows-specific issue"
create_label "platform: linux" "bfdadc" "Linux-specific issue"
create_label "platform: macos" "bfdadc" "macOS-specific issue"
create_label "java: 17" "f7c6c7" "Java 17 specific"
create_label "java: 21" "f7c6c7" "Java 21 specific"

# Help/Community labels
create_label "help wanted" "008672" "Extra attention is needed"
create_label "good first issue" "7057ff" "Good for newcomers"
create_label "needs-reproduction" "d876e3" "Needs steps to reproduce the issue"
create_label "needs-more-info" "d876e3" "Needs more information from reporter"

# Resolution labels
create_label "resolution: fixed" "0e8a16" "Issue has been fixed"
create_label "resolution: wontfix" "ffffff" "This will not be worked on"
create_label "resolution: duplicate" "cfd3d7" "This issue or pull request already exists"
create_label "resolution: invalid" "e4e669" "This doesn't seem right"
create_label "resolution: by-design" "c5def5" "Working as intended"
create_label "resolution: external" "c5def5" "Issue is external to this project"

# Special labels
create_label "pinned" "fbca04" "Pinned issue/PR - won't be auto-closed"
create_label "needs-backport" "fbca04" "Needs to be backported to older version"
create_label "community-contribution" "0366d6" "Contribution from community member"
create_label "hacktoberfest" "ff6b00" "Good issue for Hacktoberfest"
create_label "bounty" "129e5e" "Has a bounty associated"

# Automation labels
create_label "bot" "ededed" "Automated action or bot-created"
create_label "dependencies" "0366d6" "Pull requests that update a dependency file"

echo "Label creation complete!"