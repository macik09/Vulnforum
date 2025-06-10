from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt_identity
from app.models.models import db, User # Zakładam, że Twoje modele są w app/models/models.py

# Stwórz nowy blueprint dla portfela
wallet_bp = Blueprint('wallet', __name__, url_prefix='/api/wallet')

@wallet_bp.route('/', methods=['GET'])
@jwt_required()
def get_wallet_balance():
    """
    Endpoint do pobierania aktualnego salda portfela zalogowanego użytkownika.
    Wymaga uwierzytelnienia JWT.
    """
    # Pobierz ID użytkownika z tokena JWT
    user_id = get_jwt_identity()
    user = db.session.get(User, user_id) # Używamy db.session.get dla lepszej praktyki

    if not user:
        return jsonify({"message": "Użytkownik nie znaleziony"}), 404

    # Zwróć bieżące saldo użytkownika
    return jsonify({"balance": user.wallet_balance}), 200

@wallet_bp.route('/add', methods=['POST'])
@jwt_required()
def add_funds():
    """
    Endpoint do dodawania środków do portfela zalogowanego użytkownika.
    Wymaga uwierzytelnienia JWT.
    Oczekuje JSON z kluczem 'amount' (np. {"amount": 5.0}).
    """
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

