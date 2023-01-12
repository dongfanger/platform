import os
import shutil

current_path = os.path.abspath(__file__)
root_path = os.path.dirname(os.path.dirname(current_path))
frontend_path = os.path.join(root_path, "frontend")
dist_path = os.path.join(frontend_path, "dist")
static_path = os.path.join(root_path, "src", "main", "resources", "static")

os.chdir(frontend_path)
if os.path.exists(dist_path):
    shutil.rmtree(dist_path)
os.system("npm run build")
if os.path.exists(static_path):
    shutil.rmtree(static_path)
shutil.copytree(dist_path, static_path)
