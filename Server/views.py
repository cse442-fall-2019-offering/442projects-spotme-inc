import datetime
from flask import request, jsonify

from .app import app, db
from . import models, util
from sqlalchemy.orm import joinedload

@app.route("/")
def index():
    return "Hello, World!"

from . import util

@app.route("/matchscore", methods=["GET"])
def get_match_score():
	user1Id = request.args[0];
	user2Id = request.args[1];

	user1Obj = models.User.query.filter_by(id=user1Id).one()
	user2Obj = models.User.query.filter_by(id=user2Id).one()

	matchScore = util.match_score(user1Obj, user2Obj)

	return jsonify("score:" + matchScore)

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
    new_match1 = models.AcceptedMatches()
    new_match1.user1 = request.args["user1"]
    new_match1.user2 = request.args["user2"]

    new_match2 = models.AcceptedMatches()
    new_match2.user1 = request.args["user2"]
    new_match2.user2 = request.args["user1"]

    db.session.add(new_match1)
    db.session.add(new_match2)
    db.session.commit()

    return jsonify({"put-received": True})

@app.route("/accepted-matches", methods=["DELETE"])
def accepted_matches_remove():
    models.AcceptedMatches.query.filter_by(user1=request.args["user1"], user2=request.args["user2"]).delete()
    return jsonify({"delete-received": True})

@app.route("/stored-chats", methods=["GET"])
def chats_get_history():
    sender = models.User.query.filter_by(id=request.args["id"]).one()
    receiver = models.User.query.filter_by(id=request.args["other_id"]).one()
    chat_list = []

    for message in models.ChatMessage.query.filter_by(sender=sender.id, receiver=receiver.id).all():
        chat_list.append(message.to_dict())

    for message in models.ChatMessage.query.filter_by(sender=receiver.id, receiver=sender.id).all():
        chat_list.append(message.to_dict())

    chat_list.sort(key=lambda x: x["time"])

	return jsonify({"messages": chat_list})

@app.route("/stored-chats", methods=["PUT"])
def chats_enter_chat():
    new_message = models.ChatMessage()
    new_message.sender = request.args["user1"]
    new_message.receiver = request.args["user2"]
    new_message.message = request.args["message"]

    db.session.add(new_message)
    db.session.commit()

    return jsonify({"put-received": True})

@app.route("/matches", methods=["GET"])
def match_list():
    user = models.User.query.filter_by(id=request.args["id"]).one()

    other_users = models.User.query.filter(models.User.id != request.args["id"]).all()

    matches = []
    for ou in other_users:
        matches.append((util.match_score(user, ou), ou))

    matches.sort(key=lambda x: x[0], reverse=True)

    output = []
    for m in matches:
        output.append(m[1].to_dict())

    return jsonify({"matches": output})
