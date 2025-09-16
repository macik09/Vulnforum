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
        return jsonify({"message": "Użytkownik nie znaleziony"}), 404

    return jsonify({"balance": user.wallet_balance}), 200

@wallet_bp.route('/add', methods=['POST'])
@jwt_required()
def add_funds():
    user_id = get_jwt_identity()
    user = db.session.get(User, user_id)

    if not user:
        return jsonify({"message": "Użytkownik nie znaleziony"}), 404

    if user.used_nonces is None:
        user.used_nonces = []
        
    data = request.get_json()
    amount = data.get('amount')
    nonce = data.get('nonce')  

   
    if not amount or not isinstance(amount, (int, float)) or amount <= 0:
        return jsonify({"message": "Nieprawidłowa kwota. Musi być liczbą dodatnią."}), 400

  
    if nonce:
        if nonce in user.used_nonces:
            return jsonify({"message": "Nonce już użyty"}), 400
        else:
            user.used_nonces.append(nonce)

    try:
        user.wallet_balance += float(amount)
        db.session.commit()

        return jsonify({
            "message": f"Dodano {amount} vulndolców. Nowe saldo: {user.wallet_balance}",
            "new_balance": user.wallet_balance
        }), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({"message": f"Wystąpił błąd podczas dodawania środków: {str(e)}"}), 500

@wallet_bp.route('/purchase', methods=['POST'])
@jwt_required()
def purchase_article():
    user_id = get_jwt_identity()
    user = db.session.get(User, user_id)

    if not user:
        return jsonify({"message": "Użytkownik nie znaleziony"}), 404

    data = request.get_json()
    amount = data.get('amount')

    if not amount or not isinstance(amount, (int, float)) or amount <= 0:
        return jsonify({"message": "Nieprawidłowa kwota. Musi być liczbą dodatnią."}), 400

    if user.wallet_balance < float(amount):
        return jsonify({"message": "Niewystarczające środki na koncie."}), 400

    try:
        user.wallet_balance -= float(amount)
        db.session.commit() 

        response = make_response(jsonify({
            "message": f"Zakup za {amount} vulndolców zakończony sukcesem. Nowe saldo: {user.wallet_balance}",
            "new_balance": user.wallet_balance
        }))
        response.headers['X-Access-Key'] = SECRET_UNLOCK_KEY 
        return response, 200
    except Exception as e:
        db.session.rollback()
        return jsonify({"message": f"Wystąpił błąd podczas zakupu: {str(e)}"}), 500


