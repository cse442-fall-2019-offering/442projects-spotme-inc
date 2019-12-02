import datetime
import base64
import io
from PIL import Image
from sqlalchemy.orm import joinedload
from sqlalchemy.orm.exc import NoResultFound
from flask import request, jsonify

from .app import app, db
from . import models, util

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
        if k == "picture":
            data = base64.b64decode(v)
            img = Image.open(io.BytesIO(data))
            img.thumbnail((512, 512))
            out_data = io.BytesIO()
            img.save(out_data, format="jpeg")
            v = out_data.getvalue()
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
    new_match = models.AcceptedMatches()
    new_match.user1 = request.args["user1"]
    new_match.user2 = request.args["user2"]

    db.session.add(new_match)
    db.session.commit()

    return jsonify({"put-received": True})

@app.route("/accepted-matches", methods=["DELETE"])
def accepted_matches_remove():
    matches = []
    try:
        matches.append(models.AcceptedMatches.query.filter_by(user1=request.args["user1"], user2=request.args["user2"]).one())
    except NoResultFound:
        pass

    try:
        matches.append(models.AcceptedMatches.query.filter_by(user2=request.args["user1"], user1=request.args["user2"]).one())
    except NoResultFound:
        pass

    for match in matches:
        db.session.delete(match)
    db.session.commit()

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
        score, distance, fitness_level_desired = util.match_score(user, ou)
        ouJson = ou.to_dict()
        ouJson.update({'score': score, 'distance': distance, 'fitness_level_desired': fitness_level_desired})
        matches.append((score, ouJson))

    matches.sort(key=lambda x: x[0], reverse=True)

    output = []
    for m in matches:
        output.append(m[1])

    return jsonify({"matches": output})

@app.route("/ratings", methods=["GET"])
def ratings_get():

    rated_user = models.User.query.filter_by(id=request.args["id"]).one()

    total_entry = models.Ratings.query.filter_by(rated_user = rated_user.id).count()

    if total_entry == 0:

        return jsonify({"rating": "Not rated"})

    total_rate = 0.0

    for rating in models.Ratings.query.filter_by(rated_user = rated_user.id).all():

        total_rate = total_rate + rating.rate

    avg = total_rate/total_entry

    return jsonify({"rating": avg})

@app.route("/singlerating", methods=["GET"])
def single_rating_get():

    rating_count = models.Ratings.query.filter_by(current_user=request.args["rater"], rated_user=request.args["ratee"]).count()

    if rating_count == 0:
        return jsonify({"rating": 0})

    ratings_obj = models.Ratings.query.filter_by(current_user=request.args["rater"], rated_user=request.args["ratee"]).one()
    return jsonify({"rating": ratings_obj.rate})

@app.route("/ratings", methods=["PUT"])
def enter_ratings():

    new_rating = models.Ratings()
    new_rating.current_user = request.args["user1"] #current user id
    new_rating.rated_user = request.args["rated_user"] #rated user id
    new_rating.rate = request.args["rating"]

    #If the database already has one (or more for whatever reason) rating from this user for the rated user,
    #delete them all.
    for oldRating in models.Ratings.query.filter_by(current_user=new_rating.current_user, rated_user=new_rating.rated_user).all():
        db.session.delete(oldRating)

    db.session.add(new_rating)
    db.session.commit()

    return jsonify({"put-received": True})
