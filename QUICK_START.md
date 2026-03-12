# 🚀 QUICK START: What to Do RIGHT NOW

## Step 1: Set Up Branch Protection (5 minutes)

**Do this FIRST before any coding!**

1. Go to: `https://github.com/YOUR-USERNAME/YOUR-REPO/settings/branches`
2. Click "Add branch protection rule"
3. Branch name: `main`
4. Check: ✅ Require pull request before merging
5. Check: ✅ Require approvals (set to 1)
6. Click "Create"
7. **Repeat for `develop` branch**

**How to verify it's working:**

- Try to push to main: `git push origin main` → Should be blocked! ✅

---

## Step 2: Create Develop Branch (2 minutes)

**ONE person does this (Member 1 or 2, decide together)**

```powershell
cd D:\3-2\Projects\SEPM\Inventory-Management-System\inventory_management

# Make sure you're on main
git checkout main
git pull origin main

# Create develop branch
git checkout -b develop

# Push to GitHub
git push origin develop
```

**Both members should now pull:**

```powershell
git checkout main
git pull origin main
git checkout develop
git pull origin develop
```

---

## Step 3: Member 1 - Start Your First Feature (30 minutes)

```powershell
# Create your feature branch
git checkout -b feature-inventory

# Now you can start coding!
# Create the files as I showed you earlier:
# - entities (Category, Item)
# - repositories
# - services
# - controllers
```

---

## 📋 Today's Checklist

- [ ] Set up branch protection rules on GitHub (main & develop)
- [ ] Create and push develop branch
- [ ] Both members pull develop branch
- [ ] Member 1: Create feature-inventory branch
- [ ] Member 2: Create feature-requests branch
- [ ] Start coding in your feature branches (NOT in main/develop!)

---

## ❓ Questions to Confirm Understanding

Ask yourself these before starting:

1. **Can I push directly to main?** → NO! Must use PR
2. **Where do I create Category.java?** → In `feature-inventory` branch
3. **Who reviews my pull request?** → My teammate (Member 2)
4. **What happens after PR is approved?** → Merge to develop, delete feature branch
5. **Do I work in main or develop?** → NO! Always in feature-\* branches

If you can answer these, you understand the workflow! 🎉

---

## 🎯 Next: Ready to Code?

Once branches are set up, tell me and I'll help you create:

1. Application configuration (database setup)
2. Base entities (User, Role, Category, Item, Request)
3. Your inventory module (Category & Item controllers/services)

**But this time, we'll do it in the RIGHT branch!** 🌟
