from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models.models import db, User # Zakładam, że Twoje modele są w app/models/models.py

# Stwórz nowy blueprint dla portfela
wallet_bp = Blueprint('wallet', __name__, url_prefix='/api/wallet')

@wallet_bp.route('/', methods=['GET'])
@jwt_required()
def get_wallet_balance():

    # Pobierz ID użytkownika z tokena JWT
    user_id = get_jwt_identity()
    user = db.session.get(User, user_id) # Używamy db.session.get dla lepszej praktyki

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

    data = request.get_json()
    amount = data.get('amount')

    # Walidacja kwoty
    if not amount or not isinstance(amount, (int, float)) or amount <= 0:
        return jsonify({"message": "Nieprawidłowa kwota. Musi być liczbą dodatnią."}), 400

    try:
        # Dodaj kwotę do salda portfela użytkownika
        user.wallet_balance += float(amount)
        db.session.commit() # Zapisz zmiany w bazie danych

        return jsonify({
            "message": f"Dodano {amount} vulndolców. Nowe saldo: {user.wallet_balance}",
            "new_balance": user.wallet_balance
        }), 200
    except Exception as e:
        # W razie błędu cofnij transakcję i zwróć błąd serwera
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

    # Walidacja kwoty
    if not amount or not isinstance(amount, (int, float)) or amount <= 0:
        return jsonify({"message": "Nieprawidłowa kwota. Musi być liczbą dodatnią."}), 400

    if user.wallet_balance < float(amount):
        return jsonify({"message": "Niewystarczające środki na koncie."}), 400

    try:
        # Odejmij kwotę z salda portfela użytkownika
        user.wallet_balance -= float(amount)
        db.session.commit()  # Zapisz zmiany w bazie danych

        return jsonify({
            "message": f"Zakup za {amount} vulndolców zakończony sukcesem. Nowe saldo: {user.wallet_balance}",
            "new_balance": user.wallet_balance
        }), 200
    except Exception as e:
        db.session.rollback()
        return jsonify({"message": f"Wystąpił błąd podczas zakupu: {str(e)}"}), 500
