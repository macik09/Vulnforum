from flask import Blueprint, request, jsonify
from flask_jwt_extended import create_access_token
from app.models.models import User
from app import db
from sqlalchemy import text 

auth_bp = Blueprint('auth', __name__, url_prefix='/api')

@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")

    user = User.query.filter_by(username=username, password=password).first()

    if user:
        access_token = create_access_token(identity=str(user.id), additional_claims={
            "username": user.username,
            "role": user.role
        })
        return jsonify({"token": access_token, "role": user.role})
    
    return jsonify({"message": "Invalid credentials"}), 401


@auth_bp.route('/register', methods=['POST'])
def register():
    data = request.get_json()
    username = data.get("username", "")
    password = data.get("password", "")

    sql = f"""
    INSERT INTO "user" (username, password, role, wallet_balance)
    VALUES ('{username}', '{password}', 'user', 5.0);
    """

    print("Executing SQL:\n", sql)

    db.session.execute(text(sql))  
    db.session.commit()

    return jsonify({"message": "User created"}), 201