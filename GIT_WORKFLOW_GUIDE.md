# Git Workflow Guide for Stationery Inventory Management System

## 🎯 Why This Matters for Your Marks

- Shows proper software engineering practices
- Demonstrates team collaboration
- Professional code review process
- Teachers specifically look for this!

---

## 📋 Branch Protection Rules Setup

### **Step 1: Go to GitHub Repository Settings**

1. Open your repository: `https://github.com/your-username/your-repo-name`
2. Click **"Settings"** tab (top right)
3. Click **"Branches"** in left sidebar
4. Click **"Add branch protection rule"** button

### **Step 2: Protect `main` Branch**

**Branch name pattern:** `main`

**Enable these checkboxes:**

- ✅ **Require a pull request before merging**
  - Check: "Require approvals" → Set to 1
  - This means your teammate MUST review before merge
- ✅ **Require conversation resolution before merging** (optional but good)
- ✅ **Do not allow bypassing the above settings**

**Click "Create" button**

### **Step 3: Protect `develop` Branch**

Repeat Step 2, but use `develop` as branch name pattern.

**Result:**

- ❌ Cannot push directly to `main` or `develop`
- ✅ Must create Pull Request
- ✅ Teammate must review and approve
- ✅ Professional workflow!

---

## 🌳 Branch Structure

```
main (stable, protected)
  ├── develop (integration branch, protected)
      ├── feature-inventory (Member 1 - Category & Item)
      ├── feature-requests (Member 2 - Request module)
      ├── feature-auth (Member 1 - Login/Register/Security)
      ├── feature-docker (Member 1 - Docker setup)
      └── feature-testing (Both members)
```

---

## 🚀 Proper Workflow: Step-by-Step

### **Phase 1: Initial Setup (Do Together - ONE PERSON DOES THIS)**

```powershell
# Make sure you're on main branch
git checkout main
git pull origin main

# Create develop branch from main
git checkout -b develop

# Push develop to GitHub
git push origin develop

# Now both teammates have: main and develop branches
```

---

### **Phase 2: Member 1 - Start Inventory Module**

#### **Step 1: Create Feature Branch**

```powershell
# Start from develop branch
git checkout develop
git pull origin develop

# Create your feature branch
git checkout -b feature-inventory

# Verify you're on the right branch
git branch
# Should show: * feature-inventory
```

#### **Step 2: Do Your Work**

Now create files:

- `entity/Category.java`
- `entity/Item.java`
- `repository/CategoryRepository.java`
- `repository/ItemRepository.java`
- `service/CategoryService.java`
- `service/ItemService.java`
- `controller/CategoryController.java`
- `controller/ItemController.java`
- `dto/CategoryDTO.java`
- `dto/ItemDTO.java`

#### **Step 3: Commit Your Changes**

```powershell
# See what files you changed
git status

# Add all your files
git add .

# Commit with meaningful message
git commit -m "feat: Add Category and Item entities with CRUD operations

- Created Category and Item entities
- Implemented repositories for database access
- Added service layer with business logic
- Created REST controllers for API endpoints
- Added DTOs for request/response handling"

# Push to GitHub
git push origin feature-inventory
```

#### **Step 4: Create Pull Request on GitHub**

1. Go to your GitHub repository
2. You'll see: **"feature-inventory had recent pushes"** → Click **"Compare & pull request"**
3. Fill in PR details:
   - **Base:** `develop` ← **Compare:** `feature-inventory`
   - **Title:** "Feature: Inventory Module (Category & Item Management)"
   - **Description:**

     ```
     ## What This PR Does
     - Adds Category and Item entities
     - Implements CRUD operations for inventory management
     - Creates REST API endpoints:
       - GET /categories - List all categories
       - POST /categories - Add new category
       - GET /items - List all items
       - POST /items - Add new item
       - PUT /items/{id} - Update item
       - DELETE /items/{id} - Delete item

     ## Testing Done
     - Tested entity creation
     - Verified database tables created
     - Tested API endpoints with Postman

     ## Screenshots (optional)
     [Add Postman screenshots here]
     ```
4. **Request review from:** Your teammate (Member 2)
5. Click **"Create pull request"**

#### **Step 5: Code Review Process**

**Member 2 must:**

1. Go to Pull Requests tab
2. Click on the PR
3. Click **"Files changed"** tab
4. Review the code:
   - Read through each file
   - Ask questions if unclear (use comment feature)
   - Check for bugs or improvements
