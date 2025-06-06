from flask import Blueprint, request, jsonify
import json
import os
from datetime import datetime

messages_bp = Blueprint('messages', __name__, url_prefix='/api/messages')

# Lokalizacja przykładowej bazy wiadomości
MESSAGES_FILE = os.path.join(os.path.dirname(__file__), '../models/messages.json')

# Funkcja pomocnicza – wczytuje wiadomości z pliku
def load_messages():
    if not os.path.exists(MESSAGES_FILE):
        return []
    with open(MESSAGES_FILE, 'r') as f:
        return json.load(f)

# Funkcja pomocnicza – zapisuje wiadomości
def save_messages(messages):
    with open(MESSAGES_FILE, 'w') as f:
        json.dump(messages, f, indent=2)

# Pobierz wszystkie wiadomości (dla testów – w realnej aplikacji wymagałoby autoryzacji)
@messages_bp.route('/', methods=['GET'])
def get_messages():
    messages = load_messages()
    return jsonify(messages)

# Wyślij wiadomość
@messages_bp.route('/', methods=['POST'])
def send_message():
    data = request.json
    required = ['sender', 'recipient', 'content']

    if not all(key in data for key in required):
        return jsonify({"error": "Missing fields"}), 400

    messages = load_messages()

    message = {
        "id": len(messages) + 1,
        "sender": data['sender'],
        "recipient": data['recipient'],
        "content": data['content'],
        "timestamp": datetime.utcnow().isoformat()
    }

    messages.append(message)
    save_messages(messages)

    return jsonify({"status": "Message sent", "message": message}), 201
