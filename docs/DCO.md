# Developer Certificate of Origin (DCO)

## What is the DCO?

The Developer Certificate of Origin (DCO) is a lightweight way for contributors to certify that they wrote or otherwise have the right to submit the code they are contributing to the project.

## Why do we use the DCO?

For a cryptographic library like this, it's important to maintain clear provenance of all code contributions. The DCO helps us:

- Ensure all contributions are properly licensed
- Maintain a clear audit trail of code authorship
- Protect both contributors and users from potential legal issues
- Follow best practices for security-sensitive code

## The DCO Text

```
Developer Certificate of Origin
Version 1.1

Copyright (C) 2004, 2006 The Linux Foundation and its contributors.

Everyone is permitted to copy and distribute verbatim copies of this
license document, but changing it is not allowed.

Developer's Certificate of Origin 1.1

By making a contribution to this project, I certify that:

(a) The contribution was created in whole or in part by me and I
    have the right to submit it under the open source license
    indicated in the file; or

(b) The contribution is based upon previous work that, to the best
    of my knowledge, is covered under an appropriate open source
    license and I have the right under that license to submit that
    work with modifications, whether created in whole or in part
    by me, under the same open source license (unless I am
    permitted to submit under a different license), as indicated
    in the file; or

(c) The contribution was provided directly to me by some other
    person who certified (a), (b) or (c) and I have not modified
    it.

(d) I understand and agree that this project and the contribution
    are public and that a record of the contribution (including all
    personal information I submit with it, including my sign-off) is
    maintained indefinitely and may be redistributed consistent with
    this project or the open source license(s) involved.
```

## How to Sign Off Your Commits

### Method 1: Using the -s flag (Recommended)

When making a commit, add the `-s` or `--signoff` flag:

```bash
git commit -s -m "feat: add new validation method"
```

This automatically adds a `Signed-off-by` line to your commit message:
```
feat: add new validation method

Signed-off-by: Jane Developer <jane@example.com>
```

### Method 2: Manual Sign-off

You can manually add the sign-off line to your commit message:

```bash
git commit -m "feat: add new validation method

Signed-off-by: Jane Developer <jane@example.com>"
```

### Method 3: Configure Git to Always Sign Off

Set up Git to automatically sign off all commits:

```bash
git config --global user.name "Jane Developer"
git config --global user.email "jane@example.com"
git config --global commit.signoff true
```

## Fixing Unsigned Commits in Your PR

Most commonly, you'll notice missing sign-offs after pushing to your PR when the DCO check fails. Here's how to fix it:

### Quick Fix for All Commits in Your PR

This is usually what you need:

```bash
# Fetch the latest main branch
git fetch origin main

# Sign off all commits in your PR
git rebase --signoff origin/main

# Force push to update your PR
git push --force-with-lease
```

Your PR will automatically update and the DCO check will run again.

### Fix Specific Number of Commits

If you know exactly how many commits need signing:

```bash
# Example: Sign off your last 2 commits
git rebase --signoff HEAD~2

# Force push to update your PR
git push --force-with-lease
```

### Fix Just the Last Commit

If only your most recent commit is unsigned:

```bash
# Amend the last commit with sign-off
git commit --amend -s --no-edit

# Force push to update your PR
git push --force-with-lease
```

### Check Which Commits Need Signing

To see which commits in your branch are missing sign-offs:

```bash
# Show commits in your branch that aren't in main
git log --oneline origin/main..HEAD

# Check for Signed-off-by lines
git log origin/main..HEAD --pretty=format:"%h %s%n%b" | grep -B1 "Signed-off-by"
```

### If Something Goes Wrong

If you make a mistake during rebase:

```bash
# Abort the rebase and start over
git rebase --abort

# Or if you already pushed, reset to the remote version
git fetch origin
git reset --hard origin/your-branch-name
```

Then try the signing process again.

## Important Notes

1. **Use Your Real Name**: The sign-off must include your real name (no pseudonyms or anonymous contributions).

2. **Use a Valid Email**: The email address should be valid and associated with your GitHub account.

3. **Understand What You're Signing**: By signing off, you're legally stating that you have the right to contribute the code under the project's license.

4. **Company Contributions**: If you're contributing on behalf of your employer, ensure you have permission and use your company email address.

## Verification

Our CI system automatically checks that all commits in a pull request are properly signed off. If the check fails:

1. You'll see a failed "DCO Check" status on your PR
2. A comment will be added with instructions on how to fix it
3. After fixing, push your changes and the check will run again

## Questions?

If you have questions about the DCO or need help with signing off your commits:

1. Check the [Contributing Guide](CONTRIBUTING.md)
2. Open a [GitHub Discussion](https://github.com/lucimber/bcrypt-java/discussions)
3. Review the [official DCO website](https://developercertificate.org/)

## Alternative: Small Contributions

For very small contributions (typos, documentation fixes), you may see projects accept contributions without DCO. However, for BCrypt Java, we require DCO for all contributions to maintain consistency and legal clarity.

## Not a CLA

The DCO is **not** a Contributor License Agreement (CLA). It's much simpler:
- No need to sign any external documents
- No need to create accounts on third-party services
- Just add a line to your commit messages
- You retain copyright of your contributions