5. **If good:** Click **"Review changes"** → **"Approve"** → **"Submit review"**
6. **Then merge:** Click **"Merge pull request"** → **"Confirm merge"**
7. **Delete branch:** Click **"Delete branch"** button (keeps repo clean)

#### **Step 6: Update Your Local Repository**

```powershell
# Switch back to develop
git checkout develop

# Pull the merged changes
git pull origin develop

# Delete local feature branch (no longer needed)
git branch -d feature-inventory

# Now develop has your inventory module!
```

---

### **Phase 3: Member 1 - Authentication Module**

```powershell
# Start fresh from updated develop
git checkout develop
git pull origin develop

# Create new feature branch
git checkout -b feature-auth

# Do your work:
# - User & Role entities
# - Authentication service
# - JWT token generation
# - Security configuration
# - Login/Register endpoints

# Commit and push
git add .
git commit -m "feat: Add authentication and authorization system"
git push origin feature-auth

# Create PR, get review, merge (same process as above)
```

---

### **Phase 4: Member 1 - Docker Setup**

```powershell
git checkout develop
git pull origin develop
git checkout -b feature-docker

# Create Dockerfile, docker-compose.yml
# Update configuration

git add .
git commit -m "feat: Add Docker containerization"
git push origin feature-docker

# PR → Review → Merge
```

---

## 📝 Commit Message Best Practices

**Format:** `type: description`

**Types:**

- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `test:` - Adding tests
- `refactor:` - Code restructuring

**Examples:**

```bash
git commit -m "feat: Add Item entity with category relationship"
git commit -m "fix: Resolve stock quantity validation bug"
git commit -m "test: Add unit tests for ItemService"
git commit -m "docs: Update API documentation for items endpoint"
```

---

## 🔄 If You Need to Update Branch with Latest Changes

```powershell
# Your teammate merged something to develop
# You want those changes in your feature branch

git checkout feature-inventory
git pull origin develop  # Get latest develop changes
# Resolve any conflicts if they occur
git push origin feature-inventory
```

---

## ⚠️ Common Mistakes to Avoid

### ❌ **DON'T:**

```powershell
# Don't work directly on main or develop
git checkout main
# ❌ BAD - editing files here...

# Don't push directly to protected branches
git push origin main  # ❌ Will be rejected!
```

### ✅ **DO:**

```powershell
# Always create feature branch
git checkout -b feature-something
# ✅ GOOD - work here, then PR
```

---

## 🎯 Quick Reference Commands

```powershell
# See current branch
git branch

# See all branches (including remote)
git branch -a

# Switch to branch
git checkout branch-name

# Create and switch to new branch
git checkout -b new-branch-name

# See what changed
git status
git diff

# Undo uncommitted changes to a file
git restore filename.java

# See commit history
git log --oneline

# Pull latest from remote
git pull origin branch-name

# Push to remote
git push origin branch-name
```

---

## 📊 Your Work Distribution with Branches

### **Member 1 (You)**

1. `feature-inventory` → Category & Item module
2. `feature-auth` → Authentication system
3. `feature-docker` → Docker containerization
4. `feature-testing` (part) → Tests for your modules

### **Member 2**

1. `feature-requests` → Request module
2. `feature-validation` → Global exception handling & validation
3. `feature-cicd` → GitHub Actions deployment
4. `feature-testing` (part) → Tests for their modules

---

## 🎓 What Teachers Look For

✅ **Multiple branches used** (not just main)
✅ **Pull Requests with descriptions**
✅ **Code reviews with comments**
✅ **Meaningful commit messages**
✅ **Protected main branch**
✅ **Both members contributing**

This shows you understand **professional software development workflow**!

---

## 🆘 If Something Goes Wrong

### **I pushed to wrong branch!**

```powershell
# Don't panic! You can undo
git reset --hard HEAD~1  # Undo last commit
# Or ask for help - it's fixable!
```

### **I have merge conflicts!**

1. Git will mark conflicts in files: `<<<<<<< HEAD`
2. Edit file to resolve conflict
3. Remove conflict markers
4. `git add filename.java`
5. `git commit -m "fix: Resolve merge conflict"`
6. `git push`

### **Need help?**

Ask your teammate or instructor - that's what teams are for!

---

**Remember:** This workflow might seem complex at first, but it's how real software teams work at Google, Microsoft, etc. You're learning professional skills! 🚀
