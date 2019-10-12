from sqlalchemy import inspect

from .app import db

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

    def to_dict(self):
        return {c.key: getattr(self, c.key)
                for c in inspect(self).mapper.column_attrs}
