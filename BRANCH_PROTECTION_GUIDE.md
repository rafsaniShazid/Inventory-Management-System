# Branch Protection Rules - Visual Guide

## 🔒 How Branch Protection Works

### WITHOUT Protection (❌ Not Professional)

```
Student → Writes bad code → Pushes to main → OOPS! Main is broken! 💥
```

### WITH Protection (✅ Professional)

```
Member 1 → feature-inventory → Creates PR → Member 2 reviews →
Finds bugs → Member 1 fixes → Member 2 approves → Merge to develop →
All good! ✨
```

---

## 📸 Screenshot Guide for GitHub Settings

### Navigation Path:

```
GitHub Repo Homepage
    ↓ Click "Settings" tab
    ↓ Click "Branches" (left sidebar)
    ↓ Click "Add branch protection rule"
```

### What You'll See:

```
┌─────────────────────────────────────────┐
│ Branch protection rule                   │
├─────────────────────────────────────────┤
│ Branch name pattern: [main          ]   │
│                                          │
│ ☑ Require a pull request before merging │
│   ☑ Require approvals: [1]             │
│                                          │
│ ☐ Require status checks to pass         │
│                                          │
│ ☑ Do not allow bypassing                │
│                                          │
│        [Create] button                   │
└─────────────────────────────────────────┘
```

---

## 🎯 Why Each Setting Matters

### 1. "Require a pull request before merging"

**Without this:**

```powershell
git push origin main  # ✅ Direct push works
# Result: No review, might push bugs!
```

**With this:**

```powershell
git push origin main  # ❌ BLOCKED!
# Result: Must create PR → Forces code review
```

### 2. "Require approvals: 1"

**What it means:**

- Your teammate MUST click "Approve" button
- They MUST actually read your code
- Shows collaboration in Git history
- Teachers can see both members participated

**Without this:**

- You could approve your own PR
- Defeats the purpose of code review

### 3. "Do not allow bypassing"

**What it means:**

- Even admins can't skip the rules
- No shortcuts allowed
- Professional standard

---

## 🌐 Finding Your Repository Settings

**URL Pattern:**

```
https://github.com/[username]/[repository-name]/settings/branches
```

**Example:**

```
https://github.com/john-doe/inventory-management-system/settings/branches
```

**If you can't find it:**

1. Go to your repository main page
2. Look for tabs: `Code`, `Issues`, `Pull requests`, **`Settings`**
3. If you don't see Settings → Ask your teammate who created repo (they need to add you as collaborator)

---

## 👥 Adding Collaborator (Important!)

### Repository Owner Does This:

1. Go to: `https://github.com/[username]/[repo]/settings/access`
2. Click "Add people"
3. Enter teammate's GitHub username
4. Select "Write" permission
5. They'll get email invitation

### Both Members Need Write Access:

- To create branches
- To push code
- To review PRs
- To merge approved PRs

---

## ✅ How to Verify Protection is Working

### Test 1: Try Direct Push to Main

```powershell
git checkout main
echo "test" > test.txt
git add test.txt
git commit -m "test"
git push origin main
```

**Expected Result:**

```
remote: error: GH006: Protected branch update failed for refs/heads/main.
remote: error: Changes must be made through a pull request.
To https://github.com/username/repo.git
 ! [remote rejected] main -> main (protected branch hook declined)
```

**If you see this error: ✅ Protection is working!**
**If it pushes successfully: ❌ Protection NOT set up correctly**

```powershell
# Clean up test
git reset --hard HEAD~1
```

### Test 2: Feature Branch Should Work

```powershell
git checkout -b test-branch
git push origin test-branch  # ✅ Should work fine!
```

---

## 🎓 What This Shows Teachers

When your teacher reviews your GitHub:

**They see:**
✅ Multiple branches (feature-inventory, feature-requests, etc.)
✅ Pull requests with descriptions
✅ Code review comments
✅ "Approved by: [teammate]" on PRs
✅ Merge commits showing proper workflow
✅ Protected branches

**This proves:**

- You understand Git workflow
- You work as a team
- You follow industry standards
- You do code reviews

**Result: Better marks! 🌟**

---

## 🆘 Troubleshooting

### "I don't see Settings tab"

**Solution:** You're not added as collaborator. Repository owner needs to add you.

### "Branch protection not working"

**Check:**

1. Is the branch name pattern exactly `main` (not `Main` or `master`)?
2. Did you click "Create" button at bottom?
3. Did you save changes?
4. Try refreshing the page

### "Can't create pull request"

**Check:**

1. Are you on a feature branch? (not main/develop)
2. Did you push your branch to GitHub first?
3. Are there any commits to merge?

---

## 📚 Additional Resources

**Learn More:**

- [GitHub Branch Protection Docs](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [Pull Request Tutorial](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/proposing-changes-to-your-work-with-pull-requests/about-pull-requests)

**Remember:** This is the SAME workflow used by companies like Microsoft, Google, Facebook! 🚀

You're learning professional software engineering! 💪
