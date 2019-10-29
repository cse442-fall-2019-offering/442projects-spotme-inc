from sqlalchemy import inspect
from sqlalchemy.sql import func

from .app import db

class ChatMessage(db.Model):
    id = db.Column(db.Integer(), primary_key=True)

    # First user
    sender = db.Column(db.Integer(), db.ForeignKey("user.id"), nullable=False)

    # Person User1 is speaking to
    receiver = db.Column(db.Integer(), db.ForeignKey("user.id"), nullable=False)

    # Chat message sent from User 1 to receiver
    message = db.Column(db.Text(), nullable=False)

    # Tine stamp message was sent at
    time = db.Column(db.DateTime(), server_default=func.now(), nullable=False)

    def to_dict(self):
        return {
            "sender": self.sender,
            "receiver": self.receiver,
            "message": self.message,
            "time": self.time.strftime("%Y-%m-%dT%H:%M:%S.%f%z"),
        }

class AcceptedMatches(db.Model):
    id = db.Column(db.Integer(), primary_key=True)

    # The first user of an accepted pair
    user1 = db.Column(db.Integer(), db.ForeignKey("user.id"), nullable=False)
    
    # The second user of an accepted pair
    user2 = db.Column(db.Integer(), db.ForeignKey("user.id"), nullable=False)

    __table_args__ = (db.Index("idx_accepted_match", "user1", "user2", unique=True),)

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)

    # Google username
    username = db.Column(db.String(64), unique=True, nullable=False)

    # User's name
    name = db.Column(db.String(64), nullable=False)

    # User's date of birth
    dob = db.Column(db.Date(), nullable=True)

    # User's gender
    gender = db.Column(db.Integer(), nullable=True)

    # Weight in Kg
    weight = db.Column(db.Float(), nullable=True)

    # User's prefered partner gender
    partner_gender = db.Column(db.Integer(), nullable=True)

    # User's prefered partner fitness level
    partner_level = db.Column(db.Integer(), nullable=True)

    # Fitness level
    level = db.Column(db.Integer(), nullable=True)

    # User's last latitude and logitude
    lat = db.Column(db.Float(), nullable=True)
    lon = db.Column(db.Float(), nullable=True)
    
    # User's distance preference
    radius = db.Column(db.Integer(), nullable=True)

    accepted_matches = db.relationship("User", secondary="accepted_matches",
            primaryjoin="User.id==AcceptedMatches.user1",
            secondaryjoin="User.id==AcceptedMatches.user2")

    def to_dict(self):
        return {
            "id": self.id,
            "username": self.username,
            "name": self.name,
            "dob": self.dob.strftime("%Y-%m-%d"),
            "gender": self.gender,
            "weight": self.weight,
            "partner_gender": self.partner_gender,
            "partner_level": self.partner_level,
            "level": self.level,
            "lat": self.lat,
            "lon": self.lon,
            "radius": self.radius,
        }
