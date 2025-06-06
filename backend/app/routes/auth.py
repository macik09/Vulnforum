from flask import Blueprint, request, jsonify
import json
from app.utils.jwt_utils import generate_token

auth_bp = Blueprint('auth', __name__, url_prefix='/api')

@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    with open("app/models/users.json") as f:
        users = json.load(f)
    
    for user in users:
        if user["username"] == data["username"] and user["password"] == data["password"]:
            token = generate_token(user)
            return jsonify({"token": token, "role": user["role"], "username": user["username"]})

    
    return jsonify({"message": "Invalid credentials"}), 401
