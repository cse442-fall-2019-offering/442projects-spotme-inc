import datetime
from flask import request, jsonify

from .app import app, db
from . import models

@app.route("/")
def index():
    return "Hello, World!"

@app.route("/user", methods=["GET"])
def user_get():
    user = models.User.query.filter_by(id=request.args["id"]).one()
    return jsonify(user.to_dict())

@app.route("/user", methods=["POST"])
def user_update():
    user_json = request.json["user"]
    update_dict = {}
    for k, v in user_json.items():
        if k == "id":
            continue
        if k == "dob":
            v = datetime.datetime.strptime(v, "%Y-%m-%d")
        update_dict[k] = v
    user = models.User.query.filter_by(id=user_json["id"]) \
            .update(update_dict)
    db.session.commit()
    return ""
