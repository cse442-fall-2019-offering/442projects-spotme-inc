import datetime
from flask import request, jsonify

from .app import app, db
from . import models
from sqlalchemy.orm import joinedload

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

@app.route("/accepted-matches", methods=["GET"])
def accepted_matches_get():

	user = models.User.query.options(joinedload('accepted_matches')).filter_by(id=request.args["id"]).first()

	mylist = []

	for match in user.accepted_matches:
		mylist.append(match.to_dict())

	return jsonify({"matches":mylist})

@app.route("/accepted-matches", methods=["PUT"])
def accepted_matches_put():
	newMatch1 = models.AcceptedMatches()
	newMatch1.user1 = request.args["user1"]
	newMatch1.user2 = request.args["user2"]

	newMatch2 = models.AcceptedMatches()
	newMatch2.user1 = request.args["user2"]
	newMatch2.user2 = request.args["user1"]

	db.session.add(newMatch1)
	db.session.add(newMatch2)
	db.session.commit()

	return jsonify({"put-received": True})

@app.route("/accepted-matches", methods=["DELETE"])
def accepted_matches_remove():
	
	models.AcceptedMatches.query.filter_by(user1=request.args["user1"], user2=request.args["user2"]).delete()
	return jsonify({"delete-received": True})