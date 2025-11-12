from flask import Blueprint, jsonify, request, make_response
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models.models import db, User 

wallet_bp = Blueprint('wallet', __name__, url_prefix='/api/wallet')
SECRET_UNLOCK_KEY = "iYWxpY2UiLCJyb2xlIjoidXNlciJ9.CYuplMERNN-aFy22Jss5rBv0sVRZcQggthyUyHErHZA"
@wallet_bp.route('/', methods=['GET'])
@jwt_required()
def get_wallet_balance():
    user_id = get_jwt_identity()
    user = db.session.get(User, user_id) 

    if not user:
        return jsonify({"message": "User not found"}), 404

    return jsonify({"balance": user.wallet_balance}), 200

@wallet_bp.route('/add', methods=['POST'])
@jwt_required()
def add_funds():
    user_id = get_jwt_identity()
    user = db.session.get(User, user_id)

    if not user:
        return jsonify({"message": "User not found"}), 404

    if user.used_nonces is None:
        user.used_nonces = []
        
    data = request.get_json()
    amount = data.get('amount')
    nonce = data.get('nonce')  

    if not amount or not isinstance(amount, (int, float)) or amount <= 0:
        return jsonify({"message": "Invalid amount. Must be a positive number."}), 400

    if nonce:
        if nonce in user.used_nonces:
            return jsonify({"message": "Nonce already used"}), 400
        else:
            user.used_nonces.append(nonce)

    try:
        user.wallet_balance += float(amount)
        db.session.commit()

        return jsonify({
            "message": f"Added {amount} vulndolces. New balance: {user.wallet_balance}",
            "new_balance": user.wallet_balance
        }), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({"message": f"Error while adding funds: {str(e)}"}), 500

@wallet_bp.route('/purchase', methods=['POST'])
@jwt_required()
def purchase_article():
    user_id = get_jwt_identity()
    user = db.session.get(User, user_id)

    if not user:
        return jsonify({"message": "User not found"}), 404

    data = request.get_json()
    amount = data.get('amount')

    if not amount or not isinstance(amount, (int, float)) or amount <= 0:
        return jsonify({"message": "Invalid amount. Must be a positive number."}), 400

    if user.wallet_balance < float(amount):
        return jsonify({"message": "Insufficient funds."}), 400

    try:
        user.wallet_balance -= float(amount)
        db.session.commit() 

        response = make_response(jsonify({
            "message": f"Purchase of {amount} vulndolces successful. New balance: {user.wallet_balance}",
            "new_balance": user.wallet_balance
        }))
        response.headers['X-Access-Key'] = SECRET_UNLOCK_KEY 
        return response, 200
    except Exception as e:
        db.session.rollback()
        return jsonify({"message": f"Error during purchase: {str(e)}"}), 500
