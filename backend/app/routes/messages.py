from flask import Blueprint, request, jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from datetime import datetime
from app.models.models import db, Message, User

messages_bp = Blueprint('messages', __name__, url_prefix='/api/messages')

# Pobierz wszystkie wiadomości aktualnie zalogowanego użytkownika
@messages_bp.route('/', methods=['GET'])
@jwt_required()
def get_messages():
    user_id = get_jwt_identity()

    messages = Message.query.filter(
        (Message.sender_id == user_id) | (Message.receiver_id == user_id)
    ).order_by(Message.timestamp.desc()).all()

    return jsonify([
        {
            "id": m.id,
            "sender": User.query.get(m.sender_id).username,
            "recipient": User.query.get(m.receiver_id).username,
            "content": m.content,
            "timestamp": m.timestamp.isoformat()
        } for m in messages
    ])

# Wyślij wiadomość
@messages_bp.route('/', methods=['POST'])
@jwt_required()
def send_message():
    user_id = get_jwt_identity()
    data = request.get_json()
    recipient_username = data.get("recipient")
    content = data.get("content")

    if not recipient_username or not content:
        return jsonify({"error": "Missing recipient or content"}), 400

    recipient = User.query.filter_by(username=recipient_username).first()
    if not recipient:
        return jsonify({"error": "Recipient not found"}), 404

    new_message = Message(
        sender_id=user_id,
        receiver_id=recipient.id,
        content=content,
        timestamp=datetime.utcnow()
    )
    db.session.add(new_message)
    db.session.commit()

    return jsonify({
        "status": "Message sent",
        "message": {
            "id": new_message.id,
            "sender": User.query.get(user_id).username,
            "recipient": recipient.username,
            "content": content,
            "timestamp": new_message.timestamp.isoformat()
        }
    }), 201
