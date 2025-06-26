from flask import Blueprint, jsonify, request
from flask_jwt_extended import jwt_required, get_jwt 
from app.models.models import Article, User 
from app import db
import logging

logger = logging.getLogger(__name__)

admin_bp = Blueprint('admin', __name__, url_prefix='/api/admin')


def verify_admin_role():
    claims = get_jwt()
    if claims.get("role") != "admin":
        logger.warning(f"Access denied: User with role '{claims.get('role', 'N/A')}' tried to access admin endpoint.")
        return False, jsonify({"error": "Dostęp zabroniony. Wymagane uprawnienia administratora."}), 403
    return True, None, None 

@admin_bp.route('/articles/<int:article_id>', methods=['PATCH'])
@jwt_required()
def update_article(article_id):
   
    is_admin, error_response, status_code = verify_admin_role()
    if not is_admin:
        return error_response, status_code

    article = Article.query.get(article_id)
    if not article:
        return jsonify({"error": "Nie znaleziono artykułu"}), 404

    data = request.get_json()
    is_paid = data.get("is_paid")

    if is_paid is None:
        return jsonify({"error": "Brak wartości is_paid"}), 400

    article.is_paid = bool(is_paid)
    db.session.commit()

    return jsonify({"message": "Zaktualizowano artykuł", "is_paid": article.is_paid}), 200

@admin_bp.route('/users', methods=['GET'])
@jwt_required()
def get_all_users():
    
    is_admin, error_response, status_code = verify_admin_role()
    if not is_admin:
        return error_response, status_code

    users = User.query.all()
    result = []
    for user in users:
        result.append({
            "id": user.id,
            "username": user.username,
            "balance": user.wallet_balance,
            "unlocked_articles": [ua.article_id for ua in user.unlocked_articles]
        })
    return jsonify(result), 200