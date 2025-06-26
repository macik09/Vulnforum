from app import db
from datetime import datetime
import json
from sqlalchemy.ext.mutable import MutableList
from sqlalchemy import Text


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(80), unique=True, nullable=False)
    password = db.Column(db.String(128), nullable=False)
    role = db.Column(db.String(20), nullable=False)
    wallet_balance = db.Column(db.Float, default=0.0)

    sent_messages = db.relationship('Message', foreign_keys='Message.sender_id', backref='sender', lazy=True)
    received_messages = db.relationship('Message', foreign_keys='Message.receiver_id', backref='receiver', lazy=True)
    unlocked_articles = db.relationship('UnlockedArticle', backref='user', lazy=True)
    used_nonces = db.Column(MutableList.as_mutable(db.PickleType), default=[])

    def add_nonce(self, nonce):
        if self.used_nonces is None:
            self.used_nonces = []

        if nonce not in self.used_nonces:
            self.used_nonces.append(nonce)
            return True
        return False


class Message(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    sender_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    receiver_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    content = db.Column(db.Text, nullable=False)
    timestamp = db.Column(db.DateTime, default=datetime.utcnow)

class Article(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(255), nullable=False)
    content = db.Column(db.Text, nullable=False)
    is_paid = db.Column(db.Boolean, default=False)

    unlocked_by = db.relationship('UnlockedArticle', backref='article', lazy=True)

class UnlockedArticle(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    article_id = db.Column(db.Integer, db.ForeignKey('article.id'), nullable=False)

class Comment(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    text = db.Column(db.Text, nullable=False)
    file_path = db.Column(db.String(255), nullable=True)
    article_id = db.Column(db.Integer, db.ForeignKey('article.id'), nullable=False)

    article = db.relationship('Article', backref=db.backref('comments', lazy=True))
