git init
git remote add scaladoc git@github.com:shkr/actors.git
git fetch --depth=1 scaladoc gh-pages
git add --all
git commit -m "scaldoc v0.1"
git merge --no-edit -s ours remotes/scaladoc/gh-pages
git push scaladoc master:gh-pages

