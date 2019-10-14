from os import path
from sys import exit
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate

app = Flask(__name__)

app.config.from_pyfile(path.join(path.pardir, "config.py"))

if not app.config["SECRET_KEY"]:
	app.logger.fatal("SECRET_KEY must be configured!")
	exit(1)

db = SQLAlchemy(app)
migrate = Migrate(app, db)
