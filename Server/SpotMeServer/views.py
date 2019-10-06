from .app import app, db
from . import models

@app.route("/")
def index():
	return "Hello, World!"
